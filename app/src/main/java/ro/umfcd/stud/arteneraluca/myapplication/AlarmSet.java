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
        final TextView nrOfTreatmentsText = (TextView) findViewById(R.id.nrOfTreatmentsText);
        final Spinner treatmentLengthSpinner = (Spinner) findViewById(R.id.treatmentLengthSpinner);

            durationToggBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked) {
                        nrOfTreatmentsText.setVisibility(View.VISIBLE);
                        treatmentLengthSpinner.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        nrOfTreatmentsText.setVisibility(View.INVISIBLE);
                        treatmentLengthSpinner.setVisibility(View.INVISIBLE);
                    }
                }
            });
    }
}
