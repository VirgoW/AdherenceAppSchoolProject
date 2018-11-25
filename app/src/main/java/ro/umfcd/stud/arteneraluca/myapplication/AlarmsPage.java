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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class AlarmsPage extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
//TODO Check if AppCompatActivity is ok
    Context m_context;
    View m_view;

    DayFragmentAdapter m_alarmPageAdapter;
    ViewPager m_dayFragmentViewer;
    TabLayout m_tabLayout;

    Calendar m_cal;

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


        ImageButton prevMonth = (ImageButton) findViewById(R.id.prevMonth);
        ImageButton nextMonth = (ImageButton) findViewById(R.id.nextMonth);

        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Test(-1);
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Test(1);
            }
        });

        SetupView();
        //TODO Improve cal
        m_cal = Calendar.getInstance();
        Test(0);
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
        //Get the ViewPager and set the adapter so it can display items
        m_dayFragmentViewer = (ViewPager) findViewById(R.id.viewpager);
        m_alarmPageAdapter = new DayFragmentAdapter(getSupportFragmentManager(), m_context);
        m_dayFragmentViewer.setAdapter(m_alarmPageAdapter);

        //Populate the adapter data with days name and value
        AddDayTabs(m_dayFragmentViewer);

        //Give the TabLayout the ViewPager to use
        m_tabLayout = (TabLayout) findViewById(R.id.topTabLayout);
        m_tabLayout.setupWithViewPager(m_dayFragmentViewer);

        for(int index = 0; index < 7; index ++)
        {
            TabLayout.Tab tab = m_tabLayout.getTabAt(index);
            tab.setCustomView(m_alarmPageAdapter.getTabView(index));
        }
    }

    private void AddDayTabs(ViewPager viewPager)
    {
        String[] dayNames = getResources().getStringArray(R.array.dayNames);
        for(int index = 0; index < 7; index ++)
        {
            m_alarmPageAdapter.addDayTitle(new DayFragment(), dayNames[index], Integer.toString(index));
        }
    }


    private void Test(int plus)
    {
        int calDate;
        m_cal.add(Calendar.MONTH, plus);
        String[] monthNames = getResources().getStringArray(R.array.monthNames);
        calDate = m_cal.get(Calendar.MONTH);
        ((TextView) findViewById(R.id.testDate)).setText(monthNames[calDate]);

        //LocalDate date;
        //Date.from(ZonedDateTime.now().minusMonths(1).toInstant());
        //String strDateFormat = "M";
        //SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
    }
}
