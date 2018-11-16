package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class NotificationsApp extends Application {

    public static final String CHANNEL_1_ID = "alarms";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();

    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "alarms",
                    NotificationManager.IMPORTANCE_MAX
            );
            channel1.enableVibration(true);
            channel1.setDescription("this is for the alarms notifications");
            channel1.enableLights(true);

            NotificationManager manager = getSystemService(NotificationManager.class);
            //check if manager is null
            if (manager != null){
                manager.createNotificationChannel(channel1);
            }
            //manager.createNotificationChannel(channel1);
        }
    }
}


