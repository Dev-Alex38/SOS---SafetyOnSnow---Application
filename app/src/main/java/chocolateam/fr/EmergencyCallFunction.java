package chocolateam.fr;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class EmergencyCallFunction {

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    // Méthode pour démarrer l'action après un délai spécifique
    public void startCallFunction(int delayMillis, Context context, ArrayList<String> contacts) {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callAction(context, contacts);
            }
        }, delayMillis);
    }

    private void callAction(Context context, ArrayList<String> contacts) {
        if (contacts != null && !contacts.isEmpty()) {
            SmsManager smsManager = SmsManager.getDefault();
            Intent sentIntent = new Intent("SMS_SENT");
            PendingIntent sentPI;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
                sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_IMMUTABLE);
            } else {
                sentPI = PendingIntent.getBroadcast(context, 0, sentIntent, PendingIntent.FLAG_MUTABLE);
            }
            for (String contact : contacts) {
                smsManager.sendTextMessage(contact, null, "Safety On Snow vous alerte ! Votre contact a été victime d'une chute.", sentPI, null);
            }
            Toast.makeText(context, R.string.toast_contact_prevenus, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.empty_contact_list, Toast.LENGTH_SHORT).show();
        }
    }

}