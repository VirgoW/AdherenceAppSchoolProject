package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent alarmIntent = new Intent("android.intent.action.DIALOG");
        int alarmType ;
        alarmType = intent.getIntExtra("alarmType", -1);
        if(alarmType == R.string.alarmActivate)
        {
            ActivateAlarm(context, intent, alarmIntent);

        }
        else
        {
            SetAlarms(context, intent);
        }
    }

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

            if(! (fixedTreatment && SaveManager.getInstance().CalendarAAfterCalendarB(context, nextMondayCal, alarm.GetEndCal())) )
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
                        if(today == indexDay && todayCal.after(alarmCal) || (fixedTreatment && SaveManager.getInstance().CalendarAAfterCalendarB(context, todayCal, alarm.GetEndCal())))
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
}
