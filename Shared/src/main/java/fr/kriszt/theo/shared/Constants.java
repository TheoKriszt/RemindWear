package fr.kriszt.theo.shared;

/**
 * Recense les constantes utilisées à travers l'application
 * Permet de conserver le même nommage à tavers les deux modules (mobile et wear)
 * Permet ausi d'&changer des structures clé-valeur sans typo
 */
public class Constants {

    public static final String ACTION_LAUNCH_WEAR_APP = "fr.kriszt.theo.remindwear.shared.LAUNCH_WEAR_APP";
    public static final String ACTION_END_TRACK = "fr.kriszt.theo.remindwear.shared.END_TRACK";

    public static final String PHONE_PATH = "/phone";
    public static final String START_ACTIVITY_PATH = "/start-activity";

    public static final String KEY_TASK_ID = "TASK_ID";
    public static final String KEY_SPORT_TYPE = "SPORT_TYPE";
    public static final String KEY_DATASET = "DATASET";
    public static final String KEY_ID_CATEGORY = "ID_CATEGORY";
    public static final String KEY_CATEGORY = "CAT";
    public static final String KEY_SUBJECT = "SUB";
    public static final String KEY_DATE = "DATE";
    public static final String KEY_MINUTES = "MINUTES";
    public static final String KEY_HOUR = "HOUR";
    public static final String KEY_PARAMS = "PARAMS";

    public static final int POSTPONE_DELAY = 10;

    private Constants() {}
}
