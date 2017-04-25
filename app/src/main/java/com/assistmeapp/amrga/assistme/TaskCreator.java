package com.assistmeapp.amrga.assistme;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;

import com.assistmeapp.amrga.assistme.model.Alarm;
import com.assistmeapp.amrga.assistme.model.AlarmInfo;
import com.assistmeapp.amrga.assistme.model.DbHelper;

/**
 * Created by amrga on 9/12/2016.
 */
public class TaskCreator {

    private  String title, message, location, action, rule, interval;
    private int minute, hour, day, month, year, dueDay, dueMonth, dueYear, tag;
    private boolean repeat;

    public TaskCreator() {
    }

    public void setAttributes(String... args) {
        title = args[0];
        message = args[1];
        String[] timeArray = args[2].split(":");
        minute = Integer.parseInt(timeArray[1]);
        hour = Integer.parseInt(timeArray[0]);
        String[] dateArray = args[3].split("/");
        day = Integer.parseInt(dateArray[2]);
        month = Integer.parseInt(dateArray[1]);
        year = Integer.parseInt(dateArray[0]);
        action = args[4];
        location = args[5];
        tag = Integer.parseInt(args[6]);
        repeat = Boolean.parseBoolean(args[7]);
        if(repeat) {
            String[] dueDate = args[8].split("/");
            dueDay = Integer.parseInt(dueDate[2]);
            dueMonth = Integer.parseInt(dueDate[1]);
            dueYear = Integer.parseInt(dueDate[0]);
            rule = args[9];
            interval = args[10];
        }
    }

    public long createTask() {
        Alarm alarm = new Alarm();
        AlarmInfo info = new AlarmInfo();
        alarm.setTitle(title);
        long alarmId = 0;
        alarm.setFromDate(DbHelper.getDateStr(year, month, day));
        if(repeat) {
            alarm.setToDate(DbHelper.getDateStr(dueYear, dueMonth, dueDay));
            if (rule != null) {
                alarm.setRule(rule);
            }
            else {
                alarm.setInterval(interval);
            }
        }
        alarmId = alarm.persist(ContainerActivity.db);
        info.setAt(DbHelper.getTimeStr(hour, minute));
        info.setMessage(message);
        info.setLocation(location);
        info.setAction(action);
        info.setAlarmId(alarmId);
        info.setTag(tag);
        info.persist(ContainerActivity.db);
        return alarmId;
    }

}
