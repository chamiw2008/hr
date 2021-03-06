/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.bean;

import com.linuxense.javadbf.DBFException;
import javax.faces.application.FacesMessage;

import org.primefaces.event.FileUploadEvent;
import gov.health.entity.Institution;
import gov.health.entity.DbfFile;
import gov.health.entity.PersonInstitution;
import gov.health.entity.Person;
import gov.health.entity.Category;
import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import gov.health.data.DesignationSummeryRecord;
import gov.health.entity.Designation;
import gov.health.entity.InstitutionMonthSummery;
import gov.health.entity.InstitutionSet;
import gov.health.facade.CategoryFacade;
import gov.health.facade.DbfFileFacade;
import gov.health.facade.DesignationFacade;
import gov.health.facade.InstitutionFacade;
import gov.health.facade.InstitutionSetFacade;
import gov.health.facade.PersonFacade;
import gov.health.facade.PersonInstitutionFacade;
import gov.health.entity.TransferHistory;
import gov.health.facade.InstitutionMonthSummeryFacade;
import gov.health.facade.TransferHistoryFacade;
import java.io.ByteArrayInputStream;

import org.primefaces.model.UploadedFile;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;

import javax.faces.context.FacesContext;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.persistence.TemporalType;

import org.apache.commons.io.IOUtils;

/**
 *
 * @author buddhika
 */
@Named
@SessionScoped
public class DbfController implements Serializable {

    PersonInstitution currentPi;

    StreamedContent scImage;
    StreamedContent scImageById;
    private UploadedFile file;
    @EJB
    TransferHistoryFacade thFacade;
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
    @EJB
    InstitutionMonthSummeryFacade institutionMonthSummeryFacade;
    @EJB
    InstitutionSetFacade institutionSetFacade;
    //
    //
    InstitutionMonthSummery institutionMonthSummery;
    Institution institution;
    Person person;
    Category category;
    List<Institution> payrollInstitutions;
    //
    DbfFile defFile;
//    List<DbfFile> dbfFiles;
    List<PersonInstitution> personInstitutions;
    List<PersonInstitution> existingPersonInstitutions;
    List<PersonInstitution> previousPersonInstitutions;
    List<PersonInstitution> newPersonInstitutions;
    @Inject
    SessionController sessionController;
    Integer payYear = 0;
    Integer payMonth = 0;
    List<Integer> payYears;
    List<Integer> payMonths;
    List<InstitutionSet> insSets;
    InstitutionSet insSet;
    List<DesignationSummeryRecord> designationSummery;
    //
    int activeTab = 0;
    Long withoutNicCount;
    Long withoutDesignationCount;
    Long withoutMappedDesignationCount;
    Long withoutInstitutionCount;
    Long withoutMappedInstitutionCount;
    Long activeCount;
    Long temporaryCount;
//
    Boolean toGetRecordsagain = Boolean.TRUE;
    int[] monthColR = new int[12];
    int[] monthColG = new int[12];
    int[] monthColB = new int[12];
    int[] completedSet = new int[12];
    int setCount;

    @Inject
    InstitutionController institutionController;

    public PersonInstitution getCurrentPi() {
        return currentPi;
    }

    public void setCurrentPi(PersonInstitution currentPi) {
        this.currentPi = currentPi;
    }

    public List<Institution> getPayrollInstitutions() {
        return payrollInstitutions;
    }

    public void setPayrollInstitutions(List<Institution> payrollInstitutions) {
        this.payrollInstitutions = payrollInstitutions;
    }

