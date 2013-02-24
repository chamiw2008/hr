/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

import gov.health.entity.WebUser;
import java.io.Serializable;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical
 * Informatics)
 */
@ManagedBean
@SessionScoped
@Stateless
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
            primeTheme = "hot-sneaks";
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
}
