package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

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




    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
