package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TimePicker;

public class AlarmsPage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    Context m_context;
    View m_view;
    GridView gridView;
    AlarmAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_page);
        m_context = this;
        m_view = findViewById(android.R.id.content);
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

        gridView = (GridView) findViewById(R.id.gridViewTest);
        adapter = new AlarmAdapter(this);
        gridView.setAdapter(adapter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }
}
