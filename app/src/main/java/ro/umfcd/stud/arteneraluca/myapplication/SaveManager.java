package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.util.Xml;
import android.view.View;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class SaveManager {
    private static final SaveManager ourInstance = new SaveManager();
    private XmlPullParserFactory m_XmlFactoryObject;
    private XmlPullParser m_XmlParser;
    private boolean m_XmlParserInitialized;

    String m_testFile;


    public static SaveManager getInstance() {
        return ourInstance;
    }

    private SaveManager() {
        try {
            m_XmlFactoryObject = XmlPullParserFactory.newInstance();
            m_XmlParser = m_XmlFactoryObject.newPullParser();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        //instantiate XMLPullParser
    }

    String test;

    public void CreateFileSave(Context context)
    {
        File directory;
        m_testFile = context.getText(R.string.testSave).toString();
        directory = context.getDir(m_testFile, MODE_PRIVATE);
    }

    void TestSaveData(View v, Context context)
    {
        String testTxtToSave = ((TextView)v.findViewById(R.id.testTextInput)).getText().toString();
        try
        {
            FileOutputStream fo = context.openFileOutput(m_testFile, MODE_PRIVATE);
            fo.write(testTxtToSave.getBytes());
            fo.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    void SaveDataAlarmSet(View v, Context context)
    {
        try
        {
            String medName = ((TextView)v.findViewById(R.id.medNameText)).getText().toString();

            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            serializer.setOutput(writer);
            //Start document writing
            serializer.startDocument("UTF-8", true);
            serializer.startTag("","Alarm");

            serializer.startTag("", "MedName");
            serializer.attribute("", "name", medName);
            serializer.endTag("", "MedName");

            serializer.endTag("","Alarm");
            serializer.endDocument();
            //End document writing
            String result = writer.toString();

            FileOutputStream fo = context.openFileOutput(m_testFile, MODE_PRIVATE);
            fo.write(result.getBytes());
            System.out.println("Wrote xml file with value: " + result);
            fo.close();
        }
        catch (Exception e)
        {
            System.out.println(e.getStackTrace());
        }
    }

    void ParseXmlFile(View v, Context context)
    {
        String loadText = "";
        try{
            FileInputStream fs = context.openFileInput(m_testFile);
            m_XmlParser.setInput(fs, null);
            System.out.println("Begining xml parsing");
            int event = m_XmlParser.getEventType();
            while( event != XmlPullParser.END_DOCUMENT)
            {
                System.out.println("Event "+ event);
                String name = m_XmlParser.getName();
                System.out.println(name);

                switch(event)
                {
                    case XmlPullParser.START_TAG:
                    break;
                    case XmlPullParser.END_TAG:
                        if(name.equals("MedName"))
                        {
                            System.out.println("Number of attributes: " + m_XmlParser.getAttributeCount());
                            loadText = m_XmlParser.getAttributeValue(null,"name");
                        }
                }

                event = m_XmlParser.next();
            }
            System.out.println("Xml parsing finished");

            if(!loadText.isEmpty())
            {
                ((TextView) v.findViewById(R.id.testOutputText)).setText(loadText);
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    void TestLoadData(View v, Context context)
    {
        String content = "";
        try
        {
            FileInputStream fs = context.openFileInput(m_testFile);
            Scanner scan = new Scanner(fs);
            content = scan.next();
            scan.close();
            System.out.println(" Content of file is : " + content);
            if(!content.isEmpty())
            {
                ((TextView) v.findViewById(R.id.testOutputText)).setText(content);
            }
        }
        catch   (Exception e)
        {
            System.out.println(e);
        }
    }


}
