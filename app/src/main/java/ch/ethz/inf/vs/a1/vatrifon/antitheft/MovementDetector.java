package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.os.Debug;
import android.text.format.Time;
import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class MovementDetector extends AbstractMovementDetector {

    private int skipped;
    private float[] lastVals;       // last sensor values
    private int lastStillTimeSec;   // last time the phone was not moving

    @Override
    protected boolean doAlarmLogic(float[] values) {
        if(lastVals == null){
            skipped = 3;
            lastVals = values.clone();
            lastStillTimeSec = getTimeSeconds();
            return false;
        }

        float sensitivity = Settings.SENSITIVITY / 100f;
        int timeout = Settings.TIMEOUT;

        float diff = normDiff(lastVals, values);
        lastVals = values.clone();
        //Log.d("###", "Movement "+diff);
        //Log.d("###", "skipped val "+skipped);

        int nowSec = getTimeSeconds();

        boolean phoneMoved = diff >= sensitivity;
        if(!phoneMoved){
            // Due to the nature of some movements (e.g. a step while walking) there might be moments in which
            // the sensor yields that the phone is staying still for a short period of time.
            // We still want to count those as movement, thus we will skip a few sensor values
            // whenever the phone appears to not be moving
            skipped = Math.min(++skipped, Settings.COOLDOWN_SKIP_SENSOR_READINGS);

            if(skipped >= Settings.COOLDOWN_SKIP_SENSOR_READINGS){
                // NOW assume phone is lying still
                lastStillTimeSec = nowSec;
            }

            // either way, do not trigger an alarm in this state.
            return false;
        } else {
            skipped = 0;
        }

        int timeMovingSec = nowSec - lastStillTimeSec;

        //Log.d("###", "Last still time (s) "+lastStillTimeSec);
        //Log.d("###", "Current time time (s) "+nowSec);
        //Log.d("###", "Moving since (s) "+timeMovingSec);

        if(timeMovingSec >= timeout){
            lastVals = null; // reset for next use
            return true;
        }
        return false;
    }

    /**
     * Computes the norm of the difference of two three-dimensional vectors
     * (represented by float arrays).
     */
    private float normDiff(float[] a, float[] b){
        float res = 0f;
        for(int i = 0; i < 3; i++){
            res += Math.pow((a[i] - b[i]), 2);
        }
        return (float)Math.sqrt(res);
    }

    /**
     * returns the seconds passed since some 'arbitrary, but fixed' time :)
     */
    private int getTimeSeconds(){
        Calendar cal = Calendar.getInstance(); // yes, we need a new instance every time.
        return cal.get(Calendar.SECOND);
    }
}
