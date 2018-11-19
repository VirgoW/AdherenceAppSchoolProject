package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;

import static android.content.Context.MODE_PRIVATE;

public class SaveManager {
    private static final SaveManager ourInstance = new SaveManager();

    public static SaveManager getInstance() {
        return ourInstance;
    }

    private SaveManager() {
        //instantiate XMLPullParser
    }

    String test;

    public void CreateFileSave(Context context)
    {
        File directory;
        String filename = context.getText(R.string.testSave).toString();
        directory = context.getDir(filename, MODE_PRIVATE);
    }

    void SaveData(View v, Context context)
    {
        String testTxtToSave = ((TextView)v.findViewById(R.id.testTextInput)).getText().toString();
        String filename = context.getText(R.string.testSave).toString();
        try
        {
            FileOutputStream fo = context.openFileOutput(filename, MODE_PRIVATE);
            fo.write(testTxtToSave.getBytes());
            fo.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    void LoadData(View v, Context context)
    {
        String filename = context.getText(R.string.testSave).toString();
        String content = "";
        try
        {
            FileInputStream fs = context.openFileInput(filename);
            Scanner scan = new Scanner(fs);
            content = scan.next();
            scan.close();
            System.out.println(" Content of file is : " + content);
        }
        catch   (Exception e)
        {
            System.out.println(e);
        }
        if(!content.isEmpty())
        {
            ((TextView) v.findViewById(R.id.testOutputText)).setText(content);
        }
    }


}