    public void saveCurrentPi() {
        if (currentPi == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        if (currentPi.getId() != null && currentPi.getId() != 0) {
            getPiFacade().edit(currentPi);
            JsfUtil.addSuccessMessage("Updated");
        } else {
            getPiFacade().create(currentPi);
            JsfUtil.addSuccessMessage("Saved");
        }
    }

    public InstitutionController getInstitutionController() {
        return institutionController;
    }

    public void setInstitutionController(InstitutionController institutionController) {
        this.institutionController = institutionController;
    }

    public int getSetCount() {
        System.out.println("getSetCount()");
        if (getInstitution() == null) {
            System.out.println("ins is null");
            return 0;
        }
        String sql;
        sql = "select iset from InstitutionSet iset where iset.retired = false and iset.institution.id in " + getInstitutionController().getInsIds() + " ";
        System.out.println("sql = " + sql);
        try {
            setCount = getPiFacade().findBySQL(sql).size();
            System.out.println("setCount.size() = " + setCount );
            return setCount;
        } catch (Exception e) {
            System.out.println("Error in getting set count is " + e.getMessage());
            return 0;
        }
    }

    public void setSetCount(int setCount) {
        this.setCount = setCount;
    }

    public int[] getMonthColR() {
        return monthColR;
    }

    public void setMonthColR(int[] monthColR) {
        this.monthColR = monthColR;
    }

    public int[] getMonthColG() {
        return monthColG;
    }

    public void setMonthColG(int[] monthColG) {
        this.monthColG = monthColG;
    }

    public int[] getMonthColB() {
        return monthColB;
    }

    public void setMonthColB(int[] monthColB) {
        this.monthColB = monthColB;
    }

    public void prepareSetSeubmitColours() {
        getSetCount();
        completedSetCount(getPayYear());
        System.out.println("Set Count " + setCount);
        for (int i = 0; i < 12; i++) {
            System.out.println("Completed Sets " + completedSet[i]);
            if (setCount == 0) {
                monthColR[i] = 0;
                monthColG[i] = 255;
                monthColB[i] = 0;
            } else if (setCount == completedSet[i]) {
                monthColR[i] = 0;
                monthColG[i] = 255;
                monthColB[i] = 0;
            } else if (completedSet[i] >= (setCount / 2)) {
                monthColR[i] = 255;
                monthColG[i] = 255;
                monthColB[i] = 0;
            } else {
                monthColR[i] = 245;
                monthColG[i] = 245;
                monthColB[i] = 245;
            }
            System.out.println("i " + i);
            System.out.println("R " + monthColR[i]);
            System.out.println("G " + monthColG[i]);
        }
    }

    public int[] getCompletedSet() {
        return completedSet;
    }

    public void setCompletedSet(int[] completedSet) {
        this.completedSet = completedSet;
    }

    public static int intValue(long value) {
        int valueInt = (int) value;
        if (valueInt != value) {
            throw new IllegalArgumentException(
                    "The long value " + value + " is not within range of the int type");
        }
        return valueInt;
    }

    public void completedSetCount(Integer temPayYear) {
        int temPayMonth = 0;
        for (int i = 0; i < 12; i++) {
            temPayMonth = i + 1;
            String sql;
            sql = "select distinct pi.paySet from PersonInstitution pi where pi.retired = false and pi.payYear = " + temPayYear + " and pi.payMonth = " + temPayMonth + " and pi.payCentre.id in " + getInstitutionController().getInsIds() + " ";
            //System.out.println(sql);
            try {
                completedSet[i] = getPiFacade().findBySQL(sql).size();
                //System.out.println(completedSet[i]);
            } catch (Exception e) {
                //System.out.println(e.getMessage());
                completedSet[i] = 0;
            }
        }
    }

    public Boolean getToGetRecordsagain() {
        return toGetRecordsagain;
    }

    public void setToGetRecordsagain(Boolean toGetRecordsagain) {
        this.toGetRecordsagain = toGetRecordsagain;
    }

    public List<PersonInstitution> getPreviousPersonInstitutions() {
        return previousPersonInstitutions;
    }

    public void setPreviousPersonInstitutions(List<PersonInstitution> previousPersonInstitutions) {
        this.previousPersonInstitutions = previousPersonInstitutions;
    }

    public void getSummeryCounts(List<PersonInstitution> pis) {
        //System.out.println("getting summery counts");
        withoutNicCount = 0L;
        //System.out.println("withoutNicCount = " + withoutNicCount);
        withoutDesignationCount = 0L;
        //System.out.println("withoutDesignationCount = " + withoutDesignationCount);
        withoutMappedDesignationCount = 0L;
        //System.out.println("withoutMappedDesignationCount = " + withoutMappedDesignationCount);
        withoutInstitutionCount = 0L;
        //System.out.println("withoutInstitutionCount = " + withoutInstitutionCount);
        withoutMappedInstitutionCount = 0L;
        //System.out.println("withoutMappedInstitutionCount = " + withoutMappedInstitutionCount);
        activeCount = 0L;
        //System.out.println("activeCount = " + activeCount);
        temporaryCount = 0L;
        //System.out.println("temporaryCount  " + temporaryCount);
        for (PersonInstitution pi : pis) {
            if (pi.getNic().trim().equals("")) {
                withoutNicCount++;
            }
            if (pi.getDesignation() == null) {
                if (pi.getStrDesignation() == null || pi.getStrDesignation().trim().equals("")) {
                    withoutDesignationCount++;
                } else {
                    
                    withoutMappedDesignationCount++;
                }
            }
            if (pi.getInstitution() == null) {
                if (pi.getStrInstitution() == null || pi.getStrInstitution().trim().equals("")) {
                    withoutInstitutionCount++;
                } else {
                    withoutMappedInstitutionCount++;
                }
            }
            if (pi.getActiveState() == Boolean.TRUE) {
                activeCount++;
            }
            if (pi.getPermanent() == Boolean.FALSE) {
                temporaryCount++;
            }
        }

    }

    public InstitutionMonthSummery writeSummeryCounts(Institution institution, Integer year, Integer month) {
        //System.out.println("writing summery counts");
        List<PersonInstitution> pis;

        InstitutionMonthSummery sum;
        String sql;
        Map m = new HashMap();
        Long paySetsSubmitted;
        Long totalPaySets;

        m.put("i", institution);

        sql = "select count(ps) from InstitutionSet ps where ps.retired=false and ps.institution=:i";
        totalPaySets = getInstitutionSetFacade().findLongByJpql(sql, m, TemporalType.DATE);

        m.put("y", year);
        m.put("m", month);

        sql = "select pi from PersonInstitution pi where pi.retired=false and pi.payYear=:y and pi.payMonth=:m and pi.paySet.institution=:i";
        pis = getPiFacade().findBySQL(sql, m);

        sql = "select distinct(pi.paySet) from PersonInstitution pi where pi.retired=false and pi.payYear=:y and pi.payMonth=:m and pi.paySet.institution=:i";
        List<InstitutionSet> ps;
        ps = getInstitutionSetFacade().findBySQL(sql, m);

        paySetsSubmitted = new Long(ps.size());

        sql = "select s from InstitutionMonthSummery s where s.institution=:i and s.summeryYear=:y and s.summeryMonth=:m";
        sum = getInstitutionMonthSummeryFacade().findFirstBySQL(sql, m);
        if (sum == null) {
            sum = new InstitutionMonthSummery();
            sum.setInstitution(institution);
            sum.setSummeryMonth(month);
            sum.setSummeryYear(year);
        }
        Long twithoutNicCount = 0L;
        Long twithoutDesignationCount = 0L;
        Long twithoutMappedDesignationCount = 0L;
        Long twithoutInstitutionCount = 0L;
        Long twithoutMappedInstitutionCount = 0L;
        Long tactiveCount = 0L;
        Long ttemporaryCount = 0L;
        for (PersonInstitution pi : pis) {
            if (pi.getNic().trim().equals("")) {
                twithoutNicCount++;
            }
            if (pi.getDesignation() == null) {
                if (pi.getStrDesignation().trim().equals("")) {
                    twithoutDesignationCount++;
                } else {
                    twithoutMappedDesignationCount++;
                }
            }
            if (pi.getInstitution() == null) {
                if (pi.getStrInstitution().trim().equals("")) {
                    twithoutInstitutionCount++;
                } else {
                    twithoutMappedInstitutionCount++;
                }
            }
            if (pi.getActiveState() == Boolean.TRUE) {
                tactiveCount++;
            }
            if (pi.getPermanent() == Boolean.FALSE) {
                ttemporaryCount++;
            }
        }

        sum.setRecordsWithoutNic(twithoutNicCount);
        sum.setRecordsWithoutDesignations(twithoutDesignationCount);
        sum.setRecordsWithoutDesignationMappging(twithoutMappedDesignationCount);
        sum.setRecordsWithoutInstitution(twithoutInstitutionCount);
        sum.setRecordsWithoutInstitutionMappings(twithoutMappedInstitutionCount);
        sum.setActiveCount(tactiveCount);
        sum.setTempCount(ttemporaryCount);
        sum.setCompletedSets(paySetsSubmitted);
        sum.setTotalSets(totalPaySets);
        if (totalPaySets == null || totalPaySets == 0) {
            sum.setCompleted(false);
        } else if (paySetsSubmitted == 0) {
            sum.setCompleted(false);
            sum.setHalfway(true);

        } else {
            if (paySetsSubmitted == totalPaySets) {
                sum.setCompleted(true);
            } else {
                sum.setHalfway(true);
                sum.setCompleted(false);
            }
        }

        if (sum.getId() == null || sum.getId() == 0) {
            getInstitutionMonthSummeryFacade().create(sum);
        } else {
            getInstitutionMonthSummeryFacade().edit(sum);
        }
        return sum;
    }

    public InstitutionMonthSummery readSummeryCounts(Institution institution, Integer year, Integer month) {
        //System.out.println("reading summery counts");
        InstitutionMonthSummery sum;
        String sql;
        Map m = new HashMap();
        m.put("i", institution);
        m.put("y", year);
        m.put("m", month);
        sql = "select s from InstitutionMonthSummery s where s.institution=:i and s.summeryYear=:y and s.summeryMonth=:m";
        sum = getInstitutionMonthSummeryFacade().findFirstBySQL(sql, m);
        if (sum == null) {
            sum = new InstitutionMonthSummery();
            sum.setInstitution(institution);
            sum.setSummeryMonth(month);
            sum.setSummeryYear(year);
        }
        return sum;
    }

    public Long getWithoutNicCount() {
//        getExistingPersonInstitutions();
        return withoutNicCount;
    }

    public void setWithoutNicCount(Long withoutNicCount) {
        this.withoutNicCount = withoutNicCount;
    }

    public Long getWithoutDesignationCount() {
//        getExistingPersonInstitutions();
        return withoutDesignationCount;
    }

    public void setWithoutDesignationCount(Long withoutDesignationCount) {
        this.withoutDesignationCount = withoutDesignationCount;
    }

    public Long getWithoutMappedDesignationCount() {
//        getExistingPersonInstitutions();
        return withoutMappedDesignationCount;
    }

    public void setWithoutMappedDesignationCount(Long withoutMappedDesignationCount) {

        this.withoutMappedDesignationCount = withoutMappedDesignationCount;
    }

    public Long getWithoutInstitutionCount() {
//        getExistingPersonInstitutions();
        return withoutInstitutionCount;
    }

    public void setWithoutInstitutionCount(Long withoutInstitutionCount) {
        this.withoutInstitutionCount = withoutInstitutionCount;
    }

    public Long getWithoutMappedInstitutionCount() {
//        getExistingPersonInstitutions();
        return withoutMappedInstitutionCount;
    }

    public void setWithoutMappedInstitutionCount(Long withoutMappedInstitutionCount) {
        this.withoutMappedInstitutionCount = withoutMappedInstitutionCount;
    }

    public Long getActiveCount() {
//        getExistingPersonInstitutions();
        return activeCount;
    }

    public void setActiveCount(Long activeCount) {
        this.activeCount = activeCount;
    }

    public Long getTemporaryCount() {
//        getExistingPersonInstitutions();
        return temporaryCount;
    }

    public void setTemporaryCount(Long temporaryCount) {
        this.temporaryCount = temporaryCount;
    }

    public int getActiveTab() {
        if (getNewPersonInstitutions().size() > 0) {
            activeTab = 1;
        } else {
            activeTab = 0;
        }
        return activeTab;
    }

    public void setActiveTab(int activeTab) {
        this.activeTab = activeTab;
    }

    public void processSingleDesignationSummery(){
        System.out.println("processDesignationSummery");
        String sql = "select pi.designation.name, count(pi) from PersonInstitution pi where pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and pi.institution.id = " + getInstitution().getId() + " group by pi.designation.name";
        System.out.println("sql = " + sql);
        List lst = getPiFacade().findGroupingBySql(sql);
        System.out.println("lst size = " + lst.size());
        List<DesignationSummeryRecord> sums = new ArrayList<DesignationSummeryRecord>();
        Iterator<Object[]> itr = lst.iterator();
        while (itr.hasNext()) {
            Object[] o = itr.next();
            System.out.println("o = " + o);
            DesignationSummeryRecord s = new DesignationSummeryRecord();
            s.setDesignationName(o[0].toString());
            s.setCount(Long.valueOf(o[1].toString()));
            System.out.println("s = " + s);
            sums.add(s);
        }
        designationSummery= sums;
    }
    

    
    public void processPisWithoutNic(){
        System.out.println("processWithoutNICs");
        String sql = "select pi from PersonInstitution pi where (pi.nic=null or pi.nic='') and pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and pi.payCentre.id = " + getInstitution().getId() + " ";
        System.out.println("sql = " + sql);
        personInstitutions = getPiFacade().findBySQL(sql);
    }

    
    public void processPisWithDuplicateNic(){
        System.out.println("processWithoutNICs");
        String sql ;
//        sql= "select pi.nic, count(pi) as picount  from PersonInstitution pi where  (pi.nic<>null and pi.nic<>'') and pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and picount>1 group by pi.nic";
//        List lst = getPiFacade().findGroupingBySql(sql);
//        System.out.println("lst = " + lst);
//        sql = "select p from PersonInstitution p where p.nic in (select pi.nic, count(pi) as picount  from PersonInstitution pi where  (pi.nic<>null and pi.nic<>'') and pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and picount>1 group by pi.nic) order by p.nic";
//        System.out.println("sql = " + sql);
        Map m = new HashMap();
        m.put("py", payYear);
        m.put("pm", payMonth);
        sql = "select distinct p1 from PersonInstitution p1,  PersonInstitution p2 where p1.nic<>null and p1.nic<>'' and  p2.nic<>null and p2.nic<>'' and p1.retired = false and p1.payYear=:py and p2.payMonth=:pm and  p2.retired = false and p2.payYear=:py and p2.payMonth=:pm and p1.id <> p2.id and p1.nic=p2.nic order by p1.nic";
        personInstitutions = getPiFacade().findBySQL(sql,m);
    }

    
    public void processSinglePaycentreSummery(){
        System.out.println("processPcDesignationSummery");
        String sql = "select pi.designation.name, count(pi) from PersonInstitution pi where pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and pi.payCentre.id = " + getInstitution().getId() + " group by pi.designation.name";
        System.out.println("sql = " + sql);
        List lst = getPiFacade().findGroupingBySql(sql);
        System.out.println("lst size = " + lst.size());
        List<DesignationSummeryRecord> sums = new ArrayList<DesignationSummeryRecord>();
        Iterator<Object[]> itr = lst.iterator();
        while (itr.hasNext()) {
            Object[] o = itr.next();
            System.out.println("o = " + o);
            DesignationSummeryRecord s = new DesignationSummeryRecord();
            s.setDesignationName(o[0].toString());
            s.setCount(Long.valueOf(o[1].toString()));
            System.out.println("s = " + s);
            sums.add(s);
        }
        designationSummery= sums;
    }
    
    
    public List<DesignationSummeryRecord> getDesignationSummery() {
        return designationSummery;
    }

    public TransferHistoryFacade getThFacade() {
        return thFacade;
    }

    public void setThFacade(TransferHistoryFacade thFacade) {
        this.thFacade = thFacade;
    }

    public void setDesignationSummery(List<DesignationSummeryRecord> designationSummery) {
        this.designationSummery = designationSummery;
    }

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
        if (this.insSet != insSet) {
            //System.out.println("setInsSet");
            recreateModel();
        }
        this.insSet = insSet;
    }

