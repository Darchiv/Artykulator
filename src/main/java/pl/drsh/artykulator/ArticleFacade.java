/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.drsh.artykulator;

import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import pl.drsh.artykulator.db.Article;

/**
 *
 * @author drsh
 */
@Named
@ApplicationScoped
public class ArticleFacade extends AbstractFacade<Article> {

    @PersistenceContext(unitName = "pl.drsh_Artykulator_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ArticleFacade() {
        super(Article.class);
    }
    
}
