package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {

    private PreferenceChangeListener prefListener;
    private NotificationManager notificationManager;
    private SensorManager sm;

    private MediaPlayer mp;

    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            switch (key){
                case Settings.ACTIVATE_STR:
                    boolean active = prefs.getBoolean(key, false);
                    Log.d("###", "[Service] New activated value: " + active);
                    if (!active) {
                        Log.d("###", "[Service] service now stops himself");
                        // unregister the listeners
                        prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
                        sm.unregisterListener(listener);
                        notificationManager.cancel(Settings.ALWAYS_ON_NOTIFICATION_ID);
                        stopSelf();
                    }
                    break;
                default:
                    Log.d("###", "[service] onSharedPreferenceChanged");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // initialize the sensor listener
        sm = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        Sensor sensor = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_NORMAL);

        // initialize the preference listener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        // just in case in hasn't been set yet because Settings.java doesn't store stuff persistent...
        String timeoutStr = prefs.getString(Settings.TIMEOUT_STR, Settings.TIMEOUT_DEFAULT+"");
        int timeout = Integer.parseInt(timeoutStr);
        if(timeout > Settings.TIMEOUT_UPPER_BOUND)
            timeout = Settings.TIMEOUT_UPPER_BOUND;
        else if(timeout < Settings.TIMEOUT_LOWER_BOUND)
            timeout = Settings.TIMEOUT_LOWER_BOUND;
        Settings.TIMEOUT = timeout;
        String sensitivityStr = prefs.getString(Settings.SENSITIVITY_STR, Settings.SENSITIVITY_DEFAULT+"");
        int sensitivity = Integer.parseInt(sensitivityStr);
        if(sensitivity > Settings.SENSITIVITY_UPPER_BOUND)
            sensitivity = Settings.SENSITIVITY_UPPER_BOUND;
        else if(sensitivity < Settings.SENSITIVITY_LOWER_BOUND)
            sensitivity = Settings.SENSITIVITY_LOWER_BOUND;
        Settings.SENSITIVITY = sensitivity;

        // add the onchange listener
        prefListener = new PreferenceChangeListener();
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

        // initialize the notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("AntiTheft")
                        .setContentText("AntiTheft is running!");

        Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        // don't let the notification go away
        mBuilder.setOngoing(true);
        mBuilder.setContentIntent(resultPendingIntent);
        // Gets an instance of the NotificationManager service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(Settings.ALWAYS_ON_NOTIFICATION_ID, mBuilder.build());

        // Initialize the media player
        int sound = R.raw.warning;
        mp = MediaPlayer.create(getApplicationContext(), sound);
        mp.setVolume(1.0f, 1.0f);
        mp.setLooping(false);

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not used since we should use a started service...
        throw new UnsupportedOperationException("Unsupported operation");
    }

    @Override
    public void startAlarm() {
        // in case we're already displaying the alarm notification we stop
        if(Settings.inAlarmMode)
            return;
        else
            Settings.inAlarmMode = true;

        Log.d("###", "startAlarm");

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // initialize the notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Alarm!!!")
                        .setContentText("Click to disable the alarm...");

        Intent resultIntent = new Intent(getApplicationContext(), DisableAlarmActivity.class);
        resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        getApplicationContext(),
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        // don't let the notification go away
        mBuilder.setOngoing(true);
        mBuilder.setContentIntent(resultPendingIntent);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // set a sound, so that we hear the notification
        mBuilder.setSound(alarmSound);
        notificationManager.notify(Settings.ALARM_NOTIFICATION_ID, mBuilder.build());

        // creates a runnable that waits for the wanted time before playing the sound.
        // if the alarm gets disabled in the mean time, we don't play
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("###", "Play actual alarm sound");
                if (Settings.inAlarmMode) {
                    if (!mp.isPlaying())
                        mp.start();
                    Settings.inAlarmMode = false;
                    notificationManager.cancel(Settings.ALARM_NOTIFICATION_ID);
                }
            }
        }, Settings.TIMEOUT * 1000);
    }
}