    public List<InstitutionSet> getInsSets() {
        if (getSessionController().getLoggedUser().getRestrictedInstitution() != null) {
            setInstitution(getSessionController().getLoggedUser().getRestrictedInstitution());
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
            payYears.add(2014);
        }
        return payYears;
    }

    public void setPayYears(List<Integer> payYears) {
        this.payYears = payYears;
    }

    public Integer getPayMonth() {
        if (payMonth == null || payMonth == 0) {
            payMonth = Calendar.getInstance().get(Calendar.MONTH);
        }
        return payMonth;
    }

    public void setPayMonth(Integer payMonth) {
        if (this.payMonth != payMonth) {
            //System.out.println("setPayMonth");
            recreateModel();
        }
        this.payMonth = payMonth;
    }

    public Integer getPayYear() {
        if (payYear == null || payYear == 0) {
            payYear = Calendar.getInstance().get(Calendar.YEAR);
            //System.out.println("get Pay Year");
            recreateModel();
        }
        return payYear;
    }

    public void setPayYear(Integer payYear) {
        if (this.payYear != payYear) {
            //System.out.println("set pay year");
            recreateModel();
        }
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

    List<PersonInstitution> selectedPersonInstitutions;

    public List<PersonInstitution> getSelectedPersonInstitutions() {
        if (selectedPersonInstitutions != null) {
            return selectedPersonInstitutions;
        }
        Map m = new HashMap();
        m.put("y", getPayYear());
        m.put("m", getPayMonth());
        String jpql = "select pi from PersonInstitution pi where pi.retired = false and pi.payYear =:y and pi.payMonth =:m and pi.payCentre.id in " + getInstitutionController().getInsIds() + " order by pi.institution.name";
        //System.out.println("JPQL is " + jpql);
        selectedPersonInstitutions = getPiFacade().findBySQL(jpql, m);
        //System.out.println("selected Person Institutes are " + selectedPersonInstitutions);
        getSummeryCounts(selectedPersonInstitutions);
        return selectedPersonInstitutions;
    }

    public void setSelectedPersonInstitutions(List<PersonInstitution> selectedPersonInstitutions) {
        this.selectedPersonInstitutions = selectedPersonInstitutions;
    }

    public List<PersonInstitution> getExistingPersonInstitutions() {
        if (existingPersonInstitutions == null) {
            existingPersonInstitutions = new ArrayList<PersonInstitution>();
        }
        return existingPersonInstitutions;
    }

    public void processExistingPersonInstitutions() {
        //System.out.println("existing pis");
        if (getInstitution() == null || getInsSet() == null) {
            existingPersonInstitutions = new ArrayList<PersonInstitution>();
            getSummeryCounts(existingPersonInstitutions);
            JsfUtil.addErrorMessage("Select Institution, Year and Month to fill");
            return;
        }
        Map m = new HashMap();
        m.put("s", getInsSet());
        m.put("y", getPayYear());
        m.put("m", getPayMonth());
        m.put("c", getInstitution());
        String sql;
        sql = "select pi from PersonInstitution pi where pi.retired=false and pi.payYear =:y and pi.payMonth =:m and pi.paySet =:s and  pi.payCentre =:c ";
        //System.out.println("sql is " + sql);
        existingPersonInstitutions = getPiFacade().findBySQL(sql, m);

        getSummeryCounts(getExistingPersonInstitutions());
        institutionMonthSummery = readSummeryCounts(institution, payYear, payMonth);
    }

    public void recreateModel() {
        //System.out.println("recreating model");
        existingPersonInstitutions = null;
        selectedPersonInstitutions = null;
    }

    public List<PersonInstitution> getPersonInstitutionsWithoutNic() {
        if (getInstitution() == null || getInsSet() == null) {
            return new ArrayList<PersonInstitution>();
        }
        existingPersonInstitutions = getPiFacade().findBySQL("select pi from PersonInstitution pi where pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and pi.paySet.id = " + getInsSet().getId() + " and  pi.payCentre.id in " + getInstitutionController().getInsIds() + " and pi.person is null order by pi.name");
        return getExistingPersonInstitutions();
    }

    public List<PersonInstitution> getPersonInstitutionsWithoutDesignations() {
        if (getInstitution() == null || getInsSet() == null) {
            return new ArrayList<PersonInstitution>();
        }
        existingPersonInstitutions = getPiFacade().findBySQL("select pi from PersonInstitution pi where pi.retired = false and pi.payYear = " + getPayYear() + " and pi.payMonth = " + getPayMonth() + " and pi.paySet.id = " + getInsSet().getId() + " and  pi.payCentre.id in " + getInstitutionController().getInsIds() + " and pi.designation is null order by pi.name");
        return getExistingPersonInstitutions();
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

//    public List<DbfFile> getAppImages() {
//        if (dbfFiles == null) {
//            dbfFiles = new ArrayList<DbfFile>();
//        }
//        //System.out.println("Getting app images - count is" + dbfFiles.size());
//        return dbfFiles;
//    }
//
//    public void setAppImages(List<DbfFile> appImages) {
//        this.dbfFiles = appImages;
//    }
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
        setToGetRecordsagain(Boolean.TRUE);
    }

//    private void prepareImages(String sql) {
//        dbfFiles = getDbfFileFacade().findBySQL(sql);
//    }
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
//        if (category == null && category.getId() != null) {
//            prepareImages("Select ai from AppImage ai Where ai.category.id = " + category.getId());
//        } else {
//            dbfFiles = null;
//        }
    }

