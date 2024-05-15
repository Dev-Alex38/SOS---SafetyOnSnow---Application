package chocolateam.fr;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
            // Obtenir la dernière position connue
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            Location location = null;
            if (locationManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    }
                } else {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
            }

            // Inclure la position GPS dans le message
            String message = context.getString(R.string.message_chute);
            String message2 = context.getString(R.string.message_gps) + " ";
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                message += "\n\nPosition GPS : " + latitude + ", " + longitude;
                // Générer l'URL de Google Maps avec les coordonnées de latitude et de longitude
                String googleMapsUrl = "http://maps.google.com/maps?q=" + latitude + "," + longitude;

                // Inclure le lien dans le message SMS
                message2 += googleMapsUrl;
            } else {
                Toast.makeText(context, context.getString(R.string.no_gps_position), Toast.LENGTH_SHORT).show();
            }

            // Envoyer le SMS
            SmsManager smsManager = SmsManager.getDefault();
            for (String contact : contacts) {
                smsManager.sendTextMessage(contact, null, message, null, null);
                if (location != null) {
                    smsManager.sendTextMessage(contact, null, message2, null, null);
                }
            }
            Toast.makeText(context, R.string.toast_contact_prevenus, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.empty_contact_list, Toast.LENGTH_SHORT).show();
        }
    }
}