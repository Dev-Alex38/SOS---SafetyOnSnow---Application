package chocolateam.fr.Utils;

import android.content.Context;
import android.location.LocationManager;

public class LocationUtils {
    public boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isGPSEnabled;
    }
}