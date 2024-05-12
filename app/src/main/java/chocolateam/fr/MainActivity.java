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

import chocolateam.fr.Fragments.HistoryFragment;
import chocolateam.fr.Fragments.HomeFragment;
import chocolateam.fr.Fragments.SettingsFragment;
import chocolateam.fr.Utils.LocationUtils;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS = 3;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'est pas accordée, demandez-la à l'utilisateur
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'est pas accordée, demandez-la à l'utilisateur
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'est pas accordée, demandez-la à l'utilisateur
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, MY_PERMISSIONS_REQUEST_POST_NOTIFICATIONS);
        }

        LocationUtils locationUtils = new LocationUtils();
        if (!locationUtils.isGPSEnabled(this)) {
            // Le GPS n'est pas activé, demandez à l'utilisateur d'activer le GPS
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }


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

    private void moveToFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
    }
}