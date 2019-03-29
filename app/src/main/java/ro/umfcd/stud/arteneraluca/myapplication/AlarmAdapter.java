package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class AlarmAdapter extends BaseAdapter {
    private Context m_context;
    private ArrayList<AlarmItem> m_alarmsToDisplay;
    private int m_tabIndex;

    class AlarmItem
    {
        public String medName;
        public String hour;
        public String note;
        public String dosage;
        public int index;
    }

    class SortByHour implements Comparator<AlarmItem>
    {
        public int compare(AlarmItem a, AlarmItem b)
        {
            int result = 0;
            try
            {
                SimpleDateFormat format = new SimpleDateFormat(m_context.getText(R.string.hourFormat).toString());
                Date dateA = format.parse(a.hour);
                Date dateB = format.parse(b.hour);
                if(dateA.before(dateB))
                {
                    result = -1;
                }
                else
                {
                    result = 1;
                }
            }
            catch (Exception e)
            {
                Log.e("ArteneApp", "Exception while comparing 2 dates: ", e);
            }
            return result;
        }
    }

    public AlarmAdapter(Context context, Calendar cal, int tabIndex)
    {
        m_context = context;
        m_alarmsToDisplay = new ArrayList<>();
        m_tabIndex = tabIndex;
        CreateAlarmList(cal);
    }

    @Override
    public int getCount()
    {
        return m_alarmsToDisplay.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View gridView;
        LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
        {
            //Get layout from xml file using the inflater
            //I have modified the layout from grid_view_item to grid_view_item_trial
            gridView = inflater.inflate(R.layout.grid_view_item_trial, null);
        }
        else
        {
            gridView = (View) convertView;
        }
        //Temporary - TODO Improve this method to get all the info we need from the alarms
        //textView.setLayoutParams(new GridView.LayoutParams(150, 150));
        //textView.setPadding(1,1,1,1);
        //textView.setText(SaveManager.getInstance().GetAlarm(position));
        //textView.setBackgroundColor(Color.WHITE);

        //Populate the gridview items with each item value
        //Code tip: FINAL keyword is used to create a constant. That forces the variable to not allow to be changed
        //final TextView medName = (TextView) gridView.findViewById(R.id.grid_item_medicament);
        final TextView medName = (TextView) gridView.findViewById(R.id.medNameDisplay);

        //final TextView hour = (TextView) gridView.findViewById(R.id.grid_item_ora);
        final TextView hour = (TextView) gridView.findViewById(R.id.hourDisplay);

        //final TextView note = (TextView) gridView.findViewById(R.id.alarm_note);
        final TextView note = (TextView) gridView.findViewById(R.id.otherDetailsDisplay);
        final TextView dosage = (TextView) gridView.findViewById(R.id.dosageDisplay);
        AlarmItem alarm = m_alarmsToDisplay.get(position);
        medName.setText(alarm.medName);
        hour.setText(alarm.hour);
        note.setText(alarm.note);
        dosage.setText(alarm.dosage);
        final int index = alarm.index;
        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setAlarm = new Intent(m_context.getApplicationContext(), AlarmSet.class);
                setAlarm.putExtra(m_context.getString(R.string.alarmSetModeName), m_context.getString(R.string.alarmModeEdit));
                setAlarm.putExtra("index", index);
                setAlarm.putExtra("tabIndex", m_tabIndex);
                ((Activity) m_context).startActivityForResult(setAlarm, 1);;
            }
        });

        return gridView;
    }

    private void CreateAlarmList(Calendar cal)
    {
        for(int i=0; i< SaveManager.getInstance().GetAlarmCount(); i++)
        {
            Alarm alarm = SaveManager.getInstance().GetAlarmByIndex(i);
            if(ShouldDisplayAlarmToday(cal, alarm))
            {
                for(int j=0; j< alarm.m_dailyFrequency.size(); j++)
                {
                    AlarmItem item = new AlarmItem();
                    item.hour = alarm.m_dailyFrequency.get(j);
                    item.index = alarm.getId();
                    item.medName = alarm.GetMedName();
                    item.note = alarm.GetNote();
                    //i'm trying to get dosage to display in gridview
                    item.dosage = alarm.GetDosage();
                    m_alarmsToDisplay.add(item);
                }
            }
            else
            {
                continue;
            }
        }
        Collections.sort(m_alarmsToDisplay, new SortByHour());
    }

    private boolean ShouldDisplayAlarmToday(Calendar todayCal, Alarm alarm)
    {
        Calendar alarmCal = alarm.GetStartCal();
        if(alarm.IsFixedTimeTreament())
        {
            Calendar alarmEndCal = alarm.GetEndCal();
            try {
                SimpleDateFormat format = new SimpleDateFormat(m_context.getText(R.string.dateFormat).toString());
                String date1 = format.format(alarmEndCal.getTime());
                String date2 = format.format(todayCal.getTime());
                if(date1.equals(date2) || todayCal.after(alarmEndCal))
                {
                    return false;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int dayOfWeek = SaveManager.getInstance().GetDayOfWeek(todayCal);
        boolean displayAlarm;
        boolean weeklyFrequencyValid = false;
        if(alarm.IsDailyTreatment())
        {
            weeklyFrequencyValid = true;
        }
        else
        {
            for(int i=0; i < 7; i++)
            {
                if(i == dayOfWeek && alarm.m_weeklyDayFrequency.get(i))
                {
                    weeklyFrequencyValid = true;
                }
            }
        }
        displayAlarm = todayCal.after(alarmCal) || todayCal.equals(alarmCal);
        displayAlarm = displayAlarm && weeklyFrequencyValid;
        return displayAlarm;
    }
}
