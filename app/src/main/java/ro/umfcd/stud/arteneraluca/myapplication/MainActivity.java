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
                toMainPage.setClass(context, MasterPage.class);
                toMainPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(toMainPage);
            }
        });

    }
}
