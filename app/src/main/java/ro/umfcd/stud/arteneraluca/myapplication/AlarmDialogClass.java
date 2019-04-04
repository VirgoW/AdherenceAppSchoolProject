package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlarmDialogClass extends Activity {
    //AlertDialog.Builder mAlertDlgBuilder;
    //AlertDialog mAlertDialog;
    AlertDialog newDialog;
    //View mDialogView = null;
    //Button mOKBtn, mCancelBtn;
    final Context context = this;
    String medName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        Intent thisIntent = getIntent();
        medName = thisIntent.getStringExtra("medName");
        //Start Ringtone service to play alarm sound
        Intent startIntent = new Intent(context, RingtoneCustomService.class);
        context.startService(startIntent);
        newDialog = createAlertDialog();
        newDialog.show();


        //LayoutInflater inflater = getLayoutInflater();
        // Build the dialog


        /*

        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.alarm_dialog_layout, null);
        mOKBtn = (Button)mDialogView.findViewById(R.id.confirmTakingBtn);
        TextView text = (TextView) mDialogView.findViewById(R.id.AlarmDialogText);

        */

        /*

        Intent thisIntent = getIntent();
        int alarmId = 0;    //Do we even need this?
        String buffer;
        alarmId = thisIntent.getIntExtra("alarmID", 0);
        String textValue = getString(R.string.testAlarmDialogText) + alarmId;
        buffer = thisIntent.getStringExtra("medName");
        textValue +=". Medication = " + buffer;
        buffer = thisIntent.getStringExtra("hour");
        textValue +=". Hour = " + buffer;
        text.setText(textValue);

        */

        /*

        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.confirmTakingBtn)
                {
                    mAlertDialog.dismiss();
                    //mAlertDialog.cancel();

                    //Stop Ringtone service
                    Intent stopIntent = new Intent(context, RingtoneCustomService.class);
                    context.stopService(stopIntent);
                    finish();
                }
            }
        });
        mAlertDlgBuilder.setCancelable(false);
        mAlertDlgBuilder.setInverseBackgroundForced(true);
        mAlertDlgBuilder.setView(mDialogView);
        mAlertDialog = mAlertDlgBuilder.create();
        mAlertDialog.show();

        */
    }

    AlertDialog createAlertDialog(){
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();

        String dialogTitle = getDialogTitle(); //Not working

        builder.setView(inflater.inflate(R.layout.alarm_dialog_layout, null));//just for testing; This should be where the message is shown
        builder.setTitle(dialogTitle);

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

                    }
                }).setNeutralButton(R.string.snoozeButton, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.setCancelable(false);
        builder.setInverseBackgroundForced(true); //not sure what this was for
        return builder.create();
    }

    public String getDialogTitle(){ return medName; } //It doesn't work as intended;
    // it should take the name from intent and pass it to dialog builder


}
