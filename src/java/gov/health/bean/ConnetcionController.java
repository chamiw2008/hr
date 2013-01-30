/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import gov.health.entity.*;
import gov.health.facade.*;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import org.primefaces.event.CaptureEvent;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@ManagedBean
@RequestScoped
public class ConnetcionController implements Serializable {

    byte[] photo;
    @EJB
    WebUserFacade uFacade;
    @EJB
    PersonFacade pFacade;
    @EJB
    WebUserRoleFacade rFacade;
    @EJB
    PrivilegeFacade vFacade;
    @EJB
    InstitutionFacade institutionFacade;
    @EJB
    AreaFacade areaFacade;
    @ManagedProperty(value = "#{sessionController}")
    private SessionController sessionController;
    @ManagedProperty(value = "#{menu}")
    private Menu menu;
    @ManagedProperty(value = "#{imageController}")
    private ImageController imageController;
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
    boolean logged;
    boolean activated;
    Privilege privilege;
    String displayName;
//
    Institution institution;
    Area area;
    DataModel<Institution> institutions;
    DataModel<Area> areas;

    /**
     * Creates a new instance of ConnetcionController
     */
    public ConnetcionController() {
    }

    public String getTelNo() {
        return telNo;
    }

    public void setTelNo(String telNo) {
        this.telNo = telNo;
    }

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

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    private WebUserFacade getFacede() {
        return uFacade;
    }

    public String loginAction() {
        if (login()) {
            menu.createMenu();
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
        role.setName("Administrator");
        rFacade.create(role);

        user.setName(HOSecurity.encrypt(userName));
        user.setWebUserPassword(HOSecurity.hash(passord));
        user.setWebUserPerson(person);
        user.setActivated(true);
        user.setRole(role);
        uFacade.create(user);

        Privilege p = new Privilege();
        //
        p.setName("user Previlage");

        p.setInstAdmin(true);
        p.setInstUser(true);
        p.setSuperUser(true);
        p.setSystemAdmin(true);
        p.setWebUser(user);
        //
        getvFacade().create(p);

        //
        //Privilege for Administrator Role
        p = new Privilege();
        //
        p.setName("Role Previlage");
        p.setInstAdmin(true);
        p.setInstUser(true);
        p.setSuperUser(true);
        p.setSystemAdmin(true);        //
        p.setWebUserRole(role);
        //
        getvFacade().create(p);

//        JsfUtil.addSuccessMessage("New User Added");

        sessionController.setPrivilege(allUserPrivilege(user));

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

    public String registeUser() {
        if (!telNoOk()) {
            JsfUtil.addErrorMessage("Telephone number in correct, Please enter a valid phone number");
            return "";
        }

        if (!userNameAvailable(newUserName)) {
            JsfUtil.addErrorMessage("User name already exists. Plese enter another user name");
            return "";
        }
        if (!newPassword.equals(newPasswordConfirm)) {
            JsfUtil.addErrorMessage("Password and Re-entered password are not matching");
            return "";
        }
        WebUser user = new WebUser();
        Person person = new Person();
        user.setWebUserPerson(person);

        person.setName(newPersonName);
        person.setInstitution(institution);
        person.setArea(area);
        pFacade.create(person);
        user.setName(HOSecurity.encrypt(newUserName));
        user.setWebUserPassword(HOSecurity.hash(newPassword));
        user.setWebUserPerson(person);
        user.setTelNo(telNo);
        user.setEmail(email);
        user.setActivated(false);
        uFacade.create(user);
        //
        //
//        AppImage perImage = new AppImage();
//        perImage.setPerson(person);
//        perImage.setFileName("initial_photo_" + person.getId() + ".png");
//        perImage.setBaImage(photo);
//        perImage.setFileType("image/png");
//        imageFacade.create(perImage);
        //
        //
        JsfUtil.addSuccessMessage("New User Registered. You will be able to access the system when the administrater activate your account.");
        sessionController.setLoggedUser(user);
        sessionController.setLogged(Boolean.TRUE);
        sessionController.setActivated(false);
        return "index";
    }

    public String changePassword() {
        WebUser user = sessionController.loggedUser;
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
                    sessionController.setLoggedUser(u);
                    sessionController.setLogged(Boolean.TRUE);
                    sessionController.setActivated(u.isActivated());
                    sessionController.setPrivilege(allUserPrivilege(u));
                    JsfUtil.addSuccessMessage("Logged successfully");
                    return true;
                }
            }
        }
        return false;
    }

    private Privilege allUserPrivilege(WebUser user) {
        Privilege p = new Privilege();

        String temSQL = "SELECT p From Privilege p WHERE p.webUser.id = " + user.getId();
        List<Privilege> allP = getvFacade().findBySQL(temSQL);

        for (Privilege pv : allP) {
            //Cadre
            if (pv.isInstAdmin() == true) {
                p.setInstAdmin(true);
            }
            if (pv.isInstUser() == true) {
                p.setInstUser(true);
            }
            if (pv.isSuperUser() == true) {
                p.setSuperUser(true);
            }
            if (pv.isSystemAdmin() == true) {
                p.setSystemAdmin(true);
            }
            if (pv.getRestrictedArea() != null) {
                p.setRestrictedArea(pv.getRestrictedArea());
            }
            if (pv.getRestrictedInstitution() != null) {
                p.setRestrictedInstitution(pv.getRestrictedInstitution());
            }
        }

        return p;
    }

    public void logout() {
        sessionController.setLoggedUser(null);
        sessionController.setLogged(false);
        sessionController.setActivated(false);
        sessionController.setPrivilege(null);
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

    public boolean isActivated() {
        return sessionController.activated;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
        sessionController.setLogged(activated);
    }

    public boolean isLogged() {
        return sessionController.logged;
    }

    public void setLogged(boolean logged) {
        sessionController.setLogged(logged);
        this.logged = logged;
    }

    public WebUserRoleFacade getrFacade() {
        return rFacade;
    }

    public void setrFacade(WebUserRoleFacade rFacade) {
        this.rFacade = rFacade;
    }

    public PrivilegeFacade getvFacade() {
        return vFacade;
    }

    public void setvFacade(PrivilegeFacade vFacade) {
        this.vFacade = vFacade;
    }

    public Privilege getPrivilege() {
        return sessionController.getPrivilege();
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
        sessionController.setPrivilege(privilege);
    }

    public String getDisplayName() {
        return HOSecurity.decrypt(sessionController.getLoggedUser().getName());
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
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
}
