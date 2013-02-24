/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of 
 Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import gov.health.facade.InstitutionFacade;
import gov.health.entity.Institution;
import gov.health.entity.InstitutionSet;
import gov.health.entity.InstitutionType;
import gov.health.entity.PersonInstitution;
import gov.health.facade.InstitutionSetFacade;
import gov.health.facade.InstitutionTypeFacade;
import gov.health.facade.PersonInstitutionFacade;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@ManagedBean
@SessionScoped
public final class InstitutionController implements Serializable {

    @EJB
    private InstitutionFacade ejbFacade;
    @EJB
    InstitutionTypeFacade institutionTypeFacade;
    @EJB
    PersonInstitutionFacade piFacade;
    @EJB
    InstitutionSetFacade inSetFacade;
    @ManagedProperty(value = "#{sessionController}")
    SessionController sessionController;
    List<Institution> offItems;
    List<Institution> payCentres;
    private Institution current;
    private List<Institution> items = null;
    DataModel<InstitutionType> institutionTypes;
    private int selectedItemIndex;
    boolean selectControlDisable = false;
    boolean modifyControlDisable = true;
    String selectText = "";
    Integer offSel = 0;

    public InstitutionSetFacade getInSetFacade() {
        return inSetFacade;
    }

    public void setInSetFacade(InstitutionSetFacade inSetFacade) {
        this.inSetFacade = inSetFacade;
    }
    
    

    public List<Institution> getPayCentres() {
        String sql = "SELECT i FROM Institution i where i.retired=false and i.payCentre = true order by i.name";
        payCentres = getFacade().findBySQL(sql);
        return payCentres;
    }

    public void setPayCentres(List<Institution> payCentres) {
        this.payCentres = payCentres;
    }

    
    
    public PersonInstitutionFacade getPiFacade() {
        return piFacade;
    }

    public void setPiFacade(PersonInstitutionFacade piFacade) {
        this.piFacade = piFacade;
    }
    
    

    public Integer getOffSel() {
        return offSel;
    }

    public void setOffSel(Integer offSel) {
        recreateModel();
        this.offSel = offSel;
    }

    public InstitutionController() {
    }

