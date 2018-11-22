package ro.umfcd.stud.arteneraluca.myapplication;

public class Alarm {
    private String m_medName;
    private boolean m_isValid = false;

    public Alarm()
    {
        m_medName = "";
        m_isValid = false;
    }

    public Alarm(String newMed)
    {
        m_medName = newMed;
        m_isValid = true;
    }

    boolean IsValid()
    {
        //Code tip: In a normal project we would need a method to access the members of a class, to preserve the OOP principle called Incapsulation
        //Incapsulation: every object in a class must be hidden and only accessible from within the class. All external access must be granted through methods of the class
        return m_isValid;
    }

    public String GetMedName() {
        return m_medName;
    }

    public void SetMedName(String medName) {
        m_medName = medName;
        m_isValid = true;
    }

    /*
    struct
    {
        MedName
                Hour

        Frequency
                Day
        Week
                Month
        Other combo

        For how long
            always
        or
        number of
        Days
                Weeks
        Months
                Years
    }
    */
}
