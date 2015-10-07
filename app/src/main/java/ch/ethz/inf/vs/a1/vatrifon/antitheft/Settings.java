package ch.ethz.inf.vs.a1.vatrifon.antitheft;

public class Settings {

	// todo: we don't use this! Should we use this?
	public static final String SETTINGS_FILENAME = "AntiTheftSettings";

	public static final boolean ACTIVATE_DEFAULT = false;
	public static final int SENSITIVITY_DEFAULT = 0;
	public static final int TIMEOUT_DEFAULT = 0;

	public static int SENSITIVITY = 0;
	public static int TIMEOUT = 0;

	// todo: is this the right thing todo?
	public static boolean stopAlarm = false;

	public static final String ACTIVATE_STR = "activate";
	public static final String SENSITIVITY_STR = "sensitivity";
	public static final String TIMEOUT_STR = "timeout";

    public static final int ALWAYS_ON_NOTIFICATION_ID = 1;
    public static final int ALARM_NOTIFICATION_ID = 2;

}
