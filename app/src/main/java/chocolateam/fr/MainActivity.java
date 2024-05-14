package chocolateam.fr;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import chocolateam.fr.Fragments.HistoryFragment;
import chocolateam.fr.Fragments.HomeFragment;
import chocolateam.fr.Fragments.SettingsFragment;
import chocolateam.fr.Utils.HttpServerListener;
import chocolateam.fr.Utils.LocationService;
import chocolateam.fr.Utils.LocationUtils;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_ALL = 100;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkAllPermissions();

        LocationUtils locationUtils = new LocationUtils();
        if (!locationUtils.isGPSEnabled(this)) {
            requestGPSEnabling();
        }

        startLocationService(); // Appel pour démarrer le service de localisation
        new HttpServerListener(this).execute();

        moveToFragment(new HomeFragment());

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                moveToFragment(new HomeFragment());
            } else if (itemId == R.id.nav_settings) {
                moveToFragment(new SettingsFragment());
            } else if (itemId == R.id.nav_history) {
                moveToFragment(new HistoryFragment());
            }
            return true;
        });
    }

    private void requestGPSEnabling() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    private void moveToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }

    private void startLocationService() {
        Intent intent = new Intent(this, LocationService.class);
        startService(intent);
    }

    private void checkAllPermissions() {
        String[] permissions = {
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.READ_CONTACTS};

        ArrayList<String> permissionsToRequest = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), MY_PERMISSIONS_REQUEST_ALL);
        }
    }
}