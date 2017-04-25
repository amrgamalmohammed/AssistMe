package com.assistmeapp.amrga.assistme;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.assistmeapp.amrga.assistme.model.Alarm;
import com.assistmeapp.amrga.assistme.model.AlarmInfo;
import com.assistmeapp.amrga.assistme.model.AlarmRepeater;

import java.util.Calendar;

public class TaskNotification extends AppCompatActivity {

    private ImageView notificationTag;
    private TextView notificationTitle, notificationMessage, notificationLocation,
                        notificationAction, notificationAt, notificationDateTime;
    private Button notificationDismiss;
    private PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_notification);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ON_AFTER_RELEASE, "tag");
        wakeLock.acquire();

        Intent intent = getIntent();
        String title = intent.getStringExtra(Alarm.COL_TITLE);
        String message = intent.getStringExtra(AlarmInfo.COL_MESSAGE);
        String action = intent.getStringExtra(AlarmInfo.COL_ACTION);
        String location = intent.getStringExtra(AlarmInfo.COL_LOCATION);
        String tag = intent.getStringExtra(AlarmInfo.COL_TAG);
        String dateTime = intent.getStringExtra(AlarmRepeater.COL_DATETIME);

        location = location==null ? "Not specified" : location;
        action = action==null ? "Not specified" : action;

        notificationTag = (ImageView) findViewById(R.id.notification_tag);
        notificationTitle = (TextView) findViewById(R.id.notification_title);
        notificationMessage = (TextView) findViewById(R.id.notification_message);
        notificationLocation = (TextView) findViewById(R.id.notification_location);
        notificationAction = (TextView) findViewById(R.id.notification_action);
        notificationAt = (TextView) findViewById(R.id.notification_at);
        notificationDateTime = (TextView) findViewById(R.id.notification_dateTime);
        notificationDismiss = (Button) findViewById(R.id.notification_dismiss);
        notificationDismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        ActionFilter actionFilter = new ActionFilter(this);
        actionFilter.setAction(action);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(Long.valueOf(dateTime));
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        String min = minute < 10 ? "0"+String.valueOf(minute) : String.valueOf(minute);
        String time = hour > 12 ? String.valueOf(hour-12)+":"+min+" PM" :
                String.valueOf(hour)+":"+min+" AM";
        String date = cal.get(Calendar.YEAR)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.DAY_OF_MONTH);

        notificationTitle.setText(title);
        notificationMessage.setText(message);
        notificationLocation.setText(location);
        notificationAction.setText(action);
        notificationAt.setText(time);
        notificationDateTime.setText(date);
        notificationTag.setImageResource(Integer.parseInt(tag));

        if(actionFilter.executeAction()) {
            Toast.makeText(this, "Action executed successfully", Toast.LENGTH_LONG).show();
        }
        else {
            Toast.makeText(this, "Action was not executed", Toast.LENGTH_LONG).show();
        }

        wakeLock.release();

    }

}
