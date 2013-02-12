/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.entity;

import gov.health.entity.Institution;
import gov.health.entity.Person;
import gov.health.entity.PersonInstitution;
import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;

/**
 *
 * @author Buddhika
 */
@Entity
public class TransferHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Temporal(javax.persistence.TemporalType.DATE)
    Date createdDate;
    @ManyToOne
    Person person;
    
    @ManyToOne
    Institution fromInstitution;
    @ManyToOne
    Institution toInstitution;
    Boolean approvedFromOrigin;
    Boolean approvedOriginUser;
    @Temporal(javax.persistence.TemporalType.DATE)
    Date approvedOriginDate;
    
    Boolean approvedFromDestination;
    Boolean approvedDesignation;
    @Temporal(javax.persistence.TemporalType.DATE)
    Date approvedDestinationDate;
    
    @ManyToOne
    PersonInstitution personInstitution;

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Date getApprovedOriginDate() {
        return approvedOriginDate;
    }

    public void setApprovedOriginDate(Date approvedOriginDate) {
        this.approvedOriginDate = approvedOriginDate;
    }

    public Date getApprovedDestinationDate() {
        return approvedDestinationDate;
    }

    public void setApprovedDestinationDate(Date approvedDestinationDate) {
        this.approvedDestinationDate = approvedDestinationDate;
    }

    
    
    
    public Institution getFromInstitution() {
        return fromInstitution;
    }

    public void setFromInstitution(Institution fromInstitution) {
        this.fromInstitution = fromInstitution;
    }

    public Institution getToInstitution() {
        return toInstitution;
    }

    public void setToInstitution(Institution toInstitution) {
        this.toInstitution = toInstitution;
    }

    public Boolean getApprovedFromOrigin() {
        return approvedFromOrigin;
    }

    public void setApprovedFromOrigin(Boolean approvedFromOrigin) {
        this.approvedFromOrigin = approvedFromOrigin;
    }

    public Boolean getApprovedOriginUser() {
        return approvedOriginUser;
    }

    public void setApprovedOriginUser(Boolean approvedOriginUser) {
        this.approvedOriginUser = approvedOriginUser;
    }

    public Boolean getApprovedFromDestination() {
        return approvedFromDestination;
    }

    public void setApprovedFromDestination(Boolean approvedFromDestination) {
        this.approvedFromDestination = approvedFromDestination;
    }

    public Boolean getApprovedDesignation() {
        return approvedDesignation;
    }

    public void setApprovedDesignation(Boolean approvedDesignation) {
        this.approvedDesignation = approvedDesignation;
    }

    public PersonInstitution getPersonInstitution() {
        return personInstitution;
    }

    public void setPersonInstitution(PersonInstitution personInstitution) {
        this.personInstitution = personInstitution;
    }
    
    
    
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        if (!(object instanceof TransferHistory)) {
            return false;
        }
        TransferHistory other = (TransferHistory) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.health.facade.TransferHistory[ id=" + id + " ]";
    }
    
}
