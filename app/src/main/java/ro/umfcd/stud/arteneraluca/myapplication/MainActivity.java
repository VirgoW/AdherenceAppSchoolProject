package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button startBtn = (Button) findViewById(R.id.startBtn);
        SaveManager.getInstance().InitSave(this);

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainPage = new Intent(getApplicationContext(), AlarmsPage.class);
                startActivity(toMainPage);
            }
        });

        final Context context = this;

        Button masterButton = (Button) findViewById(R.id.GenerateReportBtn);
        masterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainPage =  new Intent("android.intent.action.DIALOG");
                toMainPage.setClass(context, ReportPage.class);
                toMainPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toMainPage);
            }
        });
/* This was for testing the vibration and it seems to work

        Button startVibration = (Button) findViewById(R.id.startV);
        Button stopVibration = (Button) findViewById(R.id.stopV);
        final Vibrator vibrate = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        startVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long pattern[] = {0,500,1000,500,1000};
                //vibrate.vibrate(pattern,0);

                if (Build.VERSION.SDK_INT >= 26)
                {
                    //v.vibrate(VibrationEffect.createOneShot(150, VibrationEffect.DEFAULT_AMPLITUDE));
                    int[] amplitudePattern = {VibrationEffect.DEFAULT_AMPLITUDE};
                    //v.vibrate(VibrationEffect.createWaveform(vibratePattern,amplitudePattern,0));
                    vibrate.vibrate(VibrationEffect.createWaveform(pattern,amplitudePattern,0)); //for testing only

                }
                else
                {
                    //v.vibrate(vibratePattern,0);
                    vibrate.vibrate(pattern,0); //for testing only
                }

            }
        });

        stopVibration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vibrate.cancel();

            }
        });
        */
    }

}
