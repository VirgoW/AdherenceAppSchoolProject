package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.Date;

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
}
