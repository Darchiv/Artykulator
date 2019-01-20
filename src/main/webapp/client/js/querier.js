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

function buildLevel(namespace, obj, level, props) {
    console.log('buildLevel', namespace, obj, level, props)
    
    var result = Object.keys(obj).map((key) => {
        var element
        
        if (typeof obj[key] == 'object') {
            element = buildLevel(namespace + '.' + key, obj[key], level + 1, props)
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
    
    result.unshift(h('div', {style: {'padding-left': level*30 + 'px'}}, "{"))
    
    result.push(h('input', {
        type: 'text',
        id: 'newObjInput_' + namespace,
        style: {'margin-left': (level+1)*30 + 'px'}
    }))
    
    result.push(h('button', {
        className: 'btn btn-primary',
        onClick: props.onNewObject.bind(null, obj, 'newObjInput_' + namespace),
    }, "Add an object"))
    
    result.push(h('div', {style: {'padding-left': level*30 + 'px'}}, "}"))
    
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
    
    onSubmit(schema, saveQuery) {
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
                this.setState({
                    message: 'OK',
                    msgType: 'success'
                })
                
                saveQuery({
                    url: url,
                    method: method,
                    params: schema
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
                    message: JSON.stringify(json, null, 2),
                    msgType: 'success'
                })
                
                saveQuery({
                    url: url,
                    method: method,
                    params: schema
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
        console.log('Render QueryBuilder. schema = ', props.schema)
        
        return h('div', null, [
              h('label', null, "URL", h('input', {type: 'text', id: 'inputUrl', value: props.url})),
              h('label', null, "Method", h('input', {type: 'text', id: 'inputMethod', value: props.method})),
              h('div', null, buildLevel('', props.schema, 0, props)),
              h('h3', null, "Submit"),
              h('button', {className: "btn btn-primary", onClick: this.onSubmit.bind(this, props.schema, props.saveQuery)}, "Submit"),
              (this.state.message) ? h('div', {className: 'alert alert-' + this.state.msgType, role: 'alert'}, this.state.message) : null
        ])
    }
}


class Main extends preact.Component {
    constructor() {
        super()
        
        this.state = {
            queries: null,
            query: {params: {}}
        }
        
        this.fetchQueries();
    }
    
    saveQuery(query) {
        console.log('saveQuery', query)
        
        fetch('api/Querier', {
            method: 'post',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                url: query.url,
                method: query.method,
                params: JSON.stringify(query.params)
            })
        })
        .then(response => {
            console.log('saveQueryResponse', response)
    
            this.fetchQueries();
        })
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
        
        try {
            query.params = JSON.parse(query.params);
        } catch (e) {
            console.warn(e.message)
            query.params = {}
        }
        
        this.setState({query: query})
    }
    
    onNewObject(parent, id) {
        var objName = document.getElementById(id).value
        console.log('onNewObject', parent, objName)
        
        if (!objName) return
        
        parent[objName] = {}
        
        document.getElementById(id).value = ''
        
        console.log(parent)
        
        this.forceUpdate()
        //this.setState({query: this.state.query})
    }
    
    onNewArray(parent) {
        console.log('onNewArray', parent)
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
                    h(QueryBuilder, {
                        onNewObject: this.onNewObject.bind(this),
                        onNewArray: this.onNewArray.bind(this),
                        schema: this.state.query.params,
                        method: this.state.query.method,
                        url: this.state.query.url,
                        saveQuery: this.saveQuery.bind(this)
                    }, null)
                    
                ])
            ])
          )
        )
    }
}


preact.render(h(Main), document.getElementById('app'))
