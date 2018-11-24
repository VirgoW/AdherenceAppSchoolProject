package ro.umfcd.stud.arteneraluca.myapplication;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

public class DayFragment extends Fragment {

    AlarmAdapter m_alarmAdapter;

    public DayFragment()
    {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.alarm_page_days_fragment, null);
        GridView gridView = (GridView) view.findViewById(R.id.gridViewTestFragment);
        m_alarmAdapter = new AlarmAdapter(view.getContext());
        gridView.setAdapter(m_alarmAdapter);
        return view;
    }

    public void NotifyAdapterDataSetChanged()
    {
        m_alarmAdapter.notifyDataSetChanged();
    }
}
