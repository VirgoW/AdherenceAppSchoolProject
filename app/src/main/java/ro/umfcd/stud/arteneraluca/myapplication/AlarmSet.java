package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AlarmSet extends AppCompatActivity {
    Context m_context;
    View m_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_set);
        m_context = this;
        m_view = findViewById(android.R.id.content);
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
        Button saveAlarm = (Button) findViewById(R.id.alarmSetButton);
        saveAlarm.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                TrySaveAlarm();
            }
        });
    }

    private void TrySaveAlarm()
    {
        TextView medName = (TextView) m_view.findViewById(R.id.medNameText);
        if(medName.getText().toString().isEmpty())
        {
            TextView medNameError = (TextView ) m_view.findViewById(R.id.errorMedName);
            medNameError.setVisibility(View.VISIBLE);
            ((ScrollView) m_view.findViewById(R.id.scroll_bar)).fullScroll(View.FOCUS_UP);
        }
        else
        {
            SaveAlarm();
            onBackPressed();
            finish();
        }
    }

    private void SaveAlarm()
    {
        SaveManager.getInstance().AddNewAlarm(m_view, m_context);
    }
}
