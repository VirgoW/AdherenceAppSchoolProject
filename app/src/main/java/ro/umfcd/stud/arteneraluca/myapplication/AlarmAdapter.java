package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class AlarmAdapter extends BaseAdapter {
    private Context m_context;

    public AlarmAdapter(Context context)
    {
        m_context = context;
    }

    @Override
    public int getCount()
    {
        return SaveManager.getInstance().GetNumberOfSavedAlarms();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        final TextView textView = new TextView(m_context);
        //Temporary - TODO Improve this method to get all the info we need from the alarms
        //textView.setLayoutParams(new GridView.LayoutParams(150, 150));
        //textView.setPadding(1,1,1,1);
        textView.setText(SaveManager.getInstance().GetAlarm(position));
        textView.setBackgroundColor(Color.WHITE);
        return textView;
    }
}
