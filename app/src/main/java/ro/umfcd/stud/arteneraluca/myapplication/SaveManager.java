package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class SaveManager {
    private static final SaveManager ourInstance = new SaveManager();
    private XmlPullParserFactory m_XmlFactoryObject;
    private XmlPullParser m_XmlParser;
    private boolean m_XmlParserInitialized;

    private ArrayList<Alarm> m_alarmList;

    String m_testFile;


    public static SaveManager getInstance() {
        return ourInstance;
    }

    private SaveManager() {
        try {
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
        m_testFile = context.getText(R.string.testSave).toString();

        File path = context.getFilesDir();
        File file = new File(path, m_testFile);

        //Check and create the save file if it does not exist.
        if(file.exists())
        {
            ParseXmlFile(context);
        }
        else
        {
            try{
                OutputStreamWriter os = new OutputStreamWriter (context.openFileOutput(m_testFile, MODE_PRIVATE));
                os.close();
            }
            catch(Exception e)
            {
                Log.e("ArteneApp", "Exception occured in SaveManager while creating / opening the save file: ", e);
            }
        }


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

                AddNewTag(serializer, context.getText(R.string.medNameTag).toString(), "name", m_alarmList.get(index).m_medName);

                serializer.endTag("","Alarm");
            }
            serializer.endDocument();
            //End document writing
            String result = writer.toString();

            OutputStreamWriter  os = new OutputStreamWriter (context.openFileOutput(m_testFile, MODE_PRIVATE));
            os.write(result);
            System.out.println("Wrote xml file with value: " + result);
            os.close();


            InputStream is = context.openFileInput(m_testFile);
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
            is.close();
        }
        catch (Exception e)
        {
            Log.e("ArteneApp", "Exception occured while writing the XML: ", e);
        }
    }

    void AddNewAlarm(View v, Context context)
    {
        String medName = ((TextView)v.findViewById(R.id.medNameText)).getText().toString();
        m_alarmList.add(new Alarm(medName));
        SaveDataToXml(context);
    }

    void ParseXmlFile(Context context)
    {
        m_alarmList.clear();

        try{
            InputStream is = context.openFileInput(m_testFile);
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

            FileInputStream fs = context.openFileInput(m_testFile);
            m_XmlParser.setInput(fs, null);
            int event = m_XmlParser.getEventType();
            while( event != XmlPullParser.END_DOCUMENT)
            {
                Alarm newAlarm = new Alarm();
                String name = m_XmlParser.getName();
                System.out.println(name);

                switch(event)
                {
                    case XmlPullParser.START_TAG:
                    break;
                    case XmlPullParser.END_TAG:
                        if(name.equals(context.getText(R.string.medNameTag).toString()))
                        {
                            newAlarm.m_medName = m_XmlParser.getAttributeValue(null,"name");
                        }
                }
                event = m_XmlParser.next();
                if(!newAlarm.m_medName.isEmpty())
                {
                    m_alarmList.add(newAlarm);
                }
            }
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

    String GetAlarm(int index)
    {
        return m_alarmList.get(index).m_medName;
    }

}
