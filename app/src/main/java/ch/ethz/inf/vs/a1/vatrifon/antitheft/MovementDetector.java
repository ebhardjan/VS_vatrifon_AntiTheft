package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.util.Log;

public class MovementDetector extends AbstractMovementDetector {

    private static final String LOG_TAG = "##MovementDetector";
    private int skipped;
    private float[] lastVals;       // last sensor values
    private int lastStillTimeSec;   // last time the phone was not moving

    @Override
    protected boolean doAlarmLogic(float[] values) {
        if(lastVals == null){
            skipped = Settings.COOLDOWN_SKIP_SENSOR_READINGS;
            lastVals = values.clone();
            lastStillTimeSec = getTimeSeconds();
            return false;
        }

        float sensitivity = Settings.SENSITIVITY / 100f;
        int period = Settings.PERIOD;

        float diff = normDiff(lastVals, values);
        lastVals = values.clone();

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
                Log.d(LOG_TAG, "Moving: no");
                return false;
            }

            // either way, do not trigger an alarm in this state.
            Log.d(LOG_TAG, "Moving: not sure");
            return false;
        } else {
            skipped = 0;
        }

        int timeMovingSec = nowSec - lastStillTimeSec;

        Log.d(LOG_TAG, "Moving: yes");
        Log.d(LOG_TAG, "Moving since (s) " + timeMovingSec);

        if(timeMovingSec >= period){
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
        return (int)(System.currentTimeMillis() / 1000L);
    }
}
