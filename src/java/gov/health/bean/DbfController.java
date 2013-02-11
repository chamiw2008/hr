/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.bean;

import gov.health.entity.Institution;
import gov.health.entity.DbfFile;
import gov.health.entity.PersonInstitution;
import gov.health.entity.Person;
import gov.health.entity.Category;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import gov.health.entity.Designation;
import gov.health.entity.InstitutionSet;
import gov.health.facade.CategoryFacade;
import gov.health.facade.DbfFileFacade;
import gov.health.facade.DesignationFacade;
import gov.health.facade.InstitutionFacade;
import gov.health.facade.InstitutionSetFacade;
import gov.health.facade.PersonFacade;
import gov.health.facade.PersonInstitutionFacade;
import java.io.ByteArrayInputStream;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author buddhika
 */
@ManagedBean
@SessionScoped
public class DbfController implements Serializable {

    StreamedContent scImage;
    StreamedContent scImageById;
    private UploadedFile file;
    @EJB
    DbfFileFacade dbfFileFacade;
    @EJB
    InstitutionFacade insFacade;
    @EJB
    PersonFacade perFacade;
    @EJB
    CategoryFacade catFacade;
    @EJB
    PersonInstitutionFacade piFacade;
    @EJB
    DesignationFacade desFacade;
    @EJB
    InstitutionSetFacade insetFacade;
    //
    //
    Institution institution;
    Person person;
    Category category;
    //
    DbfFile defFile;
    List<DbfFile> dbfFiles;
    List<PersonInstitution> existingPersonInstitutions;
    List<PersonInstitution> newPersonInstitutions;
    @ManagedProperty(value = "#{sessionController}")
    SessionController sessionController;
    Integer payYear;
    Integer payMonth;
    List<Integer> payYears;
    List<Integer> payMonths;
    List<InstitutionSet> insSets;
    InstitutionSet insSet;

    public InstitutionSetFacade getInsetFacade() {
        return insetFacade;
    }

    public void setInsetFacade(InstitutionSetFacade insetFacade) {
        this.insetFacade = insetFacade;
    }

    public InstitutionSet getInsSet() {
        return insSet;
    }

    public void setInsSet(InstitutionSet insSet) {
        this.insSet = insSet;
    }

    public List<InstitutionSet> getInsSets() {
        if (getSessionController().getPrivilege().getRestrictedInstitution() != null) {
            setInstitution(getSessionController().getPrivilege().getRestrictedInstitution());
        }
        if (getInstitution() == null || getInstitution().getId() == null || getInstitution().getId() == 0) {
            return null;
        }

        String sql;
        sql = "select s from InstitutionSet s where s.retired = false and s.institution.id = " + getInstitution().getId();
        insSets = getInsetFacade().findBySQL(sql);
        return insSets;
    }

    public void setInsSets(List<InstitutionSet> insSets) {
        this.insSets = insSets;
    }

    public List<Integer> getPayMonths() {
        if (payMonths == null) {
            payMonths = new ArrayList<Integer>();
            payMonths.add(1);
            payMonths.add(2);
            payMonths.add(3);
            payMonths.add(4);
            payMonths.add(5);
            payMonths.add(6);
            payMonths.add(7);
            payMonths.add(8);
            payMonths.add(9);
            payMonths.add(10);
            payMonths.add(11);
            payMonths.add(12);
        }
        return payMonths;
    }

    public void setPayMonths(List<Integer> payMonths) {
        this.payMonths = payMonths;
    }

    public List<Integer> getPayYears() {
        if (payYears == null) {
            payYears = new ArrayList<Integer>();
            payYears.add(2011);
            payYears.add(2012);
            payYears.add(2013);
        }
        return payYears;
    }

