/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gov.health.facade;

import gov.health.entity.InstitutionMonthSummery;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author Administrator
 */
@Stateless
public class InstitutionMonthSummeryFacade extends AbstractFacade<InstitutionMonthSummery> {
    @PersistenceContext(unitName = "HOPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public InstitutionMonthSummeryFacade() {
        super(InstitutionMonthSummery.class);
    }
    
}
