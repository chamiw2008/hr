/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.data;

import gov.health.entity.Designation;

/**
 *
 * @author Buddhika
 */
public class DesignationSummeryRecord {
    
    
    String designationName;
    Designation designation;
    Long count;
    String comment;

    public String getDesignationName() {
        return designationName;
    }

    public void setDesignationName(String designationName) {
        this.designationName = designationName;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    
    
}
