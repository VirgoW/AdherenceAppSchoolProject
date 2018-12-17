package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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
import android.widget.TimePicker;

import java.text.DateFormat;
import java.util.Calendar;

public class AlarmSet extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Context m_context;
    View m_view;

    TextView currentSelectedHourPicker;
    String m_mode;
    int m_alarmIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        m_context = this;
        m_view = findViewById(android.R.id.content);

        //TODO add mode

        //Tratament continuu/fix switch
        InitialiseSwitch();

        //Date picker textView
        InitialiseDatePicker();

        //Treatment radioGroup
        InitialiseFrequencyRadioGroup();

        //Hour pickers
        InitialiseHourPickerLayout();

        //Save alarm button

        Button saveAlarm = (Button) findViewById(R.id.alarmSetButton);
        saveAlarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                TrySaveAlarm();
            }
        });

        Intent myIntent = getIntent();
        m_mode = myIntent.getStringExtra(m_context.getString(R.string.alarmSetModeName));
        m_alarmIndex = myIntent.getIntExtra("index", -1);
        if(m_mode.equals(m_context.getString(R.string.alarmModeEdit)))
        {
            Button deleteAlarm = (Button) findViewById(R.id.alarmDeleteButton);
            deleteAlarm.setVisibility(View.VISIBLE);
            deleteAlarm.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v) {
                    TryDeleteAlarm();
                }
            });
        }
    }

    //Methods
    private void InitialiseSwitch()
    {
        Switch alarmDuration_switch = (Switch) findViewById(R.id.alarmDuration_switch);
        final TextView alarmsNumber_Text = (TextView) findViewById(R.id.alarmsNumber_Text);
        final Spinner treatmentLengthSpinner = (Spinner) findViewById(R.id.treatmentLengthSpinner);

        alarmDuration_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    alarmsNumber_Text.setVisibility(View.VISIBLE);
                    treatmentLengthSpinner.setVisibility(View.VISIBLE);
                } else {
                    alarmsNumber_Text.setVisibility(View.GONE);
                    treatmentLengthSpinner.setVisibility(View.GONE);
                }
            }
        });
    }

    private  void InitialiseDatePicker()
    {
        TextView alarm_startDate = (TextView) findViewById(R.id.startDateSelection);
        alarm_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment datePicker = new DatePickerFragment();
                datePicker.show(getSupportFragmentManager(),"date picker");

            }
        });
    }

    //on Date set for DatePicker
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

    private void InitialiseFrequencyRadioGroup()
    {
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
    }

    private void InitialiseHourPickerLayout()
    {
        final LinearLayout hourPickersLayout = (LinearLayout) findViewById(R.id.hourPickers_LinearLayout);
        final Spinner perDaySpinner = (Spinner) findViewById(R.id.freq_perDay_spinner);
        final DialogFragment timePicker = new TimePickerFragment();

        perDaySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ClearLayout(hourPickersLayout);

                int perDayAlarms = perDaySpinner.getSelectedItemPosition();
                for (int i = 0; i <= perDayAlarms ; i++)
                {
                    final TextView hourPicker = new TextView(m_context);
                    hourPickersLayout.addView(hourPicker);
                    //hourPicker.setText(R.string.hour_picker_hint);
                    hourPicker.setHint(R.string.hour_picker_hint);
                   // int hourPickerId = i+1;
                   // hourPicker.setId(hourPickerId);
                    hourPicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentSelectedHourPicker = hourPicker;
                            timePicker.show(getSupportFragmentManager(),"time picker");
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //Do nothing
            }
        });
    }

    private void ClearLayout(LinearLayout linearLayout)
    {
        if (linearLayout.getChildCount() > 0)
        {
            linearLayout.removeAllViews();
        }
    }

    // on time set for TimePicker
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        currentTime.set(Calendar.MINUTE, minute);
        String setTimeString = DateFormat.getTimeInstance(DateFormat.SHORT).format(currentTime.getTime());
        currentSelectedHourPicker.setText(setTimeString);
    }


    //Save alarm method

    private void TrySaveAlarm()
    {
        if(!IsFormValid())
        {
            ((ScrollView) m_view.findViewById(R.id.scroll_bar)).fullScroll(View.FOCUS_UP);
        }
        else
        {
            if(m_mode.equals(m_context.getString(R.string.alarmModeEdit)))
            {
                SaveAlarm();
            }
            else
            {
                AddNewAlarm();
            }

            onBackPressed();
            finish();
        }
    }

    private boolean IsFormValid() {
        boolean medNameValid = true;
        boolean dosageValid = true;
        boolean startDateValid = true;
        boolean hourPickersLayoutValid = true;
        TextView medName = (TextView) findViewById(R.id.medNameText);
        TextView dosage = (TextView) findViewById(R.id.DosageInput_Text);
        TextView startDateSelection = (TextView) findViewById(R.id.startDateSelection);
        LinearLayout hourPickersLayout = (LinearLayout) findViewById(R.id.hourPickers_LinearLayout);

        if (medName.getText().toString().isEmpty()) {
            medNameValid = false;
        }
        if (dosage.getText().toString().isEmpty()) {
            dosageValid = false;
        }
        if (startDateSelection.getText().toString().isEmpty())
        {
            startDateValid = false;
        }
        //TODO Hour(s)
        //Is checking every child of the layout.
        // If one of them is empty, the hourPickerLayoutValid becomes false
        for (int i = 0; i < hourPickersLayout.getChildCount(); i++)
        {
            TextView hourPicker = (TextView) hourPickersLayout.getChildAt(i);
            if(hourPicker.getText().toString().isEmpty())
            {
                hourPickersLayoutValid = false;
                break;
            }
        }


        if(medNameValid && dosageValid && startDateValid && hourPickersLayoutValid) {
            return true;
        }
        else
        {
            TextView formValidError = (TextView) m_view.findViewById(R.id.errorInvalidForm);
            formValidError.setVisibility(View.VISIBLE);
            return false;
        }
    }

    private void AddNewAlarm()
    {
        SaveManager.getInstance().AddNewAlarm(m_context, m_view);
    }
    private void SaveAlarm()
    {
        SaveManager.getInstance().SaveAlarm(m_alarmIndex, m_context, m_view);
    }

    private void TryDeleteAlarm()
    {
        SaveManager.getInstance().DeleteAlarm(m_alarmIndex, m_context);
        onBackPressed();
        finish();
    }
}