    public InstitutionFacade getInsFacade() {
        return insFacade;
    }

    public void setInsFacade(InstitutionFacade insFacade) {
        this.insFacade = insFacade;
    }

    public Institution getInstitution() {
        if (getSessionController().getLoggedUser().getRestrictedInstitution() == null) {
            return institution;
        } else {
            return getSessionController().getLoggedUser().getRestrictedInstitution();
        }
    }

    public void setInstitution(Institution institution) {
        if (this.institution != institution) {
            //System.out.println("set Institution");
            recreateModel();
        }
        this.institution = institution;
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
//        if (person == null && person.getId() != null) {
//            prepareImages("Select ai from AppImage ai Where ai.person.id = " + person.getId());
//        } else {
//            dbfFiles = null;
//        }
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
            //System.out.println("Error " + e.getMessage());
        }

    }

    private Boolean isCorrectDbfFile(DBFReader reader) {
        Boolean correct = true;
        try {
            //System.out.println("field count is " + reader.getFieldCount());
            for (int i = 0; i < reader.getFieldCount(); i++) {
                //System.out.println("field " + i + " is " + reader.getField(i).getName());

            }
        } catch (DBFException ex) {
            Logger.getLogger(DbfController.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        for (PersonInstitution pi : getExistingPersonInstitutions()) {
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

    public void markTransfer(Person p, Institution fromIns, Institution toIns, PersonInstitution pi) {
        TransferHistory hx = new TransferHistory();
//        hx.setPersonInstitution(pi);
        hx.setFromInstitution(fromIns);
        hx.setToInstitution(toIns);
        hx.setPerson(person);
        thFacade.create(hx);
    }

    public void handleFileUpload(FileUploadEvent event) {
        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded.");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        InputStream in;
        String temNic;
        Boolean newEntries = false;
        if (sessionController.getLoggedUser().getRestrictedInstitution() != null) {
            setInstitution(sessionController.getLoggedUser().getRestrictedInstitution());
        } else {
            if (getInstitution() == null) {
                JsfUtil.addErrorMessage("Please select an institution");
                return;
            }
        }
        if (getInstitution() == null) {
            JsfUtil.addErrorMessage("Please select an institute");
            return;
        }
        if (event.getFile() == null) {
            JsfUtil.addErrorMessage("Please select the dbf file to upload");
            return;
        }
//        if (payYear == null || payYear == 0) {
//            JsfUtil.addErrorMessage("Please select a year");
//            return ;
//        }
//        if (payMonth == null || payMonth == 0) {
//            JsfUtil.addErrorMessage("Please select a Month");
//            return ;
//        }
        if (insSet == null) {
            JsfUtil.addErrorMessage("Please select a Set");
            return;
        }
        newEntries = getExistingPersonInstitutions().size() <= 0;

        try {
            in = event.getFile().getInputstream();
            DBFReader reader = new DBFReader(in);
            if (!isCorrectDbfFile(reader)) {
                JsfUtil.addErrorMessage("But the file you selected is not the correct file. Please make sure you selected the correct file named PYREMPMA.DBF. If you are sure that you selected the correct file, you may be using an old version.");
                return;
            }

            int numberOfFields = reader.getFieldCount();

            //System.out.println("Number of fields is " + numberOfFields);
            for (int i = 0; i < numberOfFields; i++) {
                DBFField field = reader.getField(i);
                //System.out.println("Data Field " + i + " is " + field.getName());
            }

            Object[] rowObjects;

            newPersonInstitutions = new ArrayList<PersonInstitution>();

            while ((rowObjects = reader.nextRecord()) != null) {

                Person p = null;
                PersonInstitution pi = new PersonInstitution();
                Institution attachedIns;

                String insName = "";
                if (institution.isInsmapAddress() == true) {
                    insName = rowObjects[21].toString() + " " + rowObjects[22].toString() + " " + rowObjects[23].toString();
                }

//                Add Site
                if (institution.isInsmapSite() == true) {
                    insName = insName + rowObjects[11].toString();
                }

//                Add Section
                if (institution.isInsmapSection() == true) {
                    insName = insName + rowObjects[13].toString();
                }

                String empNo = "";

                try {
                    empNo = rowObjects[0].toString();
                } catch (Exception e) {
                    empNo = "999999.0";
                }

                if (empNo != "999999.0") {

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
//                            p.setDob(null);
                            }
                            try {
                                p.setNic(rowObjects[48].toString());
                            } catch (Exception e) {
                            }
                            try {
                                p.setName(p.getTitle() + " " + p.getInitials() + " " + p.getSurname());
                            } catch (Exception e) {
                            }
                            getPerFacade().create(p);
                        } else {
                            if (p.getInstitution() != getInstitution()) {
                                markTransfer(p, p.getInstitution(), institution, pi);
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
                    if (newEntries) {
                        getPiFacade().create(pi);
                    }
                    newPersonInstitutions.add(pi);
                }
            }
            if (newEntries) {
                JsfUtil.addSuccessMessage("Date in the file " + file.getFileName() + " recorded successfully. ");
                newPersonInstitutions = new ArrayList<PersonInstitution>();
                getSummeryCounts(newPersonInstitutions);
                toGetRecordsagain = Boolean.TRUE;
            } else {
                JsfUtil.addSuccessMessage("Date in the file " + file.getFileName() + " is listed successfully. If you are satisfied, please click the Save button to permanantly save the new set of data Replacing the old ones under " + institution.getName() + ".");
                toGetRecordsagain = Boolean.TRUE;
            }

        } catch (IOException e) {
            //System.out.println("Error " + e.getMessage());
        }
    }

    public String uploadAndReplaceData() {
        extractData();
        return "upload_view";
    }

    public String extractData() {
        //System.out.println("extracting date");
        InputStream in;
        //System.out.println("1");
        String temNic;
        //System.out.println("2");
        if (sessionController.getLoggedUser().getRestrictedInstitution() != null) {
            setInstitution(sessionController.getLoggedUser().getRestrictedInstitution());
        } else {
            if (getInstitution() == null) {
                JsfUtil.addErrorMessage("Please select an institution");
                return "upload_view";
            }
        }
        //System.out.println("3");
        if (getInstitution() == null) {
            JsfUtil.addErrorMessage("Please select an institute");
            return "upload_view";
        }
        //System.out.println("4");
        if (file == null) {
            JsfUtil.addErrorMessage("Please select the dbf file to upload");
            return "upload_view";
        }
        //System.out.println("5");
        //System.out.println("Pay year is " + payYear);
        if (payYear == null || payYear == 0) {
            JsfUtil.addErrorMessage("Please select a year");
            return "upload_view";
        }
        //System.out.println("6");
        if (payMonth == null || payMonth == 0) {
            JsfUtil.addErrorMessage("Please select a Month");
            return "upload_view";
        }
        //System.out.println("7");
        if (insSet == null) {
            JsfUtil.addErrorMessage("Please select a Set");
            return "upload_view";
        }
        //System.out.println("8");
        try {
            //System.out.println("9");
            in = file.getInputstream();
            //System.out.println("10");
            DBFReader reader = new DBFReader(in);
            //System.out.println("11");
            if (!isCorrectDbfFile(reader)) {
                JsfUtil.addErrorMessage("But the file you selected is not the correct file. Please make sure you selected the correct file named PYREMPMA.DBF. If you are sure that you selected the correct file, you may be using an old version.");
                return "";
            }
            //System.out.println("12");
            int numberOfFields = reader.getFieldCount();
            //System.out.println("13");
            //System.out.println("Number of fields is " + numberOfFields);
            for (int i = 0; i < numberOfFields; i++) {
                DBFField field = reader.getField(i);
                //System.out.println("Data Field " + i + " is " + field.getName());
            }
            //System.out.println("14");
            Object[] rowObjects;
            //System.out.println("15");
            newPersonInstitutions = new ArrayList<PersonInstitution>();
            //System.out.println("16");
            while ((rowObjects = reader.nextRecord()) != null) {
                //System.out.println("17");
                Person p = null;
                PersonInstitution pi = new PersonInstitution();
                Institution attachedIns;
                //System.out.println("18");
                String insName = "";

                if (!institution.isInsMapToPaycentre()) {
                    //System.out.println("18a");
                    if (institution.isInsmapAddress() == true) {
                        //System.out.println("18b");
                        insName = rowObjects[21].toString() + " " + rowObjects[22].toString() + " " + rowObjects[23].toString();
                    }
//                Add Site
                    if (institution.isInsmapSite() == true) {
                        //System.out.println("18c");
                        insName = insName + rowObjects[11].toString();
                    }
//                Add Section
                    if (institution.isInsmapSection() == true) {
                        //System.out.println("18d");
                        insName = insName + rowObjects[13].toString();
                    }
                }
                //System.out.println("insName = " + insName);
                //System.out.println("19");
                String empNo = "";
                //System.out.println("20");
                try {
                    empNo = rowObjects[0].toString();
                } catch (Exception e) {
                    empNo = "999999.0";
                }
                //System.out.println("21");
                if (!"999999.0".equals(empNo)) {

                    if (institution.isInsMapToPaycentre()) {
                        System.out.println("is mapped to ps");
                        attachedIns = getInstitution();
                    } else {
                        System.out.println("not mapped to ps");
                        if (insName.trim().equals("")) {
                            System.out.println("insname is empty");
                            attachedIns = null;
                        } else {
                            System.out.println("finding attached ins");
                            attachedIns = findInstitution(insName);
                        }
                    }
                    //System.out.println("22");
                    temNic = rowObjects[48].toString();
                    //System.out.println("23");
                    if ("".equals(temNic.trim())) {
                        //System.out.println("24a");
                        pi.setPerson(null);
                    } else {
                        //System.out.println("24b");
                        p = getPerFacade().findFirstBySQL("select p from Person p where p.retired = false and p.nic = '" + temNic + "'");
                        if (p == null) {
                            //System.out.println("24b1");
                            p = new Person();
                            p.setCreatedAt(Calendar.getInstance().getTime());
                            p.setCreater(sessionController.getLoggedUser());
                            p.setInstitution(attachedIns);
                            p.setTitle(rowObjects[1].toString());
                            p.setInitials(rowObjects[3].toString());
                            p.setSurname(rowObjects[2].toString());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                            try {
                                //System.out.println("24b1a");
                                p.setDob(dateFormat.parse(rowObjects[7].toString()));
                            } catch (Exception e) {
//                            p.setDob(null);
                            }
                            //System.out.println("24b2");
                            try {
                                //System.out.println("24c");
                                p.setNic(rowObjects[48].toString());
                            } catch (Exception e) {
                                //System.out.println("25");
                            }
                            try {
                                //System.out.println("26 = " + 26);
                                p.setName(p.getTitle() + " " + p.getInitials() + " " + p.getSurname());
                            } catch (Exception e) {
                                //System.out.println("27");
                            }
                            //System.out.println("28");
                            getPerFacade().create(p);
                            //System.out.println("29");
                        } else {
                            //System.out.println("30 = " + 30);
                            if (p.getInstitution() != getInstitution()) {
                                //System.out.println("31");
                                markTransfer(p, p.getInstitution(), institution, pi);
                            }
                            //System.out.println("32");
                        }
                        //System.out.println("33");
                    }
                    //System.out.println("34 = " + 34);
                    pi.setPerson(p);
                    //System.out.println("35");

                    pi.setInstitution(attachedIns);
                    //System.out.println("36");
                    pi.setStrInstitution(insName);
                    //System.out.println("37");
                    pi.setPayCentre(getInstitution());
                    //System.out.println("38");
                    pi.setNic(rowObjects[48].toString());
                    //System.out.println("39");
                    pi.setEmpNo(rowObjects[0].toString());
                    //System.out.println("40");
                    pi.setAddress1(rowObjects[18].toString());
                    //System.out.println("41");
                    pi.setAddress2(rowObjects[19].toString());
                    //System.out.println("42");
                    pi.setAddress3(rowObjects[20].toString());
                    //System.out.println("43");
                    pi.setOffAddress1(rowObjects[21].toString());
                    //System.out.println("44");
                    pi.setOffAddress2(rowObjects[22].toString());
                    //System.out.println("45");
                    pi.setOffAddress3(rowObjects[23].toString());
                    //System.out.println("46");

                    String desName = rowObjects[8].toString();
                    //System.out.println("47");
                    //System.out.println("desName = " + desName);
                    if (desName != null) {
                        pi.setDesignation(findDesignation(desName));
                        //System.out.println("pi.getDesignation() = " + pi.getDesignation());
                    }
                    //System.out.println("48");
                    pi.setStrDesignation(desName);
                    //System.out.println("pi.getStrDesignation() = " + pi.getStrDesignation());
                    //System.out.println("50");
                    pi.setName(rowObjects[1].toString() + " " + rowObjects[2].toString() + " " + rowObjects[3].toString());
                    pi.setPayMonth(payMonth);
                    pi.setPayYear(payYear);
                    pi.setPaySet(insSet);
                    //System.out.println("51");
                    if (rowObjects[4].toString().equals("") || rowObjects[50].toString().equals("")) {
                        pi.setPermanent(Boolean.FALSE);
                    } else {
                        pi.setPermanent(Boolean.TRUE);
                    }
                    //System.out.println("52");
                    try {
                        if (Integer.valueOf(rowObjects[4].toString()) == 0) {
                            pi.setNopay(Boolean.TRUE);
                        } else {

                        }
                    } catch (Exception e) {
                    }
                    //System.out.println("53");
                    try {
                        pi.setActiveState((Boolean) rowObjects[40]);
                    } catch (Exception e) {
                        pi.setActiveState(true);
                    }
                    //System.out.println("54");
                    try {
                        pi.setNopay((Boolean) rowObjects[31]);
                    } catch (Exception e) {
                        pi.setNopay(false);
                    }
                    //System.out.println("55");
                    try {
                        pi.setConSal((Double) rowObjects[4]);
                    } catch (Exception e) {
                        pi.setConSal(0.0);
                    }
                    //System.out.println("55");
                    //System.out.println("pi = " + pi);
                    newPersonInstitutions.add(pi);
                    //System.out.println("pi added");
                }
                //System.out.println("56");
            }
            //System.out.println("57");
            for (PersonInstitution temPi : newPersonInstitutions) {
                temPi.setRetired(true);
                //System.out.println("saving pi " + temPi);
                getPiFacade().create(temPi);
            }
            //System.out.println("58");
            getSummeryCounts(newPersonInstitutions);
//            institutionMonthSummery = writeSummeryCounts(institution, payYear, payMonth);
            JsfUtil.addSuccessMessage("Data Captured. But NOT Recorded to the database. Please click Save to confirm.");
        } catch (Exception e) {
            //System.out.println("Error " + e.getMessage());
        }
        return "upload_submit";
    }

    public void savePi(PersonInstitution pi) {
        if (pi.getId() != null && pi.getId() != 0) {
            getPiFacade().edit(pi);
        } else {
            getPiFacade().create(pi);
        }
        JsfUtil.addSuccessMessage("Updated");
    }

    public void savePiDs(PersonInstitution pi, Designation d) {
        if (pi == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        //System.out.println("pi is " + pi);
        //System.out.println("d is " + d.getName());
        pi.setDesignation(d);
        if (pi.getId() != null && pi.getId() != 0) {
            getPiFacade().edit(pi);
        } else {
            getPiFacade().create(pi);
        }
        JsfUtil.addSuccessMessage("Updated");
    }

    public void savePiIn(PersonInstitution pi, Institution i) {
        if (pi == null) {
            JsfUtil.addErrorMessage("Nothing to save");
            return;
        }
        //System.out.println("pi is " + pi);
        //System.out.println("i is " + i.getName());
        pi.setInstitution(i);
        if (pi.getId() != null && pi.getId() != 0) {
            getPiFacade().edit(pi);
        } else {
            getPiFacade().create(pi);
        }
        JsfUtil.addSuccessMessage("Updated");
    }

    public void removePi(PersonInstitution pi) {
        if (newPersonInstitutions == null) {
            JsfUtil.addErrorMessage("Nothing to remove");
            return;
        }
        newPersonInstitutions.remove(pi);
        JsfUtil.addSuccessMessage("Removed");
    }

    public String submit() {
        if (newPersonInstitutions == null || newPersonInstitutions.isEmpty()) {
            JsfUtil.addSuccessMessage("Nothing to save");
            return "upload_view";
        }
        for (PersonInstitution pi : getExistingPersonInstitutions()) {
            pi.setRetired(true);
            pi.setRetiredAt(Calendar.getInstance().getTime());
            pi.setRetirer(sessionController.loggedUser);
            getPiFacade().edit(pi);
        }
        for (PersonInstitution pi : newPersonInstitutions) {
            pi.setRetired(false);
            if (pi.getId() != null && pi.getId() != 0) {
                getPiFacade().edit(pi);
            } else {
                getPiFacade().create(pi);
            }
        }
        recreateModel();
        newPersonInstitutions = new ArrayList<PersonInstitution>();
        institutionMonthSummery = writeSummeryCounts(institution, payYear, payMonth);
        processExistingPersonInstitutions();
        return "upload_view";
    }

    public String cancelSubmit() {
        recreateModel();
        newPersonInstitutions = new ArrayList<PersonInstitution>();
        getExistingPersonInstitutions();
        return "upload_view";
    }

    private Designation findDesignation(String designationName) {
        //System.out.println("d1");
        designationName = designationName.trim();
//        //System.out.println("d2");
        Designation search;
//        //System.out.println("d3");
        Designation des;
//        //System.out.println("d4");
        if (designationName.equals("")) {
            return null;
        }
        //System.out.println("d5");
        Map m = new HashMap();
        m.put("i", getInstitution());
        m.put("n", designationName);

        search = getDesFacade().findFirstBySQL("select d from Designation d where d.mappedToDesignation is not null and d.institution=:i and lower(d.name) =:n", m);
        //System.out.println("search = " + search);
        if (search != null) {
            //System.out.println("search.getMappedToDesignation() = " + search.getMappedToDesignation());
            return search.getMappedToDesignation();
        }

        //System.out.println("d6");
        m = new HashMap();
        m.put("n", designationName.toLowerCase());
        search = getDesFacade().findFirstBySQL("select d from Designation d where (d.official=true or d.mappedToDesignation is not null) and lower(d.name) =:n", m);
        //System.out.println("search 1 = " + search);

        if (search != null) {
            //System.out.println("search.getOfficial() = " + search.getOfficial());
            //System.out.println("d7");
            if (search.getOfficial() == null) {
                //System.out.println("d9");
                //System.out.println("search.getMappedToDesignation() = " + search.getMappedToDesignation());
                return search.getMappedToDesignation();
            } else {
                if (search.getOfficial() == true) {
                    //System.out.println("d10");
                    //System.out.println("search.getOfficial() = " + search.getOfficial());
                    return search;
                } else {
                    //System.out.println("d11");
                    //System.out.println("search.getMappedToDesignation() = " + search.getMappedToDesignation());
                    return search.getMappedToDesignation();
                }
            }
        }

        return null;

    }

    private Institution findInstitution(String insName) {
        System.out.println("inside findInstitutions");
        System.out.println("insName = " + insName);
        insName = insName.trim();
        System.out.println("insName = " + insName);
        if (insName.equals("")) {
            System.out.println("null");
            return null;
        }
        Map m = new HashMap();
        m.put("i", getInstitution());
        System.out.println("m = " + m);
        Institution ins;
        //Get CapitalSimple Match for that percerticular institute
        String sql;
        sql = "select d from Institution d where d.institution=:i and d.mappedToInstitution is not null and d.retired = false and upper(d.name) = '" + insName.toUpperCase() + "'";
        System.out.println("sql = " + sql);
        ins = getInsFacade().findFirstBySQL(sql, m);
        System.out.println("ins = " + ins);
        if (ins != null) {
            System.out.println("ins is not null and returnning = ");
            System.out.println("ins.getMappedToInstitution() = " + ins.getMappedToInstitution());
            return ins.getMappedToInstitution();
        }

        //Get Capital/Simple Match for any other institute
        sql = "select d from Institution d where d.retired = false  and (d.mappedToInstitution is not null or d.official=true ) and  upper(d.name) = '" + insName.toUpperCase() + "'";
        System.out.println("sql = " + sql);
        ins = getInsFacade().findFirstBySQL(sql);
        System.out.println("ins");
        System.out.println("ins = " + ins);
        
        if (ins != null) {
            System.out.println("ins.getOfficial() = " + ins.getOfficial());
            System.out.println("ins is not null");
            if (ins.getOfficial() == null) {
                System.out.println("ins is official is null");
                return ins.getMappedToInstitution();
            } else {
                System.out.println("ins official is not null");
                if (ins.getOfficial()) {
                    System.out.println("ins official is true");
                    return ins;
                } else {
                    System.out.println("ins.getMappedToInstitution() = " + ins.getMappedToInstitution());
                    return ins.getMappedToInstitution();
                }
            }
        }
        System.out.println("returning null");
        return null;
    }

    public InstitutionMonthSummery getInstitutionMonthSummery() {
        return institutionMonthSummery;
    }

    public void setInstitutionMonthSummery(InstitutionMonthSummery institutionMonthSummery) {
        this.institutionMonthSummery = institutionMonthSummery;
    }

    public InstitutionMonthSummeryFacade getInstitutionMonthSummeryFacade() {
        return institutionMonthSummeryFacade;
    }

    public void setInstitutionMonthSummeryFacade(InstitutionMonthSummeryFacade institutionMonthSummeryFacade) {
        this.institutionMonthSummeryFacade = institutionMonthSummeryFacade;
    }

    public InstitutionSetFacade getInstitutionSetFacade() {
        return institutionSetFacade;
    }

    public void setInstitutionSetFacade(InstitutionSetFacade institutionSetFacade) {
        this.institutionSetFacade = institutionSetFacade;
    }

    public List<PersonInstitution> getPersonInstitutions() {
        return personInstitutions;
    }

    public void setPersonInstitutions(List<PersonInstitution> personInstitutions) {
        this.personInstitutions = personInstitutions;
    }



}
