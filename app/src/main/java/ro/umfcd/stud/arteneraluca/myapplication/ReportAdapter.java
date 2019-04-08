package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ReportAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ReportItem> mReportItemList;

    class ReportItem {
        public String medName;
        public Calendar alarmStartDate;
        public int confirmedDoses;
        public int missedDoses;
        public int index;
    }

    public ReportAdapter(Context context)
    {
        mContext = context;
        mReportItemList = new ArrayList<>();
        CreateReportList();
    }

    private void CreateReportList() {
        for(int i=0; i< SaveManager.getInstance().GetAlarmCount(); i++)
        {
            Treatment treatment = SaveManager.getInstance().GetAlarmByIndex(i);
            ReportItem item = new ReportItem();
            item.index = treatment.getId();
            item.medName = treatment.GetMedName();
            item.alarmStartDate = treatment.GetStartCal();
            item.confirmedDoses = treatment.GetConfirmedCount();
            item.missedDoses = treatment.GetDeniedCount();
            mReportItemList.add(item);
        }
    }


    @Override
    public int getCount() {
        return mReportItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView;
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if(convertView == null)
        {
            //Get layout from xml file using the inflater
            gridView = inflater.inflate(R.layout.report_grid_view, null);
        }
        else
        {
            gridView = (View) convertView;
        }

        ReportItem reportItem = mReportItemList.get(position);

        TextView infoMedName = (TextView) gridView.findViewById(R.id.infoMedName);
        TextView infoStartDate = (TextView) gridView.findViewById(R.id.infoStartDate);
        TextView infoConfirmedDoses = (TextView) gridView.findViewById(R.id.infoConfirmedDoses);
        TextView infoMissedDoses = (TextView) gridView.findViewById(R.id.infoMissedDoses);

        infoMedName.setText(reportItem.medName);
        infoConfirmedDoses.setText(Integer.toString(reportItem.confirmedDoses)); //confirmedDoses will always be a round number
        infoMissedDoses.setText(Integer.toString(reportItem.missedDoses)); //missedDoses will always be a round number


        Calendar startDate = reportItem.alarmStartDate; // the value to be formatted
        java.text.DateFormat formatter = java.text.DateFormat.getDateInstance(
                DateFormat.SHORT); // one of SHORT, MEDIUM, LONG, FULL, or DEFAULT
        formatter.setTimeZone(startDate.getTimeZone());
        String formattedStartDate = formatter.format(startDate.getTime());
        infoStartDate.setText(formattedStartDate);

        return gridView;
    }
}
