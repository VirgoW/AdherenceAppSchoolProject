package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static android.view.ViewGroup.*;

public class AlarmSet extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    Context m_context;
    View m_view;

    TextView currentSelectedHourPicker;
    String m_mode;
    int m_alarmIndex;
    int m_previousTabIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        m_context = this;
        m_view = findViewById(android.R.id.content);

        Intent myIntent = getIntent();
        m_mode = myIntent.getStringExtra(m_context.getString(R.string.alarmSetModeName));
        m_alarmIndex = myIntent.getIntExtra("index", -1);
        boolean editMode = m_mode.equals(m_context.getString(R.string.alarmModeEdit));
        if(editMode)
        {
            FillFormFromCache();
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


        //Tratament continuu/fix switch
        InitialiseSwitch(editMode);

        //Tratament fis spinner
        InitialiseTreatmentSpinner(editMode);

        //Date picker textView
        InitialiseDatePicker(editMode);

        //Treatment radioGroup
        InitialiseFrequencyRadioGroup(editMode);
        InitialiseCheckboxes(editMode);

        //Administrari pe zi spinner
        Initialise_perDaySpinner();

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

        m_previousTabIndex = myIntent.getIntExtra("tabIndex", 0);
    }

    //Methods
    private void InitialiseSwitch(boolean editMode)
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

    private void InitialiseTreatmentSpinner(boolean editMode)
    {
        Spinner treatmentLengthSpinner = (Spinner) findViewById(R.id.treatmentLengthSpinner);
        int nextSpinnerPosition = treatmentLengthSpinner.getSelectedItemPosition();
        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,R.array.Treatment_length,R.layout.custom_spinner_item);
        spinAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        treatmentLengthSpinner.setAdapter(spinAdapter);
        if(editMode)
        {
            treatmentLengthSpinner.setSelection(nextSpinnerPosition);
        }
    }

    private  void InitialiseDatePicker(boolean editMode)
    {
        TextView alarm_startDate = (TextView) findViewById(R.id.startDateSelection);
        if(editMode)
        {
            SimpleDateFormat format = new SimpleDateFormat(m_context.getText(R.string.dateFormat).toString());
            String startDate = format.format(SaveManager.getInstance().GetAlarm(m_alarmIndex).GetStartCal().getTime());
            alarm_startDate.setText(startDate);
        }
        else
        {
            Calendar c = Calendar.getInstance();
            String currentDateString = DateFormat.getDateInstance().format(c.getTime());
            alarm_startDate.setText(currentDateString);

        }
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

    private void InitialiseFrequencyRadioGroup(boolean editMode)
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
        if(editMode)
        {
            Alarm editAlarm = SaveManager.getInstance().GetAlarm(m_alarmIndex);
            int daily;
            if(editAlarm.IsDailyTreatment())
            {
                daily = R.id.dailyRadioBtn;
            }
            else
            {
                daily = R.id.weeklyRadioBtn;
            }
            alarmFreq_radioGroup.check(daily);
        }
    }

    private void InitialiseCheckboxes(boolean editMode)
    {
        LinearLayout checkboxesLay = (LinearLayout) findViewById(R.id.checkboxes_layout);
        int layout_padding = (int) getResources().getDimensionPixelSize(R.dimen.checkboxesLay_padding);
        checkboxesLay.setPadding(layout_padding,layout_padding,layout_padding,layout_padding);
        int checkbox_padding = (int) getResources().getDimensionPixelSize(R.dimen.chkText_off);
        for(int i = 0; i < checkboxesLay.getChildCount(); ++i)
        {
            final CheckBox checkbox = (CheckBox) checkboxesLay.getChildAt(i);
            checkbox.setButtonDrawable(R.drawable.checkbox);
            checkbox.setPadding(checkbox_padding,0,0,0);
            String[] chk_dayNames = getResources().getStringArray(R.array.dayNames);
            checkbox.setText(chk_dayNames[i]);
            checkbox.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.project_text_size));

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        checkbox.setBackgroundColor(getResources().getColor(R.color.custom_checkbox));
                    else
                        checkbox.setBackgroundColor(0);
                }
            });
        }

        if(editMode)
        {
            Alarm editAlarm = SaveManager.getInstance().GetAlarm(m_alarmIndex);
            if(!editAlarm.IsDailyTreatment())
            {
                for(int i=0; i<7;i++)
                {
                    ((CheckBox) checkboxesLay.getChildAt(i)).setChecked(editAlarm.m_weeklyDayFrequency.get(i));
                }
            }
        }
    }

    private void Initialise_perDaySpinner()
    {
        Spinner perDaySpinner = (Spinner) findViewById(R.id.freq_perDay_spinner);
        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this,R.array.perDay_freq,R.layout.custom_spinner_item);
        spinAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        perDaySpinner.setAdapter(spinAdapter);
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

                    int hourPicker_layout = (int) getResources().getDimensionPixelSize(R.dimen.alarm_set_vertical_padding);
                    setMargins(hourPicker,0,hourPicker_layout,0,0);

                    hourPicker.setHint(R.string.hour_picker_hint);
                    hourPicker.setTextSize(TypedValue.COMPLEX_UNIT_PX,getResources().getDimensionPixelSize(R.dimen.project_text_size));
                    hourPicker.setSingleLine(true);
                    hourPicker.setBackgroundResource(R.drawable.alarm_set_borders);
                    hourPicker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            currentSelectedHourPicker = hourPicker;
                            timePicker.show(getSupportFragmentManager(),"time picker");
                        }
                    });
                }

                if(m_mode.equals(m_context.getString(R.string.alarmModeEdit)))
                {
                    ArrayList<String> values = SaveManager.getInstance().GetAlarm(m_alarmIndex).m_dailyFrequency;
                    for (int i = 0; i < hourPickersLayout.getChildCount() ; i++)
                    {
                        if(values.size() <= i)
                        {
                            break;
                        }
                        TextView hourPicker = (TextView) hourPickersLayout.getChildAt(i);
                        hourPicker.setText(values.get(i));
                    }
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

    private void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof MarginLayoutParams)
        {
            MarginLayoutParams p = (MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    // on time set for TimePicker
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute)
    {
        Calendar currentTime = Calendar.getInstance();
        currentTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
        currentTime.set(Calendar.MINUTE, minute);
        SimpleDateFormat format = new SimpleDateFormat(m_context.getText(R.string.hourFormat).toString());
        String setTimeString = format.format(currentTime.getTime());
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
        }
    }

    private boolean IsFormValid() {
        boolean medNameValid = true;
        boolean dosageValid = true;
        boolean startDateValid = true;
        boolean hourPickersLayoutValid = true;
        boolean frequencyValid = true;
        boolean fixedTimeTreamentValid = false;
        TextView medName = (TextView) findViewById(R.id.medNameTextInput);
        TextView dosage = (TextView) findViewById(R.id.DosageInput_Text);
        TextView startDateSelection = (TextView) findViewById(R.id.startDateSelection);
        LinearLayout hourPickersLayout = (LinearLayout) findViewById(R.id.hourPickers_LinearLayout);
        RadioGroup alarmFrequency = (RadioGroup) findViewById(R.id.alarmFreq_RadioGroup);
        Switch fixedTimeTreatment = (Switch) findViewById(R.id.alarmDuration_switch);

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

        //Weekly  frequency - check for selected days > 0
        if(alarmFrequency.getCheckedRadioButtonId() != R.id.dailyRadioBtn)
        {
            LinearLayout dayPickerLayout = (LinearLayout) findViewById(R.id.checkboxes_layout);
            int numberOfCheckedDays = 0;
            for(int i=0; i < dayPickerLayout.getChildCount(); i++)
            {
                CheckBox dayPicker = (CheckBox) dayPickerLayout.getChildAt(i);
                if(dayPicker.isChecked())
                {
                    numberOfCheckedDays++;
                }
            }
            frequencyValid = numberOfCheckedDays > 0;
        }
        if(fixedTimeTreatment.isChecked())
        {
            String alarmNumberText = ((TextView) findViewById(R.id.alarmsNumber_Text)).getText().toString();
            if(alarmNumberText.isEmpty())
            {
                fixedTimeTreamentValid = false;
            }
            else
            {
                fixedTimeTreamentValid = true;
            }
        }
        else
        {
            fixedTimeTreamentValid = true;
        }

        if(medNameValid && dosageValid && startDateValid && hourPickersLayoutValid && frequencyValid && fixedTimeTreamentValid)
        {
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
    }

    private void FillFormFromCache()
    {
        Alarm editAlarm = SaveManager.getInstance().GetAlarm(m_alarmIndex);
        TextView medName = (TextView) findViewById(R.id.medNameTextInput);
        TextView dosage = (TextView) findViewById(R.id.DosageInput_Text);
        TextView notes = (TextView) findViewById(R.id.other_details);
        TextView startDate = (TextView) findViewById(R.id.startDateSelection);
        RadioGroup alarmFrequency = (RadioGroup) findViewById(R.id.alarmFreq_RadioGroup);


        medName.setText(editAlarm.GetMedName());
        dosage.setText(editAlarm.GetDosage());
        final Spinner perDaySpinner = (Spinner) findViewById(R.id.freq_perDay_spinner);
        perDaySpinner.setSelection(editAlarm.m_dailyFrequency.size() - 1);
        notes.setText(editAlarm.GetNote());
        SimpleDateFormat format = new SimpleDateFormat(m_context.getText(R.string.dateFormat).toString());
        String startDateCal = format.format(editAlarm.GetStartCal().getTime());
        startDate.setText(startDateCal);

        //Fixed or Continuous treatment
        Switch fixedTimeTreatment = (Switch) findViewById(R.id.alarmDuration_switch);
        TextView alarmsNumber_Text = (TextView) findViewById(R.id.alarmsNumber_Text);
        Spinner treatmentLengthSpinner = (Spinner) findViewById(R.id.treatmentLengthSpinner);

        fixedTimeTreatment.setChecked(editAlarm.IsFixedTimeTreatment());
        if(fixedTimeTreatment.isChecked())
        {
            alarmsNumber_Text.setVisibility(View.VISIBLE);
            treatmentLengthSpinner.setVisibility(View.VISIBLE);
            alarmsNumber_Text.setText(Integer.toString(editAlarm.GetFixedFrequencyNumber()));
            treatmentLengthSpinner.setSelection(editAlarm.GetFixedFrequencySpinnerPosition());
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("tabIndex", m_previousTabIndex);
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }
}
