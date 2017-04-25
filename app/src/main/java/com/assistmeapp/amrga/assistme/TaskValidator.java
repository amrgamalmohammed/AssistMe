package com.assistmeapp.amrga.assistme;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by amrga on 9/11/2016.
 */
public class TaskValidator {

    private  String title, message, time, date, dueDate,
                        location, tag, action, rule, interval, repeat, error;

    public TaskValidator(String... args) {
        title = args[0];
        message = args[1];
        time = args[2];
        date = args[3];
        dueDate = args[4];
        location = args[5];
        action = args[6];
        tag = args[7];
        rule = args[8];
        interval = args[9];
        repeat = args[10];
    }

    public boolean validate() {
        if (title == null) {error="Title field cannot be empty!"; return false;}
        if (message == null) {error="Message field cannot be empty!"; return false;}
        if (time.equals("0:0")) {error="Forgot to specify time!"; return false;}
        if (date.equals("0-0-0")) {error="Forgot to specify date!"; return false;}
        if (isDateExpired()) {error="This task is already expired!"; return false;}
//        if (location == null) {error="You should specify a location!"; return false;}
//        if (action == null) {error="You should pick an action!"; return false;}
        if (Boolean.valueOf(repeat)) {
            if (rule == null && interval == null) {
                return false;
            }
            if (dueDate.equals("0-0-0")) {
                error="Due date has to specified for repeating!";
                return false;
            }
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date from = sdf.parse(date);
                Date to = sdf.parse(dueDate);
                if (from.after(to)) {
                    error="Due date is before original date!";
                    return false;
                }
            }
            catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    private boolean isDateExpired() {
        Calendar cal = Calendar.getInstance();
        long today = cal.getTimeInMillis();
        String[] timeOfDay = time.split(":");
        String[] dateOfDay = date.split("-");
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateOfDay[2]));
        cal.set(Calendar.MONTH, Integer.parseInt(dateOfDay[1]));
        cal.set(Calendar.YEAR, Integer.parseInt(dateOfDay[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeOfDay[1]));
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeOfDay[0]));
        long test = cal.getTimeInMillis();
        return test < today;
    }

    public String getError() {
        return this.error;
    }

}
