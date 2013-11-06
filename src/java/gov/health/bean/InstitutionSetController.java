/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of 
 Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import gov.health.entity.Institution;
import gov.health.entity.InstitutionSet;
import gov.health.facade.InstitutionSetFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.inject.Named;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public  class InstitutionSetController implements Serializable {

    @EJB
    private InstitutionSetFacade ejbFacade;
    @Inject
    SessionController sessionController;
    private InstitutionSet current;
    private List<InstitutionSet> items = null;
    private Institution institution;
    String newName;

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
    
    
    
    
    InstitutionSet toRemove;

    public InstitutionSet getToRemove() {
        return toRemove;
    }

    public void setToRemove(InstitutionSet toRemove) {
        this.toRemove = toRemove;
    }
    
    
    
    public InstitutionSetController() {
    }

    public InstitutionSetFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(InstitutionSetFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public InstitutionSet getCurrent() {
        // InstitutionSet
        if (current == null) {
            current = new InstitutionSet();
        }
        return current;
    }

    public void setCurrent(InstitutionSet current) {
        this.current = current;
    }

    private InstitutionSetFacade getFacade() {
        return ejbFacade;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public List<InstitutionSet> getItems() {
        Long insId = 0L;
        if (getSessionController().getLoggedUser().getRestrictedInstitution() != null) {
            insId = getSessionController().getLoggedUser().getRestrictedInstitution().getId();
        } else if (getInstitution() != null && getInstitution().getId() != 0) {
            insId = getInstitution().getId();
        } else {
            items = new ArrayList<InstitutionSet>();
        }
        items = getFacade().findBySQL("select s from InstitutionSet s where s.retired = false and s.institution.id = " + insId);
        return items;
    }

    public static int intValue(long value) {
        int valueInt = (int) value;
        if (valueInt != value) {
            throw new IllegalArgumentException(
                    "The long value " + value + " is not within range of the int type");
        }
        return valueInt;
    }

    private void recreateModel() {
        items = null;
    }

    public void addDirectly() {
        try {
            if(getNewName().trim().equals("")){
                JsfUtil.addErrorMessage("Please enter a name");
                return;
            }
            setCurrent(new InstitutionSet());
            getCurrent().setName(newName);
            if (getSessionController().getLoggedUser().getRestrictedInstitution() != null) {
                getCurrent().setInstitution(getSessionController().getLoggedUser().getRestrictedInstitution());
                System.out.println("1");
            } else if (getInstitution() != null && getInstitution().getId() != 0) {
                getCurrent().setInstitution(getInstitution());
                System.out.println("2");
                System.out.println(getInstitution().getName());
            } else {
                System.out.println("3");
                JsfUtil.addErrorMessage("Please select an Institute");
                return;
            }
            System.out.println("4");
            getCurrent().setCreatedAt(Calendar.getInstance().getTime());
            getCurrent().setCreater(sessionController.loggedUser);
            getFacade().create(getCurrent());
            setCurrent(new InstitutionSet());
            newName="";
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("savedNewSuccessfully"));

        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Error");
        }
    }

    public void delete() {
        if (getToRemove() != null) {
            getToRemove().setRetired(true);
            getToRemove().setRetiredAt(Calendar.getInstance().getTime());
            getToRemove().setRetirer(sessionController.loggedUser);
            getFacade().edit(getToRemove());
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("deleteSuccessful"));
        } else {
            JsfUtil.addErrorMessage(new MessageProvider().getValue("nothingToDelete"));
        }
        recreateModel();
        getItems();
        current = null;
    }

    @FacesConverter(forClass = InstitutionSet.class)
    public static class InstitutionSetControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InstitutionSetController controller = (InstitutionSetController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "institutionSetController");
            return controller.getEjbFacade().find(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuffer sb = new StringBuffer();
            sb.append(value);
            return sb.toString();
        }

        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof InstitutionSet) {
                InstitutionSet o = (InstitutionSet) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + InstitutionSetController.class.getName());
            }
        }
    }
}
