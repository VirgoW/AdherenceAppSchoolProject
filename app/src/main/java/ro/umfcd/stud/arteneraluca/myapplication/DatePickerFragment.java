package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment
{
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar currentTimeDate = Calendar.getInstance();
        int year = currentTimeDate.get(Calendar.YEAR);
        int month = currentTimeDate.get(Calendar.MONTH);
        int day = currentTimeDate.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), 0,(DatePickerDialog.OnDateSetListener) getActivity(),
                year, month,day);
    }
}
