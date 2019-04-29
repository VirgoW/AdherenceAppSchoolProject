package ro.umfcd.stud.arteneraluca.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.i("ArteneApp", "boot complete");
            // Set alarms
            SaveManager saveManager = SaveManager.getInstance();

            /*
            Intent i = context.getPackageManager().getLaunchIntentForPackage("ro.umfcd.stud.arteneraluca.myapplication");
            context.startActivity(i);
            */

            int treatmentListSize = saveManager.GetAlarmCount();
            for(int treatmentIndex = 0; treatmentIndex < treatmentListSize; ++treatmentIndex)
            {
                saveManager.ClearSystemAlarms(treatmentIndex,context);
                saveManager.AddSystemAlarms(treatmentIndex,context);
            }
        }
    }
}
