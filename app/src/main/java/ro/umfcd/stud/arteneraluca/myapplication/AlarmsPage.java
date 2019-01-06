package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
    int m_lastTabIndex;

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
                setAlarm.putExtra(m_context.getString(R.string.alarmSetModeName), m_context.getString(R.string.alarmModeNew));
                setAlarm.putExtra("tabIndex", m_tabLayout.getSelectedTabPosition());
                startActivityForResult(setAlarm, 1);

                //DialogFragment timePicker = new TimePickerFragment();
                //timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Button resetToTodayBtn = (Button) findViewById(R.id.resetToCurrentDay);
        resetToTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InstantiateCalToCurrentDay();
            }
        });

        ImageButton prevMonth = (ImageButton) findViewById(R.id.prevMonth);
        ImageButton nextMonth = (ImageButton) findViewById(R.id.nextMonth);
        ImageButton prevWeek = (ImageButton) findViewById(R.id.previousWeek);
        ImageButton nextWeek = (ImageButton) findViewById(R.id.nextWeek);

        prevMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMonth(-1);
                UpdateFragments();
            }
        });
        nextMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMonth(1);
                UpdateFragments();
            }
        });

        prevWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWeek(-1);
                UpdateFragments();
            }
        });

        nextWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddWeek(1);
                UpdateFragments();
            }
        });


        m_cal = Calendar.getInstance();
        SetupView();

        m_lastTabIndex = SaveManager.getInstance().GetDayOfWeek(m_cal);
    }

    @Override
    public void onResume() {
        super.onResume();
        UpdateFragments();

        TabLayout.Tab tab = m_tabLayout.getTabAt(m_lastTabIndex);
        tab.select();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true); // Idk what it does exactly, but i think it puts the app in the background, which is exactly what i want the functionality to be.
    }

    private void SetupView() {
        //Get the ViewPager so it can display items
        m_dayFragmentViewer = (ViewPager) findViewById(R.id.viewpager);
        //Give the TabLayout the ViewPager to use
        m_tabLayout = (TabLayout) findViewById(R.id.topTabLayout);
        InstantiateCalToCurrentDay();
    }

    private void AddMonth(int plus) {
        int calDay;
        m_cal.add(Calendar.MONTH, plus);
        calDay = m_cal.get(Calendar.DAY_OF_MONTH);
        m_cal.add(Calendar.DAY_OF_MONTH, -calDay + 1);
        //LocalDate date;
        //Date.from(ZonedDateTime.now().minusMonths(1).toInstant());
        //String strDateFormat = "M";
        //SimpleDateFormat sdf = new SimpleDateFormat(strDateFormat);
    }
    private void UpdateMonthTextView()
    {
        int calMonth;
        calMonth = m_cal.get(Calendar.MONTH);
        String[] monthNames = getResources().getStringArray(R.array.monthNames);
        ((TextView) findViewById(R.id.testDate)).setText(monthNames[calMonth]);
    }

    private void AddWeek(int direction) {
        m_cal.add(Calendar.WEEK_OF_MONTH, direction);
    }

    private void UpdateFragments()
    {
        UpdateMonthTextView(); // TODO update month better per week changed
        //m_alarmPageAdapter.notifyDataSetChanged();
        m_alarmPageAdapter = new DayFragmentAdapter(getSupportFragmentManager(), m_context, m_cal);
        m_dayFragmentViewer.setAdapter(null);
        m_dayFragmentViewer.setAdapter(m_alarmPageAdapter);
        m_tabLayout.removeAllTabs();
        m_tabLayout.setupWithViewPager(m_dayFragmentViewer);
        for (int index = 0; index < 7; index++) {
            TabLayout.Tab tab = m_tabLayout.getTabAt(index);
            tab.setCustomView(m_alarmPageAdapter.getTabView(index));
        }
    }

    private void InstantiateCalToCurrentDay()
    {
        m_cal = Calendar.getInstance();
        //m_cal.add(Calendar.WEEK_OF_MONTH, -1); // Week of month starts at position zero for us
        //m_alarmPageAdapter.UpdateCalendar(m_cal);
        int calMonth;
        calMonth = m_cal.get(Calendar.MONTH);
        String[] monthNames = getResources().getStringArray(R.array.monthNames);
        ((TextView) findViewById(R.id.testDate)).setText(monthNames[calMonth]);

        UpdateFragments();

        int dayOfWeek = SaveManager.getInstance().GetDayOfWeek(m_cal);
        TabLayout.Tab tab = m_tabLayout.getTabAt(dayOfWeek);
        tab.select();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1)
        {
            if(resultCode == RESULT_OK)
            {
                m_lastTabIndex = data.getIntExtra("tabIndex", 0);
            }
        }
    }
}
