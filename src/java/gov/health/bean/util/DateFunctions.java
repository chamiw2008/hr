/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.health.bean.util;

import java.util.Calendar;

/**
 *
 * @author Chaminda
 */
public class DateFunctions {

    public static int getCurrentYear() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; //zero-based  
        System.out.println("year = " + year + "\nmonth = " + month);
        return year;
    }

    public static int getCurrentMonth() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1; //zero-based  
        System.out.println("year = " + year + "\nmonth = " + month);
        return month;
    }
}
