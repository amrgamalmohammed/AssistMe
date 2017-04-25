package com.assistmeapp.amrga.assistme;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.security.acl.Group;

/**
 * Created by amrga on 9/10/2016.
 */
public class RepeatingFragment extends DialogFragment {

    private Spinner firstSpinner, secondSpinner, thirdSpinner;
    private Button submit, dismiss;
    private SwitchCompat ruleSwitch, intervalSwitch;
    private TextInputLayout intervalMinutes, intervalHours, intervalDays, intervalMonths, intervalYears;
    private EditText intervalMinutesEdit, intervalHoursEdit, intervalDaysEdit, intervalMonthsEdit, intervalYearsEdit;
    private int minute, hour, day, month, year;
    private boolean rule, interval;
    private String firstRule, secondRule, thirdRule, ruleString, intervalString;

    public RepeatingFragment() {
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (!NewTaskActivity.getRepeat()) {
            NewTaskActivity.setRepeatSwitch(false);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_repeating, container, false);
        firstSpinner = (Spinner) view.findViewById(R.id.first_spinner);
        secondSpinner = (Spinner) view.findViewById(R.id.second_spinner);
        thirdSpinner = (Spinner) view.findViewById(R.id.third_spinner);
        ArrayAdapter<CharSequence> numbersAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.numbers_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> daysAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.days_array, android.R.layout.simple_spinner_item);
        ArrayAdapter<CharSequence> monthsAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.months_array, android.R.layout.simple_spinner_item);
        numbersAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstSpinner.setAdapter(numbersAdapter);
        secondSpinner.setAdapter(daysAdapter);
        thirdSpinner.setAdapter(monthsAdapter);
        firstSpinner.setEnabled(false);
        secondSpinner.setEnabled(false);
        thirdSpinner.setEnabled(false);

        ruleSwitch = (SwitchCompat) view.findViewById(R.id.rule_switch);
        ruleSwitch.setOnCheckedChangeListener(new CustomCheckedListener());
        intervalSwitch = (SwitchCompat) view.findViewById(R.id.interval_switch);
        intervalSwitch.setOnCheckedChangeListener(new CustomCheckedListener());

        submit = (Button) view.findViewById(R.id.submit_repeating);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repeating;
                if (rule) {
                    if (validateRule()) {
                        repeating = firstRule+" "+secondRule+" of "+thirdRule;
                        NewTaskActivity.setRule(ruleString);
                        NewTaskActivity.setRepeatingText(repeating);
                        NewTaskActivity.setRepeat(true);
                    }
                }
                else if (interval) {
                    if (validateInterval()) {
                        repeating = minute+" "+hour+" "+day+" "+month+" "+year;
                        NewTaskActivity.setInterval(repeating);
                        NewTaskActivity.setRepeatingText(intervalString);
                        NewTaskActivity.setRepeat(true);
                    }
                }
                dismiss();
            }
        });

        dismiss = (Button) view.findViewById(R.id.dismiss_repeating);
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        intervalMinutes = (TextInputLayout) view.findViewById(R.id.interval_minutes_input);
        intervalHours = (TextInputLayout) view.findViewById(R.id.interval_hours_input);
        intervalDays = (TextInputLayout) view.findViewById(R.id.interval_days_input);
        intervalMonths = (TextInputLayout) view.findViewById(R.id.interval_months_input);
        intervalYears = (TextInputLayout) view.findViewById(R.id.interval_years_input);

        intervalMinutesEdit = (EditText) view.findViewById(R.id.interval_minutes_edit);
        intervalHoursEdit = (EditText) view.findViewById(R.id.interval_hours_edit);
        intervalDaysEdit = (EditText) view.findViewById(R.id.interval_days_edit);
        intervalMonthsEdit = (EditText) view.findViewById(R.id.interval_months_edit);
        intervalYearsEdit = (EditText) view.findViewById(R.id.interval_years_edit);

        enableInterval(false);

        return view;
    }

    private class CustomCheckedListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.rule_switch :
                    if (isChecked) {
                        rule = true;
                        intervalSwitch.setChecked(false);
                        toggleSpinner(firstSpinner, true);
                        toggleSpinner(secondSpinner, true);
                        toggleSpinner(thirdSpinner, true);
                    }
                    else {
                        rule = false;
                        toggleSpinner(firstSpinner, false);
                        toggleSpinner(secondSpinner, false);
                        toggleSpinner(thirdSpinner, false);
                    }
                    break;
                case R.id.interval_switch :
                    if (isChecked) {
                        interval = true;
                        ruleSwitch.setChecked(false);
                        enableInterval(true);
                    }
                    else {
                        interval = false;
                        enableInterval(false);
                    }
                    break;
            }
        }

    }

    private void toggleSpinner(Spinner spinner, boolean flag) {
        spinner.getSelectedView().setEnabled(flag);
        spinner.setEnabled(flag);
    }
    private void enableInterval(boolean flag) {
        toggleInputView(intervalMinutes, flag);
        toggleInputView(intervalHours, flag);
        toggleInputView(intervalDays, flag);
        toggleInputView(intervalMonths, flag);
        toggleInputView(intervalYears, flag);

        toggleEditText(intervalMinutesEdit, flag);
        toggleEditText(intervalHoursEdit, flag);
        toggleEditText(intervalDaysEdit, flag);
        toggleEditText(intervalMonthsEdit, flag);
        toggleEditText(intervalYearsEdit, flag);
    }
    private void toggleInputView (TextInputLayout input, boolean flag) {
        input.setEnabled(flag);
    }
    private void toggleEditText (EditText edit, boolean flag) {
        edit.setEnabled(flag);
        if(flag) {edit.setTextColor(getResources().getColor(R.color.colorAccent));}
        else {edit.setTextColor(getResources().getColor(R.color.textDisabled));}
    }
    private boolean validateInterval () {
        try {
            minute = Integer.parseInt(intervalMinutesEdit.getText().toString());
            hour = Integer.parseInt(intervalHoursEdit.getText().toString());
            day = Integer.parseInt(intervalDaysEdit.getText().toString());
            month = Integer.parseInt(intervalMonthsEdit.getText().toString());
            year = Integer.parseInt(intervalYearsEdit.getText().toString());
            if (minute==0 && hour==0 && day==0 && month==0 && year==0) {
                return  false;
            }
            intervalString = "Every ";
            if(minute!=0) {intervalString+=(minute+" minute ");}
            if(hour!=0) {intervalString+=(hour+" hour ");}
            if(day!=0) {intervalString+=(day+" day ");}
            if(month!=0) {intervalString+=(month+" month ");}
            if(year!=0) {intervalString+=(year+" year ");}
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
    private boolean validateRule () {
        try {
            firstRule = firstSpinner.getSelectedItem().toString();
            secondRule = secondSpinner.getSelectedItem().toString();
            thirdRule = thirdSpinner.getSelectedItem().toString();
            int spinnerOne = firstSpinner.getSelectedItemPosition();
            int spinnerTwo = secondSpinner.getSelectedItemPosition();
            int spinnerThree = thirdSpinner.getSelectedItemPosition();
            ruleString = spinnerOne+" "+spinnerTwo+" "+spinnerThree;
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

}
