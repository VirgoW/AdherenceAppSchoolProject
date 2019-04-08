package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.io.File;
import java.text.DateFormat;
import java.util.Calendar;


public class ReportPage extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_page_layout);
        Button shareReportBtn = findViewById(R.id.downloadReportButton);
        final Context context = getApplicationContext();

        GridView gridView = (GridView) findViewById(R.id.reportPageGridView);
        ReportAdapter gridViewAdapter = new ReportAdapter(context);
        gridView.setAdapter(gridViewAdapter);

        shareReportBtn.setOnClickListener(new View.OnClickListener() {
            /*
                On click listener for the share report button.
                This method will take the internally stored information about the treatments and share it with an option selected by the user.
                The method will get the filepath to the save file (used by save manager), then get the URI of that file using the FIleProvider
                If this action succeeds, a Send activity is created for the user to choose a way to share the file.
            */
            @Override
            public void onClick(View v) {
                String fileName = getText(R.string.reportSaveFileName).toString();
                File requestFile =  AlarmHelperClass.CreateReportFile(context, fileName);
                Uri fileUri = null;
                try
                {
                    fileUri = FileProvider.getUriForFile(context, "ro.umfcd.stud.arteneraluca.myapplication.fileprovider", requestFile);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }

                if(fileUri != null)
                {
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("plain/*");
                    myIntent.putExtra(Intent.EXTRA_TEXT, "Raport informatii tratamente");
                    myIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    myIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(myIntent, "Raport informatii tratamente"));
                }


            }
        });


        TextView reportCurrentDateValue = (TextView) findViewById(R.id.reportCurrentDateValue);

        Calendar currentDate = Calendar.getInstance(); // the value to be formatted
        java.text.DateFormat formatter = java.text.DateFormat.getDateInstance(
                DateFormat.SHORT); // one of SHORT, MEDIUM, LONG, FULL, or DEFAULT
        formatter.setTimeZone(currentDate.getTimeZone());
        String formatted = formatter.format(currentDate.getTime());

        reportCurrentDateValue.setText(" " + formatted);
    }
}
