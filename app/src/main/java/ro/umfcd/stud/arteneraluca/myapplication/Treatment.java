package ro.umfcd.stud.arteneraluca.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Treatment implements Serializable {

    private String m_medName;
    private String m_notes;
    private String m_dose;
    private boolean m_fixedTimeTreatment;
    private boolean m_dailyTreatment = false;
    private int m_fixedFrequencyNumber;
    private int m_fixedFrequencySpinnerPosition;
    private Calendar m_StartCal;
    private Calendar m_EndCal;
    public ArrayList<String> m_dailyFrequency;
    public ArrayList<Boolean> m_weeklyDayFrequency;
    public int m_id;

    public Treatment()
    {
        m_medName = "";
        m_dose = "";
        m_notes = "";
        m_fixedTimeTreatment = false;
        m_fixedFrequencyNumber = 0;
        m_fixedFrequencySpinnerPosition = -1;
        m_StartCal = null;
        m_EndCal = null;
        m_weeklyDayFrequency = new ArrayList<>();
        m_dailyFrequency = new ArrayList<>();
        for(int i=0; i<7;i++)
        {
            m_weeklyDayFrequency.add(false);
        }
    }

    public Treatment(String newMed)
    {
        m_medName = newMed;
    } //Seems to be not used

    /*
        Check for weekly treatment to be valid. That means dailyTreatment = true and we have more than 0 weekly days selected
        For the alarm to be valid, we need to have all members valid.
        If the treatment has a fixed time, the end calendar must be valid.
     */
    boolean IsValid()
    {

        boolean isWeeklyTreatementValid = !m_dailyTreatment && m_weeklyDayFrequency.size() > 0;
        boolean isValid = !m_medName.isEmpty() && !m_dose.isEmpty() && m_StartCal != null && m_dailyFrequency.size() > 0 && (m_dailyTreatment || isWeeklyTreatementValid);
        if(m_fixedTimeTreatment)
        {
            isValid = isValid &&  m_EndCal != null;
        }

        return isValid;
    }

    public String GetMedName() {
        return m_medName;
    }

    public void SetMedName(String medName) {
        m_medName = medName;
    }

    public void SetNote(String note)
    {
        m_notes = note;
    }

    public String GetNote()
    {
        return m_notes;
    }

    public String GetDosage()
    {
        return m_dose;
    }

    public void SetDosage(String dose)
    {
        m_dose = dose;
    }

    public boolean IsDailyTreatment() {
        return m_dailyTreatment;
    }

    public void SetDailyTreatment(boolean dailyTreatment) {
        m_dailyTreatment = dailyTreatment;
    }

    public Calendar GetStartCal() {
        return m_StartCal;
    }

    public void SetStartCal(Calendar startCal) {
        m_StartCal = startCal;
    }

    public Calendar GetEndCal() { return m_EndCal;}

    public void SetEndCal(Calendar endCal) { m_EndCal = endCal;}

    public boolean IsFixedTimeTreatment() {
        return m_fixedTimeTreatment;
    }

    public void SetFixedTimeTreatment(boolean fixedTimeTreatment) {
        m_fixedTimeTreatment = fixedTimeTreatment;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int id) {
        m_id = id;
    }

    public int GetFixedFrequencyNumber() { return m_fixedFrequencyNumber;}
    public void SetFixedFrequencyNumber(int number) { m_fixedFrequencyNumber = number;}

    public int GetFixedFrequencySpinnerPosition() { return m_fixedFrequencySpinnerPosition;}
    public void SetFixedFrequencySpinnerPosition(int position){m_fixedFrequencySpinnerPosition = position;}
}
