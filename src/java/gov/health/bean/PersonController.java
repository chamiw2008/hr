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
import gov.health.facade.PersonFacade;
import gov.health.entity.Person;
import gov.health.entity.PersonContact;
import gov.health.facade.ContactTypeFacade;
import gov.health.facade.InstitutionFacade;
import gov.health.facade.PersonContactFacade;
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
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public  class PersonController implements Serializable {

    @EJB
    private PersonFacade ejbFacade;
    @EJB
    InstitutionFacade institutionFacade;
    @EJB
    PersonContactFacade perConFacade;
    @EJB
    ContactTypeFacade ctFacade;
    @Inject
    SessionController sessionController;
    List<Person> lstItems;
    private Person current;
    List<PersonContact> currentContacts;
    DataModel<PersonContact> personContacts;
    PersonContact currentContact;
    private DataModel<Person> items = null;
    DataModel<Institution> institutions;
    private int selectedItemIndex;
    boolean selectControlDisable = false;
    boolean modifyControlDisable = true;
    String selectText = "";

    public ContactTypeFacade getCtFacade() {
        return ctFacade;
    }

    public void setCtFacade(ContactTypeFacade ctFacade) {
        this.ctFacade = ctFacade;
    }

    public DataModel<PersonContact> getPersonContacts() {
        personContacts = new ListDataModel<PersonContact>(getCurrentContacts());
        return personContacts;
    }

    public void setPersonContacts(DataModel<PersonContact> personContacts) {
        this.personContacts = personContacts;
    }

    public PersonContact getCurrentContact() {
        if (currentContact == null) {
            currentContact = new PersonContact();
            currentContact.setContactType(getCtFacade().findFirstBySQL("select ct from ContactType ct"));
            System.out.print("Getting new Contact" + currentContact.getContactType().getName());
        }
        return currentContact;
    }

    public void setCurrentContact(PersonContact currentContact) {
        this.currentContact = currentContact;
    }

    public void addContact() {
        if (currentContact == null) {
            JsfUtil.addErrorMessage("No Contact to add");
            return;
        }
        currentContact.setPerson(current);
        getCurrentContacts().add(currentContact);
        getPerConFacade().create(currentContact);
        currentContact = new PersonContact();
    }

    public void removeContact() {
        if (currentContact == null) {
            return;
        }
        if (currentContact.getId() != 0) {
            getPerConFacade().remove(currentContact);
        }
        currentContact = new PersonContact();
    }

    public List<PersonContact> getCurrentContacts() {
        if (currentContacts == null) {
            currentContacts = new ArrayList<PersonContact>();
        }
        return currentContacts;
    }

    public void setCurrentContacts(List<PersonContact> currentContacts) {
        this.currentContacts = currentContacts;
    }

    public PersonContactFacade getPerConFacade() {
        return perConFacade;
    }

    public void setPerConFacade(PersonContactFacade perConFacade) {
        this.perConFacade = perConFacade;
    }

    public PersonController() {
    }

    public DataModel<Institution> getInstitutions() {
        return new ListDataModel<Institution>(getInstitutionFacade().findBySQL("SELECT d FROM Institution d WHERE d.retired=false ORDER BY d.name"));
    }

    public void setInstitutions(DataModel<Institution> institutions) {
        this.institutions = institutions;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

    public List<Person> getLstItems() {
        return getFacade().findBySQL("Select d From Person d WHERE d.retired=false ORDER BY d.name");
    }

    public void setLstItems(List<Person> lstItems) {
        this.lstItems = lstItems;
    }

    public int getSelectedItemIndex() {
        return selectedItemIndex;
    }

    public void setSelectedItemIndex(int selectedItemIndex) {
        this.selectedItemIndex = selectedItemIndex;
    }

    public Person getCurrent() {
        if (current == null) {
            current = new Person();
        }
        return current;
    }

    public void setCurrent(Person current) {
        this.current = current;
        String temSql = "";
        if (current != null && current.getId() != null) {
            temSql = "select c from PersonContact c where c.retired = false and c.person.id = " + current.getId();
            currentContacts = getPerConFacade().findBySQL(temSql);
            System.out.println("Getting new set of contacts " + currentContacts.size());
        } else {
            currentContacts = null;
            System.out.println("Setting new set of contacts to null");
        }
        currentContact = new PersonContact();
    }

    private PersonFacade getFacade() {
        return ejbFacade;
    }

    public DataModel<Person> getItems() {
        String temSql;
        if (selectText.trim().equals("")) {
            temSql = "select p from Person p where p.retired=false order by p.name";
        } else {
            temSql = "select p from Person p where p.retired=false and lower(p.name) like '%" + selectText.toLowerCase() + "%' order by p.name";
        }
        System.out.println(temSql);
        List<Person> temLstPer = getFacade().findBySQL(temSql);
        items = new ListDataModel(temLstPer);
        System.out.println(temLstPer.size());
        if (temLstPer.size() == 1) {
            current = temLstPer.get(0);
            System.out.println("CUrrent is " + current.getName());
        }else{
            System.out.println("CUrrent is null");
            current = null;
        }
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
                    current = (Person) items.getRowData();
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

    public Person searchItem(String itemName, boolean createNewIfNotPresent) {
        Person searchedItem = null;
        items = new ListDataModel(getFacade().findAll("name", itemName, true));
        if (items.getRowCount() > 0) {
            items.setRowIndex(0);
            searchedItem = (Person) items.getRowData();
        } else if (createNewIfNotPresent) {
            searchedItem = new Person();
            searchedItem.setName(itemName);
            searchedItem.setCreatedAt(Calendar.getInstance().getTime());
            searchedItem.setCreater(sessionController.loggedUser);
            getFacade().create(searchedItem);
        }
        return searchedItem;
    }

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
        setCurrent(new Person());
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
//        for (PersonContact pc : currentContacts) {
//            if (pc != null && pc.getId() != null) {
//                if (pc.getId() == 0) {
//                    getPerConFacade().create(pc);
//                } else {
//                    getPerConFacade().edit(pc);
//                }
//            }
//        }
        currentContact = null;
        currentContacts = null;
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
            current = new Person();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, "Error");
        }

    }

    public void cancelSelect() {
        this.prepareSelect();
        currentContacts = null;
        currentContact = null;
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

    public PersonFacade getEjbFacade() {
        return ejbFacade;
    }

    public void setEjbFacade(PersonFacade ejbFacade) {
        this.ejbFacade = ejbFacade;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    @FacesConverter(forClass = Person.class)
    public static class PersonControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonController controller = (PersonController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personController");
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
            if (object instanceof Person) {
                Person o = (Person) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + PersonController.class.getName());
            }
        }
    }
}
