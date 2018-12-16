package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;

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
        m_alarmList.add(CompactAlarm(context, view));
        SaveDataToXml(context);
    }

    void SaveAlarm(int alarmIndex, Context context, View view)
    {
        m_alarmList.set(alarmIndex, CompactAlarm(context, view));
        SaveDataToXml(context);
    }

    void DeleteAlarm(int alarmIndex, Context context)
    {
        m_alarmList.remove(alarmIndex);
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
                serializer.startTag("","Alarm");
                //Add a new tag - specify the serializer, the tag name, the attribute name and the tag value
                //Currently we have only 1 attribute per tag..
                AddNewTag(serializer, context.getText(R.string.medNameTag).toString(), "name", m_alarmList.get(index).GetMedName());
                AddNewTag(serializer, context.getText(R.string.dosageTag).toString(), "name", m_alarmList.get(index).GetDosage());
                SimpleDateFormat format = new SimpleDateFormat("d MMM YYYY");
                String startDate = format.format(m_alarmList.get(index).GetStartCal().getTime());
                AddNewTag(serializer, context.getText(R.string.dateTag).toString(), "name", startDate);
                AddNewTag(serializer, context.getText(R.string.frequencyTag).toString(), "name", m_alarmList.get(index).GetFrequency());
                AddNewTag(serializer, context.getText(R.string.notesTag).toString(), "name", m_alarmList.get(index).GetNote());
                //To add all elements

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
                            SimpleDateFormat format = new SimpleDateFormat("d MMM YYYY");
                            String startDate = m_XmlParser.getAttributeValue(null,"name");
                            Calendar date = Calendar.getInstance();
                            date.setTime(format.parse(startDate));
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
                }

                event = m_XmlParser.next();

                //TODO - improve this method for better Alarm managing
                //Add Check for Alarm tag start and end
                if(newAlarm.IsValid())
                {
                    m_alarmList.add(newAlarm);
                    newAlarm = new Alarm();
                }
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

    int GetNumberOfSavedAlarms()
    {
        return m_alarmList.size();
    }

    //Temporary method - TODO replace method with more advanced version possibly using a struct to get all the info that is supposed to be displayed in the UI
    Alarm GetAlarm(int index)
    {
        return m_alarmList.get(index);
    }

    void Debug_PrintFileContents(Context context)
    {
        try
        {
            InputStream is = context.openFileInput(m_saveFile);
            if(is != null)
            {
                Scanner scan = new Scanner(is);
                System.out.println("reading save file: ");
                while(scan.hasNext())
                {
                    String print = scan.next();
                    System.out.println("_"+print);
                }
            }
        }
        catch(Exception e)
        {
            Log.e("ArteneApp", "Exception in Debug file print: ", e);
        }
    }

    private Alarm CompactAlarm(Context context, View view)
    {
        //TODO extract alarm page info into Alarm object
        Alarm newAlarm = new Alarm();

        String medName;
        String dosage;
        String notes;
        String frequency;
        Calendar startCal;

        TextView nameValue = (TextView) view.findViewById(R.id.medNameText);
        TextView dosageValue = (TextView) view.findViewById(R.id.DosageInput_Text);
        TextView notesValue = (TextView) view.findViewById(R.id.other_details);
        Switch frequncyValue = (Switch) view.findViewById(R.id.alarmDuration_switch);

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
        newAlarm.SetStartCal(Calendar.getInstance());
        return newAlarm;
    }
}
