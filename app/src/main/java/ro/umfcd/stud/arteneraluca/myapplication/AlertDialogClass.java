package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AlertDialogClass extends Activity {
    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    Button mOKBtn, mCancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        //Start Ringtone service to play alarm sound
        Intent startIntent = new Intent(context, RingtoneCustomService.class);
        context.startService(startIntent);

        LayoutInflater inflater = getLayoutInflater();
        // Build the dialog
        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.alarm_dialog_layout, null);
        mOKBtn = (Button)mDialogView.findViewById(R.id.ID_Ok);
        TextView text = (TextView) mDialogView.findViewById(R.id.AlarmDialogText);


        Intent thisIntent = getIntent();
        int alarmId = 0;
        String buffer;
        alarmId = thisIntent.getIntExtra("alarmID", 0);
        String textValue = "Please take your meds! Id =" + alarmId;
        buffer = thisIntent.getStringExtra("medName");
        textValue +=". Name = " + buffer;
        buffer = thisIntent.getStringExtra("hour");
        textValue +=". Hour = " + buffer;
        text.setText(textValue);

        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.ID_Ok)
                {
                    mAlertDialog.dismiss();
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
    }

    //TODO Create DialogFragment for the alarm
}
