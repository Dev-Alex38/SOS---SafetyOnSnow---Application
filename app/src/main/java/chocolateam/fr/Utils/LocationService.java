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
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

public class LocationService extends Service {

    private LocationManager mLocationManager;
    private SharedPreferences.Editor editor;

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
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startLocationUpdates();
        return START_STICKY;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000,   // 10 secondes
                10, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                editor.putString("latitude", String.valueOf(latitude));
                editor.putString("longitude", String.valueOf(longitude));
                editor.apply();
            }
        }
    };
}