/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import gov.health.entity.Area;
import gov.health.entity.Institution;
import gov.health.entity.Person;
import gov.health.entity.WebUser;
import gov.health.entity.WebUserRole;
import gov.health.facade.AppImageFacade;
import gov.health.facade.AreaFacade;
import gov.health.facade.InstitutionFacade;
import gov.health.facade.PersonFacade;
import gov.health.facade.WebUserFacade;
import gov.health.facade.WebUserRoleFacade;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@Named
@SessionScoped
public class SessionController implements Serializable {

    WebUser loggedUser = null;
    boolean logged = false;
    boolean activated = false;
    String primeTheme;
    String defLocale;
    boolean sysAdmin;
    boolean superUser;
    boolean insUser;
    boolean insAdmin;

    /**
     * Creates a new instance of SessionController
     */
    public SessionController() {
    }

    public boolean isSysAdmin() {
        if (loggedUser == null) {
            return false;
        }
        if (loggedUser.getRole().getName().equalsIgnoreCase("sysAdmin")) {
            return true;
        } else {
            return false;
        }
    }

    public void setSysAdmin(boolean sysAdmin) {
        this.sysAdmin = sysAdmin;
    }

    public boolean isSuperUser() {
        if (loggedUser == null) {
            return false;
        }
        if (loggedUser.getRole().getName().equalsIgnoreCase("superUser")) {
            return true;
        } else {
            return false;
        }

    }

    public void setSuperUser(boolean superUser) {
        this.superUser = superUser;
    }

    public boolean isInsUser() {
        if (loggedUser == null) {
            return false;
        }
        if (loggedUser.getRole().getName().equalsIgnoreCase("insUser")) {
            return true;
        } else {
            return false;
        }

    }

    public void setInsUser(boolean insUser) {
        this.insUser = insUser;
    }

    public boolean isInsAdmin() {
        if (getLoggedUser() == null) {
            return false;
        }
        if (loggedUser.getRole().getName().equalsIgnoreCase("insAdmin")) {
            return true;
        } else {
            return false;
        }

    }

    public void setInsAdmin(boolean insAdmin) {
        this.insAdmin = insAdmin;
    }

    public String getDefLocale() {
        defLocale = "en";
        if (getLoggedUser() != null) {
            if (getLoggedUser().getDefLocale() != null) {
                if (!getLoggedUser().getDefLocale().equals("")) {
                    return getLoggedUser().getDefLocale();
                }
            }
        }
        return defLocale;
    }

    public void setDefLocale(String defLocale) {
        this.defLocale = defLocale;
    }

    public String getPrimeTheme() {
        if (primeTheme == null || primeTheme.equals("")) {
            primeTheme = "redmond";
        }
        if (getLoggedUser() != null) {
            if (getLoggedUser().getPrimeTheme() != null) {
                if (!getLoggedUser().getPrimeTheme().equals("")) {
                    return getLoggedUser().getPrimeTheme();
                }
            }
        }
        return primeTheme;
    }

    public void setPrimeTheme(String primeTheme) {
        this.primeTheme = primeTheme;
    }

    /**
     *
     * @return
     */
    public WebUser getLoggedUser() {
        return loggedUser;
    }

    /**
     *
     * @param loggedUser
     */
    public void setLoggedUser(WebUser loggedUser) {
        this.loggedUser = loggedUser;
    }

    /**
     *
     * @return
     */
    public boolean isLogged() {
        return logged;
    }

    /**
     * Set whether user
     *
     * @param logged
     */
    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    /**
     * Get whether user is activated
     *
     * @return
     */
    public boolean isActivated() {
        return activated;
    }

    /**
     * Mark logged user as activated
     *
     * @param activated
     */
    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    byte[] photo;
    @EJB
    WebUserFacade uFacade;
    @EJB
    PersonFacade pFacade;
    @EJB
    WebUserRoleFacade rFacade;
    @EJB
    InstitutionFacade institutionFacade;
    @EJB
    AreaFacade areaFacade;
    @Inject
    private ImageController imageController;
    @Inject
    InstitutionController institutionController;
    @EJB
    AppImageFacade imageFacade;
    //
    WebUser current;
    String userName;
    String passord;
    String newPassword;
    String newPasswordConfirm;
    String newPersonName;
    String newUserName;
    String newDesignation;
    String newInstitution;
    String newPasswordHint;
    String telNo;
    String email;
    //

    String displayName;
//
    Institution institution;
    Area area;
    DataModel<Institution> institutions;
    DataModel<Area> areas;
    WebUserRole role;

    public InstitutionController getInstitutionController() {
        return institutionController;
    }

    public void setInstitutionController(InstitutionController institutionController) {
        this.institutionController = institutionController;
    }

    public WebUserRole getRole() {
        return role;
    }

    public void setRole(WebUserRole role) {
        this.role = role;
    }

    /**
     * Creates a new instance of sessionController
     */
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public AppImageFacade getImageFacade() {
        return imageFacade;
    }

    public void setImageFacade(AppImageFacade imageFacade) {
        this.imageFacade = imageFacade;
    }

