package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class AlarmDialogClass extends Activity {
    //AlertDialog.Builder mAlertDlgBuilder;
    //AlertDialog mAlertDialog;
    AlertDialog newDialog;
    //View mDialogView = null;
    //Button mOKBtn, mCancelBtn;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent thisIntent = getIntent();
        int treatmentIndex = thisIntent.getIntExtra("treatmentIndex", -1);
        Treatment treatment = SaveManager.getInstance().GetAlarm(treatmentIndex);

        String alarmDialogTitle = treatment.GetMedName();
        String alarmDialogMessage = GetDialogMessage(treatment);
        //Start Ringtone service to play treatment sound
        Intent startIntent = new Intent(context, RingtoneCustomService.class);
        context.startService(startIntent);

        newDialog = CreateAlertDialog(alarmDialogTitle,alarmDialogMessage,treatment,thisIntent);
        newDialog.show();
        ModifyDialogProperties(newDialog,treatment);


    }

    AlertDialog CreateAlertDialog(String title, String message, final Treatment treatment, final Intent intent){
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.confirmButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Stop Ringtone service
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
                treatment.IncreaseConfirmedCount();
                newDialog.dismiss();
                finish();
            }
        })
                .setNegativeButton(R.string.denyButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
                treatment.IncreaseDeniedCount();
                newDialog.cancel();
                finish();
            }
        })
                .setNeutralButton(R.string.snoozeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
                treatment.IncreaseDelayedCount();
                //TODO snooze functionality
                Snooze(intent);
                newDialog.dismiss();
                finish();
            }
        });
        return builder.create();
    }

    String GetDialogMessage(Treatment treatment)
    {
        String dialogMessage = "E timpul să luați medicamentul!" + "\nDoză: ";
        String buffer = treatment.GetDosage();
        dialogMessage += buffer;
        dialogMessage += "\n\nAlte detalii: \n";
        buffer = treatment.GetNote();
        dialogMessage += buffer;
        return dialogMessage;
    }

    void ModifyDialogProperties(AlertDialog dialog, Treatment treatment)
    {
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        ((TextView) dialog.findViewById(android.R.id.message))
                .setTextSize(getResources().getDimensionPixelSize(R.dimen.dialog_text_size));

        if(treatment.IsAlarmDelayable()) {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(true);
        }
        else {
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
        }

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
        long snoozeTime = currentCal.getTimeInMillis() + TimeUnit.SECONDS.toMillis(10);

        PendingIntent snoozePendInt = PendingIntent.getBroadcast(context, alarmID, intent, PendingIntent.FLAG_UPDATE_CURRENT); 
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.set(AlarmManager.RTC_WAKEUP,snoozeTime, snoozePendInt);

    }


}
