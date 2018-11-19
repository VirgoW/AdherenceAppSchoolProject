package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class AlarmsPage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Context m_context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_page);
        m_context = this;

        Button setAlarm_btn = (Button) findViewById(R.id.setAlarm_btn);
        setAlarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setAlarm = new Intent(getApplicationContext(), AlarmSet.class);
                startActivity(setAlarm);

                //DialogFragment timePicker = new TimePickerFragment();
                //timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button saveTestTextBtn = (Button) findViewById(R.id.testTextInputButton);
        Button loadTestTextBtn = (Button) findViewById(R.id.testTextOutputButton);
        saveTestTextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                SaveValueToFile();
            }
        });
        loadTestTextBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                LoadValueFromFile();
            }
        });
    }

    public void SaveValueToFile()
    {
        SaveManager.getInstance().SaveData(this.findViewById(android.R.id.content), m_context);

        //alarmTriggeredNumber ++;
        //alarmDrugConsumedConfirmedNumber ++
        /*
        struct
        {
         Name
         Hour

         Frequency
            Day
            Week
            Month
            Other combo

         For how long
            always
                or
           number of
                    Days
                    Weeks
                    Months
                    Years
            }
         */

    }

    public void LoadValueFromFile()
    {
        SaveManager.getInstance().LoadData(this.findViewById(android.R.id.content), m_context);
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
