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
import java.util.List;

public class DayFragmentAdapter extends FragmentPagerAdapter
{
    private final List<Fragment> m_fragmentList = new ArrayList<>();
    private final List<String> m_fragmentDayNameList = new ArrayList<>();
    private final List<String> m_fragmentDayValueList = new ArrayList<>();
    private Context m_context;

    public DayFragmentAdapter(FragmentManager manager, Context context)
    {
        super(manager);
        m_context = context;
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

    public void addDayTitle(Fragment fragment, String dayTitle, String dayValue)
    {
        //TODO add day date here and in the layout
        m_fragmentList.add(fragment);
        m_fragmentDayNameList.add(dayTitle);
        m_fragmentDayValueList.add(dayValue);
    }

    public View getTabView(int position)
    {
        LayoutInflater inflater = LayoutInflater.from(m_context);
        View tabView = inflater.inflate(R.layout.tab_item_layout,null);
        TextView day = (TextView) tabView.findViewById(R.id.dayValueTabItem);
        TextView dayName = (TextView) tabView.findViewById(R.id.dayNameTabItem);
        day.setText(m_fragmentDayValueList.get(position));
        dayName.setText(m_fragmentDayNameList.get(position));
        return tabView;
    }
}
