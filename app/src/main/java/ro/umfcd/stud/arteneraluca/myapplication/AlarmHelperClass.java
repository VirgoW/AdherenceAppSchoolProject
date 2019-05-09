package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;

public class AlarmHelperClass {

    /*
        Static method that cancels an alert
        @Param context - The context from which the method is called
        @Param id - The id of the alert that needs be canceled
        @Param intent - The intent that needs to be canceled
     */
    public static void CancelIntentWithId(Context context, int id, Intent intent)
    {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null)
        {
            alarmMgr.cancel(pendingIntent);
        }
    }

    public static boolean HasTreatmentEnded(Treatment treatment)
    {
        Calendar treatmentEndCalendar = null;
        Calendar todayCal = Calendar.getInstance();
        boolean isFixedTimeTreatment = treatment.IsFixedTimeTreatment();
        if(isFixedTimeTreatment)
        {
            treatmentEndCalendar = treatment.GetEndCal();
        }
        if(treatmentEndCalendar != null && isFixedTimeTreatment && CalendarAAfterCalendarB(todayCal, treatmentEndCalendar))
        {
            return true;
        }
        return false;
    }

    public static Boolean CalendarAAfterCalendarB(Calendar calA, Calendar calB)
    {
        try
        {
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

    public static File CreateReportFile(Context context, String fileName)
    {
        File path = context.getFilesDir();
        File reportFile = new File(path, fileName);

        try
        {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter (context.openFileOutput(fileName, MODE_PRIVATE)));
            //Report file start
            writer.write(context.getString(R.string.ReportFileStart));
            writer.newLine();
            writer.newLine();
            
            for(int i=0; i< SaveManager.getInstance().GetAlarmCount(); i++)
            {
                writer.write(context.getString(R.string.ReportTreatmentTag));
                writer.newLine();

                Treatment treatment = SaveManager.getInstance().GetAlarmByIndex(i);
                writer.write(treatment.GetMedName());
                writer.newLine();

                Calendar startDate = treatment.GetStartCal(); // the value to be formatted
                java.text.DateFormat formatter = java.text.DateFormat.getDateInstance(DateFormat.SHORT);
                formatter.setTimeZone(startDate.getTimeZone());
                String formattedStartDate = context.getString(R.string.startDateTextView) + formatter.format(startDate.getTime());
                writer.write(formattedStartDate);
                writer.newLine();

                writer.write(context.getString(R.string.confirmedDosesTextView) + treatment.GetConfirmedCount());
                writer.newLine();
                writer.write(context.getString(R.string.missedDosesTextView) + treatment.GetDeniedCount());
                writer.newLine();
                writer.newLine();
            }

            //Report file end
            writer.close();
        }
        catch(Exception e)
        {
            Log.e("ArteneApp", "Exception occurred while creating / opening the alarm report file file: ", e);
        }

        return reportFile;
    }

    public static void EnableBootReceiver(Context context)
    {

        ComponentName componentName = new ComponentName(context, BootReceiver.class);
        PackageManager packageManager = context.getPackageManager();
        if(packageManager.getComponentEnabledSetting(componentName) != PackageManager.COMPONENT_ENABLED_STATE_ENABLED)
        {
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
            Log.i("ArteneApp","BootReceiver enabled");
        }


    }

    public static void DisableBootReceiver(Context context)
    {
            ComponentName componentName = new ComponentName(context, BootReceiver.class);
            PackageManager packageManager = context.getPackageManager();
            packageManager.setComponentEnabledSetting(componentName,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                    PackageManager.DONT_KILL_APP);

        Log.i("ArteneApp","BootReceiver disabled");
    }

    public static int GetTreatmentAlarmId(int treatmentIndex)
    {
        return treatmentIndex+1;
    }

    public static int GetTreatmentHourAlarmId(int treatmentIndex, int hourIndex)
    {
        return (treatmentIndex + 1) * 1000 + hourIndex;
    }

    public static int GetTreatmentWeekDayAlarmId(int treatmentIndex, int weekDayIndex, int hourIndex)
    {
        return ((treatmentIndex + 1) * 1000 + weekDayIndex) * 1000 + hourIndex;
    }
}
