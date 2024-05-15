package chocolateam.fr.Utils;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationService extends Service {

    private LocationManager mLocationManager;
    private SharedPreferences.Editor editor;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                updateLocationInSharedPreferences(latitude, longitude);

                Log.d("LocationService", "Latitude: " + latitude + ", Longitude: " + longitude);
                Toast.makeText(getApplicationContext(), "Latitude: " + latitude + ", Longitude: " + longitude, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            // Vérifiez si le fournisseur activé est GPS_PROVIDER
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.d("LocationService", "GPS Provider enabled");
                Toast.makeText(getApplicationContext(), "GPS Provider enabled", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            // Vérifiez si le fournisseur désactivé est GPS_PROVIDER
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                Log.d("LocationService", "GPS Provider disabled");
                Toast.makeText(getApplicationContext(), "GPS Provider disabled", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        editor = getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE).edit();
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                5000,   // 10 secondes
                10, locationListener);
    }

    private void updateLocationInSharedPreferences(double latitude, double longitude) {
        editor.putString("latitude", String.valueOf(latitude));
        editor.putString("longitude", String.valueOf(longitude));
        editor.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            mLocationManager.removeUpdates(locationListener);
        }
    }
}