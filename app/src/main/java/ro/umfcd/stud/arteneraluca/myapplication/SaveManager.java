package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class SaveManager {

    private static final SaveManager ourInstance = new SaveManager();
    private XmlPullParserFactory m_XmlFactoryObject;
    private XmlPullParser m_XmlParser;

    private ArrayList<Alarm> m_alarmList;
    String m_saveFile;

    /*
    This class an instance -> It can be called from anywhere and it does not require you to create an object of this class
    We make it an instance because we need to access the save data from everywhere, but most importantly, we want it to be the SAME data.

    Code tip: static members of a class are blocks of memory that are shared across all object of that class.
     */
    public static SaveManager getInstance() {
        return ourInstance;
    }

    private SaveManager() {
        try {
            //Initialize all objects
            m_XmlFactoryObject = XmlPullParserFactory.newInstance();
            m_XmlParser = m_XmlFactoryObject.newPullParser();
            m_alarmList = new ArrayList<>();
        }
        catch(Exception e)
        {
            Log.e("ArteneApp", "Exception occured in SaveManager constructor: ", e);
        }

    }

    public void InitSave(Context context)
    {
        m_saveFile = context.getText(R.string.testSave).toString();

        File path = context.getFilesDir();
        File file = new File(path, m_saveFile);

        //Check and create the save file if it does not exist.
        if(file.exists())
        {
            //If it exist, read it and cache the alarms in m_alarmList.
            ParseXmlFile(context);
        }
        else
        {
            //If the file does not exit, create it by opening it for writing
            try{
                OutputStreamWriter os = new OutputStreamWriter (context.openFileOutput(m_saveFile, MODE_PRIVATE));
                os.close();
            }
            catch(Exception e)
            {
                Log.e("ArteneApp", "Exception occured in SaveManager while creating / opening the save file: ", e);
            }
        }
    }

    void AddNewAlarm(Context context, View view)
    {
        Alarm newAlarm = CompactAlarm(context, view, m_alarmList.size());
        m_alarmList.add(newAlarm);
        AddSystemAlarms(m_alarmList.size() - 1, context);
        SaveDataToXml(context);
    }

    void SaveAlarm(int alarmIndex, Context context, View view)
    {
        Alarm newAlarm = CompactAlarm(context, view, alarmIndex);
        m_alarmList.set(alarmIndex, newAlarm);

        ClearSystemAlarms(alarmIndex, context);
        AddSystemAlarms(alarmIndex, context);
        SaveDataToXml(context);
    }

    void DeleteAlarm(int alarmIndex, Context context)
    {
        ClearSystemAlarms(alarmIndex, context);
        m_alarmList.remove(alarmIndex);

        for(int i=0; i< m_alarmList.size(); i++)
        {
            if(m_alarmList.get(i).getId() != i)
            {
                m_alarmList.get(i).setId(i);
                ClearSystemAlarms(i, context);
                //AddSystemAlarms(i, context);
            }
        }
        SaveDataToXml(context);
    }

    void SaveDataToXml(Context context)
    {
        try
        {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();

            serializer.setOutput(writer);
            //Start document writing
            serializer.startDocument("UTF-8", true);
            for(int index = 0; index < m_alarmList.size(); index++)
            {
                Alarm alarm = m_alarmList.get(index);
                serializer.startTag("","Alarm");
                //Add a new tag - specify the serializer, the tag name, the attribute name and the tag value
                //Currently we have only 1 attribute per tag..
                AddNewTag(serializer, context.getText(R.string.medNameTag).toString(), "name", alarm.GetMedName());
                AddNewTag(serializer, context.getText(R.string.dosageTag).toString(), "name", alarm.GetDosage());
                SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
                String startDate = format.format(alarm.GetStartCal().getTime());
                AddNewTag(serializer, context.getText(R.string.dateTag).toString(), "name", startDate);
                AddNewTag(serializer, context.getText(R.string.frequencyTag).toString(), "name", Boolean.toString(alarm.IsFixedTimeTreatment()));
                if(alarm.IsFixedTimeTreatment())
                {
                    String endDate = format.format(alarm.GetEndCal().getTime());
                    AddNewTag(serializer, context.getText(R.string.dateEndTag).toString(), "name", endDate);
                    AddNewTag(serializer, context.getText(R.string.fixedFrequencyNumber).toString(), "name", Integer.toString(alarm.GetFixedFrequencyNumber()));
                    AddNewTag(serializer, context.getText(R.string.fixedFrequencySpinnerPosition).toString(), "name", Integer.toString(alarm.GetFixedFrequencySpinnerPosition()));
                }

                AddNewTag(serializer, context.getText(R.string.notesTag).toString(), "name", alarm.GetNote());
                AddNewTag(serializer, context.getText(R.string.dailyTreatmentTag).toString(), "name", Boolean.toString(alarm.IsDailyTreatment()));

                for(int i=0; i< alarm.m_dailyFrequency.size(); i++)
                {
                    AddNewTag(serializer, context.getText(R.string.dailyFrequencyHourTag).toString(),"name", alarm.m_dailyFrequency.get(i));
                }

                if(!alarm.IsDailyTreatment())
                for(int i=0; i<alarm.m_weeklyDayFrequency.size();i++)
                {
                    AddNewTag(serializer, context.getText(R.string.weeklyFrequencyDayTag).toString(),"index", Integer.toString(i));
                    AddNewTag(serializer, context.getText(R.string.weeklyFrequencyDayTag).toString(),"name", Boolean.toString(alarm.m_weeklyDayFrequency.get(i)));
                }

                serializer.endTag("","Alarm");
            }
            serializer.endDocument();
            //End document writing
            String result = writer.toString();

            OutputStreamWriter  os = new OutputStreamWriter (context.openFileOutput(m_saveFile, MODE_PRIVATE));
            os.write(result);
            System.out.println("Wrote xml file with value: " + result);
            os.close();
        }
        catch (Exception e)
        {
            Log.e("ArteneApp", "Exception occured while writing the XML: ", e);
        }
    }

    void ParseXmlFile(Context context)
    {
        m_alarmList.clear();

        try{
            //Open an input stream for readit from the file
            FileInputStream fs = context.openFileInput(m_saveFile);
            m_XmlParser.setInput(fs, null);

            int event = m_XmlParser.getEventType();
            Alarm newAlarm = new Alarm();
            int weeklyIndex = -1;
            while(event != XmlPullParser.END_DOCUMENT)
            {
                String name = m_XmlParser.getName();
                switch(event)
                {
                    case XmlPullParser.START_TAG:
                    break;
                    case XmlPullParser.END_TAG:
                        if(name.equals(context.getText(R.string.medNameTag).toString()))
                        {
                            newAlarm.SetMedName(m_XmlParser.getAttributeValue(null,"name"));
                        }
                        if(name.equals(context.getText(R.string.dosageTag).toString()))
                        {
                            newAlarm.SetDosage(m_XmlParser.getAttributeValue(null,"name"));
                        }

                        if(name.equals(context.getText(R.string.dateTag).toString()))
                        {
                            SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
                            String startDate = m_XmlParser.getAttributeValue(null,"name");
                            Date dateFormat = format.parse(startDate);
                            Calendar date = Calendar.getInstance();
                            date.setTime(dateFormat);
                            newAlarm.SetStartCal(date);
                        }
                        if(name.equals(context.getText(R.string.frequencyTag).toString()))
                        {
                            newAlarm.SetFixedTimeTreatment(Boolean.parseBoolean(m_XmlParser.getAttributeValue(null,"name")));
                        }
                        if(name.equals(context.getText(R.string.dateEndTag).toString()) && newAlarm.IsFixedTimeTreatment())
                        {
                            SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
                            String endDate = m_XmlParser.getAttributeValue(null,"name");
                            Date dateFormat = format.parse(endDate);
                            Calendar date = Calendar.getInstance();
                            date.setTime(dateFormat);
                            newAlarm.SetEndCal(date);
                        }
                        if(name.equals(context.getText(R.string.fixedFrequencyNumber).toString()) && newAlarm.IsFixedTimeTreatment())
                        {
                            newAlarm.SetFixedFrequencyNumber(Integer.parseInt(m_XmlParser.getAttributeValue(null, "name")));
                        }
                        if(name.equals(context.getText(R.string.fixedFrequencySpinnerPosition).toString()) && newAlarm.IsFixedTimeTreatment())
                        {
                            newAlarm.SetFixedFrequencySpinnerPosition(Integer.parseInt(m_XmlParser.getAttributeValue(null, "name")));
                        }
                        if(name.equals(context.getText(R.string.notesTag).toString()))
                        {
                            newAlarm.SetNote(m_XmlParser.getAttributeValue(null,"name"));
                        }
                        if(name.equals(context.getText(R.string.dailyTreatmentTag).toString()))
                        {
                            newAlarm.SetDailyTreatment(Boolean.parseBoolean(m_XmlParser.getAttributeValue(null, "name")));
                        }
                        if(name.equals(context.getText(R.string.dailyFrequencyHourTag).toString()))
                        {
                            newAlarm.m_dailyFrequency.add(m_XmlParser.getAttributeValue(null, "name"));
                        }
                        if(name.equals(context.getText(R.string.weeklyFrequencyDayTag).toString()))
                        {
                            String attribute = (m_XmlParser.getAttributeValue(null, "index"));
                            if((attribute != null))
                            {
                                weeklyIndex = Integer.parseInt(attribute);
                            }
                            else
                            {
                                newAlarm.m_weeklyDayFrequency.set(weeklyIndex, Boolean.parseBoolean(m_XmlParser.getAttributeValue(null, "name")));
                            }
                        }
                        if(name.equals("Alarm") && newAlarm.IsValid())
                        {
                            newAlarm.setId(m_alarmList.size());
                            m_alarmList.add(newAlarm);
                            newAlarm = new Alarm();
                        }

                }
                event = m_XmlParser.next();
            }
            fs.close();
        }
        catch (Exception e)
        {
            Log.e("ArteneApp", "Exception while adding parsing XML: ", e);
        }
    }

    void AddNewTag(XmlSerializer serializer, String tagName, String attributeName, String attributeValue)
    {
        try{
            serializer.startTag("", tagName);
            serializer.attribute("", attributeName, attributeValue);
            serializer.endTag("", tagName);
        }
        catch(Exception e)
        {
            Log.e("ArteneApp", "Exception while adding new tag: ", e);
        }
    }

    Alarm GetAlarm(int index)
    {
        for(int i=0; i<m_alarmList.size();i++)
        {
            if(m_alarmList.get(i).getId() == index)
            {
                return m_alarmList.get(i);
            }
        }
        return null;
    }
    Alarm GetAlarmByIndex(int index)
    {
        return m_alarmList.get(index);
    }

    int GetAlarmCount()
    {
        return m_alarmList.size();
    }

    private Alarm CompactAlarm(Context context, View view, int id)
    {
        Alarm newAlarm = new Alarm();

        newAlarm.setId(id);
        TextView nameValue = (TextView) view.findViewById(R.id.medNameTextInput);
        TextView dosageValue = (TextView) view.findViewById(R.id.DosageInput_Text);
        TextView notesValue = (TextView) view.findViewById(R.id.other_details);
        Switch frequncyValue = (Switch) view.findViewById(R.id.alarmDuration_switch);

        newAlarm.SetMedName(nameValue.getText().toString());
        newAlarm.SetDosage(dosageValue.getText().toString());
        if(notesValue != null)
        {
            newAlarm.SetNote(notesValue.getText().toString());
        }

        LinearLayout hourPickersLayout = view.findViewById(R.id.hourPickers_LinearLayout);
        for (int i = 0; i < hourPickersLayout.getChildCount(); i++)
        {
            TextView hourPicker = (TextView) hourPickersLayout.getChildAt(i);
            if(!hourPicker.getText().toString().isEmpty())
            {
                newAlarm.m_dailyFrequency.add(hourPicker.getText().toString());
            }
        }
        RadioGroup alarmFrequency = view.findViewById(R.id.alarmFreq_RadioGroup);
        boolean dailyFreq = alarmFrequency.getCheckedRadioButtonId() == R.id.dailyRadioBtn;
        newAlarm.SetDailyTreatment(dailyFreq);
        if(!dailyFreq)
        {
            LinearLayout dayPickerLayout = (LinearLayout) view.findViewById(R.id.checkboxes_layout);
            for(int i=0; i < dayPickerLayout.getChildCount(); i++)
            {
                CheckBox dayPicker = (CheckBox) dayPickerLayout.getChildAt(i);
                if(dayPicker.isChecked())
                {
                    newAlarm.m_weeklyDayFrequency.set(i, true);
                }
            }
        }
        Calendar date = Calendar.getInstance();
        try
        {
            TextView startDateTextView = (TextView) view.findViewById(R.id.startDateSelection);
            SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
            String startDate = startDateTextView.getText().toString();
            Date formatedDate = format.parse(startDate);
            date.setTime(formatedDate);
        }
        catch (Exception e)
        {
            Log.e("ArteneApp", "Exception while creating fragment view: ", e);
        }
        newAlarm.SetStartCal(date);

        if(frequncyValue.isChecked())
        {
            //If the frequency value is checked, it means the medication treatment has a fixed period of time
            //So we add that end date based on the user input
            newAlarm.SetFixedTimeTreatment(true);
            String frequencyNumberString = ((TextView) view.findViewById(R.id.alarmsNumber_Text)).getText().toString();
            int frequencyNumber = Integer.parseInt(frequencyNumberString);
            Spinner treatmentLenghtSpinner = (Spinner) view.findViewById(R.id.treatmentLengthSpinner);
            int treatmentOption = treatmentLenghtSpinner.getSelectedItemPosition();

            Calendar treatmentEndDate = (Calendar) newAlarm.GetStartCal().clone();
            switch(treatmentOption)
            {
                case 0:
                    //Days
                    treatmentEndDate.add(Calendar.DAY_OF_YEAR, frequencyNumber);
                    newAlarm.SetEndCal(treatmentEndDate);

                    break;
                case 1:
                    //Weeks
                    treatmentEndDate.add(Calendar.WEEK_OF_YEAR, frequencyNumber);
                    newAlarm.SetEndCal(treatmentEndDate);
                    break;
                case 2:
                    //Months
                    treatmentEndDate.add(Calendar.MONTH, frequencyNumber);
                    newAlarm.SetEndCal(treatmentEndDate);
                    break;
                case 3:
                    //Years
                    treatmentEndDate.add(Calendar.YEAR, frequencyNumber);
                    newAlarm.SetEndCal(treatmentEndDate);
                    break;
            }

            newAlarm.SetFixedFrequencyNumber(frequencyNumber);
            newAlarm.SetFixedFrequencySpinnerPosition(treatmentOption);
        }
        else
        {
            newAlarm.SetFixedTimeTreatment(false);
        }
        return newAlarm;
    }

    public int GetDayOfWeek(Calendar cal)
    {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 2;
        if(dayOfWeek < 0)
        {
            dayOfWeek = 7 - Math.abs(dayOfWeek);
        }
        return dayOfWeek;
    }

    void ClearSystemAlarms(int alarmIndex, Context context) {
        //Clear System alarms that start with alarm index
        int alarmId;
        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Alarm alarm = m_alarmList.get(alarmIndex);

        //Daily alarm
        if (alarm.IsDailyTreatment())
        {
            for(int indexHour = 0 ; indexHour < alarm.m_dailyFrequency.size(); indexHour++)
            {
                alarmId = alarmIndex * 10 + indexHour;
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_NO_CREATE);
                if (pendingIntent != null)
                {
                    alarmMgr.cancel(pendingIntent);
                }
            }
        }
        //Weekly alarm
        else
        {
            //Clear the setter for this week
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarmIndex, intent, PendingIntent.FLAG_NO_CREATE);
            if (pendingIntent != null)
            {
                alarmMgr.cancel(pendingIntent);
            }
            for(int indexDay = 0; indexDay < 7; indexDay++)
            {
                if(alarm.m_weeklyDayFrequency.get(indexDay))
                {
                    for(int indexHour = 0 ; indexHour < alarm.m_dailyFrequency.size(); indexHour++)
                    {
                        alarmId = alarmIndex * 10 + indexDay;
                        alarmId = alarmId * 10 + indexHour;

                        pendingIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_NO_CREATE);
                        if (pendingIntent != null)
                        {
                            alarmMgr.cancel(pendingIntent);
                        }
                    }
                }
            }
        }
    }

    void AddSystemAlarms(int alarmIndex, Context context)
    {
        //Add system alarms
        int alarmId;
        Intent intent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Alarm alarm = m_alarmList.get(alarmIndex);

        //Daily alarm
        if (alarm.IsDailyTreatment())
        {
            for(int indexHour = 0 ; indexHour < alarm.m_dailyFrequency.size(); indexHour++)
            {
                alarmId = alarmIndex * 10 + indexHour;
                intent.putExtra("alarmType", R.string.alarmActivate);
                intent.putExtra("alarmID", alarmId);
                intent.putExtra("medName", alarm.GetMedName());
                intent.putExtra("hour", alarm.m_dailyFrequency.get(indexHour));

                String hourString = alarm.m_dailyFrequency.get(indexHour);
                int hour = Integer.parseInt(hourString.substring(0,2));
                int minute = Integer.parseInt(hourString.substring(3));

                Calendar alarmStartCalendar = alarm.GetStartCal();
                Calendar alarmEndCalendar = null;
                if(alarm.IsFixedTimeTreatment())
                {
                   alarmEndCalendar = alarm.GetEndCal();
                }
                Calendar alarmCalendar = Calendar.getInstance();
                alarmCalendar.setTimeInMillis(System.currentTimeMillis());
                alarmCalendar.set(Calendar.HOUR_OF_DAY, hour);
                alarmCalendar.set(Calendar.MINUTE, minute);

                Calendar todayCal = Calendar.getInstance();

                long milis1 = alarmStartCalendar.getTimeInMillis();
                long milis2= todayCal.getTimeInMillis();
                long diffDays = (milis1 - milis2) / (24*60*60*1000);

                if(alarmEndCalendar != null && (todayCal.after(alarmEndCalendar) || alarmCalendar.after(alarmEndCalendar)))
                {
                    //If today is after the end of the treatment or the next alarm would be after the end of treatment, skip this alarm.
                    continue;
                }

                if(diffDays > 0)
                {
                    alarmCalendar.add(Calendar.DATE, (int)diffDays); //TODO Possible casting issues
                }
                else if(todayCal.after(alarmCalendar))
                {
                    //Make the alarm start tomorrow if current time is after set time.
                    alarmCalendar.add(Calendar.DAY_OF_MONTH, 1);
                }


                PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmCalendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingAlarmIntent);
            }
        }
        //Weekly alarm
        else {
            //Pass the alarm and the alarm type - a setter of alarms in this case
            intent.putExtra("alarmType", R.string.alarmSet);
            //Manual serialize object
            SerializeAlarmIntoIntent(intent, alarm);

            //Start today and set the alarms until monday, then set an alarm for monday
            PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmIndex, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Calendar todayCal = Calendar.getInstance();
            alarmMgr.set(AlarmManager.RTC_WAKEUP, todayCal.getTimeInMillis(), pendingAlarmIntent); //TODO end cal
        }
    }

    void SerializeAlarmIntoIntent(Intent intent, Alarm alarm)
    {
        intent.putExtra("medName", alarm.GetMedName());
        intent.putExtra("dailyHours", alarm.m_dailyFrequency.size());
        for(int i=0; i< alarm.m_dailyFrequency.size(); i++)
        {
            String extraString = "hourString" + i;
            intent.putExtra(extraString, alarm.m_dailyFrequency.get(i));
        }
        intent.putExtra("alarmId", alarm.getId());
        for(int i=0; i< 7; i++)
        {
            String extraString = "dayBoolean" + i;
            intent.putExtra(extraString, alarm.m_weeklyDayFrequency.get(i));
        }

    }

    Boolean DeserializeAlarmFromIntent(Intent intent, Alarm alarm)
    {
        String medName = intent.getStringExtra("medName");
        alarm.SetMedName(medName);
        int dailyHours = intent.getIntExtra("dailyHours", -1);
        for(int i=0; i < dailyHours; i++)
        {
            String extraString = "hourString" + i;
            String hour = intent.getStringExtra(extraString);
            alarm.m_dailyFrequency.add(hour);
        }
        for(int i=0; i < 7; i++)
        {
            String extraString = "dayBoolean" + i;
            Boolean weekChecked = intent.getBooleanExtra(extraString, false);
            alarm.m_weeklyDayFrequency.set(i, weekChecked);
        }
        int alarmId = intent.getIntExtra("alarmId", -1);
        alarm.setId(alarmId);
        if(alarmId >= 0 && !medName.isEmpty())
        {
            return true;
        }
        return false;
    }

    Boolean CalendarAAfterCalendarB(Context context, Calendar calA, Calendar calB)
    {
        try
        {
            /*
            SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
            String date1 = format.format(calA.getTime());
            String date2 = format.format(calB.getTime());
            if(date1.equals(date2) || calA.after(calB))*/
            Date dateA = calA.getTime();
            Date dateB = calB.getTime();

            if(dateA.after(dateB) || dateA.equals(dateB))
            {
                return true;
            }
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }
        return false;
    }
}
