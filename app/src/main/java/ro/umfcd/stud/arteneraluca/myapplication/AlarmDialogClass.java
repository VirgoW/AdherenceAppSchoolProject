package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

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
        int alarmIndex = thisIntent.getIntExtra("treatmentIndex", -1);
        Treatment treatment = SaveManager.getInstance().GetAlarm(alarmIndex);

        String alarmDialogTitle = treatment.GetMedName();
        String alarmDialogMessage = GetDialogMessage(treatment);
        //Start Ringtone service to play treatment sound
        Intent startIntent = new Intent(context, RingtoneCustomService.class);
        context.startService(startIntent);
        newDialog = CreateAlertDialog(alarmDialogTitle,alarmDialogMessage);
        newDialog.show();
        ModifyDialogProperties(newDialog);

        //LayoutInflater inflater = getLayoutInflater();
        // Build the dialog

        //mAlertDlgBuilder = new AlertDialog.Builder(this);
        //mDialogView = inflater.inflate(R.layout.alarm_dialog_layout, null);
        //mOKBtn = (Button)mDialogView.findViewById(R.id.confirmTakingBtn);
        //TextView text = (TextView) mDialogView.findViewById(R.id.AlarmDialogText);


        //Intent thisIntent = getIntent();
        //int alarmId = 0;    //Do we even need this?
        //String buffer;
        //alarmId = thisIntent.getIntExtra("alarmID", 0);
        //String textValue = getString(R.string.testAlarmDialogText) + alarmId;
        //buffer = thisIntent.getStringExtra("medName");
        //textValue +=". Medication = " + buffer;
        //buffer = thisIntent.getStringExtra("hour");
        //textValue +=". Hour = " + buffer;
        //text.setText(textValue);


        //mOKBtn.setOnClickListener(new View.OnClickListener() {
           // @Override
           // public void onClick(View v) {
            //    if(v.getId() == R.id.confirmTakingBtn)
             //   {
             //       mAlertDialog.dismiss();
                    //mAlertDialog.cancel();

                    //Stop Ringtone service
               //     Intent stopIntent = new Intent(context, RingtoneCustomService.class);
               //     context.stopService(stopIntent);
               //     finish();
      //          }
      //      }
      //  });
      //  mAlertDlgBuilder.setCancelable(false);
      //  mAlertDlgBuilder.setInverseBackgroundForced(true);
      //  mAlertDlgBuilder.setView(mDialogView);
     //   mAlertDialog = mAlertDlgBuilder.create();
     //   mAlertDialog.show();

    }

    AlertDialog CreateAlertDialog(String title, String message){
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //LayoutInflater inflater = getLayoutInflater();

        //builder.setView(inflater.inflate(R.layout.alarm_dialog_layout, null));//just for testing; This should be where the message is shown
        builder.setTitle(title);
        builder.setMessage(message);

        builder.setPositiveButton(R.string.confirmButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                newDialog.dismiss();
                //Stop Ringtone service
                Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                context.stopService(stopIntent);
                finish();
            }
        })
                .setNegativeButton(R.string.denyButton, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        })
                .setNeutralButton(R.string.snoozeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        //builder.setCancelable(false);
        //builder.setInverseBackgroundForced(true); //not sure what this was for
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

    void ModifyDialogProperties(AlertDialog dialog)
    {
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false); //I'll activate them when i implement their functionality
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setEnabled(false);

        ((TextView) dialog.findViewById(android.R.id.message))
                .setTextSize(getResources().getDimensionPixelSize(R.dimen.dialog_text_size));

    }


}
