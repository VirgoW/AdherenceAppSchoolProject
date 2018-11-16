package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Page1ChooseOption extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page1_choose_option);

        Button AlarmsBtn = (Button) findViewById(R.id.AlarmsBtn);
        AlarmsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toAlarms = new Intent(getApplicationContext(), SeeAndSetAlarms.class);
                startActivity(toAlarms);

            }
        });


    }
}