    public void setPayYears(List<Integer> payYears) {
        this.payYears = payYears;
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

    public DesignationFacade getDesFacade() {
        return desFacade;
    }

    public void setDesFacade(DesignationFacade desFacade) {
        this.desFacade = desFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public List<DbfFile> getDbfFiles() {
        return dbfFiles;
    }

    public void setDbfFiles(List<DbfFile> dbfFiles) {
        this.dbfFiles = dbfFiles;
    }

    public List<PersonInstitution> getExistingPersonInstitutions() {
        if (getInstitution() == null || getInsSet() == null || getPayMonth() == null || getPayYear() == null) {
            return new ArrayList<PersonInstitution>();
        }
        existingPersonInstitutions = getPiFacade().findBySQL("select pi from PersonInstitution pi where pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and pi.paySet.id = " + getInsSet().getId() + " and  pi.payCentre.id = " + getInstitution().getId());
        return existingPersonInstitutions;
    }

    public void setExistingPersonInstitutions(List<PersonInstitution> existingPersonInstitutions) {
        this.existingPersonInstitutions = existingPersonInstitutions;
    }

    public List<PersonInstitution> getNewPersonInstitutions() {
        if (newPersonInstitutions == null) {
            newPersonInstitutions = new ArrayList<PersonInstitution>();
        }
        return newPersonInstitutions;
    }

    public void setNewPersonInstitutions(List<PersonInstitution> newPersonInstitutions) {
        this.newPersonInstitutions = newPersonInstitutions;
    }

    public PersonInstitutionFacade getPiFacade() {
        return piFacade;
    }

    public void setPiFacade(PersonInstitutionFacade piFacade) {
        this.piFacade = piFacade;
    }

    public StreamedContent getScImageById() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getRenderResponse()) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Get ID value from actual request param.
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            DbfFile temImg = getDbfFileFacade().find(Long.valueOf(id));
            return new DefaultStreamedContent(new ByteArrayInputStream(temImg.getBaImage()), temImg.getFileType());
        }
    }

    public void setScImageById(StreamedContent scImageById) {
        this.scImageById = scImageById;
    }

    public StreamedContent getScImage() {
        return scImage;
    }

    public List<DbfFile> getAppImages() {
        if (dbfFiles == null) {
            dbfFiles = new ArrayList<DbfFile>();
        }
        System.out.println("Getting app images - count is" + dbfFiles.size());
        return dbfFiles;
    }

    public void setAppImages(List<DbfFile> appImages) {
        this.dbfFiles = appImages;
    }

    public void setScImage(StreamedContent scImage) {
        this.scImage = scImage;
    }

    public DbfFile getDefFile() {
        return defFile;
    }

    public void setDefFile(DbfFile defFile) {
        this.defFile = defFile;
    }

    public DbfFileFacade getDbfFileFacade() {
        return dbfFileFacade;
    }

    public void setDbfFileFacade(DbfFileFacade dbfFileFacade) {
        this.dbfFileFacade = dbfFileFacade;
    }

    /**
     * Creates a new instance of ImageController
     */
    public DbfController() {
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    private void prepareImages(String sql) {
        dbfFiles = getDbfFileFacade().findBySQL(sql);
    }

    public CategoryFacade getCatFacade() {
        return catFacade;
    }

    public void setCatFacade(CategoryFacade catFacade) {
        this.catFacade = catFacade;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category == null && category.getId() != null) {
            prepareImages("Select ai from AppImage ai Where ai.category.id = " + category.getId());
        } else {
            dbfFiles = null;
        }
    }

    public InstitutionFacade getInsFacade() {
        return insFacade;
    }

    public void setInsFacade(InstitutionFacade insFacade) {
        this.insFacade = insFacade;
    }

    public Institution getInstitution() {
        if (getSessionController().getPrivilege().getRestrictedInstitution() == null) {
            return institution;
        } else {
            return getSessionController().getPrivilege().getRestrictedInstitution();
        }
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
        if (institution == null && institution.getId() != null) {
            prepareImages("Select ai from AppImage ai Where ai.institution.id = " + institution.getId());
        } else {
            dbfFiles = null;
        }
    }

    public PersonFacade getPerFacade() {
        return perFacade;
    }

    public void setPerFacade(PersonFacade perFacade) {
        this.perFacade = perFacade;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        if (person == null && person.getId() != null) {
            prepareImages("Select ai from AppImage ai Where ai.person.id = " + person.getId());
        } else {
            dbfFiles = null;
        }
    }

    public void savePersonImage() {
        if (person == null) {
            JsfUtil.addErrorMessage("Please select a Person");
            return;
        }
        defFile = new DbfFile();
        defFile.setPerson(person);
        saveImage();
        setPerson(person);
    }

    public void saveImage() {
        InputStream in;
        if (file == null) {
            JsfUtil.addErrorMessage("Please upload an image");
            return;
        }
        JsfUtil.addSuccessMessage(file.getFileName());
        try {
            defFile.setFileName(file.getFileName());
            defFile.setFileType(file.getContentType());
            in = file.getInputstream();
            defFile.setBaImage(IOUtils.toByteArray(in));
            dbfFileFacade.create(defFile);
            JsfUtil.addSuccessMessage(file.getFileName() + " saved successfully");
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }

    }

    private Boolean isCorrectDbfFile(DBFReader reader) {
        Boolean correct = true;
        try {
            if (!reader.getField(0).getName().equalsIgnoreCase("F1_EMPNO")) {
                correct = false;
            }
            if (!reader.getField(2).getName().equalsIgnoreCase("F1_SURNAME")) {
                correct = false;
            }
            if (!reader.getField(7).getName().equalsIgnoreCase("F1_DOB")) {
                correct = false;
            }
            if (!reader.getField(48).getName().equalsIgnoreCase("F1_NICNO")) {
                correct = false;
            }
        } catch (Exception e) {
        }
        return correct;
    }

    public void replaceData() {
        if (institution == null) {
            JsfUtil.addErrorMessage("Please select an institute");
            return;
        }
        if (newPersonInstitutions == null) {
            JsfUtil.addErrorMessage("Please upload a dbf file before saving data");
            return;
        }
        for (PersonInstitution pi : existingPersonInstitutions) {
            pi.setRetired(true);
            pi.setRetiredAt(Calendar.getInstance().getTime());
            pi.setRetirer(sessionController.loggedUser);
            getPiFacade().edit(pi);
        }
        for (PersonInstitution pi : newPersonInstitutions) {
//            getPerFacade().create(pi.getPerson());
            getPiFacade().create(pi);
        }
        getExistingPersonInstitutions();
        existingPersonInstitutions = newPersonInstitutions;
        newPersonInstitutions = new ArrayList<PersonInstitution>();
        JsfUtil.addSuccessMessage("Data Replaced Successfully");
    }

    public void markTransfer(Person p, Institution fromIns, Institution toIns) {
    }

    public String extractData() {
        InputStream in;
        String temNic;
        if (sessionController.privilege.getRestrictedInstitution() != null) {
            setInstitution(sessionController.getPrivilege().getRestrictedInstitution());
        }
        if (getInstitution() == null) {
            JsfUtil.addErrorMessage("Please select an institute");
            return "";
        }
        if (file == null) {
            JsfUtil.addErrorMessage("Please select the dbf file to upload");
            return "";
        }
        if (payYear == null || payYear == 0) {
            JsfUtil.addErrorMessage("Please select a year");
            return "";
        }
        if (payMonth == null || payMonth == 0) {
            JsfUtil.addErrorMessage("Please select a Month");
            return "";
        }
        if (insSet == null) {
            JsfUtil.addErrorMessage("Please select a Set");
            return "";
        }

        try {

            in = file.getInputstream();
            DBFReader reader = new DBFReader(in);

            if (!isCorrectDbfFile(reader)) {
                JsfUtil.addErrorMessage("But the file you selected is not the correct file. Please make sure you selected the correct file named PYREMPMA.DBF. If you are sure that you selected the correct file, you may be using an old version.");
                return "";
            }

            int numberOfFields = reader.getFieldCount();

            System.out.println("Number of fields is " + numberOfFields);
            for (int i = 0; i < numberOfFields; i++) {
                DBFField field = reader.getField(i);
                System.out.println("Data Field " + i + " is " + field.getName());
            }

            Object[] rowObjects;

            newPersonInstitutions = new ArrayList<PersonInstitution>();


            while ((rowObjects = reader.nextRecord()) != null) {

                Person p = null;
                PersonInstitution pi = new PersonInstitution();
                Institution attachedIns;

                String insName;
                insName = rowObjects[21].toString() + " " + rowObjects[22].toString() + " " + rowObjects[23].toString();

                if (insName.trim().equals("")) {
                    attachedIns = getInstitution();
                } else {
                    attachedIns = findInstitution(insName);
                }

                temNic = rowObjects[48].toString();

                if ("".equals(temNic.trim())) {
                    pi.setPerson(null);
                } else {
                    p = getPerFacade().findFirstBySQL("select p from Person p where p.retired = false and p.nic = '" + temNic + "'");
                    if (p == null) {
                        p = new Person();
                        p.setCreatedAt(Calendar.getInstance().getTime());
                        p.setCreater(sessionController.getLoggedUser());
                        p.setInstitution(attachedIns);
                        p.setTitle(rowObjects[1].toString());
                        p.setInitials(rowObjects[3].toString());
                        p.setSurname(rowObjects[2].toString());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                        try {
                            p.setDob(dateFormat.parse(rowObjects[7].toString()));
                        } catch (Exception e) {
                            p.setDob(null);
                        }
                        p.setNic(rowObjects[48].toString());
                        p.setName(p.getTitle() + " " + p.getInitials() + " " + p.getSurname());
                        getPerFacade().create(p);
                    } else {
                        if (p.getInstitution() != getInstitution()) {
                            markTransfer(p, p.getInstitution(), institution);
                        }
                    }

                }


                pi.setPerson(p);
                pi.setInstitution(attachedIns);
                pi.setPayCentre(getInstitution());
                pi.setNic(rowObjects[48].toString());

                pi.setEmpNo(rowObjects[0].toString());
                pi.setAddress1(rowObjects[18].toString());
                pi.setAddress2(rowObjects[19].toString());
                pi.setAddress3(rowObjects[20].toString());
                pi.setOffAddress1(rowObjects[21].toString());
                pi.setOffAddress2(rowObjects[22].toString());
                pi.setOffAddress3(rowObjects[23].toString());

                pi.setDesignation(findDesignation(rowObjects[8].toString()));

                pi.setName(rowObjects[1].toString() + " " + rowObjects[2].toString() + " " + rowObjects[3].toString());
                pi.setPayMonth(payMonth);
                pi.setPayYear(payYear);
                pi.setPaySet(insSet);


                if (rowObjects[4].toString().equals("") || rowObjects[50].toString().equals("")) {
                    pi.setPermanent(Boolean.FALSE);
                } else {
                    pi.setPermanent(Boolean.TRUE);
                }
                try {
                    if (Integer.valueOf(rowObjects[4].toString()) == 0) {
                        pi.setNopay(Boolean.TRUE);
                    } else {
                    }
                } catch (Exception e) {
                }


                try {
                    pi.setActiveState((Boolean) rowObjects[40]);
                } catch (Exception e) {
                    pi.setActiveState(true);
                }
                try {
                    pi.setNopay((Boolean) rowObjects[31]);
                } catch (Exception e) {
                    pi.setNopay(false);
                }
                newPersonInstitutions.add(pi);
            }
            JsfUtil.addSuccessMessage("Date in the file " + file.getFileName() + " is listed successfully. If you are satisfied, please click the Save button to permanantly save the new set of data Replacing the old ones under " + institution.getName() + ".");
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
        return "";
    }

    private Designation findDesignation(String designationName) {
        designationName = designationName.trim();
        if (designationName.equals("")) {
            return null;
        }
        Designation des = getDesFacade().findFirstBySQL("select d from Designation d where lower(d.name) = '" + designationName.toLowerCase() + "'");
        if (des == null) {
            des = new Designation();
            des.setName(designationName);
            des.setCreatedAt(Calendar.getInstance().getTime());
            des.setCreater(sessionController.loggedUser);
            des.setOfficial(Boolean.FALSE);
            getDesFacade().create(des);
        } else {
            if (des.getOfficial().equals(Boolean.FALSE)) {
                if (des.getMappedToDesignation() != null) {
                    return des.getMappedToDesignation();
                }
            }
        }
        return des;
    }

    private Institution findInstitution(String insName) {
        insName = insName.trim();
        if (insName.equals("")) {
            return null;
        }
        Institution ins = getInsFacade().findFirstBySQL("select d from Institution d where d.retired = false and lower(d.name) = '" + insName.toLowerCase() + "'");
        if (ins == null) {
            ins = new Institution();
            ins.setName(insName);
            ins.setCreatedAt(Calendar.getInstance().getTime());
            ins.setCreater(sessionController.loggedUser);
            ins.setOfficial(Boolean.FALSE);
            getInsFacade().create(ins);
        } else {
            if (ins.getOfficial().equals(Boolean.FALSE)) {
                if (ins.getMappedToInstitution() != null) {
                    return ins.getMappedToInstitution();
                }
            }
        }
        return ins;
    }
}
