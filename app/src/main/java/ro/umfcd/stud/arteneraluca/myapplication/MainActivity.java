package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

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
    }
}
