package ch.ethz.inf.vs.a1.vatrifon.antitheft;

public class Settings {

	// we don't use this! Should we use this?
	public static final String SETTINGS_FILENAME = "AntiTheftSettings";

	public static final boolean ACTIVATE_DEFAULT = false;
	public static final int SENSITIVITY_DEFAULT = 30;
	public static final int TIMEOUT_DEFAULT = 0;
	public static final int COOLDOWN_SKIP_SENSOR_READINGS = 6;

    public static final int TIMEOUT_UPPER_BOUND = 60;
    public static final int TIMEOUT_LOWER_BOUND = 0;
    public static final int SENSITIVITY_UPPER_BOUND = 200;
    public static final int SENSITIVITY_LOWER_BOUND = 20;

	public static int SENSITIVITY = 30;
	// period of time after the triggering of the alarm, after which the alarm sound plays
	public static int TIMEOUT = 5;
	// period of time the phone needs to be moving to trigger the alarm
	public static int PERIOD = 5;

    public static boolean inAlarmMode = false;

	public static final String ACTIVATE_STR = "activate";
	public static final String SENSITIVITY_STR = "sensitivity";
	public static final String TIMEOUT_STR = "timeout";

    public static final int ALWAYS_ON_NOTIFICATION_ID = 1;
    public static final int ALARM_NOTIFICATION_ID = 2;

}
