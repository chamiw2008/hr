/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.facade;

import gov.health.entity.InstitutionSet;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author IT
 */
@Stateless
public class InstitutionSetFacade extends AbstractFacade<InstitutionSet> {
    @PersistenceContext(unitName = "HOPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InstitutionSetFacade() {
        super(InstitutionSet.class);
    }
    
    
    
    
    
    
}
