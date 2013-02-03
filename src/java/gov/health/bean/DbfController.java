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
import gov.health.facade.CategoryFacade;
import gov.health.facade.DbfFileFacade;
import gov.health.facade.DesignationFacade;
import gov.health.facade.InstitutionFacade;
import gov.health.facade.PersonFacade;
import gov.health.facade.PersonInstitutionFacade;
import java.io.ByteArrayInputStream;
import javax.faces.bean.ManagedBean;
import org.primefaces.model.UploadedFile;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import java.io.*;
import java.text.SimpleDateFormat;
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
        if (institution == null) {
            return null;
        }
        existingPersonInstitutions = getPiFacade().findBySQL("select pi from PersonInstitution pi where pi.retired = false and pi.institution.id = " + getInstitution().getId() );
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
        return institution;
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
            return ;
        }
        if (newPersonInstitutions == null) {
            JsfUtil.addErrorMessage("Please upload a dbf file before saving data");
            return ;
        }
        for (PersonInstitution pi:existingPersonInstitutions){
            pi.setRetired(true);
            pi.setRetiredAt(Calendar.getInstance().getTime());
            pi.setRetirer(sessionController.loggedUser);
            getPiFacade().edit(pi);
        }
        for(PersonInstitution pi:newPersonInstitutions){
            getPerFacade().create(pi.getPerson());
            getPiFacade().create(pi);
        }
        getExistingPersonInstitutions();
        JsfUtil.addSuccessMessage("Data Replaced Successfully");
    }

    public String extractData() {
        InputStream in;
        if (institution == null) {
            JsfUtil.addErrorMessage("Please select an institute");
            return "";
        }
        if (file == null) {
            JsfUtil.addErrorMessage("Please select the dbf file to upload");
            return "";
        }
        JsfUtil.addSuccessMessage(file.getFileName() + " was uploaded successfully.");
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

                Person p = new Person();
                PersonInstitution pi = new PersonInstitution();

                p.setCreatedAt(Calendar.getInstance().getTime());
                p.setCreater(sessionController.getLoggedUser());

                p.setInstitution(institution);

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


                pi.setPerson(p);
                pi.setInstitution(institution);
                pi.setNic(rowObjects[48].toString());

                pi.setEmpNo(rowObjects[0].toString());
                pi.setAddress1(rowObjects[18].toString());
                pi.setAddress2(rowObjects[19].toString());
                pi.setAddress3(rowObjects[20].toString());
                pi.setOffAddress1(rowObjects[21].toString());
                pi.setOffAddress2(rowObjects[22].toString());
                pi.setOffAddress3(rowObjects[23].toString());

                pi.setDesignation(findDesignation(rowObjects[8].toString()));

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
        Designation des = getDesFacade().findFirstBySQL("select d from Designation d where lower(d.name) = '" + designationName.toLowerCase() + "'");
        if (des == null) {
            des = new Designation();
            des.setName(designationName);
            des.setCreatedAt(Calendar.getInstance().getTime());
            des.setCreater(sessionController.loggedUser);
            getDesFacade().create(des);
        }
        return des;
    }

}
