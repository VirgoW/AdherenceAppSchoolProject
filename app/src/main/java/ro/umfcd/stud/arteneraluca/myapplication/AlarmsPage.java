package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

public class AlarmsPage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
//TODO Check if AppCompatActivity is ok
    Context m_context;
    View m_view;
    DayFragmentAdapter m_alarmPageAdapter;
    ViewPager m_dayFragmentViewer;
    TabLayout m_tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarms_page);
        m_context = this;
        m_view = findViewById(android.R.id.content);
        Button setAlarm_btn = (Button) findViewById(R.id.alarmSetButton);
        setAlarm_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setAlarm = new Intent(getApplicationContext(), AlarmSet.class);
                startActivity(setAlarm);

                //DialogFragment timePicker = new TimePickerFragment();
                //timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        SetupView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        m_alarmPageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0)
        {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    public void onBackPressed()
    {
        moveTaskToBack(true); // Idk what it does exactly, but i think it puts the app in the background, which is exactly what i want the functionality to be.
    }


    private void SetupView()
    {
        m_dayFragmentViewer = (ViewPager) findViewById(R.id.viewpager);
        AddDayTabs(m_dayFragmentViewer);

        m_tabLayout = (TabLayout) findViewById(R.id.topTabLayout);
        m_tabLayout.setupWithViewPager(m_dayFragmentViewer);
    }

    private void AddDayTabs(ViewPager viewPager)
    {
        m_alarmPageAdapter = new DayFragmentAdapter(getSupportFragmentManager());
        String[] dayNames = getResources().getStringArray(R.array.dayNames);
        for(int index = 0; index < 7; index ++)
        {
            m_alarmPageAdapter.addDayTitle(new DayFragment(), dayNames[index]);
        }
        viewPager.setAdapter(m_alarmPageAdapter);
    }
}
