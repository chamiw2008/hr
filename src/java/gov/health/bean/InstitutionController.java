/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of 
 Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import javax.faces.application.FacesMessage;

import org.primefaces.event.NodeCollapseEvent;
import org.primefaces.event.NodeExpandEvent;
import org.primefaces.event.NodeSelectEvent;
import org.primefaces.event.NodeUnselectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import gov.health.facade.InstitutionFacade;
import gov.health.entity.Institution;
import gov.health.entity.InstitutionSet;
import gov.health.entity.InstitutionType;
import gov.health.entity.PersonInstitution;
import gov.health.facade.InstitutionSetFacade;
import gov.health.facade.InstitutionTypeFacade;
import gov.health.facade.PersonInstitutionFacade;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ejb.EJB;
import javax.inject.Named;

import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.persistence.TemporalType;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class InstitutionController implements Serializable {

    @EJB
    private InstitutionFacade ejbFacade;
    @EJB
    InstitutionTypeFacade institutionTypeFacade;
    @EJB
    PersonInstitutionFacade piFacade;
    @EJB
    InstitutionSetFacade inSetFacade;

    @Inject
    SessionController sessionController;
    List<Institution> offItems;
    List<Institution> payCentres;
    private List<Institution> officialInstitutions;
    private Institution current;
    private List<Institution> items = null;
    List<InstitutionType> institutionTypes;
    private int selectedItemIndex;
    boolean selectControlDisable = false;
    boolean modifyControlDisable = true;
    String selectText = "";
    Integer offSel = 0;

    List<Institution> selectedIns;
    List<Institution> selectedPcs;

    List<Institution> paycentreInstitutions;

    Institution payCentre;

    List<Institution> mappedInstitutions;
    Institution mappingsForInstitution;
    Institution currentMappingInstitution;

    public void saveCurrentMapping() {
        if(getSessionController().getLoggedUser().getRestrictedInstitution()!=null){
            currentMappingInstitution=getSessionController().getLoggedUser().getRestrictedInstitution();
        }
        if (currentMappingInstitution == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        currentMappingInstitution.setInstitution(mappingsForInstitution);
        if (currentMappingInstitution.getId() == null || currentMappingInstitution.getId() == 0) {
            getFacade().create(currentMappingInstitution);
            JsfUtil.addSuccessMessage("Saved");
        } else {
            getFacade().edit(currentMappingInstitution);
            JsfUtil.addSuccessMessage("Updated");
        }
        currentMappingInstitution = null;
        getCurrentMappingInstitution();
    }

    public void saveIndividualMapping(Institution mappingFor, Institution mappedTo) {
        //System.out.println("mapped for " + mappingFor);
        //System.out.println("mapped to " + mappedTo);
        mappingFor.setMappedToInstitution(mappedTo);
        mappingFor.setInstitution(mappingsForInstitution);
        if (mappingFor.getId() == null || mappingFor.getId() == 0) {
            getFacade().create(mappingFor);
            JsfUtil.addSuccessMessage("Saved");
        } else {
            getFacade().edit(mappingFor);
            JsfUtil.addSuccessMessage("Updated");
        }
    }

    public String toMapGeneralInstitutions() {
        mappingsForInstitution = null;
        return "institution_mapping_general";
    }

    List<String> unmappedInstitutions;
    String selectedToMap;
    Institution institutionToMap;

    public void listUnmappedInstitutions() {
        if(getSessionController().getLoggedUser().getRestrictedInstitution()!=null){
            mappingsForInstitution=getSessionController().getLoggedUser().getRestrictedInstitution();
        }
        String sql;
        if (mappingsForInstitution == null) {
            sql = "select distinct(pi.strInstitution) from PersonInstitution pi where pi.institution is null and pi.name is not null and pi.name<>'' ";
            unmappedInstitutions = getEjbFacade().findString(sql);
        }else{
            sql = "select distinct(pi.strInstitution) from PersonInstitution pi where pi.payCentre=:i and pi.institution is null and pi.name is not null and pi.name<>'' ";
            Map m = new HashMap();
            m.put("i", mappingsForInstitution);
            unmappedInstitutions = getEjbFacade().findString(sql,m);
        }
    }

    public void mapToInstitution() {
        //System.out.println("mapToInstitution");
        if (institutionToMap == null) {
            JsfUtil.addErrorMessage("Please select designations");
            return;
        }
        String sql;
        Map m = new HashMap();
        m.put("stri", selectedToMap);
        //System.out.println("m = " + m);
        sql = "Select i from Institution i where i.name =:stri";
        //System.out.println("sql = " + sql);
        Institution i = getFacade().findFirstBySQL(sql, m);
        //System.out.println("i = " + i);
        if (i == null) {
            i = new Institution();
            i.setName(selectedToMap);
            i.setMappedToInstitution(institutionToMap);
            getFacade().create(i);
        } else {
            i.setMappedToInstitution(institutionToMap);
            getFacade().create(i);
        }
        sql = "select pi from PersonInstitution pi where pi.strInstitution=:s and pi.institution is null";
        List<PersonInstitution> pis = getPiFacade().findBySQL(sql, m, TemporalType.DATE);
        //System.out.println("pis = " + pis);
        for (PersonInstitution pi : pis) {
            pi.setInstitution(institutionToMap);
            getPiFacade().edit(pi);
        }

    }

    public List<String> getUnmappedInstitutions() {
        return unmappedInstitutions;
    }

    public void setUnmappedInstitutions(List<String> unmappedInstitutions) {
        this.unmappedInstitutions = unmappedInstitutions;
    }

    public String getSelectedToMap() {
        return selectedToMap;
    }

    public void setSelectedToMap(String selectedToMap) {
        this.selectedToMap = selectedToMap;
    }

    public Institution getInstitutionToMap() {
        return institutionToMap;
    }

    public void setInstitutionToMap(Institution institutionToMap) {
        this.institutionToMap = institutionToMap;
    }

    public List<Institution> getMappedInstitutions() {
        return mappedInstitutions;
    }

    public void listMappedInstitutions() {
        String sql;
        if(getSessionController().getLoggedUser().getRestrictedInstitution()!=null){
            mappingsForInstitution=getSessionController().getLoggedUser().getRestrictedInstitution();
        }
        if (mappingsForInstitution == null) {
            sql = "select i from Institution i where i.retired=false and i.mappedToInstitution is not null and i.institution is null order by i.name";
            //System.out.println("sql is " + sql);
            mappedInstitutions = getFacade().findBySQL(sql);
            //System.out.println("mappedInstitutions is " + mappedInstitutions);
        } else {
            Map m = new HashMap();
            m.put("ii", mappingsForInstitution);
            sql = "select i from Institution i where i.retired=false and i.mappedToInstitution is not null and i.institution=:ii order by i.name";
            mappedInstitutions = getFacade().findBySQL(sql, m);
        }
    }

    public void setMappedInstitutions(List<Institution> mappedInstitutions) {
        this.mappedInstitutions = mappedInstitutions;
    }

    public Institution getMappingsForInstitution() {
        return mappingsForInstitution;
    }

    public void setMappingsForInstitution(Institution mappingsForInstitution) {
        this.mappingsForInstitution = mappingsForInstitution;
    }

    public Institution getCurrentMappingInstitution() {
        if (currentMappingInstitution == null) {
            currentMappingInstitution = new Institution();
            currentMappingInstitution.setInstitution(mappingsForInstitution);
        }
        return currentMappingInstitution;
    }

    public void setCurrentMappingInstitution(Institution currentMappingInstitution) {
        this.currentMappingInstitution = currentMappingInstitution;
    }

    public void removeMapping() {
        if (currentMappingInstitution == null) {
            JsfUtil.addErrorMessage("Nothing to remove");
            return;
        }
        currentMappingInstitution.setRetired(true);
        getFacade().edit(currentMappingInstitution);
    }

    public List<Institution> getPaycentreInstitutions() {
        if (getPayCentre() == null) {
            return new ArrayList<Institution>();
        }
        String jpql = "select mi from Institution mi where mi.retired=false and mi.institution=:i order by mi.name";
        Map m = new HashMap();
        m.put("i", getPayCentre());
        paycentreInstitutions = getFacade().findBySQL(jpql, m);
        return paycentreInstitutions;
    }

    public void saveInstitution(Institution ins) {
        if (ins == null) {
            JsfUtil.addErrorMessage("Nothing to update");
            return;
        }
        if (ins.getId() == null || ins.getId() == 0) {
            getFacade().create(ins);
            JsfUtil.addSuccessMessage("Saved");
        } else {
            getFacade().edit(ins);
            JsfUtil.addSuccessMessage("Updated");
        }
    }

    public void setPaycentreInstitutions(List<Institution> paycentreInstitutions) {
        this.paycentreInstitutions = paycentreInstitutions;
    }

    public void savePayCentre() {
        if (payCentre == null) {
            JsfUtil.addErrorMessage("Please select a pay centre");
            return;
        }
        if (payCentre.getId() == null || payCentre.getId() == 0) {
            getFacade().create(payCentre);
            JsfUtil.addSuccessMessage("Saved");
        } else {
            getFacade().edit(payCentre);
            JsfUtil.addSuccessMessage("Updated");
        }
    }

    public Institution getPayCentre() {
        return payCentre;
    }

    public void setPayCentre(Institution payCentre) {
        this.payCentre = payCentre;
    }

    String insIds;

    public String getInsIds() {
        insIds = "";
        int c = 0;
        List<Institution> ins = getSelectedIns();
        for (Institution i : ins) {
            if (c + 1 == ins.size()) {
                insIds = insIds + i.getId() + " ";
            } else {
                insIds = insIds + i.getId() + ", ";
            }
            c = c + 1;
        }
        insIds = " (" + insIds + ")";
        return insIds;
    }

    public void setInsIds(String insIds) {
        this.insIds = insIds;
    }

    public List<Institution> getSelectedIns() {
//        if (selectedIns != null) {
//            return selectedIns;
//        }
        selectedIns = new ArrayList<Institution>(getOwnAndAllChildInstitutions(null, current));
        return selectedIns;
    }

    public void setSelectedIns(List<Institution> selectedIns) {
        this.selectedIns = selectedIns;
    }

    public List<Institution> getSelectedPcs() {
        return selectedPcs;
    }

    public void setSelectedPcs(List<Institution> selectedPcs) {
        this.selectedPcs = selectedPcs;
    }

    TreeNode root;

    TreeNode selectedNode;

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public List<Institution> getSlectedAllInstitutions(Institution i) {
        return new ArrayList<Institution>(getOwnAndAllChildInstitutions(null, i));
    }

    public List<Institution> getMyAllInstitutions() {
        return new ArrayList<Institution>(getOwnAndAllChildInstitutions(null, getSessionController().getLoggedUser().getRestrictedInstitution()));
    }

    public Set<Institution> getOwnAndAllChildInstitutions(Set<Institution> collection, Institution i) {
        if (collection == null) {
            collection = new TreeSet<Institution>();
            if (i != null) {
                collection.add(i);
            }
        }
        List<Institution> ins = childInstitutions(i);
        if (ins.isEmpty()) {
            return collection;
        } else {
            for (Institution ci : ins) {
                collection.add(ci);
                getOwnAndAllChildInstitutions(collection, ci);
            }
        }
        return collection;
    }

    public List<Institution> childInstitutions(Institution parent) {
        Map m = new HashMap();
        List<Institution> ins;
        String sql;
        if (parent == null) {
            sql = "select ci from Institution ci where ci.retired=false and ci.official=true and ci.superInstitution is null order by ci.name";
            ins = getFacade().findBySQL(sql);
        } else {
            sql = "select ci from Institution ci where ci.retired=false and ci.official=true and ci.superInstitution=:pi order by ci.name";
            m.put("pi", parent);
            ins = getFacade().findBySQL(sql, m);
        }
        //System.out.println("sql is " + sql);
        //System.out.println("Institutions retreved are " + ins);
        return ins;
    }

    public List<Institution> completeOffcialInstitutions(String qry) {
        String temSql;
        temSql = "SELECT i FROM Institution i where i.retired=false and i.official = true and LOWER(i.name) like '%" + qry.toLowerCase() + "%' order by i.name";
        return getFacade().findBySQL(temSql);
    }

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

    @Inject
    DbfController dbfController;

    public DbfController getDbfController() {
        return dbfController;
    }

    public void setDbfController(DbfController dbfController) {
        this.dbfController = dbfController;
    }

    public void setSelectedNode(TreeNode selectedNode) {
        if (this.selectedNode != selectedNode) {
            //System.out.println("set select node");
            getDbfController().recreateModel();
            getDbfController().getSelectedPersonInstitutions();
        }
        this.selectedNode = selectedNode;
    }

    public void onNodeExpand(NodeExpandEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Expanded", event.getTreeNode().toString());

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void onNodeCollapse(NodeCollapseEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Collapsed", event.getTreeNode().toString());

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public Institution findInstitution(String insName, boolean createNew) {
        insName = insName.trim();
        if (insName.equals("")) {
            return null;
        }
        Institution ins = getFacade().findFirstBySQL("select d from Institution d where d.retired = false and lower(d.name) = '" + insName.toLowerCase() + "'");
        if (ins == null && createNew == true) {
            ins = new Institution();
            ins.setName(insName);
            ins.setCreatedAt(Calendar.getInstance().getTime());
            ins.setCreater(sessionController.loggedUser);
            ins.setOfficial(Boolean.FALSE);
            getFacade().create(ins);
        }
        return ins;
    }

    public void onNodeSelect(NodeSelectEvent event) {
        current = findInstitution(event.getTreeNode().toString(), false);
        //System.out.println("set on node select");
        getDbfController().recreateModel();
        getDbfController().getSelectedPersonInstitutions();
        JsfUtil.addSuccessMessage(current.getName());
    }

    public void onNodeUnselect(NodeUnselectEvent event) {
        current = null;
    }

    public void createInsTree() {
        root = new DefaultTreeNode("Root", null);
        root.setExpanded(true);
        if (getSessionController().getLoggedUser() != null) {
            if (getSessionController().getLoggedUser().getRestrictedInstitution() != null) {
                TreeNode tn = new DefaultTreeNode(getSessionController().getLoggedUser().getRestrictedInstitution().getName(), root);
                tn.setExpanded(false);
                addChildInstituionNodes(getSessionController().getLoggedUser().getRestrictedInstitution(), tn);
            } else {

                //System.out.println("Logged user and ins " + getSessionController().getLoggedUser().getRestrictedInstitution());
                addChildInstituionNodes(null, root);
            }
        }

    }

    private boolean addChildInstituionNodes(Institution parent, TreeNode parentNode) {
        if (parent == null) {
            //System.out.println("parent is null");
        } else {
            //System.out.println("parent is " + parent.getName());
        }
        List<Institution> cis = childInstitutions(parent);
        if (cis.isEmpty()) {
            //System.out.println("no children");
            return false;
        } else {
            for (Institution i : cis) {
                //System.out.println("one child is " + i.getName());
                TreeNode tn = new DefaultTreeNode(i.getName(), parentNode);
                tn.setExpanded(true);
                addChildInstituionNodes(i, tn);
            }
            return true;
        }
    }

    public InstitutionController() {
//        createInsTree();
//        root = new DefaultTreeNode("Root", null);
//        TreeNode node0 = new DefaultTreeNode("Node 0", root);
//        TreeNode node1 = new DefaultTreeNode("Node 1", root);
//        TreeNode node2 = new DefaultTreeNode("Node 2", root);
//
//        TreeNode node00 = new DefaultTreeNode("Node 0.0", node0);
//        TreeNode node01 = new DefaultTreeNode("Node 0.1", node0);
//
//        TreeNode node10 = new DefaultTreeNode("Node 1.0", node1);
//        TreeNode node11 = new DefaultTreeNode("Node 1.1", node1);
//
//        TreeNode node000 = new DefaultTreeNode("Node 0.0.0", node00);
//        TreeNode node001 = new DefaultTreeNode("Node 0.0.1", node00);
//        TreeNode node010 = new DefaultTreeNode("Node 0.1.0", node01);
//
//        TreeNode node100 = new DefaultTreeNode("Node 1.0.0", node10);
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

    public List<InstitutionType> getInstitutionTypes() {
        String temSQL;
        temSQL = "SELECT i FROM InstitutionType i WHERE i.retired = false ORDER BY i.orderNo";
        return getInstitutionTypeFacade().findBySQL(temSQL);
    }

    public void setInstitutionTypes(List<InstitutionType> institutionTypes) {
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
        if (this.current != current) {
            recreateModel();
        }
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
        //System.out.println(temSql);

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
        selectedIns = null;
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
        if (current == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        if (current.getInstitutionType() == null) {
            JsfUtil.addErrorMessage("Please select a category");
            return;
        }
        if (current.getId() != null && current.getId() != 0) {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(new MessageProvider().getValue("savedOldSuccessfully"));
        } else {
            current.setCreatedAt(Calendar.getInstance().getTime());
            current.setCreater(sessionController.loggedUser);
            current.setOfficial(true);
            getFacade().create(current);

            if (current.getPayCentre() == true) {
                InstitutionSet insSet = new InstitutionSet();
                insSet.setName("Default");
                insSet.setCreatedAt(Calendar.getInstance().getTime());
                insSet.setCreater(sessionController.loggedUser);
                insSet.setInstitution(current);

                inSetFacade.create(insSet);
            }

            JsfUtil.addSuccessMessage(new MessageProvider().getValue("savedNewSuccessfully"));
        }
        this.prepareSelect();
        recreateModel();
        getItems();
        selectText = "";
        selectedItemIndex = intValue(current.getId());
    }

    
    public void addAllToMoh(){
        Institution moh = findInstitution("Logical Ministry", false);
        if(moh==null){
            JsfUtil.addErrorMessage("Can Not Locate Ministry");
            return;
        }
        List<Institution> is = getFacade().findAll();
        for(Institution i: is){
            System.out.println("i = " + i);
            if(i.equals(moh)){
                System.out.println("i is Moh");
            }else{
                System.out.println("i is NOT Moh");
//                System.out.println("i.getSuperInstitution().getName() = " + i.getSuperInstitution().getName());
                if(i.getSuperInstitution()==null || i.getSuperInstitution().getName()==null || i.getSuperInstitution().getName().trim().equals("")){
                    System.out.println("No Supper Institution");
                    i.setSuperInstitution(moh);
                    getFacade().edit(i);
                    System.out.println("set the super Institutions");
                }
            }
        }
        createInsTree();
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

    public List<Institution> getOfficialInstitutions() {
        String temSql;
        if (getSelectText().equals("")) {
            temSql = "SELECT i FROM Institution i where i.retired=false and i.official = true order by i.name";
        } else {
            temSql = "SELECT i FROM Institution i where i.retired=false and i.official = true and LOWER(i.name) like '%" + getSelectText().toLowerCase() + "%' order by i.name";
        }
        officialInstitutions = getFacade().findBySQL(temSql);
        return officialInstitutions;
    }

    public List<Institution> completeOfficialInstitutions(String qry) {
        String temSql;
        List<Institution> ins;
        temSql = "SELECT i FROM Institution i where i.retired=false and i.official = true and LOWER(i.name) like '%" + qry.toLowerCase() + "%' order by i.name";
        ins = getFacade().findBySQL(temSql);
        return ins;
    }

    public List<Institution> completePayCentres(String qry) {
        String temSql;
        List<Institution> ins;
        temSql = "SELECT i FROM Institution i where i.retired=false and i.payCentre = true and LOWER(i.name) like '%" + qry.toLowerCase() + "%' order by i.name";
        ins = getFacade().findBySQL(temSql);
        return ins;
    }

    public List<Institution> getAllOfficialInstitutions() {
        String temSql;
        temSql = "SELECT i FROM Institution i where i.retired=false and i.official = true order by i.name";
        officialInstitutions = getFacade().findBySQL(temSql);
        return officialInstitutions;
    }

    public void setOfficialInstitutions(List<Institution> officialInstitutions) {
        this.officialInstitutions = officialInstitutions;
    }

    public TreeNode getRoot() {
        return root;
    }

    public void setRoot(TreeNode root) {
        this.root = root;
    }

    @FacesConverter(forClass = Institution.class)
    public static class InstitutionControllerConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InstitutionController controller = (InstitutionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "institutionController");
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
            if (object instanceof Institution) {
                Institution o = (Institution) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type "
                        + object.getClass().getName() + "; expected type: " + InstitutionController.class.getName());
            }
        }
    }

    @FacesConverter("institutionConverter")
    public static class InstitutionConverter implements Converter {

        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            InstitutionController controller = (InstitutionController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "institutionController");
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
