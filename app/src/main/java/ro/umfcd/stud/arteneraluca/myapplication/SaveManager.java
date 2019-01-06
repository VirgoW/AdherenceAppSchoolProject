package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
        Alarm newAlarm = CompactAlarm(context, view);
        newAlarm.setId(m_alarmList.size());
        m_alarmList.add(newAlarm);
        SaveDataToXml(context);
    }

    void SaveAlarm(int alarmIndex, Context context, View view)
    {
        Alarm newAlarm = CompactAlarm(context, view);
        m_alarmList.set(alarmIndex, newAlarm);
        SaveDataToXml(context);
    }

    void DeleteAlarm(int alarmIndex, Context context)
    {
        m_alarmList.remove(alarmIndex);

        for(int i=0; i< m_alarmList.size(); i++)
        {
            m_alarmList.get(i).setId(i);
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
                AddNewTag(serializer, context.getText(R.string.frequencyTag).toString(), "name", alarm.GetFrequency());
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
                            Date test = format.parse(startDate);
                            Calendar date = Calendar.getInstance();
                            date.setTime(test);
                            newAlarm.SetStartCal(date);
                        }
                        if(name.equals(context.getText(R.string.frequencyTag).toString()))
                        {
                            newAlarm.SetFrequency(m_XmlParser.getAttributeValue(null,"name"));
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

    private Alarm CompactAlarm(Context context, View view)
    {
        Alarm newAlarm = new Alarm();

        TextView nameValue = (TextView) view.findViewById(R.id.medNameText);
        TextView dosageValue = (TextView) view.findViewById(R.id.DosageInput_Text);
        TextView notesValue = (TextView) view.findViewById(R.id.other_details);
        Switch frequncyValue = (Switch) view.findViewById(R.id.alarmDuration_switch); //TODO fixed ammount of time

        newAlarm.SetMedName(nameValue.getText().toString());
        newAlarm.SetDosage(dosageValue.getText().toString());
        if(notesValue != null)
        {
            newAlarm.SetNote(notesValue.getText().toString());
        }
        if(frequncyValue.isChecked())
        {
            newAlarm.SetFrequency( context.getText(R.string.durationOn).toString());
        }
        else
        {
            newAlarm.SetFrequency( context.getText(R.string.durationOff).toString());
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
            Date test = format.parse(startDate);
            date.setTime(test);
        }
        catch (Exception e)
        {
            Log.e("ArteneApp", "Exception while creating fragment view: ", e);
        }
        newAlarm.SetStartCal(date);
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
}
