/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.facade;

import gov.health.entity.Province;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author IT
 */
@Stateless
public class ProvinceFacade extends AbstractFacade<Province> {
    @PersistenceContext(unitName = "HOPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ProvinceFacade() {
        super(Province.class);
    }
    
}
