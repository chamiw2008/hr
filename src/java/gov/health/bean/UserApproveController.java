/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.bean;

import gov.health.entity.Institution;
import gov.health.entity.WebUser;
import gov.health.entity.WebUserRole;
import gov.health.facade.AreaFacade;
import gov.health.facade.InstitutionFacade;
import gov.health.facade.WebUserFacade;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.inject.Inject;

/**
 *
 * @author Buddhika
 */
@Named
@SessionScoped
public class UserApproveController implements Serializable {

    List<WebUser> toApproveUsers;
    List<WebUser> users;
    WebUser selectedUser;
    //
    @EJB
    WebUserFacade userFacade;
    @EJB
    AreaFacade areaFacade;
    @EJB
    InstitutionFacade institutionFacade;
    //
    String activateComments;
    @Inject
    private SessionController sessionController;
    @Inject
    private ImageController imageController;
    List<Institution> institutions;
    WebUserRole role;
    Institution institution;

    public Institution getInstitution() {
        return institution;
    }

    public void setInstitution(Institution institution) {
        this.institution = institution;
    }

    public AreaFacade getAreaFacade() {
        return areaFacade;
    }

    public void setAreaFacade(AreaFacade areaFacade) {
        this.areaFacade = areaFacade;
    }

    public WebUserRole getRole() {
        return role;
    }

    public void setRole(WebUserRole role) {
        this.role = role;
    }

    public ImageController getImageController() {
        return imageController;
    }

    public void setImageController(ImageController imageController) {
        this.imageController = imageController;
    }

    public String viewPerImage() {
        System.out.println("VIew Per Img");
        System.out.println("VIew Per Img" + selectedUser.getWebUserPerson().getName());
        imageController.setPerson(selectedUser.getWebUserPerson());
        System.out.println("person_image");
        return "person_image";
    }

    public void removeInsRes() {
        setInstitution(null);
        getSelectedUser().setRestrictedInstitution(null);
        getUserFacade().edit(selectedUser);
        JsfUtil.addErrorMessage("Restrictions Removed");
    }

    public List<WebUser> getUsers() {
        String temSql;
        if (getSessionController().getLoggedUser().getRestrictedInstitution() != null) {
            temSql = "SELECT a FROM WebUser a WHERE a.retired=false AND a.webUserPerson.institution.id = " + sessionController.getLoggedUser().getRestrictedInstitution().getId() + " ORDER BY a.name ";
        } else {
            temSql = "SELECT a FROM WebUser a WHERE a.retired=false ORDER BY a.name ";
        }
        return getUserFacade().findBySQL(temSql);
    }

    public void setUsers(List<WebUser> users) {
        this.users = users;
    }

    public InstitutionFacade getInstitutionFacade() {
        return institutionFacade;
    }

    public void setInstitutionFacade(InstitutionFacade institutionFacade) {
        this.institutionFacade = institutionFacade;
    }

    public List<Institution> getInstitutions() {
        String temSql;
        if (sessionController.getLoggedUser().getRestrictedInstitution() == null) {
            temSql = "SELECT a FROM Institution a WHERE a.retired=false ORDER BY a.name ";
        } else {
            temSql = "SELECT a FROM Institution a WHERE a.retired=false AND a.id = " + sessionController.getLoggedUser().getRestrictedInstitution().getId();

        }
        return getInstitutionFacade().findBySQL(temSql);
    }

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    /**
     * Creates a new instance of UserApproveController
     */
    public UserApproveController() {
    }

    public String getActivateComments() {
        return activateComments;
    }

    public void setActivateComments(String activateComments) {
        this.activateComments = activateComments;
    }

    public SessionController getSessionController() {
        return sessionController;
    }

    public void setSessionController(SessionController sessionController) {
        this.sessionController = sessionController;
    }

    public WebUser getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(WebUser selectedUser) {
        this.selectedUser = selectedUser;
        role = selectedUser.getRole();
        institution = selectedUser.getRestrictedInstitution();
    }

    public List<WebUser> getToApproveUsers() {
        String temSQL;
        temSQL = "SELECT u FROM WebUser u WHERE u.retired=false AND u.activated=false";
        return getUserFacade().findBySQL(temSQL);
    }

    public void setToApproveUsers(List<WebUser> toApproveUsers) {
        this.toApproveUsers = toApproveUsers;
    }

    public WebUserFacade getUserFacade() {
        return userFacade;
    }

    public void setUserFacade(WebUserFacade userFacade) {
        this.userFacade = userFacade;
    }

    public void saveUser() {
        if (selectedUser == null) {
            JsfUtil.addErrorMessage("Please select a user");
            return;
        }
        selectedUser.setRole(role);
        selectedUser.setRestrictedInstitution(institution);

        if (selectedUser.getRole() != null && "sysAdmin".equals(selectedUser.getRole().getName())) {
            selectedUser.setRestrictedInstitution(null);
        } else if (selectedUser.getRole() != null && "superUser".equals(selectedUser.getRole().getName())) {
            selectedUser.setRestrictedInstitution(null);
        } else {
            selectedUser.setRestrictedInstitution(institution);
        }
//        selectedUser.setRestrictedInstitution(institution);

        userFacade.edit(selectedUser);
        //selectedUser = null;

        JsfUtil.addSuccessMessage("Successfully activated");
    }

    public void approveUser() {
        if (selectedUser == null) {
            JsfUtil.addErrorMessage("Please select a user to approve");
            return;
        }
        selectedUser.setActivated(true);
        selectedUser.setActivatedAt(Calendar.getInstance().getTime());
        selectedUser.setActivator(sessionController.loggedUser);
        selectedUser.setActivateComments(activateComments);
        userFacade.edit(selectedUser);

        selectedUser = null;

        JsfUtil.addSuccessMessage("Successfully activated");
    }
}
