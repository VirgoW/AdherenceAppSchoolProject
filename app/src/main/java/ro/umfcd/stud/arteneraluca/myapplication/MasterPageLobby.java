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

public class MasterPageLobby extends Activity {

    AlertDialog.Builder mAlertDlgBuilder;
    AlertDialog mAlertDialog;
    View mDialogView = null;
    Button mOKBtn, mCancelBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final Context context = this;

        LayoutInflater inflater = getLayoutInflater();
        // Build the dialog
        mAlertDlgBuilder = new AlertDialog.Builder(this);
        mDialogView = inflater.inflate(R.layout.master_dialog_layout, null);
        mOKBtn = (Button)mDialogView.findViewById(R.id.master_OkBtn);
        mCancelBtn = (Button)mDialogView.findViewById(R.id.master_CancelBtn);
        final TextView text = (TextView) mDialogView.findViewById(R.id.passwordText);

        mOKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.master_OkBtn)
                {
                    if(text.getText().toString().equals("1234"))
                    {
                        Intent toMasterPage = new Intent(getApplicationContext(), MasterPage.class);
                        startActivity(toMasterPage);
                    }
                    else
                    {
                        Intent toMainPage = new Intent(getApplicationContext(), AlarmsPage.class);
                        startActivity(toMainPage);
                    }
                    mAlertDialog.dismiss();
                    finish();
                }
            }
        });
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.master_CancelBtn)
                {
                    mAlertDialog.dismiss();
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
}
