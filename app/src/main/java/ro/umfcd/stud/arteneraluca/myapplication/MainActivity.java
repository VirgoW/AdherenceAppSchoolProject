package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startBtn = (Button) findViewById(R.id.startBtn);
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toMainPage = new Intent(getApplicationContext(), AlarmsPage.class);
                startActivity(toMainPage);
            }
        });


    }

    private void CreateFileSave()
    {
        File directory;
        String filename = getText(R.string.testSave).toString();
        directory = getDir(filename, MODE_PRIVATE);

    }
}
