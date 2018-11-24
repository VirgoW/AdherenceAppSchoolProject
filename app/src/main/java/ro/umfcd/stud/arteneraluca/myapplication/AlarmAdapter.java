package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
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
        View gridView;
        //final TextView textView = new TextView(m_context);
        LayoutInflater inflater = (LayoutInflater) m_context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
        {
            //Get layout from xml file using the inflater
            gridView = inflater.inflate(R.layout.grid_view_item, null);
        }
        else
        {
            gridView = (View) convertView;
        }
        //Temporary - TODO Improve this method to get all the info we need from the alarms
        //textView.setLayoutParams(new GridView.LayoutParams(150, 150));
        //textView.setPadding(1,1,1,1);
        //textView.setText(SaveManager.getInstance().GetAlarm(position));
        //textView.setBackgroundColor(Color.WHITE);

        //Populate the gridview items with each item value
        //Code tip: FINAL keyword is used to create a constant. That forces the variable to not allow to be changed
        final TextView medName = (TextView) gridView.findViewById(R.id.grid_item_medicament);
        medName.setText(SaveManager.getInstance().GetAlarm(position));

        return gridView;
    }
}
