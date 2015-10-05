package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity{

    private PreferenceChangeListener prefListener;
    private SharedPreferences prefs;

    private class PreferenceChangeListener implements
            SharedPreferences.OnSharedPreferenceChangeListener {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
            switch (key){
                case Settings.ACTIVATE_STR:
                    boolean active = prefs.getBoolean(key, Settings.ACTIVATE_DEFAULT);
                    Log.d("###", "[activity] New activated value: " + active);
                    if (active) {
                        // start service and set notification
                        Intent intent = new Intent(getApplicationContext(), AntiTheftServiceImpl.class);
                        startService(intent);
                    } else {
                        // stopping the service is handled in service...
                    }
                    break;
                case Settings.TIMEOUT_STR:
                    // getInt() doesn't work since text-field is stored as string...
                    String delayS = prefs.getString(key, ""+Settings.TIMEOUT_DEFAULT);
                    int delay = Integer.parseInt(delayS);
                    Log.d("###", "[activity] New timeout value: "+ delay);
                    break;
                default:
                    Log.d("###", "[activity] onSharedPreferenceChanged");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize the preference listener
        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefListener = new PreferenceChangeListener();

        // Display the fragment as the main content
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // register the preference listener
        prefs.registerOnSharedPreferenceChangeListener(prefListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister the preference listener
        prefs.unregisterOnSharedPreferenceChangeListener(prefListener);
    }


    public static class SettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
