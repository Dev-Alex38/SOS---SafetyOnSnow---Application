package chocolateam.fr;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.LocationManager;
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
            SharedPreferences sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE);
            String latitude = sharedPreferences.getString("latitude", "0.0");
            String longitude = sharedPreferences.getString("longitude", "0.0");

            // Inclure la position GPS dans le message
            String message = context.getString(R.string.message_chute);
            String message2 = context.getString(R.string.message_gps) + " ";
            message += "\n\nPosition GPS : " + latitude + ", " + longitude;
            // Générer l'URL de Google Maps avec les coordonnées de latitude et de longitude
            String googleMapsUrl = "http://maps.google.com/maps?q=" + latitude + "," + longitude;

            // Inclure le lien dans le message SMS
            message2 += googleMapsUrl;

            // Envoyer le SMS
            SmsManager smsManager = SmsManager.getDefault();
            for (String contact : contacts) {
                smsManager.sendTextMessage(contact, null, message, null, null);
                smsManager.sendTextMessage(contact, null, message2, null, null);
            }
            Toast.makeText(context, R.string.toast_contact_prevenus, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.empty_contact_list, Toast.LENGTH_SHORT).show();
        }
    }
}