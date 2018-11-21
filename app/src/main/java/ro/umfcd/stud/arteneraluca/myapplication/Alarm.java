package ro.umfcd.stud.arteneraluca.myapplication;

public class Alarm {
    public String m_medName;


    public Alarm()
    {
        m_medName = "";
    }
    public Alarm(String newMed)
    {
        m_medName = newMed;
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
