package ro.umfcd.stud.arteneraluca.myapplication;

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

    class AlarmItem
    {
        public String medName;
        public String hour;
        public String note;
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

    public AlarmAdapter(Context context, Calendar cal)
    {
        m_context = context;
        m_alarmsToDisplay = new ArrayList<>();
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
            gridView = inflater.inflate(R.layout.grid_view_item, null);
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
        final TextView medName = (TextView) gridView.findViewById(R.id.grid_item_medicament);
        final TextView hour = (TextView) gridView.findViewById(R.id.grid_item_ora);
        final TextView note = (TextView) gridView.findViewById(R.id.alarm_note);
        AlarmItem alarm = m_alarmsToDisplay.get(position);
        medName.setText(alarm.medName);
        hour.setText(alarm.hour);
        note.setText(alarm.note);
        final int index = alarm.index;
        gridView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setAlarm = new Intent(m_context.getApplicationContext(), AlarmSet.class);
                setAlarm.putExtra(m_context.getString(R.string.alarmSetModeName), m_context.getString(R.string.alarmModeEdit));
                setAlarm.putExtra("index", index);
                m_context.startActivity(setAlarm);
            }
        });

        return gridView;
    }

    private void CreateAlarmList(Calendar cal)
    {
        int currentDay = cal.get(Calendar.DAY_OF_YEAR);

        for(int i=0; i< SaveManager.getInstance().GetAlarmCount(); i++)
        {
            Alarm alarm = SaveManager.getInstance().GetAlarmByIndex(i);
            int alarmStartDay = alarm.GetStartCal().get(Calendar.DAY_OF_YEAR);
            if(currentDay >= alarmStartDay)
            {
                //TODO Check for weekly frequency

                for(int j=0; j< alarm.m_dailyFrequency.size(); j++)
                {
                    AlarmItem item = new AlarmItem();
                    item.hour = alarm.m_dailyFrequency.get(j);
                    item.index = alarm.getId();
                    item.medName = alarm.GetMedName();
                    item.note = alarm.GetNote();
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
}
