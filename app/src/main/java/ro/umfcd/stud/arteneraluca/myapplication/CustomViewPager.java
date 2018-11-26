package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.Calendar;

public class CustomViewPager extends ViewPager
{
    Context m_context;
    Calendar m_cal;

    public CustomViewPager(Context context)
    {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        m_context = context;
    }

    public void SetCal(Calendar cal)
    {
        m_cal = cal;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() == MotionEvent.EDGE_LEFT)
        {
            //Reset adapter
            Log.d("TEST TEST TEST --------", "SWIPE EDGE LEFT");
        }
        if(event.getAction() == MotionEvent.ACTION_DOWN && event.getEdgeFlags() == MotionEvent.EDGE_RIGHT)
        {
            Log.d("TEST TEST TEST --------", "SWIPE EDGE RIGHT");
        }
        return super.onInterceptTouchEvent(event);
    }


}
