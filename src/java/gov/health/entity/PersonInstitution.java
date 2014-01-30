/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

/**
 *
 * @author buddhika
 */
@Entity
public class PersonInstitution implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    //Main Properties
    String name;
    String description;
    //Created Properties
    @ManyToOne
    WebUser creater;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Date createdAt;
    //Retairing properties
    boolean retired;
    @ManyToOne
    WebUser retirer;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    Date retiredAt;
    String retireComments;
    String nic;
    @ManyToOne
    Designation designation;
    @ManyToOne
    Institution institution;
    @ManyToOne
    Institution payCentre;    
    @ManyToOne
    Person person;
    String empNo;
    Double conSal;
    Double appSal;
    String site;
    String dept;
    String epfNo;
    String empCat;
    String grade;
    String empClass;
    @Lob
    String address1;
    @Lob
    String address2;
    @Lob
    String address3;
    @Lob
    String offAddress1;
    @Lob
    String offAddress2;
    @Lob
    String offAddress3;
    @Temporal(javax.persistence.TemporalType.DATE)
    Date appDate;
    Boolean nopay;
    Boolean activeState;
    Boolean permanent ;


    Integer payYear;
    Integer payMonth;
    @ManyToOne
    InstitutionSet paySet;

    String strDesignation;
    String strInstitution;

    public String getStrDesignation() {
        return strDesignation;
    }

    public void setStrDesignation(String strDesignation) {
        this.strDesignation = strDesignation;
    }

    public String getStrInstitution() {
        return strInstitution;
    }

    public void setStrInstitution(String strInstitution) {
        this.strInstitution = strInstitution;
    }
    
    
    
    
    public InstitutionSet getPaySet() {
        return paySet;
    }

    public void setPaySet(InstitutionSet paySet) {
        this.paySet = paySet;
    }
    
    


    public Institution getPayCentre() {
        return payCentre;
    }

    public void setPayCentre(Institution payCentre) {
        this.payCentre = payCentre;
    }

    
    
    public Boolean getPermanent() {
        return permanent;
    }

    public void setPermanent(Boolean permanent) {
        this.permanent = permanent;
    }

    
    
    public Integer getPayMonth() {
        return payMonth;
    }

    public void setPayMonth(Integer payMonth) {
        this.payMonth = payMonth;
    }

    public Integer getPayYear() {
        return payYear;
    }

    public void setPayYear(Integer payYear) {
        this.payYear = payYear;
    }
    
    
    
    public Boolean getActiveState() {
        return activeState;
    }

    public void setActiveState(Boolean activeState) {
        this.activeState = activeState;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public Date getAppDate() {
        return appDate;
    }

    public void setAppDate(Date appDate) {
        this.appDate = appDate;
    }

    public Double getAppSal() {
        return appSal;
    }

    public void setAppSal(Double appSal) {
        this.appSal = appSal;
    }

    public Double getConSal() {
        return conSal;
    }

    public void setConSal(Double conSal) {
        this.conSal = conSal;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getEmpCat() {
        return empCat;
    }

    public void setEmpCat(String empCat) {
        this.empCat = empCat;
    }

    public String getEmpClass() {
        return empClass;
    }

    public void setEmpClass(String empClass) {
        this.empClass = empClass;
    }

    public String getEmpNo() {
        return empNo;
    }

    public void setEmpNo(String empNo) {
        this.empNo = empNo;
    }

    public String getEpfNo() {
        return epfNo;
    }

    public void setEpfNo(String epfNo) {
        this.epfNo = epfNo;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Boolean getNopay() {
        return nopay;
    }

    public void setNopay(Boolean nopay) {
        this.nopay = nopay;
    }

    public String getOffAddress1() {
        return offAddress1;
    }

    public void setOffAddress1(String offAddress1) {
        this.offAddress1 = offAddress1;
    }

    public String getOffAddress2() {
        return offAddress2;
    }

    public void setOffAddress2(String offAddress2) {
        this.offAddress2 = offAddress2;
    }

    public String getOffAddress3() {
        return offAddress3;
    }

    public void setOffAddress3(String offAddress3) {
        this.offAddress3 = offAddress3;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public WebUser getCreater() {
        return creater;
    }

    public void setCreater(WebUser creater) {
        this.creater = creater;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Designation getDesignation() {
        return designation;
    }

    public void setDesignation(Designation designation) {
        this.designation = designation;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getRetireComments() {
        return retireComments;
    }

    public void setRetireComments(String retireComments) {
        this.retireComments = retireComments;
    }

    public boolean isRetired() {
        return retired;
    }

    public void setRetired(boolean retired) {
        this.retired = retired;
    }

    public Date getRetiredAt() {
        return retiredAt;
    }

    public void setRetiredAt(Date retiredAt) {
        this.retiredAt = retiredAt;
    }

    public WebUser getRetirer() {
        return retirer;
    }

    public void setRetirer(WebUser retirer) {
        this.retirer = retirer;
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
        if (!(object instanceof PersonInstitution)) {
            return false;
        }
        PersonInstitution other = (PersonInstitution) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "gov.health.entity.PersonInstitution[ id=" + id + " ]";
    }
}
