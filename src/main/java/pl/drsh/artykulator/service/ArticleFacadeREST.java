/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import pl.drsh.artykulator.UserFacade;
import pl.drsh.artykulator.db.Article;
import pl.drsh.artykulator.db.Revision;
import pl.drsh.artykulator.db.RevisionPK;
import pl.drsh.artykulator.db.User;

/**
 *
 * @author drsh
 */
@Stateless
@Path("article")
public class ArticleFacadeREST extends AbstractFacade<Article> {

    @PersistenceContext(unitName = "pl.drsh_Artykulator_PU")
    private EntityManager em;
    
    @Inject
    private UserFacade userFacade;

    public ArticleFacadeREST() {
        super(Article.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Article entity) {
        User addedBy = entity.getAddedBy();
        
        if (addedBy == null) {
            throw new BadRequestException();
        }
        
        if (userFacade.find(addedBy.getId()) == null) {
            throw new BadRequestException();
        }
        
        entity.setDateAdded(new Date());
        entity.setRevisionList(new ArrayList<Revision>());
        
        try {
            super.create(entity);
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Article entity) {
        Article article = super.find(id);
        
        if (article == null) {
            throw new NotFoundException();
        }
        
        entity.setId(id);
        entity.setDateAdded(article.getDateAdded());
        entity.setAddedBy(article.getAddedBy());
        
        List<Revision> editRevisions = entity.getRevisionList();
        
        if (editRevisions == null || editRevisions.size() != 1) {
            throw new BadRequestException();
        }
        
        User editingUser = userFacade.find(editRevisions.get(0).getUserId().getId());
        
        if (editingUser == null) {
            throw new BadRequestException();
        }
        
        List<Revision> articleRevisions = article.getRevisionList();
        articleRevisions.add(new Revision(new RevisionPK(id), new Date(), editingUser));
        
        entity.setRevisionList(articleRevisions);
        
        if (entity.getDescription() == null || entity.getDescription().equals("")) {
            entity.setDescription(article.getDescription());
        }
        
        if (entity.getFile() == null) {
            entity.setFile(article.getFile());
        }
        
        try {
            super.edit(entity);
        } catch (Exception e) {
            throw new BadRequestException();
        }
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        Article article = super.find(id);
        
        if (article == null) {
            throw new NotFoundException();
        }
        
        super.remove(article);
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Article find(@PathParam("id") Integer id) {
        Article article = super.find(id);

        if (article == null) {
            throw new NotFoundException();
        } 
        
        return article;
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Article> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Article> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
