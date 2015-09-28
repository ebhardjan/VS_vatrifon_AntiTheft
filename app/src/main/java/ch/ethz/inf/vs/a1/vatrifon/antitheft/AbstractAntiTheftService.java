package ch.ethz.inf.vs.a1.vatrifon.antitheft;

import android.app.Service;

public abstract class AbstractAntiTheftService extends Service {

	protected AbstractMovementDetector listener;

	@Override
	public void onCreate() {
		listener = new MovementDetector();
		listener.setCallbackService(this);
	}

	/**
	 * Starts the alarm when a deliberate move is detected.
	 */
	public abstract void startAlarm();
}
