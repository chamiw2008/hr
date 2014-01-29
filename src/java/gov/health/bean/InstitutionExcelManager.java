/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.bean;

import gov.health.entity.Institution;
import gov.health.facade.DesignationFacade;
import gov.health.facade.InstitutionFacade;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Buddhika
 */
@Named
@SessionScoped
public class InstitutionExcelManager implements Serializable {

    /**
     *
     * EJBs
     *
     */
    @EJB
    DesignationFacade designationFacade;
    @EJB
    private InstitutionFacade institutionFacade;
    /**
     *
     * Values of Excel Columns
     *
     *
     */

    int insName = 0;
    int isPayCentre = 1;
    int startRow = 1;
    /**
     * DataModals
     *
     */

    /**
     *
     * Uploading File
     *
     */
    private UploadedFile file;

    /**
     * Creates a new instance of DemographyExcelManager
     */
    public InstitutionExcelManager() {
    }

    public int getInsName() {
        return insName;
    }

    public void setInsName(int insName) {
        this.insName = insName;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    @Inject
    InstitutionController institutionController;

    public String importToExcel() {
        System.out.println("importing to excel");
        String strName;

        File inputWorkbook;
        Workbook w;
        Cell cell;
        InputStream in;
        JsfUtil.addSuccessMessage(file.getFileName());
        try {
            JsfUtil.addSuccessMessage(file.getFileName());
            in = file.getInputstream();
            File f;
            f = new File(Calendar.getInstance().getTimeInMillis() + file.getFileName());
            FileOutputStream out = new FileOutputStream(f);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            in.close();
            out.flush();
            out.close();

            inputWorkbook = new File(f.getAbsolutePath());

            JsfUtil.addSuccessMessage("Excel File Opened");
            w = Workbook.getWorkbook(inputWorkbook);
            Sheet sheet = w.getSheet(0);

            System.out.println("sheet.getRows = " + sheet.getRows());
            System.out.println("startRow = " + startRow);

            for (int i = startRow; i < sheet.getRows(); i++) {
                System.out.println("i = " + i);

                Map m = new HashMap();
                Institution institution;
                //Ins Name
                cell = sheet.getCell(insName, i);
                strName = cell.getContents();
                System.out.println("Ins Name  is " + strName);
                institution = getInstitutionController().findInstitution(strName, true);
                System.out.println("institution = " + institution);
                if (institution == null) {
                    continue;
                }
                institution.setOfficial(Boolean.TRUE);
                cell = sheet.getCell(isPayCentre, i);
                strName = cell.getContents();
                if (strName.trim().equals("1")) {
                    institution.setPayCentre(Boolean.TRUE);
                    System.out.println("Pay centre");
                } else {
                    institution.setPayCentre(Boolean.FALSE);
                    System.out.println("Not Pay centre");
                }
                if (institution.getId() == null) {
                    getInstitutionFacade().create(institution);
                } else {
                    getInstitutionFacade().edit(institution);
                }

            }

            JsfUtil.addSuccessMessage("Succesful. All the data in Excel File Impoted to the database");
            return "";
        } catch (IOException ex) {
            JsfUtil.addErrorMessage(ex.getMessage());
            return "";
        } catch (BiffException e) {
            JsfUtil.addErrorMessage(e.getMessage());
            return "";
        }

      

    }

    public DesignationFacade getDesignationFacade() {
        return designationFacade;
    }

    public void setDesignationFacade(DesignationFacade designationFacade) {
        this.designationFacade = designationFacade;
    }

    public int getIsPayCentre() {
        return isPayCentre;
    }

    public void setIsPayCentre(int isPayCentre) {
        this.isPayCentre = isPayCentre;
    }

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public InstitutionController getInstitutionController() {
        return institutionController;
    }

    public void setInstitutionController(InstitutionController institutionController) {
        this.institutionController = institutionController;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

}
