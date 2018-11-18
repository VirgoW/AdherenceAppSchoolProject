package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Debug;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

public class AlarmsPage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_page);

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
        String testTxtToSave = ((TextView)findViewById(R.id.testTextInput)).getText().toString();
        String filename = getText(R.string.testSave).toString();
        try
        {
            FileOutputStream fo = openFileOutput(filename, MODE_PRIVATE);
            fo.write(testTxtToSave.getBytes());
            fo.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }



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
        String filename = getText(R.string.testSave).toString();
        String content = "";
        try
        {
            FileInputStream fs = openFileInput(filename);
            Scanner scan = new Scanner(fs);
            content = scan.next();
            scan.close();
            System.out.println(" Content of file is : " + content);
        }
        catch   (Exception e)
        {
            System.out.println(e);
        }
        if(!content.isEmpty())
        {
            ((TextView) findViewById(R.id.testOutputText)).setText(content);
        }
    }

    public void Test()
    {

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
