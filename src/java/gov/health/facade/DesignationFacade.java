/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.facade;

import gov.health.entity.Designation;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author IT
 */
@Stateless
public class DesignationFacade extends AbstractFacade<Designation> {
    @PersistenceContext(unitName = "HOPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DesignationFacade() {
        super(Designation.class);
    }
    
}
