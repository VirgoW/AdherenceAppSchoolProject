package ro.umfcd.stud.arteneraluca.myapplication;

import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class RingtoneCustomService extends Service {
    private Ringtone ringtone;
    private Vibrator vibrator;
    long [] vibratePattern = {0,500,1000};


    @Override
    public void onCreate() {
        super.onCreate();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        this.ringtone = RingtoneManager.getRingtone(this, uri);
        ringtone.play();
        Vibrate();
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        ringtone.stop();
        stopVibrating();
    }


    public void Vibrate()
    {
        if (Build.VERSION.SDK_INT >= 26)
        {
            int[] amplitudePattern = {VibrationEffect.DEFAULT_AMPLITUDE};
            vibrator.vibrate(VibrationEffect.createWaveform(vibratePattern,amplitudePattern,0));

        }
        else
        {
            vibrator.vibrate(vibratePattern,0);
        }
    }

    public void stopVibrating()
    {
        vibrator.cancel();
    }

}
