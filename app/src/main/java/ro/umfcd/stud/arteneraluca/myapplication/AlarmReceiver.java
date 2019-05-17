package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

public class AlarmReceiver extends BroadcastReceiver {
    /*
        This method will be called when the system reaches a set point in time at which an alert was registered in the alarm manager.
        @Param context - The activity context
        @param intent - The intent which holds the alarm information
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        int alarmType;
        int treatmentIndex;

        alarmType = intent.getIntExtra("alarmType", -1);
        treatmentIndex = intent.getIntExtra("treatmentIndex", -1);


        //Make sure the app has the data loaded when trying to set or activate alarms.
        if(SaveManager.getInstance().GetAlarmCount() == 0)
        {
            SaveManager.getInstance().InitSave(context);
        }

        if (alarmType == R.string.alarmActivate) {
            Intent alarmIntent = new Intent("android.intent.action.DIALOG");
            int alarmID = intent.getIntExtra("alarmID", -1);
            alarmIntent.putExtra("alarmID", alarmID);

            ActivateAlarm(context, alarmIntent, treatmentIndex);
        }
        else {
            SetAlarms(context, treatmentIndex);
        }
    }

    /*
        Activate the alert dialog for a set event to take the dose of a treatment

        @Param context - Activity context
        @Param intent - The intent that holds the information of the alarm
        @Param alarmIntent - The intent used to create an alert dialog with the treatment information
     */
    private void ActivateAlarm(Context context, Intent alarmIntent, int treatmentIndex) {
        alarmIntent.putExtra("treatmentIndex", treatmentIndex);
        alarmIntent.setClass(context, AlarmDialogClass.class);
        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }

    private void SetAlarms(Context context, int treatmentIndex) {
        Treatment treatment = SaveManager.getInstance().GetAlarm(treatmentIndex);

        //Check if treatment has ended and if so, don't set a treatment anymore for it.
        if (AlarmHelperClass.HasTreatmentEnded(treatment)) {
            return;
        }
        if (treatment.IsDailyTreatment()) {
            ScheduleDailyAlarm(context, treatment);
        } else {
            ScheduleWeeklyAlarm(context, treatment);
        }
    }

