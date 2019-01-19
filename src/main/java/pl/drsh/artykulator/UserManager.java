/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator;

import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import pl.drsh.artykulator.db.Article;
import pl.drsh.artykulator.db.User;

/**
 *
 * @author drsh
 */
@Named
@RequestScoped
@Transactional
public class UserManager {
    @Getter @Setter private String name;
    
    @Getter @Setter private int editId;
    
    @Inject
    private UserFacade userFacade;
    
    @PostConstruct
    void init() {
        Map<String, String> params = FacesContext.getCurrentInstance().
            getExternalContext().getRequestParameterMap();
        String id = params.get("id");
        
        
        if (id != null && !id.equals("")) {
            try {
               int idNum = Integer.parseInt(id);
               this.editId = idNum;
        
                User user = userFacade.find(idNum);

                if (user != null) {
                    this.name = user.getName();
                }
            } catch (NumberFormatException e) {
                
            }
        }
        
    }
    
    public void add() {
        User user = new User();
        user.setName(this.name);
        userFacade.create(user);
    }
    
    public void remove(int id) throws AbortProcessingException {
        System.err.println("Remove id = " + id);
        userFacade.remove(userFacade.find(id));
    }
    
//    public void remove(ActionEvent event) {
//	String id = (String) event.getComponent().getAttributes().get("removeId");
//        
//        System.out.println("Remove id = "+ id);
//    }
    
    public void edit() {
        User user = userFacade.find(this.editId);
        user.setName(this.name);
        userFacade.edit(user);
    }
    
}
