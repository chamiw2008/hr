/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of 
 Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import gov.health.facade.WebUserRoleFacade;
import gov.health.entity.WebUserRole;
import java.io.Serializable;
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
public  class WebUserRoleController implements Serializable {
    
    @Inject
    private SessionController sessionController;
    @EJB
    private WebUserRoleFacade ejbFacade;
    List<WebUserRole> lstItems;
    List<WebUserRole> lstInsAdminRoles;
    private WebUserRole current;
    private List<WebUserRole> items = null;
    private int selectedItemIndex;
    boolean selectControlDisable = false;
    boolean modifyControlDisable = true;
    String selectText = "";

    public List<WebUserRole> getLstInsAdminRoles() {
        return getFacade().findBySQL("Select d From WebUserRole d where d.name = 'insAdmin' or d.name = 'insUser' ");
    }

    public void setLstInsAdminRoles(List<WebUserRole> lstInsAdminRoles) {
        this.lstInsAdminRoles = lstInsAdminRoles;
    }

    public WebUserRoleFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(WebUserRoleFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public WebUserRoleController() {
    }

    public List<WebUserRole> getLstItems() {
        return getFacade().findBySQL("Select d From WebUserRole d where d.name = 'sysAdmin' or d.name = 'superUser' or d.name = 'insUser' or d.name = 'insAdmin'");
    }

    public void setLstItems(List<WebUserRole> lstItems) {
        this.lstItems = lstItems;
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void setSelectedItemIndex(int selectedItemIndex) {
        this.selectedItemIndex = selectedItemIndex;
    }

    public WebUserRole getCurrent() {
        return current;
    }

    public void setCurrent(WebUserRole current) {
        this.current = current;
    }

    private WebUserRoleFacade getFacade() {
        return ejbFacade;
    }

    public List<WebUserRole> getItems() {
        items = getFacade().findAll("name", true);
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

    @FacesConverter(forClass = WebUserRole.class)
    public static class WebUserRoleControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            WebUserRoleController controller = (WebUserRoleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "webUserRoleController");
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

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof WebUserRole) {
                WebUserRole o = (WebUserRole) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + WebUserRoleController.class.getName());
            }
        }
    }
}
