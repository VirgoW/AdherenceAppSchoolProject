package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DayFragmentAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> m_fragmentList = new ArrayList<>();
    private final List<String> m_fragmentDayNameList = new ArrayList<>();
    private final List<Integer> m_fragmentDayValueList = new ArrayList<>();
    private Context m_context;
    private Calendar m_cal;

    public DayFragmentAdapter(FragmentManager manager, Context context, Calendar cal)
    {
        super(manager);
        m_context = context;
        m_cal = cal;
        UpdateDayTabs();
    }

    public void SetContext(Context context)
    {
        m_context = context;
    }

    @Override
    public Fragment getItem(int index) {
        return DayFragment.newInstance();//m_fragmentList.get(index);
    }

    @Override
    public int getCount() {
        return 7;//m_fragmentList.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return null;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE; //TODO if necessary, improve this.
    }

    @Override
    public void notifyDataSetChanged() {
        m_fragmentDayNameList.clear();
        m_fragmentDayValueList.clear();
        UpdateDayTabs(); //TODO update tabs if not done already
    }

    public View getTabView(int position)
    {
        LayoutInflater inflater = LayoutInflater.from(m_context);
        View tabView = inflater.inflate(R.layout.tab_item_layout,null);
        TextView day = (TextView) tabView.findViewById(R.id.dayValueTabItem);
        TextView dayName = (TextView) tabView.findViewById(R.id.dayNameTabItem);
        day.setText(Integer.toString(m_fragmentDayValueList.get(position)));
        dayName.setText(m_fragmentDayNameList.get(position));
        return tabView;
    }

    //Populate the adapter data with days name and value
    private void UpdateDayTabs()
    {
        String[] dayNames = m_context.getResources().getStringArray(R.array.dayNames);
        int dayOfWeek = m_cal.get(Calendar.DAY_OF_WEEK) - 2; //Day of week starts at 1 and index starts at 0, and first day of the week is sunday
        for(int index = 0; index < 7; index ++)
        {
            int dayOffset = index - dayOfWeek;
            UpdateAddCal(Calendar.DAY_OF_MONTH, dayOffset);

            int day = m_cal.get(Calendar.DAY_OF_MONTH);
            m_fragmentDayNameList.add(dayNames[index]);
            m_fragmentDayValueList.add(day);

            //TODO send cal info to tab content

            UpdateAddCal(Calendar.DAY_OF_MONTH, -dayOffset);

        }
    }

    private void UpdateAddCal(int calType, int value)
    {
        m_cal.add(calType, value);
    }
}
