package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class AlertDialogClass extends Activity {
    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    Button mOKBtn, mCancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);

        LayoutInflater inflater = getLayoutInflater();

        // Build the dialog
        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.alarm_dialog_layout, null);
        mOKBtn = (Button)mDialogView.findViewById(R.id.ID_Ok);
        mOKBtn.setOnClickListener(mDialogbuttonClickListener);
        mAlertDlgBuilder.setCancelable(false);
        mAlertDlgBuilder.setInverseBackgroundForced(true);
        mAlertDlgBuilder.setView(mDialogView);
        mAlertDialog = mAlertDlgBuilder.create();
        mAlertDialog.show();
    }

    View.OnClickListener mDialogbuttonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.ID_Ok)
            {
                mAlertDialog.dismiss();
                finish();
            }
        }
    };

    //TODO Create DialogFragment for the alarm
}
