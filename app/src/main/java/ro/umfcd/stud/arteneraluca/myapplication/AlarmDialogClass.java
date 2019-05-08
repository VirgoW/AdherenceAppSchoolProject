package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmDialogClass extends Activity {

    AlertDialog newDialog;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This will make the dialog to appear on the screen even if the phone is locked

       /*getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                |WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                |WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                |WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
       */

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        //uses permission in android manifest
        if(Build.VERSION.SDK_INT >= 26) //This doesn't do anything from what I notice
        {
            getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
        }
        else
        {
            getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }

        Intent thisIntent = getIntent();
        int treatmentIndex = thisIntent.getIntExtra("treatmentIndex", -1);
        Treatment treatment = SaveManager.getInstance().GetAlarm(treatmentIndex);


        newDialog = CreateAlertDialog(treatment,thisIntent);
        newDialog.show();
        SetDialogTitle(newDialog,treatment);
        ModifyDialogProperties(newDialog,treatment);

        //Start Ringtone service to play treatment sound
        if(newDialog != null)
        {
            Intent startIntent = new Intent(context, RingtoneCustomService.class);
            context.startService(startIntent);
        }

    }
    // Build the dialog and set up the button click handlers
    AlertDialog CreateAlertDialog(final Treatment treatment, final Intent intent){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        //Inflate a title template
        LayoutInflater inflater = this.getLayoutInflater();
        builder.setCustomTitle(inflater.inflate(R.layout.alarm_dialog_title,null));
        builder.setMessage(GetDialogMessage(treatment));

        builder.setPositiveButton(R.string.confirmButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Stop Ringtone service
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
                treatment.IncreaseConfirmedCount();
                SaveManager.getInstance().SaveDataToXml(context);
                newDialog.dismiss();
                finish();
            }
        })
                .setNegativeButton(R.string.denyButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
                treatment.IncreaseDeniedCount();
                SaveManager.getInstance().SaveDataToXml(context);
                newDialog.cancel();
                finish();
            }
        })
                .setNeutralButton(R.string.snoozeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
                treatment.IncreaseDelayedCount();
                SaveManager.getInstance().SaveDataToXml(context);
                Snooze(intent);
                newDialog.dismiss();
                finish();
            }
        });
        return builder.create();
    }

    void SetDialogTitle(AlertDialog dialog, Treatment treatment)
    {
        TextView titleTextView = (TextView) dialog.findViewById(R.id.dialogTitleView);
        titleTextView.setText(GetTitleMessage(treatment));
        titleTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.dialog_text_size));
    }

    String GetDialogMessage(Treatment treatment)
    {
        String dialogMessage = "\nDoză: ";
        String buffer = treatment.GetDosage();
        dialogMessage += buffer;
        dialogMessage += "\n\nAlte detalii: \n";
        buffer = treatment.GetNote();
        dialogMessage += buffer;
        return dialogMessage;
    }

    String GetTitleMessage(Treatment treatment)
    {
        String titleMessage = " E timpul să luați \n";
        String buffer = treatment.GetMedName();
        titleMessage += buffer;
        return titleMessage;
    }

    void ModifyDialogProperties(AlertDialog dialog, Treatment treatment)
    {
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);

        if(treatment.IsAlarmDelayable()) {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
        }
        else {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
        }

        //TODO set the size from resources
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextSize(TypedValue.COMPLEX_UNIT_SP, 25.0f);

        TextView messageTextView = (TextView) dialog.findViewById(android.R.id.message);
        messageTextView.setTextSize(getResources().getDimensionPixelSize(R.dimen.dialog_text_size));

        ImageButton stopSoundBtn = (ImageButton) dialog.findViewById(R.id.soundOffBtn);
        stopSoundBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
            }
        });
    }

    void Snooze(Intent intent){
        int alarmID = intent.getIntExtra("alarmID",-1);
        intent.putExtra("alarmType", R.string.alarmActivate);
        intent.setClass(context, AlarmReceiver.class);


        if(alarmID == -1)
        {
            return;
        }
        Calendar currentCal = Calendar.getInstance();
        long snoozeTime = currentCal.getTimeInMillis() + TimeUnit.MINUTES.toMillis(30);
        //TODO increase snoozeTime to 30 minutes for the release version

        PendingIntent snoozePendInt = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT); 
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP,snoozeTime, snoozePendInt);

    }


}
