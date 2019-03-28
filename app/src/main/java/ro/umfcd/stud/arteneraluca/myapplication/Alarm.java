package ro.umfcd.stud.arteneraluca.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Alarm implements Serializable {

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

    public Alarm()
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

    public Alarm(String newMed)
    {
        m_medName = newMed;
    }

    boolean IsValid()
    {
        //Code tip: In a normal project we would need a method to access the members of a class, to preserve the OOP principle called Incapsulation
        //Incapsulation: every object in a class must be hidden and only accessible from within the class. All external access must be granted through methods of the class
        boolean isWeeklyTreatementValid = !m_dailyTreatment && m_weeklyDayFrequency.size() > 0;
        boolean isValid = !m_medName.isEmpty() && !m_dose.isEmpty() && m_StartCal != null && m_dailyFrequency.size() > 0 &&
                (m_dailyTreatment || isWeeklyTreatementValid) && (m_fixedTimeTreatment && m_EndCal != null) ;

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

    public void SetDailyTreatment(boolean dailyTreament) {
        m_dailyTreatment = dailyTreament;
    }

    public Calendar GetStartCal() {
        return m_StartCal;
    }

    public void SetStartCal(Calendar startCal) {
        m_StartCal = startCal;
    }

    public Calendar GetEndCal() { return m_EndCal;}

    public void SetEndCal(Calendar endCal) { m_EndCal = endCal;}

    public boolean IsFixedTimeTreament() {
        return m_fixedTimeTreatment;
    }

    public void SetFixedTimeTreament(boolean fixedTimeTreatment)
    {
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
