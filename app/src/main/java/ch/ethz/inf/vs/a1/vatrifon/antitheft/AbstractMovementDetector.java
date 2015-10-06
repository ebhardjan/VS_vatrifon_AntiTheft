package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public abstract class AbstractMovementDetector implements SensorEventListener {
	protected AbstractAntiTheftService antiTheftService;

	public void setCallbackService(AbstractAntiTheftService service) {
		antiTheftService = service;
	}
	
	/**
	 * Takes the sensor values, pass them to doAlarmLogic() to check if the alarm should be fired,
	 * then calls antiTheftService.startAlarm() if a deliberate movement is detected.
	 */
	@Override
	 public final void onSensorChanged(SensorEvent event) {
		float[] values;

		// populate the 'values' array with the sensor values
		values = event.values;
		
		boolean isAlarm = doAlarmLogic(values);
		if (isAlarm) {
			((AbstractAntiTheftService)antiTheftService).startAlarm();
		}
	}
	
	/**
	 * Implements the sensor logic that is needed to trigger the alarm.
	 * @param values: the sensor values detected by the service.
	 * @return true if the service should start the alarm, false otherwise.
	 */
	protected abstract boolean doAlarmLogic(float[] values);
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// Do Nothing
	}
}