    public ImageController getImageController() {
        return imageController;
    }

    public void setImageController(ImageController imageController) {
        this.imageController = imageController;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public void oncapture(CaptureEvent captureEvent) {
        photo = captureEvent.getData();
    }

    private WebUserFacade getFacede() {
        return uFacade;
    }

    public String loginAction() {

        HttpServletRequest request;
        request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Enumeration headerIter = request.getHeaderNames();
        String userAgent = request.getHeader("User-Agent");

        while (headerIter.hasMoreElements()) {
            String headername = (String) headerIter.nextElement();
            System.out.println("headername + : " + request.getHeader(headername));
        }

        String clientAddr = request.getRemoteAddr();
        String clientPc = request.getRemoteHost();
        System.out.println("Client : " + clientPc + " & client address : " + clientAddr + " & Browser : " + userAgent);

        if (login()) {
            return "";
        } else {
            JsfUtil.addErrorMessage("Login Failure. Please try again");
            return "";
        }
    }

    private boolean login() {
        if (userName.trim().equals("")) {
            JsfUtil.addErrorMessage("Please enter a username");
            return false;
        }
        // password
        if (isFirstVisit()) {
            prepareFirstVisit();
            return true;
        } else {
            //JsfUtil.addSuccessMessage("Checking Old Users");
            return checkUsers();
        }
    }

    private void prepareFirstVisit() {
        WebUser user = new WebUser();
        Person person = new Person();
        person.setName(userName);
        pFacade.create(person);

        WebUserRole role = new WebUserRole();
        role.setName("superUser");
        rFacade.create(role);

        role = new WebUserRole();
        role.setName("insUser");
        rFacade.create(role);

        role = new WebUserRole();
        role.setName("insAdmin");
        rFacade.create(role);

        role = new WebUserRole();
        role.setName("sysAdmin");
        rFacade.create(role);

        user.setName(HOSecurity.encrypt(userName));
        user.setWebUserPassword(HOSecurity.hash(passord));
        user.setWebUserPerson(person);
        user.setActivated(true);
        user.setRole(role);
        uFacade.create(user);

//        JsfUtil.addSuccessMessage("New User Added");
    }

    @SuppressWarnings("empty-statement")
    private boolean telNoOk() {

        int temp; // temp value to check if the telNo is numeric
        String[] telCodes = {"071", "072", "075", "077", "078"};

        // tel no validation
        //Chaminda to write
        if (telNo.trim().length() == 10) {
            // check if the length of the String is 10 chars
            //System.out.println("length OK !");

            try {
                temp = Integer.parseInt(telNo);
                //check if this is a number
                //System.out.println("Integer OK !");

                for (int j = 0; j < telCodes.length; j++) {
                    // check if the number starts with a valid value
                    //System.out.println("looping OK ! " + telNo.substring(0, 3) + " " + telCodes[j]);

                    if (telNo.substring(0, 3).equalsIgnoreCase(telCodes[j])) {
                        return true;
                    }
                }
            } catch (NumberFormatException numberFormatException) {
                return false;
            }
        }
        return false;
    }

    public static boolean isValidEmailAddress(String email) {
        boolean result = true;
        try {
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
        } catch (AddressException ex) {
            JsfUtil.addErrorMessage("Please enter a valid Email \n" + ex.getMessage());
            result = false;
        }
        return result;
    }

    public String registeUser() {
//        if (!telNoOk()) {
//            JsfUtil.addErrorMessage("Telephone number in correct, Please enter a valid phone number");
//            return "";
//        }

        if (!userNameAvailable(newUserName)) {
            JsfUtil.addErrorMessage("User name already exists. Plese enter another user name");
            return "";
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            JsfUtil.addErrorMessage("Password and Re-entered password are not matching");
            return "";
        }

        if (!isValidEmailAddress(email)) {
            return "";
        }

        WebUser user = new WebUser();
        Person person = new Person();
        user.setWebUserPerson(person);
        user.setRole(role);

        person.setName(newPersonName);
        person.setInstitution(institution);

        person.setArea(area);
        pFacade.create(person);
        user.setName(HOSecurity.encrypt(newUserName));
        user.setWebUserPassword(HOSecurity.hash(newPassword));
        user.setWebUserPerson(person);
        user.setTelNo(telNo);
        user.setEmail(email);
        user.setActivated(Boolean.TRUE);
        if (user.getRole() != null && "sysAdmin".equals(user.getRole().getName())) {
            user.setRestrictedInstitution(null);
        } else if (user.getRole() != null && "superUser".equals(user.getRole().getName())) {
            user.setRestrictedInstitution(null);
        } else {
            user.setRestrictedInstitution(institution);
        }
        uFacade.create(user);
        JsfUtil.addSuccessMessage("New User Registered.");
        return "manage_users";
    }

    public void updateLoggedInstitution() {
        if (loggedUser == null || loggedUser.getRestrictedInstitution() == null) {
            JsfUtil.addErrorMessage("No Restricted Institution");
            return;
        }
        getInstitutionFacade().edit(
                getLoggedUser().getRestrictedInstitution());
        JsfUtil.addSuccessMessage("Updated");
    }

    public String changePassword() {
        WebUser user = getLoggedUser();
        if (!HOSecurity.matchPassword(passord, user.getWebUserPassword())) {
            JsfUtil.addErrorMessage("The old password you entered is incorrect");
            return "";
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            JsfUtil.addErrorMessage("Password and Re-entered password are not maching");
            return "";
        }

        user.setWebUserPassword(HOSecurity.hash(newPassword));
        uFacade.edit(user);
        //
        JsfUtil.addSuccessMessage("Password changed");
        return "index";
    }

    public Boolean userNameAvailable(String userName) {
        Boolean available = true;
        List<WebUser> allUsers = getFacede().findAll();
        for (WebUser w : allUsers) {
            if (userName.toLowerCase().equals(HOSecurity.decrypt(w.getName()).toLowerCase())) {
                available = false;
            }
        }
        return available;
    }

    private boolean isFirstVisit() {
        if (getFacede().count() <= 0) {
//            JsfUtil.addSuccessMessage("First Visit");
            return true;
        } else {
//            JsfUtil.addSuccessMessage("Not, Not First Visit");
            return false;
        }

    }

    private boolean checkUsers() {
//        JsfUtil.addSuccessMessage("Going to check users");
        String temSQL;
        temSQL = "SELECT u FROM WebUser u WHERE u.retired = false";
        List<WebUser> allUsers = getFacede().findBySQL(temSQL);
        for (WebUser u : allUsers) {
            if (HOSecurity.decrypt(u.getName()).equalsIgnoreCase(userName)) {
//                JsfUtil.addSuccessMessage("A user found");

                if (HOSecurity.matchPassword(passord, u.getWebUserPassword())) {
                    setLoggedUser(u);
                    setLogged(Boolean.TRUE);
                    setActivated(u.isActivated());
                    institutionController.createInsTree();
                    JsfUtil.addSuccessMessage("Logged successfully");
                    return true;
                }
            }
        }
        return false;
    }

    public void logout() {
        setLoggedUser(null);
        setLogged(false);
        setActivated(false);
    }

    public WebUser getCurrent() {
        if (current == null) {
            current = new WebUser();
            Person p = new Person();
            current.setWebUserPerson(p);
        }
        return current;
    }

    public void setCurrent(WebUser current) {
        this.current = current;
    }

    public WebUserFacade getEjbFacade() {
        return uFacade;
    }

    public void setEjbFacade(WebUserFacade ejbFacade) {
        this.uFacade = ejbFacade;
    }

    public String getPassord() {
        return passord;
    }

    public void setPassord(String passord) {
        this.passord = passord;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewDesignation() {
        return newDesignation;
    }

    public void setNewDesignation(String newDesignation) {
        this.newDesignation = newDesignation;
    }

    public String getNewInstitution() {
        return newInstitution;
    }

    public void setNewInstitution(String newInstitution) {
        this.newInstitution = newInstitution;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }

    public void setNewPasswordConfirm(String newPasswordConfirm) {
        this.newPasswordConfirm = newPasswordConfirm;
    }

    public String getNewPasswordHint() {
        return newPasswordHint;
    }

    public void setNewPasswordHint(String newPasswordHint) {
        this.newPasswordHint = newPasswordHint;
    }

    public String getNewPersonName() {
        return newPersonName;
    }

    public void setNewPersonName(String newPersonName) {
        this.newPersonName = newPersonName;
    }

    public PersonFacade getpFacade() {
        return pFacade;
    }

    public void setpFacade(PersonFacade pFacade) {
        this.pFacade = pFacade;
    }

    public WebUserFacade getuFacade() {
        return uFacade;
    }

    public void setuFacade(WebUserFacade uFacade) {
        this.uFacade = uFacade;
    }

    public String getNewUserName() {
        return newUserName;
    }

    public void setNewUserName(String newUserName) {
        this.newUserName = newUserName;
    }

    public WebUserRoleFacade getrFacade() {
        return rFacade;
    }

    public void setrFacade(WebUserRoleFacade rFacade) {
        this.rFacade = rFacade;
    }

    public String getDisplayName() {
        return HOSecurity.decrypt(getLoggedUser().getName());
    }

    public Area getArea() {
        return area;
    }

    public void setArea(Area area) {
        this.area = area;
    }

    public AreaFacade getAreaFacade() {
        return areaFacade;
    }

    public void setAreaFacade(AreaFacade areaFacade) {
        this.areaFacade = areaFacade;
    }

    public DataModel<Area> getAreas() {
        return new ListDataModel<Area>(getAreaFacade().findAll());
    }

    public void setAreas(DataModel<Area> areas) {
        this.areas = areas;
    }

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

    public DataModel<Institution> getInstitutions() {
        return new ListDataModel<Institution>(getInstitutionFacade().findAll());
    }

    public void setInstitutions(DataModel<Institution> institutions) {
        this.institutions = institutions;
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

}
