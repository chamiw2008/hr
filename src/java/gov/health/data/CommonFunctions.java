/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Buddhika
 */
public class CommonFunctions {

    public static Date calculateFirstDayOfMonth(Date monthDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(monthDate);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }
}
