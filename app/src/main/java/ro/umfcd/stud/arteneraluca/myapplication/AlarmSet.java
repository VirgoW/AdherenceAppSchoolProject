package ro.umfcd.stud.arteneraluca.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AlarmSet extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);

        ToggleButton alarmDurationToggleBtn = (ToggleButton) findViewById(R.id.alarmDurationToggleBtn);
        final TextView alarmsNumber_Text = (TextView) findViewById(R.id.alarmsNumber_Text);
        final Spinner treatmentLengthSpinner = (Spinner) findViewById(R.id.treatmentLengthSpinner);

            alarmDurationToggleBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        alarmsNumber_Text.setVisibility(View.VISIBLE);
                        treatmentLengthSpinner.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        alarmsNumber_Text.setVisibility(View.INVISIBLE);
                        treatmentLengthSpinner.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }
}
