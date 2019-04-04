package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    /*
        This method will be called when the system reaches a set point in time at which an alert was registered in the alarm manager.
        @Param context - The activity context
        @param intent - The intent which holds the alarm information
     */
    @Override
    public void onReceive(Context context, Intent intent)
    {
        int alarmType ;
        alarmType = intent.getIntExtra("alarmType", -1);
        if(alarmType == R.string.alarmActivate)
        {
            Intent alarmIntent = new Intent("android.intent.action.DIALOG");
            ActivateAlarm(context, intent, alarmIntent);
        }
        else
        {
            SetAlarms(context, intent);
        }
    }

    /*
        Activate the alert dialog for a set event to take the dose of a treatment

        @Param context - Activity context
        @Param intent - The intent that holds the information of the alarm
        @Param alarmIntent - The intent used to create an alert dialog with the treatment information
     */
    private void ActivateAlarm(Context context, Intent intent, Intent alarmIntent)
    {
        int alarmId = 0;
        String buffer;
        alarmId = intent.getIntExtra("alarmID", 0);
        buffer = intent.getStringExtra("medName");
        alarmIntent.putExtra("test", alarmId);
        alarmIntent.putExtra("medName", buffer);
        buffer = intent.getStringExtra("hour");
        alarmIntent.putExtra("hour", buffer);
        alarmIntent.setClass(context, AlertDialogClass.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }

    private void SetAlarms(Context context, Intent intent)
    {
        Alarm alarm = new Alarm();
        if(SaveManager.getInstance().DeserializeAlarmFromIntent(intent, alarm))
        {
            //Check if treatment has ended and if so, don't set an alarm anymore for it.
            if(AlarmHelperClass.HasTreatmentEnded(alarm))
            {
                return;
            }
            if(alarm.IsDailyTreatment())
            {
                ScheduleDailyAlarm(context, alarm);
            }
            else
            {
                ScheduleWeeklyAlarm(context, alarm);
            }
        }
    }

    /*
        Schedule an event next day at 00:00 to schedule an alarm for that day and continue doing so until canceled
        Schedule events for the treatment that can still happen today

        @param context - The activity context
        @param alarm - The treatment for which we will set an alarm
     */
    private void ScheduleDailyAlarm(Context context, Alarm alarm)
    {
        Intent newIntent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Schedule event for next midnight
        newIntent.putExtra("alarmType", R.string.alarmSet);
        SaveManager.getInstance().SerializeAlarmIntoIntent(newIntent, alarm);
        int alarmIndex = alarm.getId();
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmIndex, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar nextMidnight = Calendar.getInstance();
        nextMidnight.add(Calendar.DATE, 1);
        nextMidnight.set(Calendar.HOUR_OF_DAY, 0);
        nextMidnight.set(Calendar.MINUTE, 0);
        boolean fixedTreatment = alarm.IsFixedTimeTreatment();
        if(! (fixedTreatment && AlarmHelperClass.CalendarAAfterCalendarB(nextMidnight, alarm.GetEndCal())) )
        {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, nextMidnight.getTimeInMillis(), pendingAlarmIntent);
        }

        //Schedule alarms for today
        newIntent = new Intent(context, AlarmReceiver.class);
        int alarmId;
        for(int indexHour = 0 ; indexHour < alarm.m_dailyFrequency.size(); indexHour++)
        {
            alarmId = alarmIndex * 10 + indexHour;
            newIntent.putExtra("alarmType", R.string.alarmActivate);
            newIntent.putExtra("alarmID", alarmId);
            newIntent.putExtra("medName", alarm.GetMedName());
            newIntent.putExtra("hour", alarm.m_dailyFrequency.get(indexHour));

            String hourString = alarm.m_dailyFrequency.get(indexHour);
            int hour = Integer.parseInt(hourString.substring(0,2));
            int minute = Integer.parseInt(hourString.substring(3));

            Calendar alertCalendar = Calendar.getInstance();
            alertCalendar.setTimeInMillis(System.currentTimeMillis());
            alertCalendar.set(Calendar.HOUR_OF_DAY, hour);
            alertCalendar.set(Calendar.MINUTE, minute);
            alertCalendar.set(Calendar.SECOND, 0);

            Calendar todayCal = Calendar.getInstance();
            if(AlarmHelperClass.CalendarAAfterCalendarB(todayCal, alertCalendar))
            {
                continue;
            }

            pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alertCalendar.getTimeInMillis(), pendingAlarmIntent);
        }
    }

    /*
        Schedule alarms of weekly treatment for this week.
        Set event for next monday at 00:00 to schedule next week alarms.

        @param context - The activity context
        @param alarm - The treatment for which we will set an alarm
     */
    private void ScheduleWeeklyAlarm(Context context, Alarm alarm)
    {
        Intent newIntent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        boolean fixedTreatment = alarm.IsFixedTimeTreatment();

        //Set an alarm next monday at 00:00 to set once alarms for that week.
        newIntent.putExtra("alarmType", R.string.alarmSet);
        SaveManager.getInstance().SerializeAlarmIntoIntent(newIntent, alarm);

        int alarmIndex = alarm.getId();
        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmIndex, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar nextMondayCal = Calendar.getInstance();
        do
        {
            nextMondayCal.add(Calendar.DATE, 1);
        } while(nextMondayCal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY);
        nextMondayCal.set(Calendar.HOUR_OF_DAY, 0);
        nextMondayCal.set(Calendar.MINUTE, 0);

        if(! (fixedTreatment && AlarmHelperClass.CalendarAAfterCalendarB(nextMondayCal, alarm.GetEndCal())) )
        {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, nextMondayCal.getTimeInMillis(), pendingAlarmIntent);
        }

        //Set alarms for this week
        Calendar todayCal = Calendar.getInstance();

        int today = SaveManager.getInstance().GetDayOfWeek(todayCal);
        int alarmId;
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        for (int indexDay = today; indexDay < 7; indexDay++)
        {
            if (alarm.m_weeklyDayFrequency.get(indexDay))
            {
                for (int indexHour = 0; indexHour < alarm.m_dailyFrequency.size(); indexHour++)
                {
                    alarmId = alarmIndex * 10 + indexDay;
                    alarmId = alarmId * 10 + indexHour;
                    //fill alarm with info
                    alarmIntent.putExtra("alarmType", R.string.alarmActivate);
                    alarmIntent.putExtra("alarmID", alarmId);
                    alarmIntent.putExtra("medName", alarm.GetMedName());
                    String hourString = alarm.m_dailyFrequency.get(indexHour);
                    alarmIntent.putExtra("hour", hourString);

                    Calendar alarmCal = Calendar.getInstance();
                    alarmCal.setTimeInMillis(System.currentTimeMillis());
                    int hour = Integer.parseInt(hourString.substring(0,2));
                    int minute = Integer.parseInt(hourString.substring(3));
                    alarmCal.set(Calendar.HOUR_OF_DAY, hour);
                    alarmCal.set(Calendar.MINUTE, minute);
                    alarmCal.add(Calendar.DATE, indexDay - today);
                    //Skip today's alarms
                    if(today == indexDay && todayCal.after(alarmCal) || (fixedTreatment && AlarmHelperClass.CalendarAAfterCalendarB(todayCal, alarm.GetEndCal())))
                    {
                        continue;
                    }

                    //Set alarm
                    pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmId, alarmIntent, 0);
                    alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), pendingAlarmIntent);
                }
            }
        }
    }
}
