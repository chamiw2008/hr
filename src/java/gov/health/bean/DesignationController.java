/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of 
 Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import gov.health.facade.DesignationFacade;
import gov.health.entity.Designation;
import gov.health.entity.Institution;
import gov.health.entity.PersonInstitution;
import gov.health.facade.PersonInstitutionFacade;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.persistence.TemporalType;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class DesignationController implements Serializable {

    @EJB
    private DesignationFacade ejbFacade;
    @Inject
    SessionController sessionController;
    List<Designation> lstItems;
    private Designation current;
    private DataModel<Designation> items = null;
    private int selectedItemIndex;
    boolean selectControlDisable = false;
    boolean modifyControlDisable = true;
    String selectText = "";

    List<Designation> officialDesignations;


    
    
    Institution institution;

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }
    
public Designation findDesingation(String desName, boolean createNew) {
        desName = desName.trim();
        if (desName.equals("")) {
            return null;
        }
        Map m = new HashMap();
        m.put("n", desName.toLowerCase() );
        Designation des = getFacade().findFirstBySQL("select d from Designation d where d.retired = false and lower(d.name) =:n",m);
        if (des == null && createNew == true) {
            des = new Designation();
            des.setName(desName);
            des.setCreatedAt(Calendar.getInstance().getTime());
            des.setCreater(sessionController.loggedUser);
            des.setOfficial(Boolean.FALSE);
            getFacade().create(des);
        }
        return des;
    }

