package ro.umfcd.stud.arteneraluca.myapplication;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class DayFragmentAdapter extends FragmentPagerAdapter {
    private final List<Fragment> m_fragmentList = new ArrayList<>();
    private final List<String> m_fragmentDayNameList = new ArrayList<>();

    public DayFragmentAdapter(FragmentManager manager)
    {
        super(manager);
    }

    @Override
    public Fragment getItem(int index) {
        return m_fragmentList.get(index);
    }

    @Override
    public int getCount() {
        return m_fragmentList.size();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE; //TODO if necessary, improve this.
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return m_fragmentDayNameList.get(position);
    }

    public void addDayTitle(Fragment fragment, String dayTitle)
    {
        //TODO add day date here and in the layout
        m_fragmentList.add(fragment);
        m_fragmentDayNameList.add(dayTitle);
    }
}
