package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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
                finish();
            }
        });

        final Context context = this;

        Button testAlarmBtn = (Button) findViewById(R.id.testBtnAlarm);
        testAlarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                PendingIntent activity = PendingIntent.getBroadcast(context, 11, intent, 0);
                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1000, activity);
            }
        });

        Button testAlarmBtn2 = (Button) findViewById(R.id.testBtnAlarm2);
        testAlarmBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AlarmReceiver.class);
                PendingIntent pendingAlarmIntent = PendingIntent.getBroadcast(context, 11, intent, 0);
                AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
                alarmMgr.cancel(pendingAlarmIntent);
            }
        });

        //TODO add hidden switch on this activity that can be touched even while invisible?
        // We want to create a hidden method of displaying a button that would open up the review page
    }
}