    public InstitutionFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(InstitutionFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public InstitutionTypeFacade getInstitutionTypeFacade() {
        return institutionTypeFacade;
    }

    public void setInstitutionTypeFacade(InstitutionTypeFacade institutionTypeFacade) {
        this.institutionTypeFacade = institutionTypeFacade;
    }

    public DataModel<InstitutionType> getInstitutionTypes() {
        String temSQL;
        temSQL = "SELECT i FROM InstitutionType i WHERE i.retired = false ORDER BY i.orderNo";
        return new ListDataModel<InstitutionType>(getInstitutionTypeFacade().findBySQL(temSQL));
    }

    public void setInstitutionTypes(DataModel<InstitutionType> institutionTypes) {
        this.institutionTypes = institutionTypes;
    }

    public List<Institution> getOffItems() {
        String sql = "SELECT i FROM Institution i where i.retired=false and i.official = true order by i.name";
        offItems = getFacade().findBySQL(sql);
        return offItems;
    }

    public void setOffItems(List<Institution> offItems) {
        this.offItems = offItems;
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void setSelectedItemIndex(int selectedItemIndex) {
        this.selectedItemIndex = selectedItemIndex;
    }

    public Institution getCurrent() {
        if (current == null) {
            current = new Institution();
            current.setOfficial(Boolean.TRUE);
        }
        return current;
    }

    public void setCurrent(Institution current) {
        this.current = current;
    }

    private InstitutionFacade getFacade() {
        return ejbFacade;
    }

    public List<Institution> getItems() {
        String temSql;
        if (items != null) {
            return items;
        }
        if (getSelectText().equals("")) {
            if (getOffSel() == 0) {
                temSql = "SELECT i FROM Institution i where i.retired=false order by i.name";
            } else if (getOffSel() == 1) {
                temSql = "SELECT i FROM Institution i where i.retired=false and i.official = true order by i.name";
            } else if (getOffSel() == 2) {
                temSql = "SELECT i FROM Institution i where i.retired=false and i.official = false order by i.name";
            } else if (getOffSel() == 3) {
                temSql = "SELECT i FROM Institution i where i.retired=false and i.official = false and i.mappedToInstitution is null order by i.name";
            } else {
                temSql = "SELECT i FROM Institution i where i.retired=false order by i.name";
            }
        } else {
            if (getOffSel() == 0) {
                temSql = "SELECT i FROM Institution i where i.retired=false and LOWER(i.name) like '%" + getSelectText().toLowerCase() + "%' order by i.name";
            } else if (getOffSel() == 1) {
                temSql = "SELECT i FROM Institution i where i.retired=false and i.official = true and LOWER(i.name) like '%" + getSelectText().toLowerCase() + "%' order by i.name";
            } else if (getOffSel() == 2) {
                temSql = "SELECT i FROM Institution i where i.retired=false and i.official = false and LOWER(i.name) like '%" + getSelectText().toLowerCase() + "%' order by i.name";
            } else if (getOffSel() == 3) {
                temSql = "SELECT i FROM Institution i where i.retired=false and LOWER(i.name) like '%" + getSelectText().toLowerCase() + "%' and i.mappedToInstitution is null order by i.name";
            } else {
                temSql = "SELECT i FROM Institution i where i.retired=false and LOWER(i.name) like '%" + getSelectText().toLowerCase() + "%' order by i.name";
            }
        }
        items = getFacade().findBySQL(temSql);
        System.out.println(temSql);

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

//    public List<Institution> searchItems() {
//        recreateModel();
//        if (items == null) {
//            if (selectText.equals("")) {
//                items = getFacade().findAll("name", true);
//            } else {
//                items = getFacade().findAll("name", "%" + selectText + "%", true);
//                if (items.size() > 0) {
//                    current = (Institution) items.get(0);
//                    Long temLong = current.getId();
//                    selectedItemIndex = intValue(temLong);
//                } else {
//                    current = null;
//                    selectedItemIndex = -1;
//                }
//            }
//        }
//        return items;
//
//    }
//    public Institution searchItem(String itemName, boolean createNewIfNotPresent) {
//        Institution searchedItem = null;
//        items = getFacade().findAll("name", itemName, true);
//        if (items.size() > 0) {
//            searchedItem = (Institution) items.get(0);
//        } else if (createNewIfNotPresent) {
//            searchedItem = new Institution();
//            searchedItem.setName(itemName);
//            searchedItem.setCreatedAt(Calendar.getInstance().getTime());
//            searchedItem.setCreater(sessionController.loggedUser);
//            getFacade().create(searchedItem);
//        }
//        return searchedItem;
//    }
    private void recreateModel() {
        items = null;
    }

    public void prepareSelect() {
        this.prepareModifyControlDisable();
    }

    public void prepareEdit() {
        if (current != null) {
            selectedItemIndex = intValue(current.getId());
            this.prepareSelectControlDisable();
        } else {
            JsfUtil.addErrorMessage(new MessageProvider().getValue("nothingToEdit"));
        }
    }

    public void prepareAdd() {
        selectedItemIndex = -1;
        current = new Institution();
        current.setOfficial(Boolean.TRUE);
        this.prepareSelectControlDisable();
    }

    public void saveSelected() {
        
        if (selectedItemIndex > 0) {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("savedOldSuccessfully"));
        } else {
            current.setCreatedAt(Calendar.getInstance().getTime());
            current.setCreater(sessionController.loggedUser);
            getFacade().create(current);
            InstitutionSet insSet = new InstitutionSet();
            insSet.setName("Default");
            insSet.setCreatedAt(Calendar.getInstance().getTime());
            insSet.setCreater(sessionController.loggedUser);
            insSet.setInstitution(current);
            inSetFacade.create(insSet);
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("savedNewSuccessfully"));
        }
        this.prepareSelect();
        recreateModel();
        getItems();
        selectText = "";
        selectedItemIndex = intValue(current.getId());
    }

    
    public void addDirectly() {
        JsfUtil.addSuccessMessage("1");
        try {

            current.setCreatedAt(Calendar.getInstance().getTime());
            current.setCreater(sessionController.loggedUser);
            current.setOfficial(Boolean.FALSE);

            getFacade().create(current);
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("savedNewSuccessfully"));
            current = new Institution();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Error");
        }

    }

    public void cancelSelect() {
        this.prepareSelect();
    }

    public void delete() {
       
        if (current != null) {
            current.setRetired(true);
            current.setRetiredAt(Calendar.getInstance().getTime());
            current.setRetirer(sessionController.loggedUser);
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("deleteSuccessful"));
        } else {
            JsfUtil.addErrorMessage(new MessageProvider().getValue("nothingToDelete"));
        }
        recreateModel();
        getItems();
        selectText = "";
        selectedItemIndex = -1;
        current = null;
        this.prepareSelect();
    }

    public boolean isModifyControlDisable() {
        return modifyControlDisable;
    }

    public void setModifyControlDisable(boolean modifyControlDisable) {
        this.modifyControlDisable = modifyControlDisable;
    }

    public boolean isSelectControlDisable() {
        return selectControlDisable;
    }

    public void setSelectControlDisable(boolean selectControlDisable) {
        this.selectControlDisable = selectControlDisable;
    }

    public String getSelectText() {
        return selectText;
    }

    public void setSelectText(String selectText) {
        this.selectText = selectText;
        recreateModel();

    }

    public void prepareSelectControlDisable() {
        selectControlDisable = true;
        modifyControlDisable = false;
    }

    public void prepareModifyControlDisable() {
        selectControlDisable = false;
        modifyControlDisable = true;
    }

    @FacesConverter(forClass = Institution.class)
    public static class InstitutionControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InstitutionController controller = (InstitutionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "institutionController");
            return controller.ejbFacade.find(getKey(value));
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
            if (object instanceof Institution) {
                Institution o = (Institution) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + InstitutionController.class.getName());
            }
        }
    }
}
