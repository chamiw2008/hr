/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.entity;

import java.io.Serializable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 *
 * @author Administrator
 */
@Entity
public class InstitutionMonthSummery implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    Institution institution;
    Integer summeryYear;
    Integer summeryMonth;
    Long completedSets;
    Long totalSets;
    boolean completed;
    boolean started;
    boolean halfway;
    Long recordsWithoutIx;
    Long recordsWithoutDesignations;
    Long recordsWithoutInstitution;
    Long recordsWithoutDesignationMappging;
    Long recordsWithoutInstitutionMappings;
    Long recordsWithoutNic;
    Long activeCount;
    Long tempCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public Integer getSummeryYear() {
        return summeryYear;
    }

    public void setSummeryYear(Integer summeryYear) {
        this.summeryYear = summeryYear;
    }

    public Integer getSummeryMonth() {
        return summeryMonth;
    }

    public void setSummeryMonth(Integer summeryMonth) {
        this.summeryMonth = summeryMonth;
    }

    public Long getCompletedSets() {
        return completedSets;
    }

    public void setCompletedSets(Long completedSets) {
        this.completedSets = completedSets;
    }

    public Long getTotalSets() {
        return totalSets;
    }

    public void setTotalSets(Long totalSets) {
        this.totalSets = totalSets;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }


    public boolean isHalfway() {
        return halfway;
    }

    public void setHalfway(boolean halfway) {
        this.halfway = halfway;
    }

    public Long getRecordsWithoutIx() {
        return recordsWithoutIx;
    }

    public void setRecordsWithoutIx(Long recordsWithoutIx) {
        this.recordsWithoutIx = recordsWithoutIx;
    }

    public Long getRecordsWithoutDesignations() {
        return recordsWithoutDesignations;
    }

    public void setRecordsWithoutDesignations(Long recordsWithoutDesignations) {
        this.recordsWithoutDesignations = recordsWithoutDesignations;
    }

    public Long getRecordsWithoutInstitution() {
        return recordsWithoutInstitution;
    }

    public void setRecordsWithoutInstitution(Long recordsWithoutInstitution) {
        this.recordsWithoutInstitution = recordsWithoutInstitution;
    }

    public Long getRecordsWithoutDesignationMappging() {
        return recordsWithoutDesignationMappging;
    }

    public void setRecordsWithoutDesignationMappging(Long recordsWithoutDesignationMappging) {
        this.recordsWithoutDesignationMappging = recordsWithoutDesignationMappging;
    }

    public Long getRecordsWithoutInstitutionMappings() {
        return recordsWithoutInstitutionMappings;
    }

    public void setRecordsWithoutInstitutionMappings(Long recordsWithoutInstitutionMappings) {
        this.recordsWithoutInstitutionMappings = recordsWithoutInstitutionMappings;
    }

    public Long getRecordsWithoutNic() {
        return recordsWithoutNic;
    }

    public void setRecordsWithoutNic(Long recordsWithoutNic) {
        this.recordsWithoutNic = recordsWithoutNic;
    }

    public Long getActiveCount() {
        return activeCount;
    }

    public void setActiveCount(Long activeCount) {
        this.activeCount = activeCount;
    }

    public Long getTempCount() {
        return tempCount;
    }

    public void setTempCount(Long tempCount) {
        this.tempCount = tempCount;
    }

    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof InstitutionMonthSummery)) {
            return false;
        }
        InstitutionMonthSummery other = (InstitutionMonthSummery) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.health.entity.InstitutionMonthSummery[ id=" + id + " ]";
    }

}
