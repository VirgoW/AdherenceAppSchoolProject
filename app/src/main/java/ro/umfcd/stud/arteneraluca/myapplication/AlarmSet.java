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

        ToggleButton durationToggBtn = (ToggleButton) findViewById(R.id.durationToggBtn);
        final TextView nrOfTreatmentsTxt = (TextView) findViewById(R.id.nrOfTreatmentsTxt);
        final Spinner treatmentLgthSpinn = (Spinner) findViewById(R.id.treatmentLgthSpinn);

            durationToggBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        nrOfTreatmentsTxt.setVisibility(View.VISIBLE);
                        treatmentLgthSpinn.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        nrOfTreatmentsTxt.setVisibility(View.INVISIBLE);
                        treatmentLgthSpinn.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }
}
