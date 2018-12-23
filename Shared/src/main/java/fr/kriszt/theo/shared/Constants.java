package fr.kriszt.theo.shared;

/**
 * Created by T.Kriszt on 07/10/2018.
 */
public class Constants {

    public static final String ACTION_DISMISS = "fr.kriszt.theo.remindwear.shared.DISMISS";
    public static final String ACTION_LAUNCH_WEAR_APP = "fr.kriszt.theo.remindwear.shared.LAUNCH_WEAR_APP";
    public static final String ACTION_TRACK = "fr.kriszt.theo.remindwear.shared.TRACK";
    public static final String ACTION_END_TRACK = "fr.kriszt.theo.remindwear.shared.END_TRACK";
    public static final String KEY_NOTIFICATION_ID = "NOTIFICATION_ID";
    public static final String KEY_TASK_ID = "TASK_ID"; // TODO : move identifiers in Notifications
    public static final String KEY_CONTENT = "CONTENT";
    public static final String KEY_TITLE = "TITLE";
    public static final String KEY_SPORT_TYPE = "SPORT_TYPE";
    public static final String KEY_DATASET = "DATASET";
    public static final int  BOTH_ID = 4;
    public static final int PHONE_ID = 3;
    public static final int  WEAR_ID = 2;

    public static final String BOTH_PATH = "/both";
    public static final String PHONE_PATH = "/phone";
    public static final String WEAR_PATH = "/watch";

    private Constants() {};
}
