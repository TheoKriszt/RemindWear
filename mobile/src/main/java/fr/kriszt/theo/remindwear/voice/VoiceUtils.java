package fr.kriszt.theo.remindwear.voice;

import android.content.Context;
import android.content.Intent;
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

//    private static final String SPEECH_REQUEST_CODE = "fr.kriszt.theo.remindwear.SPEECH_TASK_REQUEST";
    private static final int SPEECH_REQUEST_CODE = 0;
    private static final String TAG = VoiceUtils.class.getSimpleName();

    public static void startSpeechRecognizer(Fragment fragment){

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
// Start the activity, the intent will be populated with the speech text
        fragment.startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    public static Intent getRecognizedSpeech(int requestCode, int resultCode, Intent data, Context context) {

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.w(TAG, "getRecognizedSpeech: GOT \"" + spokenText  + "\"");
            return parseSpeech(spokenText, context);
            // Do something with spokenText
        }
        return null;
    }

    private static Intent parseSpeech(String spokenText, Context context) {
        ArrayList<String> action_remind_keywords = new ArrayList<>();
        action_remind_keywords.add("rappelle");
        action_remind_keywords.add("rappelle-moi");
        action_remind_keywords.add("rappel");
        action_remind_keywords.add("crée");
        action_remind_keywords.add("tâche");
        action_remind_keywords.add("ajoute");

        ArrayList<String> action_track_keywords = new ArrayList<>();
        action_track_keywords.add("track");
        action_track_keywords.add("tracking");
        action_track_keywords.add("suivi");
        action_track_keywords.add("sportif");
        action_track_keywords.add("exercice");
        action_track_keywords.add("vélo");
        action_track_keywords.add("course");
        action_track_keywords.add("marche");

        String[] tokens = spokenText.split("[ ']");

        SpeechIntent speechIntent = SpeechIntent.UNKNOWN;

        int track_count = 0;
        int remind_count = 0;

        for (String token : tokens){
            if (action_track_keywords.contains(token)) track_count++;
            if (action_remind_keywords.contains(token)) remind_count++;
        }

        if (track_count > remind_count) {
            speechIntent = SpeechIntent.START_TRACKING;
        } else if (track_count < remind_count){
            speechIntent = SpeechIntent.CREATE_TASK;
        }
        if (speechIntent == SpeechIntent.UNKNOWN && spokenText.contains("sport")){
            speechIntent = SpeechIntent.START_TRACKING;
        }

        if (speechIntent == SpeechIntent.UNKNOWN && spokenText.startsWith("rappelle-moi")){
            speechIntent = SpeechIntent.CREATE_TASK;
        }


        Log.w(TAG, "parseSpeech: Speech Intent is " + speechIntent);


        if (speechIntent == SpeechIntent.START_TRACKING){

            return parseTrackingIntent(tokens, context);
        } else if ( speechIntent == SpeechIntent.CREATE_TASK ){
            return parseRemindIntent(tokens, context);
        } else {
            Toast.makeText(context, "Demande non reconnue", Toast.LENGTH_SHORT).show();
            return null;
        }


    }

    private static Intent parseRemindIntent(String[] tokens, Context context) {
        String phrase = Arrays.toString(tokens).replaceAll(",|\\[|\\]", "");
//        Log.w(TAG, "parseRemindIntent: tokens string : " + phrase);


        Pattern fullPattern = Pattern.compile("(?:rappelle-moi|ajoute un rappel|ajoute une t.che) (?:dans la catégorie (\\S+))? ?(?:de|d')? (.*) (demain|aujourd'hui|le \\d+ \\w+)? ?à (\\d+h\\d*|midi|minuit)");
        Matcher fullMatcher = fullPattern.matcher(phrase);
//        Log.w(TAG, "parseRemindIntent: groupCount is " + fullMatcher.groupCount());
        if (!fullMatcher.find()) {
            // Todo : non reconnu
            Toast.makeText(context, "Demande de rappel non reconnue", Toast.LENGTH_SHORT).show();
//            Log.w(TAG, "parseRemindIntent: non reconnu :");


        } else {

            for (int i = 0; i < fullMatcher.groupCount() + 1; i++) {
                Log.w(TAG, "parseRemindIntent: group " + i + " :: " + fullMatcher.group(i));
            }

            String cat = fullMatcher.group(1);
            String whatAndWhen = fullMatcher.group(2);
            String time = fullMatcher.group(4);
            String what = whatAndWhen;
            String when = "aujourd'hui";

            Pattern whatwhenPattern = Pattern.compile("(.*) (demain|aujourd'hui|le \\d+ \\w+)");
            Matcher whatwhenMatcher = whatwhenPattern.matcher(whatAndWhen);
            // ex : "faire la vaisselle le 5 janvier"  => séparer le quoi et le quand
//            Log.w(TAG, "parseRemindIntent: What and chen vaut " + whatAndWhen);
            if (whatwhenMatcher.find()) {
//                Log.w(TAG, "parseRemindIntent: What and where 0 :: " + whatwhenMatcher.group(0));
//                Log.w(TAG, "parseRemindIntent: What and where 1 :: " + whatwhenMatcher.group(1));
//                Log.w(TAG, "parseRemindIntent: What and where 2 :: " + whatwhenMatcher.group(2));
                what = whatwhenMatcher.group(1);
                if (whatwhenMatcher.group(2) != null) {
                    when = whatwhenMatcher.group(2);
                }
            } else {
                Log.w(TAG, "parseRemindIntent: Impossible de savoir quoi faire et quand avec " + whatAndWhen);
            }

            if (time.equals("midi")) time = "12h";
            if (time.equals("minuit")) time = "0h";

            if (time.endsWith("h")) time += "00";

            Calendar calendar = new GregorianCalendar();
            if (when.equals("demain")) {
                calendar.add(Calendar.HOUR, 24);
            } else if (when.matches("le \\d+ \\w+")) {
//                Log.w(TAG, "parseRemindIntent: Parsing d'un jour type ==" + when);
                Pattern datePattern = Pattern.compile("(?:le)? ?(\\d+) (\\w+)");
                Matcher dateMatcher = datePattern.matcher(when);
                if (dateMatcher.find()) {
//                    Log.w(TAG, "parseRemindIntent: jour : " + dateMatcher.group(1));
//                    Log.w(TAG, "parseRemindIntent: mois : " + dateMatcher.group(2));

                    int month = Calendar.JANUARY;
                    int dayOfMonth = Integer.parseInt(dateMatcher.group(1));
                    String[] months = {"janvier", "février", "mars", "avril", "mai", "juin", "juillet", "août", "septembre", "octobre", "novembre", "décembre"};
                    for (int i = 0; i < months.length; i++) {
                        if (dateMatcher.group(2).equals(months[i])) {
                            month = i;
                        }
                    }


                    calendar.set(Calendar.MONTH, month);
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                }

            }

            int hour = Integer.parseInt(time.split("h")[0]);
            int minutes = Integer.parseInt(time.split("h")[1]);


//            Log.w(TAG, "parseRemindIntent: Time: " + hour + " h " + minutes);
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minutes);
//            Log.w(TAG, "parseRemindIntent: DateTime" + new Date(calendar.getTimeInMillis()));
//            Log.w(TAG, "parseRemindIntent: Categorie :: " + cat);
//            Log.w(TAG, "parseRemindIntent: Quoi ?  :: " + what);

            Intent startIntent = new Intent(context, AddTaskActivity.class);
            startIntent.putExtra(Constants.KEY_HOUR, hour);
            startIntent.putExtra(Constants.KEY_MINUTES, minutes);
            startIntent.putExtra(Constants.KEY_DATE, calendar.getTimeInMillis());
            startIntent.putExtra(Constants.KEY_SUBJECT, what);
            startIntent.putExtra(Constants.KEY_CATEGORY, cat);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(startIntent);



        }

        return null;
    }

    private static Intent parseTrackingIntent(String[] tokens, Context context) {
        SportType sportType = SportType.SPORT_RUN;

        int bikeCount = 0;
        int walkCount = 0;
        int runCount = 0;

        for (String token : tokens){
            if (token.equals("vélo")) bikeCount++;
            if (token.equals("course")) runCount++;
            if (token.equals("marche")) walkCount++;
        }

        if (bikeCount > walkCount && bikeCount > runCount) {
            sportType = SportType.SPORT_BIKE;
        } else if (walkCount > runCount && walkCount > bikeCount){
            sportType = SportType.SPORT_WALK;
        }

        Intent startIntent = new Intent(context, PhoneDataService.class);
        startIntent.setAction(ACTION_LAUNCH_WEAR_APP);
        startIntent.putExtra(Constants.KEY_TASK_ID, 0);
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (bikeCount == walkCount && walkCount == runCount){
//            Log.w(TAG, "parseSpeech: Quel type de sport ? ");
            context.startService(startIntent);
            Toast.makeText(context, "Choisissez un sport sur la montre", Toast.LENGTH_LONG).show();
            return startIntent;

        } else {
//            Log.w(TAG, "parseSpeech: Type de sport : " + sportType);
            startIntent.putExtra(Constants.KEY_SPORT_TYPE, sportType);
            Toast.makeText(context, "Démarrage d'un exercice de " + sportType.getName(), Toast.LENGTH_LONG).show();
            context.startService(startIntent);
            return startIntent;
        }

    }

    enum SpeechIntent {
        CREATE_TASK,
        START_TRACKING,
        UNKNOWN
    }
}