public List<Designation> completeDesignation(String qry){
        Map m = new HashMap();
        m.put("n", "%" + qry.toLowerCase() + "%" );
        List<Designation> des = getFacade().findBySQL("select d from Designation d where d.retired = false and d.official=true and lower(d.name) like :n",m);
        return des;
}
    
    
    
    
    
    
    
    List<Designation> mappedDesignations;
    
    Designation currentMappingDesignation;

    public void saveCurrentMapping(){
        if(currentMappingDesignation==null){
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        currentMappingDesignation.setInstitution(institution);
        if(currentMappingDesignation.getId()==null || currentMappingDesignation.getId()==0){
            getFacade().create(currentMappingDesignation);
            JsfUtil.addSuccessMessage("Saved");
        }else{
            getFacade().edit(currentMappingDesignation);
            JsfUtil.addSuccessMessage("Updated");
        }
        
        Map m = new HashMap();
        m.put("s", currentMappingDesignation.getName());
        String    sql = "select pi from PersonInstitution pi where pi.strDesignation=:s and pi.designation is null";
        List<PersonInstitution> pis = getPiFacade().findBySQL(sql, m, TemporalType.DATE);
        //System.out.println("pis = " + pis);
        for (PersonInstitution pi : pis) {
            pi.setDesignation(currentMappingDesignation.getMappedToDesignation());
            getPiFacade().edit(pi);
        }

        
        
        currentMappingDesignation=null;
        getCurrentMappingDesignation();
    }

    @EJB
    PersonInstitutionFacade piFacade;

    public PersonInstitutionFacade getPiFacade() {
        return piFacade;
    }

    public void setPiFacade(PersonInstitutionFacade piFacade) {
        this.piFacade = piFacade;
    }
    
    
    
    
    public void saveIndividualMapping(Designation mappingFor, Designation mappedTo){
        System.out.println("mapped for " + mappingFor);
        System.out.println("mapped to " + mappedTo);
        mappingFor.setInstitution(institution);
        mappingFor.setMappedToDesignation(mappedTo);
        if(mappingFor.getId()==null || mappingFor.getId()==0){
            getFacade().create(mappingFor);
            JsfUtil.addSuccessMessage("Saved");
        }else{
            getFacade().edit(mappingFor);
            JsfUtil.addSuccessMessage("Updated");
        }
    }
    
    public String toMapGeneralDesignations(){
        institution=null;
        return "designation_mapping_general";
    }
    
    public List<Designation> getMappedDesignations() {
        return mappedDesignations;
    }

    
    public void listMappedDesignations(){
        String sql;
        if(institution==null){
            sql = "select i from Designation i where i.retired=false and i.mappedToDesignation is not null and i.institution is null order by i.name";
            System.out.println("sql is " + sql);
            mappedDesignations=getFacade().findBySQL(sql);
            System.out.println("mappedDesignations is " + mappedDesignations);
        }else{
            Map m = new HashMap();
            m.put("ii", institution);
            sql = "select i from Designation i where i.retired=false and i.mappedToDesignation is not null and i.institution=:ii order by i.name";
            mappedDesignations=getFacade().findBySQL(sql,m);
        }        
    }
    
    public void listUnmappedDesignations(){
        String sql;
        sql = "select distinct(pi.strDesignation) from PersonInstitution pi where pi.designation is null and pi.name is not null and pi.name<>'' ";
        unmappedDesignations = getEjbFacade().findString(sql);
    }
    
    List<String> unmappedDesignations;

    public List<String> getUnmappedDesignations() {
        return unmappedDesignations;
    }

    public void setUnmappedDesignations(List<String> unmappedDesignations) {
        this.unmappedDesignations = unmappedDesignations;
    }
    
    
    
    public void setMappedDesignations(List<Designation> mappedDesignations) {
        this.mappedDesignations = mappedDesignations;
    }

    public Designation getCurrentMappingDesignation() {
        if(currentMappingDesignation==null){
            currentMappingDesignation = new Designation();
            currentMappingDesignation.setInstitution(institution);
        }
        return currentMappingDesignation;
    }

    public void setCurrentMappingDesignation(Designation currentMappingDesignation) {
        this.currentMappingDesignation = currentMappingDesignation;
    }
    
    public void removeMapping(){
        if(currentMappingDesignation==null){
            JsfUtil.addErrorMessage("Nothing to remove");
            return;
        }
        currentMappingDesignation.setRetired(true);
        getFacade().edit(currentMappingDesignation);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    public List<Designation> getOfficialDesignations() {
        if (officialDesignations == null) {
            officialDesignations = getFacade().findBySQL("select d from Designation d where d.retired=false and d.official=true order by d.name");
        }
        return officialDesignations;
    }

    public void recreateModel() {
        items=null;
        officialDesignations = null;
    }

    public void setOfficialDesignations(List<Designation> officialDesignations) {
        this.officialDesignations = officialDesignations;
    }

    public DesignationController() {
    }

    public List<Designation> getLstItems() {
        return getFacade().findBySQL("Select d From Designation d");
    }

    public void setLstItems(List<Designation> lstItems) {
        this.lstItems = lstItems;
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void setSelectedItemIndex(int selectedItemIndex) {
        this.selectedItemIndex = selectedItemIndex;
    }

    public Designation getCurrent() {
        if (current == null) {
            current = new Designation();
        }
        return current;
    }

    public void setCurrent(Designation current) {
        this.current = current;
    }

    private DesignationFacade getFacade() {
        return ejbFacade;
    }

    public DataModel<Designation> getItems() {
        items = new ListDataModel(getFacade().findAll("name", true));
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

    public DataModel searchItems() {
        recreateModel();
        if (items == null) {
            if (selectText.equals("")) {
                items = new ListDataModel(getFacade().findAll("name", true));
            } else {
                items = new ListDataModel(getFacade().findAll("name", "%" + selectText + "%",
                        true));
                if (items.getRowCount() > 0) {
                    items.setRowIndex(0);
                    current = (Designation) items.getRowData();
                    Long temLong = current.getId();
                    selectedItemIndex = intValue(temLong);
                } else {
                    current = null;
                    selectedItemIndex = -1;
                }
            }
        }
        return items;

    }

    public Designation searchItem(String itemName, boolean createNewIfNotPresent) {
        Designation searchedItem = null;
        items = new ListDataModel(getFacade().findAll("name", itemName, true));
        if (items.getRowCount() > 0) {
            items.setRowIndex(0);
            searchedItem = (Designation) items.getRowData();
        } else if (createNewIfNotPresent) {
            searchedItem = new Designation();
            searchedItem.setName(itemName);
            searchedItem.setCreatedAt(Calendar.getInstance().getTime());
            searchedItem.setCreater(sessionController.loggedUser);
            getFacade().create(searchedItem);
        }
        return searchedItem;
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
        current = new Designation();
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

            getFacade().create(current);
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("savedNewSuccessfully"));
            current = new Designation();
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
        searchItems();
    }

    public void prepareSelectControlDisable() {
        selectControlDisable = true;
        modifyControlDisable = false;
    }

    public void prepareModifyControlDisable() {
        selectControlDisable = false;
        modifyControlDisable = true;
    }

    public DesignationFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(DesignationFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    @FacesConverter(forClass = Designation.class)
    public static class DesignationControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DesignationController controller = (DesignationController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "designationController");
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
            if (object instanceof Designation) {
                Designation o = (Designation) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + DesignationController.class.getName());
            }
        }
    }
}
