package fr.kriszt.theo.remindwear;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import fr.kriszt.theo.shared.Constants;

public class UpdateUIReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getExtras() != null) {

            /*
             * Step 2: We need to fetch the incoming sms that is broadcast.
             * For this we check the intent of the receiver.
             * */

            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (int i = 0; i < pdus.length; i++) {

                SmsMessage smsMessage = Build.VERSION.SDK_INT >= 19
                        ? Telephony.Sms.Intents.getMessagesFromIntent(intent)[i]
                        : SmsMessage.createFromPdu((byte[]) pdus[i]);



                /*
                 * Step 3: We can get the sender & body of the incoming sms.
                 * The actual parsing of otp is not done here since that is not
                 * the purpose of this implementation
                 *  */
//                String sender = smsMessage.getOriginatingAddress();
//                String body = smsMessage.getMessageBody().toString();
                String otpCode = "123456";



                /*
                 * Step 4: We have parsed the otp. Now we can create an intent
                 * and pass the otp data via that intent.
                 * We have to specify an action for this intent. Now this can be anything String.
                 * This action is important because this action identifies the broadcast event
                 *  */


                Intent in = new Intent(Constants.ACTION_UPDATE_UI);
                Bundle extras = new Bundle();
                extras.putString(Constants.KEY_TITLE, "some title");
                extras.putString(Constants.KEY_CONTENT, "some name");
                in.putExtras(extras);
                context.sendBroadcast(in);
            }
        }
        // https://medium.com/@anitaa_1990/how-to-update-an-activity-from-background-service-or-a-broadcastreceiver-6dabdb5cef74
    }