    /*
        Schedule an event next day at 00:00 to schedule an treatment for that day and continue doing so until canceled
        Schedule events for the treatment that can still happen today

        @param context - The activity context
        @param treatment - The treatment for which we will set an treatment
     */
    private void ScheduleDailyAlarm(Context context, Treatment treatment) {
        int treatmentIndex = treatment.getId();
        Intent newIntent = new Intent(context, AlarmReceiver.class);
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        //Schedule event for next midnight
        newIntent.putExtra("alarmType", R.string.alarmSet);
        newIntent.putExtra("treatmentIndex", treatment.getId());

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, AlarmHelperClass.GetTreatmentAlarmId(treatmentIndex), newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar nextMidnight = Calendar.getInstance();
        nextMidnight.add(Calendar.DATE, 1);
        nextMidnight.set(Calendar.HOUR_OF_DAY, 0);
        nextMidnight.set(Calendar.MINUTE, 0);
        nextMidnight.set(Calendar.SECOND, 1);
        boolean fixedTreatment = treatment.IsFixedTimeTreatment();
        if (!(fixedTreatment && AlarmHelperClass.CalendarAAfterCalendarB(nextMidnight, treatment.GetEndCal()))) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, nextMidnight.getTimeInMillis(), pendingAlarmIntent);
        }

        //Schedule alarms for today

        Calendar alarmStartDate = treatment.GetStartCal();
        Calendar todayCal = Calendar.getInstance();

        if(!AlarmHelperClass.CalendarAAfterCalendarB(alarmStartDate,todayCal)) {
            newIntent = new Intent(context, AlarmReceiver.class);
            int alarmId;
            for (int indexHour = 0; indexHour < treatment.m_dailyFrequency.size(); indexHour++) {
                newIntent.putExtra("alarmType", R.string.alarmActivate);
                newIntent.putExtra("treatmentIndex", treatmentIndex);

                String hourString = treatment.m_dailyFrequency.get(indexHour);

                int hour = Integer.parseInt(hourString.substring(0, 2));
                int minute = Integer.parseInt(hourString.substring(3));

                Calendar alertCalendar = Calendar.getInstance();
                alertCalendar.setTimeInMillis(System.currentTimeMillis());
                alertCalendar.set(Calendar.HOUR_OF_DAY, hour);
                alertCalendar.set(Calendar.MINUTE, minute);
                alertCalendar.set(Calendar.SECOND, 0);

                if (AlarmHelperClass.CalendarAAfterCalendarB(todayCal, alertCalendar)) {
                    continue;
                }

                alarmId = AlarmHelperClass.GetTreatmentHourAlarmId(treatmentIndex, indexHour);
                newIntent.putExtra("alarmID", alarmId);


                pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmId, newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alertCalendar.getTimeInMillis(), pendingAlarmIntent);
            }
        }
    }

    /*
        Schedule alarms of weekly treatment for this week.
        Set event for next monday at 00:00 to schedule next week alarms.

        @param context - The activity context
        @param treatment - The treatment for which we will set an treatment
     */
    private void ScheduleWeeklyAlarm(Context context, Treatment treatment) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        int treatmentIndex = treatment.getId();
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        boolean fixedTreatment = treatment.IsFixedTimeTreatment();

        //Set an treatment next monday at 00:00 to set once alarms for that week.
        alarmIntent.putExtra("alarmType", R.string.alarmSet);
        alarmIntent.putExtra("treatmentIndex", treatmentIndex);

        Calendar nextMondayCal = Calendar.getInstance();
        do {
            nextMondayCal.add(Calendar.DATE, 1);
        } while (nextMondayCal.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY);
        nextMondayCal.set(Calendar.HOUR_OF_DAY, 0);
        nextMondayCal.set(Calendar.MINUTE, 0);

        PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, AlarmHelperClass.GetTreatmentAlarmId(treatmentIndex), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (!(fixedTreatment && AlarmHelperClass.CalendarAAfterCalendarB(nextMondayCal, treatment.GetEndCal()))) {
            alarmMgr.set(AlarmManager.RTC_WAKEUP, nextMondayCal.getTimeInMillis(), pendingAlarmIntent);
        }

        //Set alarms for this week
        Calendar todayCal = Calendar.getInstance();
        Calendar alarmStartDate = treatment.GetStartCal();

        int today = SaveManager.getInstance().GetDayOfWeek(todayCal);
        int alarmId;
        alarmIntent = new Intent(context, AlarmReceiver.class);
        for (int indexDay = today; indexDay < 7; indexDay++) {
            if (treatment.m_weeklyDayFrequency.get(indexDay)) {

                Calendar indexDayCal = Calendar.getInstance();
                indexDayCal.set(Calendar.DAY_OF_WEEK,indexDay);

                if(!AlarmHelperClass.CalendarAAfterCalendarB(alarmStartDate,indexDayCal)) {
                    for (int indexHour = 0; indexHour < treatment.m_dailyFrequency.size(); indexHour++) {
                        alarmIntent.putExtra("alarmType", R.string.alarmActivate);
                        alarmIntent.putExtra("treatmentIndex", treatmentIndex);

                        Calendar alarmCal = Calendar.getInstance();
                        String hourString = treatment.m_dailyFrequency.get(indexHour);
                        alarmCal.setTimeInMillis(System.currentTimeMillis());
                        int hour = Integer.parseInt(hourString.substring(0, 2));
                        int minute = Integer.parseInt(hourString.substring(3));
                        alarmCal.set(Calendar.HOUR_OF_DAY, hour);
                        alarmCal.set(Calendar.MINUTE, minute);
                        alarmCal.add(Calendar.DATE, indexDay - today);

                        //Skip today's alarms
                        if (today == indexDay && todayCal.after(alarmCal) || (fixedTreatment && AlarmHelperClass.CalendarAAfterCalendarB(todayCal, treatment.GetEndCal()))) {
                            continue;
                        }

                        //Set treatment
                        alarmId = AlarmHelperClass.GetTreatmentWeekDayAlarmId(treatmentIndex, indexDay, indexHour);
                        alarmIntent.putExtra("alarmID", alarmId);
                        pendingAlarmIntent = PendingIntent.getBroadcast(context, alarmId, alarmIntent, 0);
                        alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmCal.getTimeInMillis(), pendingAlarmIntent);
                    }
                }
            }
        }
    }
}