package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AntiTheftServiceImpl extends AbstractAntiTheftService {

    private PreferenceChangeListener prefListener;
    private SharedPreferences prefs;
    private NotificationManager notificationManager;

    private int NOTIFICATION_ID = 1;

    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            switch (key){
                case Settings.ACTIVATE_STR:
                    boolean active = prefs.getBoolean(key, false);
                    Log.d("###", "[Service] New activated value: " + active);
                    if (active) {
                        // starting the service is handled in the activity
                    } else {
                        Log.d("###", "[Service] service now stops himself");
                        // unregister the listener
                        prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
                        notificationManager.cancel(NOTIFICATION_ID);
                        stopSelf();
                    }
                    break;
                case Settings.TIMEOUT_STR:
                    // getInt() doesn't work since text-field is stored as string...
                    String delayS = prefs.getString(key, ""+Settings.TIMEOUT_DEFAULT);
                    int delay = Integer.parseInt(delayS);
                    Log.d("###", "[service] New timeout value: "+ delay);
                    break;
                default:
                    Log.d("###", "[service] onSharedPreferenceChanged");
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("###", "Service Started (start command received)");

        // initialize the preference listener
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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
        // Sets an ID for the notification
        int mNotificationId = NOTIFICATION_ID;
        // Gets an instance of the NotificationManager service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(mNotificationId, mBuilder.build());

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // not used since we should use a started service...
        // todo: this means I can completely remove this method right?
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void startAlarm() {
        Log.d("##", "start alarm! (needs to be implemented)");
    }
}
