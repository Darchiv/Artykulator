/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

var h = preact.h


class Table extends preact.Component {
    render(props) {
        if (props.list == null || Array.isArray(props.list) && props.list.length == 0) {
            return 'No data.'
        }

        return (
            h('table', {className: 'table'},
                h('thead', null, 
                    h('tr', null,
                        Object.keys(props.meta).map((key) => h('th', {scope: 'col'},
                            props.meta[key]
                        ))
                    )
                ),
                h('tbody', null,
                    props.list.map((elem) => 
                        h('tr', null, 
                            Object.keys(props.meta).map((key) => 
                                (key == 'selectBtn' ? 
                                      h('button', {
                                          type: 'button',
                                          className: 'btn btn-primary',
                                          onClick: props.selectHandler.bind(null, elem)
                                      }, 'select')
                                    : h('td', null,
                                        elem[key]
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    }
}

var fileData = {}

function buildLevel(namespace, obj, level) {
    var result = Object.keys(obj).map((key) => {
        var element
        
        if (typeof obj[key] == 'object') {
            element = buildLevel(namespace + '.' + key, obj[key], level + 1)
        } else if (obj[key] == 'text') {
            element = h('input', {type: 'text', id: 'qb_' + namespace + '.' + key})
        } else if (obj[key] == 'number') {
            element = h('input', {type: 'number', id: 'qb_' + namespace + '.' + key})
        } else if (obj[key] == 'fileArr') {
            element = h('input', {type: 'file', id: 'qb_' + namespace + '.' + key, onchange: (e) => {
                var fileList = e.target.files
                var reader = new FileReader()
                reader.onload = function(event) {
                    fileData['qb_' + namespace + '.' + key] = Array.from(new Int8Array(event.target.result))
                    console.log(fileData['qb_' + namespace + '.' + key])
                }
                reader.readAsArrayBuffer(fileList[0])
            }})
        } else if (obj[key] == 'fileBase') {
            
        } else {
            console.warn("Invalid input type: ", obj[key])
        }

        return (
            h('div', {style: {'padding-left': level*30 + 'px'}}, [
                h('label', null, key+' ', element)
            ])
        )
    })
    
    console.log("buildLevel", result)
    return result
}

async function buildRequest(request, namespace, obj) {
    Object.keys(obj).map(async (key) => {
        var element
        
        console.log('buildRequest, mapping: ', namespace, key)
        
        if (typeof obj[key] == 'object') {
            request[key] = {}
            buildRequest(request[key], namespace + '.' + key, obj[key])
        } else if (obj[key] == 'text') {
            element = document.getElementById('qb_' + namespace + '.' + key)
            request[key] = element.value
        } else if (obj[key] == 'number') {
            element = document.getElementById('qb_' + namespace + '.' + key)
            request[key] = element.value
        } else if (obj[key] == 'fileArr') {
            request[key] = fileData['qb_' + namespace + '.' + key]
        } else if (obj[key] == 'fileBase') {
            
        } else {
            console.warn("Invalid input type: ", obj[key])
        }
    })
}

class QueryBuilder extends preact.Component {
    constructor(props) {
        super(props)
        
        this.state = {
            message: null,
            msgType: null
        }
    }
    
    onSubmit(schema) {
        var request = {}
        
        buildRequest(request, '', schema)
        
        console.log("Submiting. schema = ", schema, ", request = ", request)
        
        var url = document.getElementById('inputUrl').value
        var method = document.getElementById('inputMethod').value.toLowerCase()
        var corr = false
        
        fetch(url, {
            method: method,
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: (method == 'get' || method == 'delete') ? undefined : JSON.stringify(request)
        })
        .then(response => {
            console.log("Fetch response", response)
            
            if (!response.ok) {
                this.setState({
                    message: response.status + ' ' + response.statusText,
                    msgType: 'danger'
                })
            } else if (response.status == 204) {
                console.log('respone 204')
                
                this.setState({
                    message: 'OK',
                    msgType: 'success'
                })
            } else {
                corr = true
                return response.json()
            }
        })
        .then((json) => {
            console.log("Fetch response json", json)
    
            if (corr) {
                this.setState({
                    message: JSON.stringify(json),
                    msgType: 'success'
                })
            }
        })
        .catch((error) => {
            console.warn(error)
    
            this.setState({
                message: error.message,
                msgType: 'danger'
            })
        })
    }
    
    render(props, state) {
        var schema
        
        console.log(props.schemaJson)
        
        try {
            schema = JSON.parse(props.schemaJson);
        } catch (e) {
            console.warn(e.message)
            schema = {}
        }
        
        console.log('Render QueryBuilder. schema = ', schema)
        
        return h('div', null, [
              h('label', null, "URL", h('input', {type: 'text', id: 'inputUrl', value: props.url})),
              h('label', null, "Method", h('input', {type: 'text', id: 'inputMethod', value: props.method})),
              h('div', null, buildLevel('', schema, 0)),
              h('h3', null, "Submit"),
              h('button', {className: "btn btn-primary", onClick: this.onSubmit.bind(this, schema)}, "Submit"),
              (this.state.message) ? h('div', {className: 'alert alert-' + this.state.msgType, role: 'alert'}, this.state.message) : null
        ])
    }
}


class Main extends preact.Component {
    constructor() {
        super()
        
        this.state = {
            queries: null,
            selectedQuery: null
        }
        
        this.fetchQueries();
    }
    
    fetchQueries() {
        fetch('api/Querier', {
            method: 'get'
        })
        .then(response => response.json())
        .then((json) => {
            this.setState({
                queries: json
            })
        })
    }
    
    selectedQuery(query) {
        console.log('selectedQuery', query)
        this.setState({selectedQuery: query})
    }

    render(props, state) {
        return (
          h('div', {className: 'container'},
            h('div', {className: 'row'}, [
                h('div', {className: 'col'}, [
                    h('h3', null, 'Last valid queries'),
                    h(Table, {meta: {
                        url: 'URL',
                        method: 'Method',
                        params: 'Schema',
                        selectBtn: 'select'
                    }, list: this.state.queries,
                    selectHandler: this.selectedQuery.bind(this)})
                ]),
                h('div', {className: 'col'}, [
                    h('h3', null, 'Query builder'),
                    ((this.state.selectedQuery) ? (
                        h(QueryBuilder, {schemaJson: this.state.selectedQuery.params, method: this.state.selectedQuery.method, url: this.state.selectedQuery.url}, null)
                    ) : (
                        h(QueryBuilder, {schemaJson: "{}"}, null)
                    ))
                ])
            ])
          )
        )
    }
}


preact.render(h(Main), document.getElementById('app'))
