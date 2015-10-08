package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

public class DisableAlarmActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disable_alarm);

        // stop the alarm from going off...
        Settings.inAlarmMode = false;

        Log.d("###", "Disable alarm");

        // remove the notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Settings.ALARM_NOTIFICATION_ID);
    }

    public void onSettingsClick(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void onCloseClick(View v){
        finish();
    }
}
