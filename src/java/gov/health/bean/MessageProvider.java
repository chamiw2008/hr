/*
 * MSc(Biomedical Informatics) Project
 * 
 * Development and Implementation of a Web-based Combined Data Repository of Genealogical, Clinical, Laboratory and Genetic Data 
 * and
 * a Set of Related Tools
 */
package gov.health.bean;

/**
 *
 * @author Dr. M. H. B. Ariyaratne, MBBS, PGIM Trainee for MSc(Biomedical Informatics)
 */
import java.io.Serializable;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

public class MessageProvider  implements Serializable {

    private ResourceBundle bundle;

    public ResourceBundle getBundle() {
        if (bundle == null) {
            FacesContext context = FacesContext.getCurrentInstance();
            bundle = context.getApplication().getResourceBundle(context, "labels");
        }
        return bundle;
    }

    public String getValue(String key) {

        String result = null;
        try {
            result = getBundle().getString(key);
        } catch (MissingResourceException e) {
            result = "???" + key + "??? not found";
        }
        return result;
    }
}
