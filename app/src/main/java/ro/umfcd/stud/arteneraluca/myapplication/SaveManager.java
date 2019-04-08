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

    private ArrayList<Treatment> m_treatmentList;
    String m_saveFile;

    public static SaveManager getInstance() {
        return ourInstance;
    }

    private SaveManager() {
        try {
            //Initialize all objects
            m_XmlFactoryObject = XmlPullParserFactory.newInstance();
            m_XmlParser = m_XmlFactoryObject.newPullParser();
            m_treatmentList = new ArrayList<>();
        }
        catch(Exception e)
        {
            Log.e("ArteneApp", "Exception occurred in SaveManager constructor: ", e);
        }

    }

    public void InitSave(Context context)
    {
        m_saveFile = context.getText(R.string.treatmentSaveFileName).toString();

        File path = context.getFilesDir();
        File file = new File(path, m_saveFile);

        //Check and create the save file if it does not exist.
        if(file.exists())
        {
            //If it exist, read it and cache the alarms in m_treatmentList.
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
                Log.e("ArteneApp", "Exception occurred in SaveManager while creating / opening the save file: ", e);
            }
        }
    }

    void AddNewAlarm(Context context, View view)
    {
        Treatment newTreatment = CompactAlarm(context, view, m_treatmentList.size());
        m_treatmentList.add(newTreatment);
        AddSystemAlarms(m_treatmentList.size() - 1, context);
        SaveDataToXml(context);
    }

    void SaveAlarm(int treatmentIndex, Context context, View view)
    {
        Treatment newTreatment = CompactAlarm(context, view, treatmentIndex);
        m_treatmentList.set(treatmentIndex, newTreatment);

        ClearSystemAlarms(treatmentIndex, context);
        AddSystemAlarms(treatmentIndex, context);
        SaveDataToXml(context);
    }

    void DeleteAlarm(int treatmentIndex, Context context)
    {
        ClearSystemAlarms(treatmentIndex, context);
        m_treatmentList.remove(treatmentIndex);

        for(int i = 0; i< m_treatmentList.size(); i++)
        {
            if(m_treatmentList.get(i).getId() != i)
            {
                m_treatmentList.get(i).setId(i);
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
            for(int index = 0; index < m_treatmentList.size(); index++)
            {
                Treatment treatment = m_treatmentList.get(index);
                serializer.startTag("","Treatment");
                //Add a new tag - specify the serializer, the tag name, the attribute name and the tag value
                //Currently we have only 1 attribute per tag..
                AddNewTag(serializer, context.getText(R.string.medNameTag).toString(), "name", treatment.GetMedName());
                AddNewTag(serializer, context.getText(R.string.dosageTag).toString(), "name", treatment.GetDosage());
                SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
                String startDate = format.format(treatment.GetStartCal().getTime());
                AddNewTag(serializer, context.getText(R.string.dateTag).toString(), "name", startDate);
                AddNewTag(serializer, context.getText(R.string.frequencyTag).toString(), "name", Boolean.toString(treatment.IsFixedTimeTreatment()));
                if(treatment.IsFixedTimeTreatment())
                {
                    String endDate = format.format(treatment.GetEndCal().getTime());
                    AddNewTag(serializer, context.getText(R.string.dateEndTag).toString(), "name", endDate);
                    AddNewTag(serializer, context.getText(R.string.fixedFrequencyNumber).toString(), "name", Integer.toString(treatment.GetFixedFrequencyNumber()));
                    AddNewTag(serializer, context.getText(R.string.fixedFrequencySpinnerPosition).toString(), "name", Integer.toString(treatment.GetFixedFrequencySpinnerPosition()));
                }

                AddNewTag(serializer, context.getText(R.string.notesTag).toString(), "name", treatment.GetNote());
                AddNewTag(serializer, context.getText(R.string.dailyTreatmentTag).toString(), "name", Boolean.toString(treatment.IsDailyTreatment()));

                for(int i = 0; i< treatment.m_dailyFrequency.size(); i++)
                {
                    AddNewTag(serializer, context.getText(R.string.dailyFrequencyHourTag).toString(),"name", treatment.m_dailyFrequency.get(i));
                }

                if(!treatment.IsDailyTreatment())
                {
                    for(int i = 0; i< treatment.m_weeklyDayFrequency.size(); i++)
                    {
                        AddNewTag(serializer, context.getText(R.string.weeklyFrequencyDayTag).toString(),"index", Integer.toString(i));
                        AddNewTag(serializer, context.getText(R.string.weeklyFrequencyDayTag).toString(),"name", Boolean.toString(treatment.m_weeklyDayFrequency.get(i)));
                    }
                }
                AddNewTag(serializer, context.getText(R.string.confirmedCountTag).toString(), "name", Integer.toString(treatment.GetConfirmedCount()));
                AddNewTag(serializer, context.getText(R.string.deniedCountTag).toString(), "name", Integer.toString(treatment.GetDeniedCount()));
                serializer.endTag("","Treatment");
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
            Log.e("ArteneApp", "Exception occurred while writing the XML: ", e);
        }
    }

    void ParseXmlFile(Context context)
    {
        m_treatmentList.clear();

        try{
            //Open an input stream for readit from the file
            FileInputStream fs = context.openFileInput(m_saveFile);
            m_XmlParser.setInput(fs, null);

            int event = m_XmlParser.getEventType();
            Treatment newTreatment = new Treatment();
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
                            newTreatment.SetMedName(m_XmlParser.getAttributeValue(null,"name"));
                        }
                        if(name.equals(context.getText(R.string.dosageTag).toString()))
                        {
                            newTreatment.SetDosage(m_XmlParser.getAttributeValue(null,"name"));
                        }

                        if(name.equals(context.getText(R.string.dateTag).toString()))
                        {
                            SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
                            String startDate = m_XmlParser.getAttributeValue(null,"name");
                            Date dateFormat = format.parse(startDate);
                            Calendar date = Calendar.getInstance();
                            date.setTime(dateFormat);
                            newTreatment.SetStartCal(date);
                        }
                        if(name.equals(context.getText(R.string.frequencyTag).toString()))
                        {
                            newTreatment.SetFixedTimeTreatment(Boolean.parseBoolean(m_XmlParser.getAttributeValue(null,"name")));
                        }
                        if(name.equals(context.getText(R.string.dateEndTag).toString()) && newTreatment.IsFixedTimeTreatment())
                        {
                            SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
                            String endDate = m_XmlParser.getAttributeValue(null,"name");
                            Date dateFormat = format.parse(endDate);
                            Calendar date = Calendar.getInstance();
                            date.setTime(dateFormat);
                            newTreatment.SetEndCal(date);
                        }
                        if(name.equals(context.getText(R.string.fixedFrequencyNumber).toString()) && newTreatment.IsFixedTimeTreatment())
                        {
                            newTreatment.SetFixedFrequencyNumber(Integer.parseInt(m_XmlParser.getAttributeValue(null, "name")));
                        }
                        if(name.equals(context.getText(R.string.fixedFrequencySpinnerPosition).toString()) && newTreatment.IsFixedTimeTreatment())
                        {
                            newTreatment.SetFixedFrequencySpinnerPosition(Integer.parseInt(m_XmlParser.getAttributeValue(null, "name")));
                        }
                        if(name.equals(context.getText(R.string.notesTag).toString()))
                        {
                            newTreatment.SetNote(m_XmlParser.getAttributeValue(null,"name"));
                        }
                        if(name.equals(context.getText(R.string.dailyTreatmentTag).toString()))
                        {
                            newTreatment.SetDailyTreatment(Boolean.parseBoolean(m_XmlParser.getAttributeValue(null, "name")));
                        }
                        if(name.equals(context.getText(R.string.dailyFrequencyHourTag).toString()))
                        {
                            newTreatment.m_dailyFrequency.add(m_XmlParser.getAttributeValue(null, "name"));
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
                                newTreatment.m_weeklyDayFrequency.set(weeklyIndex, Boolean.parseBoolean(m_XmlParser.getAttributeValue(null, "name")));
                            }
                        }
                        if(name.equals(context.getText(R.string.confirmedCountTag).toString()))
                        {
                            newTreatment.SetConfirmedCount(Integer.parseInt(m_XmlParser.getAttributeValue(null, "name")));
                        }
                        if(name.equals(context.getText(R.string.deniedCountTag).toString()))
                        {
                            newTreatment.SetDeniedCount(Integer.parseInt(m_XmlParser.getAttributeValue(null, "name")));
                        }

                        if(name.equals("Treatment") && newTreatment.IsValid())
                        {
                            newTreatment.setId(m_treatmentList.size());
                            m_treatmentList.add(newTreatment);
                            newTreatment = new Treatment();
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

    Treatment GetAlarm(int index)
    {
        for(int i = 0; i< m_treatmentList.size(); i++)
        {
            if(m_treatmentList.get(i).getId() == index)
            {
                return m_treatmentList.get(i);
            }
        }
        return null;
    }
    Treatment GetAlarmByIndex(int index)
    {
        return m_treatmentList.get(index);
    }

    int GetAlarmCount()
    {
        return m_treatmentList.size();
    }

    private Treatment CompactAlarm(Context context, View view, int id)
    {
        Treatment newTreatment = new Treatment();

        newTreatment.setId(id);
        TextView nameValue = (TextView) view.findViewById(R.id.medNameTextInput);
        TextView dosageValue = (TextView) view.findViewById(R.id.DosageInput_Text);
        TextView notesValue = (TextView) view.findViewById(R.id.other_details);
        Switch frequencyValue = (Switch) view.findViewById(R.id.alarmDuration_switch);

        newTreatment.SetMedName(nameValue.getText().toString());
        newTreatment.SetDosage(dosageValue.getText().toString());
        if(notesValue != null)
        {
            newTreatment.SetNote(notesValue.getText().toString());
        }

        LinearLayout hourPickersLayout = view.findViewById(R.id.hourPickers_LinearLayout);
        for (int i = 0; i < hourPickersLayout.getChildCount(); i++)
        {
            TextView hourPicker = (TextView) hourPickersLayout.getChildAt(i);
            if(!hourPicker.getText().toString().isEmpty())
            {
                newTreatment.m_dailyFrequency.add(hourPicker.getText().toString());
            }
        }
        RadioGroup alarmFrequency = view.findViewById(R.id.alarmFreq_RadioGroup);
        boolean dailyFreq = alarmFrequency.getCheckedRadioButtonId() == R.id.dailyRadioBtn;
        newTreatment.SetDailyTreatment(dailyFreq);
        if(!dailyFreq)
        {
            LinearLayout dayPickerLayout = (LinearLayout) view.findViewById(R.id.checkboxes_layout);
            for(int i=0; i < dayPickerLayout.getChildCount(); i++)
            {
                CheckBox dayPicker = (CheckBox) dayPickerLayout.getChildAt(i);
                if(dayPicker.isChecked())
                {
                    newTreatment.m_weeklyDayFrequency.set(i, true);
                }
            }
        }
        Calendar date = Calendar.getInstance();
        try
        {
            TextView startDateTextView = (TextView) view.findViewById(R.id.startDateSelection);
            SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
            String startDate = startDateTextView.getText().toString();
            Date formattedDate = format.parse(startDate);
            date.setTime(formattedDate);
        }
        catch (Exception e)
        {
            Log.e("ArteneApp", "Exception while creating fragment view: ", e);
        }
        newTreatment.SetStartCal(date);

        if(frequencyValue.isChecked())
        {
            //If the frequency value is checked, it means the medication treatment has a fixed period of time
            //So we add that end date based on the user input
            newTreatment.SetFixedTimeTreatment(true);
            String frequencyNumberString = ((TextView) view.findViewById(R.id.alarmsNumber_Text)).getText().toString();
            int frequencyNumber = Integer.parseInt(frequencyNumberString);
            Spinner treatmentLengthSpinner = (Spinner) view.findViewById(R.id.treatmentLengthSpinner);
            int treatmentOption = treatmentLengthSpinner.getSelectedItemPosition();

            Calendar treatmentEndDate = (Calendar) newTreatment.GetStartCal().clone();
            switch(treatmentOption)
            {
                case 0:
                    //Days
                    treatmentEndDate.add(Calendar.DAY_OF_YEAR, frequencyNumber);
                    newTreatment.SetEndCal(treatmentEndDate);

                    break;
                case 1:
                    //Weeks
                    treatmentEndDate.add(Calendar.WEEK_OF_YEAR, frequencyNumber);
                    newTreatment.SetEndCal(treatmentEndDate);
                    break;
                case 2:
                    //Months
                    treatmentEndDate.add(Calendar.MONTH, frequencyNumber);
                    newTreatment.SetEndCal(treatmentEndDate);
                    break;
                case 3:
                    //Years
                    treatmentEndDate.add(Calendar.YEAR, frequencyNumber);
                    newTreatment.SetEndCal(treatmentEndDate);
                    break;
            }

            newTreatment.SetFixedFrequencyNumber(frequencyNumber);
            newTreatment.SetFixedFrequencySpinnerPosition(treatmentOption);
        }
        else
        {
            newTreatment.SetFixedTimeTreatment(false);
        }
        return newTreatment;
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

    void ClearSystemAlarms(int treatmentIndex, Context context) {
        //Clear System alarms that start with treatment index
        Intent intent = new Intent(context, AlarmReceiver.class);
        Treatment treatment = m_treatmentList.get(treatmentIndex);

        int alarmId;
        //Daily treatment
        if (treatment.IsDailyTreatment())
        {
            AlarmHelperClass.CancelIntentWithId(context, treatmentIndex, intent);
            for(int indexHour = 0; indexHour < treatment.m_dailyFrequency.size(); indexHour++)
            {
                alarmId = treatmentIndex * 10 + indexHour;
                AlarmHelperClass.CancelIntentWithId(context, alarmId, intent);
            }

        }
        //Weekly treatment
        else
        {
            //Clear the setter for this week
            AlarmHelperClass.CancelIntentWithId(context, treatmentIndex, intent);
            for(int indexDay = 0; indexDay < 7; indexDay++)
            {
                if(treatment.m_weeklyDayFrequency.get(indexDay))
                {
                    for(int indexHour = 0; indexHour < treatment.m_dailyFrequency.size(); indexHour++)
                    {
                        alarmId = treatmentIndex * 10 + indexDay;
                        alarmId = alarmId * 10 + indexHour;
                        AlarmHelperClass.CancelIntentWithId(context, alarmId, intent);
                    }
                }
            }
        }
    }

    void AddSystemAlarms(int treatmentIndex, Context context)
    {
        //Add system alarms
        Treatment treatment = m_treatmentList.get(treatmentIndex);

        //Check if treatment has ended and if so, don't set an treatment anymore for it.
        if(AlarmHelperClass.HasTreatmentEnded(treatment))
        {
            return;
        }

        //Set an event to schedule alerts for this treatment
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("alarmType", R.string.alarmSet);
        intent.putExtra("treatmentIndex", treatmentIndex);
        //Manual serialize object
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, treatmentIndex, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar todayCal = Calendar.getInstance();
        alarmMgr.set(AlarmManager.RTC_WAKEUP, todayCal.getTimeInMillis(), pendingAlarmIntent);
    }
}
