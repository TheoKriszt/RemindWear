package fr.kriszt.theo.remindwear.voice;

import android.content.Context;
import android.content.Intent;
import android.service.autofill.FieldClassification;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.kriszt.theo.remindwear.data.PhoneDataService;
import fr.kriszt.theo.remindwear.ui.activities.AddTaskActivity;
import fr.kriszt.theo.shared.Constants;
import fr.kriszt.theo.shared.SportType;

import static android.app.Activity.RESULT_OK;
import static fr.kriszt.theo.shared.Constants.ACTION_LAUNCH_WEAR_APP;

public class VoiceUtils {
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final String TAG = VoiceUtils.class.getSimpleName();
    private static final Pattern sportPattern = Pattern.compile("^(?:faire|commencer?|lancer?) ?(?:le|un|du|de la) ?(?:exercice|tracking|suivi|sport)? ?(?:sportif)? ?(?:de)? ?(vélo|course|marche)?(?: à pied)?");
    private static final Pattern remindPattern = Pattern.compile("^(?:mets-moi|ajoute-moi|ajoute|mets) une? (?:rappel|tâche)|rappelle-moi");

    public static void startSpeechRecognizer(Fragment fragment){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        fragment.startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    public static Intent getRecognizedSpeech(int requestCode, int resultCode, Intent data, Context context) {

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.w(TAG, "getRecognizedSpeech: GOT \"" + spokenText  + "\"");
            return parseSpeech(spokenText, context);
        }
        return null;
    }

    private static Intent parseSpeech(String spokenText, Context context) {
        Matcher sportMatcher = sportPattern.matcher(spokenText);
        Matcher remindMatcher = remindPattern.matcher(spokenText);


        if (sportMatcher.find()){
            Log.w(TAG, "parseSpeech: Demande de sport reconnue");
            return startTrackingIntent(sportMatcher.group(1), context);
        }else if (remindMatcher.find()){
            Log.w(TAG, "parseSpeech: Demande de rappel reconnue");
            String matched = remindMatcher.group(0);
            return parseRemindIntent(matched, spokenText, context);
        } else {
            Toast.makeText(context, "Demande non reconnue", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private static Intent startTrackingIntent(String sportName, Context context) {
        Intent startIntent = new Intent(context, PhoneDataService.class);
        startIntent.setAction(ACTION_LAUNCH_WEAR_APP);
        startIntent.putExtra(Constants.KEY_TASK_ID, 0);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (sportName == null){
            startIntent.removeExtra(Constants.KEY_SPORT_TYPE);
            context.startService(startIntent);
            Toast.makeText(context, "Choisissez un sport sur la montre", Toast.LENGTH_LONG).show();
            return startIntent;

        } else {

            SportType sportType;
            switch (sportName){
                case "course":
                    sportType = SportType.SPORT_RUN;
                    break;
                case "vélo":
                    sportType = SportType.SPORT_BIKE;
                    break;
                    default: sportType = SportType.SPORT_WALK;
            }
            startIntent.putExtra(Constants.KEY_SPORT_TYPE, sportType);
            Toast.makeText(context, "Démarrage d'un exercice de " + sportType.getName() + " sur la montre", Toast.LENGTH_LONG).show();
            context.startService(startIntent);
            return startIntent;
        }
    }

    private static Intent parseRemindIntent(String matched, String spokenText, Context context) {
        spokenText = spokenText.replace(matched, "");

        Pattern categoryPattern = Pattern.compile(" ?dans la catégorie ?(\\S+)");
        Matcher categoryMatcher = categoryPattern.matcher(spokenText);
        String category = null;
        if (categoryMatcher.find()){
            category = categoryMatcher.group(1);
            spokenText = spokenText.replace(categoryMatcher.group(0), "");
        }

        String time = null;
        Pattern timePattern = Pattern.compile(" ?à (midi|minuit|\\d{1,2}h|\\d{1,2}h\\d{1,2})$");
        Matcher timeMatcher = timePattern.matcher(spokenText);
        if (timeMatcher.find()){
            time = timeMatcher.group(1);
            spokenText = spokenText.replace( timeMatcher.group(0), "" );
        }

        String date = null;
        Pattern datePattern = Pattern.compile("(?:\\ble )?(aujourd'hui|demain|après demain|\\d{1,2}|\\d{1,2} \\w+)$");
        Matcher dateMatcher = datePattern.matcher(spokenText);
        if (dateMatcher.find()){
            date = dateMatcher.group(1);
            spokenText = spokenText.replace( dateMatcher.group(0), "" );
        }

        String what = null;
        Pattern whatPattern = Pattern.compile("(?:de|pour|d\\') ?(.*)");
        Matcher whatMatcher = whatPattern.matcher(spokenText);
        if (whatMatcher.find()){
            what = whatMatcher.group(1);
        }
        Log.w(TAG, "WHAT : " + what);
        Log.w(TAG, "CATEGORY : " + category);
        Log.w(TAG, "DATE: " + date);
        Log.w(TAG, "TIME: " + time);






        Calendar calendar = new GregorianCalendar();
        if (date != null) {
            switch (date) {
                case "demain":
                    calendar.add(Calendar.DAY_OF_YEAR, 1);
                    break;
                case "après-demain":
                    calendar.add(Calendar.DAY_OF_YEAR, 2);
                    break;
                default:
                    Pattern ddmmPattern = Pattern.compile("(\\d{1,2}) ?(\\w+)?");
                    Matcher ddmmMatcher = ddmmPattern.matcher(date);
                    if (ddmmMatcher.find()) {
                        if (ddmmMatcher.group(1) != null) {
                            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(ddmmMatcher.group(1)));
                        }

                        if (ddmmMatcher.group(2) != null) {
                            ArrayList<String> months = new ArrayList<>(Arrays.asList("janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre"));
                            if (months.contains( ddmmMatcher.group(2) )){
                                calendar.set(Calendar.MONTH, months.indexOf( ddmmMatcher.group(2) ));
                            }
                        }
//                    for (int i = 0; i < ddmmMatcher.groupCount() + 1; i++) {
//                        Log.w(TAG, "group " + i + " :: " + ddmmMatcher.group(i));
//                    }
                    } else Log.w(TAG, "Date non reconnue : " + date);
                    break;
            }
        }

        Intent startIntent = new Intent(context, AddTaskActivity.class);

        if (time != null){
            if (time.equals("midi")){
                time = "12h";
            }else if (time.equals("minuit")){
                time = "00h";
            }
            if (time.endsWith("h")){
                time += "00";
            }

            String[] timeTokens = time.split("h");
            startIntent.putExtra(Constants.KEY_HOUR, Integer.parseInt(timeTokens[0]));
            startIntent.putExtra(Constants.KEY_MINUTES, Integer.parseInt(timeTokens[1]));

        }

        startIntent.putExtra(Constants.KEY_DATE, calendar.getTimeInMillis());
        if (what != null){
            startIntent.putExtra(Constants.KEY_SUBJECT, what);
        }

        if (category != null){ // TODO : chek if category exists
            startIntent.putExtra(Constants.KEY_CATEGORY, category);
        }

        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return startIntent;
    }

}
