package ro.umfcd.stud.arteneraluca.myapplication;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Alarm implements Serializable {

    private String m_medName;
    private String m_notes;
    private String m_dose;
    private String m_frequency;
    private boolean m_dailyTreatment = false;
    private Calendar m_StartCal;
    public ArrayList<String> m_dailyFrequency;
    public ArrayList<Boolean> m_weeklyDayFrequency;
    public int m_id;

    public Alarm()
    {
        m_medName = "";
        m_dose = "";
        m_notes = "";
        m_frequency = "";
        m_StartCal = null;
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
        boolean isValid = !m_medName.isEmpty() && !m_dose.isEmpty() && !m_frequency.isEmpty() && m_StartCal != null && m_dailyFrequency.size() > 0 && (m_dailyTreatment || isWeeklyTreatementValid) ;

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

    public void SetDailyTreatment(boolean m_fixedTreatment) {
        this.m_dailyTreatment = m_fixedTreatment;
    }

    public Calendar GetStartCal() {
        return m_StartCal;
    }

    public void SetStartCal(Calendar m_StartCal) {
        this.m_StartCal = m_StartCal;
    }

    public String GetFrequency() {
        return m_frequency;
    }

    public void SetFrequency(String m_frequency) {
        this.m_frequency = m_frequency;
    }

    public int getId() {
        return m_id;
    }

    public void setId(int m_id) {
        this.m_id = m_id;
    }
}
