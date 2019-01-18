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
import pl.drsh.artykulator.db.Revision;

/**
 *
 * @author drsh
 */
@Named
@ApplicationScoped
public class RevisionFacade extends AbstractFacade<Revision> {

    @PersistenceContext(unitName = "pl.drsh_Artykulator_PU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RevisionFacade() {
        super(Revision.class);
    }
    
}
