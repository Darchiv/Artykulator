/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.UploadedFile;
import pl.drsh.artykulator.db.Article;
import pl.drsh.artykulator.db.Revision;
import pl.drsh.artykulator.db.RevisionPK;
import pl.drsh.artykulator.db.User;

/**
 *
 * @author drsh
 */
@Named
@RequestScoped
@Transactional
public class ArticleManager {
    
    @Getter @Setter private String title;
    @Getter @Setter private String description;
    @Getter @Setter private int addedBy;
    @Getter @Setter private UploadedFile file;
    
    @Getter @Setter private int editId;
    @Getter @Setter private int modifiedBy;

    @Inject
    private ArticleFacade articleFacade;
    
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
        
                Article article = articleFacade.find(idNum);

                if (article != null) {
                    this.title = article.getTitle();
                    this.description = article.getDescription();
                    this.addedBy = article.getAddedBy().getId();
                }
            } catch (NumberFormatException e) {
                
            }
        }
        
    }
    
    public void add() throws IOException {
        Article article = new Article();
        
        article.setTitle(this.title);
        article.setDescription(this.description);
        article.setAddedBy(userFacade.find(this.addedBy));
        article.setDateAdded(new Date());

        if (file != null && file.getSize() > 0) {
            InputStream input = file.getInputstream();
            int len;
            byte[] buf = new byte[1024*1024];
            
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            
            while ((len = input.read(buf)) != -1) {
                o.write(buf, 0, len);
            }
            
            article.setFile(buf);
        }

        articleFacade.create(article);
    }

    public void remove(int id) throws AbortProcessingException {
        articleFacade.remove(articleFacade.find(id));
    }
    
    public void edit() throws IOException {
        Article article = articleFacade.find(this.editId);
        
        article.setTitle(this.title);
        article.setDescription(this.description);
        article.setAddedBy(userFacade.find(this.addedBy));

        if (file != null && file.getSize() > 0) {
            InputStream input = file.getInputstream();
            int len;
            byte[] buf = new byte[1024*1024];
            
            ByteArrayOutputStream o = new ByteArrayOutputStream();
            
            while ((len = input.read(buf)) != -1) {
                o.write(buf, 0, len);
            }
            
            article.setFile(buf);
        }
        
        List<Revision> revisions = article.getRevisionList();
        User editingUser = userFacade.find(this.modifiedBy);
        revisions.add(new Revision(new RevisionPK(editId), new Date(), editingUser));

        articleFacade.edit(article);
    }
}
