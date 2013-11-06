/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.bean;

import gov.health.entity.AppImage;
import gov.health.entity.Category;
import gov.health.entity.Institution;
import gov.health.entity.Person;
import gov.health.facade.AppImageFacade;
import gov.health.facade.CategoryFacade;
import gov.health.facade.InstitutionFacade;
import gov.health.facade.PersonFacade;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import org.apache.commons.io.IOUtils;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author buddhika
 */
@Named
@SessionScoped
public class ImageController implements Serializable {

    StreamedContent scImage;
    StreamedContent scImageById;
    private UploadedFile file;
    @EJB
    AppImageFacade appImageFacade;
    @EJB
    InstitutionFacade insFacade;
    @EJB
    PersonFacade perFacade;
    @EJB
    CategoryFacade catFacade;
    //
    //
    Institution institution;
    Person person;
    Category category;
    //
    AppImage appImage;
    List<AppImage> appImages;

    public StreamedContent getScImageById() {
        FacesContext context = FacesContext.getCurrentInstance();
        if (context.getRenderResponse()) {
            // So, we're rendering the view. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        } else {
            // So, browser is requesting the image. Get ID value from actual request param.
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            AppImage temImg = getAppImageFacade().find(Long.valueOf(id));
            return new DefaultStreamedContent(new ByteArrayInputStream(temImg.getBaImage()), temImg.getFileType());
        }
    }

    public void setScImageById(StreamedContent scImageById) {
        this.scImageById = scImageById;
    }

    public StreamedContent getScImage() {
        return scImage;
    }

    public List<AppImage> getAppImages() {
        if (appImages == null) {
            appImages = new ArrayList<AppImage>();
        }
        System.out.println("Getting app images - count is" + appImages.size());
        return appImages;
    }

    public void setAppImages(List<AppImage> appImages) {
        this.appImages = appImages;
    }

    public void setScImage(StreamedContent scImage) {
        this.scImage = scImage;
    }

    public AppImage getAppImage() {
        return appImage;
    }

    public void setAppImage(AppImage appImage) {
        this.appImage = appImage;
    }

    public AppImageFacade getAppImageFacade() {
        return appImageFacade;
    }

    public void setAppImageFacade(AppImageFacade appImageFacade) {
        this.appImageFacade = appImageFacade;
    }

    /**
     * Creates a new instance of ImageController
     */
    public ImageController() {
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    private void prepareImages(String sql) {
        appImages = getAppImageFacade().findBySQL(sql);
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
            appImages = null;
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
            appImages = null;
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
            appImages = null;
        }
    }

    public void savePersonImage() {
        if (person == null) {
            JsfUtil.addErrorMessage("Please select a Person");
            return;
        }
        appImage = new AppImage();
        appImage.setPerson(person);
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
            appImage.setFileName(file.getFileName());
            appImage.setFileType(file.getContentType());
            in = file.getInputstream();
            appImage.setBaImage(IOUtils.toByteArray(in));
            appImageFacade.create(appImage);
            JsfUtil.addSuccessMessage(file.getFileName() + " saved successfully");
        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }

    }

    
}
