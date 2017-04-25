package com.assistmeapp.amrga.assistme;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.assistmeapp.amrga.assistme.model.AlarmInfo;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.Calendar;

public class NewTaskActivity extends AppCompatActivity implements PlaceSelectionListener{


    private static Toolbar toolbar;
    private static EditText editTitle, editMessage;
    private static TextView timeText, dateText, locationText, actionText, dueDateText, repeatingText;
    private static LinearLayout timePicker, datePicker, actionPicker, dueDatePicker;
    private static ImageView taskThumbnail;
    private static FloatingActionButton submit;
    private static CoordinatorLayout newTaskCoordinator;
    private static SwitchCompat repeatSwitch;

    private static final int TIME_PICKER_DIALOG = 0;
    private static final int DATE_PICKER_DIALOG = 1;
    private static final int DUE_DATE_PICKER_DIALOG = 2;
    private static int dayMinute, dayHour, day, month, year, dueDay, dueMonth, dueYear;
    private static String title, message, location, action, tag, rule, interval, error;
    private static boolean repeat;

    private static Context context;

    private static ActionsFragment actionsFragment;
    private static TagsFragment tagsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);


        setTitle("New Task");

        context = this;

        repeat = false;

        title=null; message=null; location=null; action=null; tag=null; rule=null; interval=null; error=null;

        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);

        newTaskCoordinator = (CoordinatorLayout) findViewById(R.id.new_task_coordinator);

        editTitle = (EditText) findViewById(R.id.edit_title);
        editTitle.addTextChangedListener(new TextInputChangedListener(editTitle));
        editTitle.setOnFocusChangeListener(new FocusChangeListener());
        editMessage = (EditText) findViewById(R.id.edit_message);
        editMessage.addTextChangedListener(new TextInputChangedListener(editMessage));
        editMessage.setOnFocusChangeListener(new FocusChangeListener());

        timeText = (TextView) findViewById(R.id.time_text);
        dateText = (TextView) findViewById(R.id.date_text);
        locationText = (TextView) findViewById(R.id.location_text);
        actionText = (TextView) findViewById(R.id.action_text);
        dueDateText = (TextView) findViewById(R.id.due_date_text);

        repeatingText = (TextView) findViewById(R.id.repeat_text);

        timePicker = (LinearLayout) findViewById(R.id.time_picker);
        timePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(TIME_PICKER_DIALOG);
            }
        });

        datePicker = (LinearLayout) findViewById(R.id.date_picker);
        datePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(DATE_PICKER_DIALOG);
            }
        });

        taskThumbnail = (ImageView) findViewById(R.id.task_thumbnail);
        taskThumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagsFragment = new TagsFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(tagsFragment, "fragment_dialog");
                fragmentTransaction.commit();
            }
        });

        actionPicker = (LinearLayout) findViewById(R.id.action_picker);
        actionPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionsFragment = new ActionsFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(actionsFragment, "fragment_dialog");
                fragmentTransaction.commit();
            }
        });

        dueDatePicker = (LinearLayout) findViewById(R.id.due_date_picker);
        dueDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeat) {
                    showDialog(DUE_DATE_PICKER_DIALOG);
                }
            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(this);

        repeatSwitch = (SwitchCompat) findViewById(R.id.repeat_switch);
        repeatSwitch.setOnCheckedChangeListener(new CustomCheckedListener());

        submit = (FloatingActionButton) findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tag==null) {tag=String.valueOf(R.drawable.ic_notifications);}
                TaskValidator taskValidator = new TaskValidator(title,message,String.valueOf(dayHour)+
                        ":"+String.valueOf(dayMinute),String.valueOf(year)+"-"+String.valueOf(month)+
                        "-"+String.valueOf(day),String.valueOf(dueYear)+"-"+String.valueOf(dueMonth)+
                        "-"+String.valueOf(dueDay),location,action,tag,rule,interval,String.valueOf(repeat));
                if (taskValidator.validate()) {
                    TaskCreator creator = new TaskCreator();
                    creator.setAttributes(title, message, dayHour+":"+dayMinute, year+"/"+(month+1)+"/"+day,
                            action, location, tag, String.valueOf(repeat),
                            dueYear+"/"+(dueMonth+1)+"/"+dueDay, rule, interval);
                    long alarmId = creator.createTask();
                    Intent service = new Intent(context, TaskService.class);
                    service.setAction(TaskService.POPULATE);
                    service.putExtra(AlarmInfo.COL_ALARMID, String.valueOf(alarmId));
                    startService(service);
                    finish();
                }
                else {
                    error = taskValidator.getError();
                    Snackbar snackbar = Snackbar
                            .make(newTaskCoordinator, error, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            }
        });

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar cal = Calendar.getInstance();
        switch (id){
            case TIME_PICKER_DIALOG :
                return new TimePickerDialog(this, timeSetListener, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false);
            case DATE_PICKER_DIALOG :
                return new DatePickerDialog(this,dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            case DUE_DATE_PICKER_DIALOG :
                return new DatePickerDialog(this,dueDateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        }
        return null;
    }

    protected TimePickerDialog.OnTimeSetListener timeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    dayHour = hourOfDay;
                    dayMinute = minute;
                    String min = dayMinute < 10 ? "0"+String.valueOf(dayMinute) : String.valueOf(dayMinute);
                    if (dayHour > 12) {
                        timeText.setText(dayHour-12+":"+min+"  PM");
                    }
                    else {
                        timeText.setText(dayHour+":"+min+"  AM");
                    }
                }
            };

    protected DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int thisYear, int thisMonth, int thisDay) {
                    year = thisYear;
                    month = thisMonth;
                    day = thisDay;
                    dateText.setText(year+"/"+(month+1)+"/"+day);
                }
            };

    protected DatePickerDialog.OnDateSetListener dueDateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int thisYear, int thisMonth, int thisDay) {
                    dueYear = thisYear;
                    dueMonth = thisMonth;
                    dueDay = thisDay;
                    dueDateText.setText(dueYear+"/"+(dueMonth+1)+"/"+dueDay);
                }
            };

    @Override
    public void onPlaceSelected(Place place) {

        location = new StringBuilder().append(place.getAddress()).toString();
        locationText.setText(location);
    }

    @Override
    public void onError(Status status) {

        Toast.makeText(this, "Place selection failed: " + status.getStatusMessage(),
                Toast.LENGTH_SHORT).show();
    }

    private class CustomCheckedListener implements CompoundButton.OnCheckedChangeListener{

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                RepeatingFragment repeatingFragment = new RepeatingFragment();
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.add(repeatingFragment, "fragment_repeating");
                fragmentTransaction.commit();
            }
            else {
                rule = null;
                interval = null;
                repeatingText.setText("");
                repeat = false;
            }
        }
    }

    private class TextInputChangedListener implements TextWatcher {

        private View view;

        TextInputChangedListener(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.edit_title :
                    title = editTitle.getText().toString();
                    break;
                case R.id.edit_message :
                    message = editMessage.getText().toString();
                    break;
            }
        }
    }

    private class FocusChangeListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        }
    }

    public static void setActionText (String actionString) {
        action = actionString;
        actionText.setText(action);
        actionsFragment.dismiss();
    }

    public static void setTag (int image, String titleText) {
        tag = String.valueOf(image);
        taskThumbnail.setImageResource(image);
        title = titleText;
        editTitle.setText(title);
        tagsFragment.dismiss();
    }
    public static void setRepeatSwitch(boolean flag) {
        repeatSwitch.setChecked(flag);
    }
    public static boolean getRepeat() {return repeat;}
    public static void setRepeat(boolean flag) {repeat = flag;}
    public static void setRule(String ruleString) {interval=null; rule=ruleString;}
    public static void setInterval(String intervalString) {rule=null; interval=intervalString;}
    public static void setRepeatingText(String text) {repeatingText.setText(text);}
}
