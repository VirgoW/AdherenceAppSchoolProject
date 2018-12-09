package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class AlarmSet extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    Context m_context;
    View m_view;
    DialogFragment datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        m_context = this;
        m_view = findViewById(android.R.id.content);
        Calendar calendar = Calendar.getInstance();

//Tratament continuu/fix switch

        Switch alarmDuration_switch = (Switch) findViewById(R.id.alarmDuration_switch);
        final TextView alarmsNumber_Text = (TextView) findViewById(R.id.alarmsNumber_Text);
        final Spinner treatmentLengthSpinner = (Spinner) findViewById(R.id.treatmentLengthSpinner);

            alarmDuration_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        alarmsNumber_Text.setVisibility(View.VISIBLE);
                        treatmentLengthSpinner.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        alarmsNumber_Text.setVisibility(View.GONE);
                        treatmentLengthSpinner.setVisibility(View.GONE);
                    }
                }
            });

//Date picker textView

        TextView alarm_startDate = (TextView) findViewById(R.id.startDateSelection);
        String currentDateString = DateFormat.getDateInstance().format(calendar.getTime());
        alarm_startDate.setText(currentDateString);

        alarm_startDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    datePicker = new DatePickerFragment();
                    datePicker.show(getSupportFragmentManager(),"date picker");

                }
            });

//Treatment radioGroup

        RadioGroup alarmFreq_radioGroup = (RadioGroup) findViewById(R.id.alarmFreq_RadioGroup);
        final LinearLayout checkboxes_layout = (LinearLayout) findViewById(R.id.checkboxes_layout);
        alarmFreq_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // This will get the radiobutton that has changed in its check state
                RadioButton weeklyRadioBtn = (RadioButton)group.findViewById(R.id.weeklyRadioBtn);
                // This puts the value (true/false) into the variable
                boolean isChecked = weeklyRadioBtn.isChecked();
                // If the radiobutton that has changed in check state is now checked...
                if (isChecked)
                {
                    checkboxes_layout.setVisibility(View.VISIBLE);
                }
                else
                {
                    checkboxes_layout.setVisibility(View.GONE);
                }
            }
        });







        Button saveAlarm = (Button) findViewById(R.id.alarmSetButton);
        saveAlarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                TrySaveAlarm();
            }
        });
    }

    @Override
    public void onDateSet (DatePicker view , int year, int month, int dayOfMonth){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String setDateString = DateFormat.getDateInstance().format(c.getTime());
        TextView alarm_startDate = (TextView) findViewById(R.id.startDateSelection);
        alarm_startDate.setText(setDateString);
    }


    private void TrySaveAlarm()
    {
        TextView medName = (TextView) m_view.findViewById(R.id.medNameText);
        if(medName.getText().toString().isEmpty())
        {
            TextView medNameError = (TextView ) m_view.findViewById(R.id.errorMedName);
            medNameError.setVisibility(View.VISIBLE);
            ((ScrollView) m_view.findViewById(R.id.scroll_bar)).fullScroll(View.FOCUS_UP);
        }
        else
        {
            SaveAlarm();
            onBackPressed();
            finish();
        }
    }

    private void SaveAlarm()
    {
        SaveManager.getInstance().AddNewAlarm(m_view, m_context);
    }
}
