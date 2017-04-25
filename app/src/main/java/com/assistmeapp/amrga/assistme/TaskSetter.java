package com.assistmeapp.amrga.assistme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.StringBuilderPrinter;

import com.assistmeapp.amrga.assistme.model.AlarmInfo;

/**
 * Created by amrga on 9/17/2016.
 */
public class TaskSetter extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, TaskService.class);
        service.setAction(TaskService.CREATE);
        service.putExtra(AlarmInfo.COL_ALARMID, String.valueOf(-1));
        context.startService(service);
    }
}
