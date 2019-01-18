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
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.model.UploadedFile;
import pl.drsh.artykulator.db.Article;

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

    @Inject
    private ArticleFacade articleFacade;
    
    @Inject
    private UserFacade userFacade;
    
    public void add() throws IOException {
        Article article = new Article();
        
        article.setTitle(this.title);
        article.setDescription(this.description);
        article.setAddedBy(userFacade.find(this.addedBy));
        article.setDateAdded(new Date());

        if (file != null) {
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
}
