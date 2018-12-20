package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DayFragment extends Fragment {

    AlarmAdapter m_alarmAdapter;

    public static DayFragment newInstance(Calendar cal, Context context)
    {
        DayFragment fragment = new DayFragment();
        SimpleDateFormat format = new SimpleDateFormat(context.getText(R.string.dateFormat).toString());
        String startDate = format.format(cal.getTime());
        Bundle args = new Bundle();
        args.putString("calendar", startDate);
        fragment.setArguments(args);
        return fragment;
    }
    public DayFragment()
    {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        //TODO update cal on update page cal
        View view = inflater.inflate(R.layout.alarm_page_days_fragment, null);
        try
        {
            SimpleDateFormat format = new SimpleDateFormat( this.getContext().getText(R.string.dateFormat).toString());
            String calString = getArguments().getString("calendar");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(format.parse(calString));
            GridView gridView = (GridView) view.findViewById(R.id.gridViewTestFragment);
            m_alarmAdapter = new AlarmAdapter(view.getContext(), calendar);
            gridView.setAdapter(m_alarmAdapter);
        }
        catch (Exception e)
        {
            Log.e("ArteneApp", "Exception while creating fragment view: ", e);
        }
        finally
        {
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(m_alarmAdapter != null)
        {
            m_alarmAdapter.notifyDataSetChanged();
        }
    }
}
