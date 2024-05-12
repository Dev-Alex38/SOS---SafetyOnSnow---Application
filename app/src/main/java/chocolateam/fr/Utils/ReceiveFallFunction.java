package chocolateam.fr.Utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import chocolateam.fr.ContactsDatabaseHelper;
import chocolateam.fr.EmergencyCallFunction;
import chocolateam.fr.R;

public class ReceiveFallFunction {

    private static final int NOTIFICATION_ID = 1;
    private static final String CHANNEL_ID = "chocolateam.fr.channel";
    private static final long COUNTDOWN_TIME = 30 * 1000; // 30 seconds in milliseconds
    private boolean isCancelled = false;
    private Context context;
    private ContactsDatabaseHelper dbHelper;
    private ArrayList<String> contacts;
    public ReceiveFallFunction(Context context) {
        this.context = context;
    }

    public void handleEmergencyNotification() {
        // Check permissions
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.SEND_SMS}, 0);
            return;
        }

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            return;
        }

        // Create notification channel
        createNotificationChannel();

        // Display notification
        showNotification();

        // Show popup with timer
        showTimerPopup();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Chute détectée";
            String description = "Notification pour signaler une chute détectée";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void showNotification() {
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder = new Notification.Builder(context, CHANNEL_ID)
                    .setContentTitle("Chute détectée")
                    .setContentText("Une chute a été détectée. Cliquez pour plus de détails.")
                    .setSmallIcon(R.drawable.ic_warning);
        }

        NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    private void showTimerPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.popup_timer, null);
        builder.setView(dialogView);

        TextView textViewTimer = dialogView.findViewById(R.id.textViewTimer);
        Button cancelButton = dialogView.findViewById(R.id.btnCancel);

        final AlertDialog dialog = builder.create();
        dialog.show();

        new CountDownTimer(COUNTDOWN_TIME, 1000) {
            public void onTick(long millisUntilFinished) {
                if (!isCancelled) {
                    textViewTimer.setText("Temps restant : " + millisUntilFinished / 1000 + " secondes");
                }
            }

            public void onFinish() {
                if (!isCancelled) {
                    dialog.dismiss();
                    startEmergencyCall();
                }
            }
        }.start();

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isCancelled = true;
                dialog.dismiss();
                // You can add code here to handle cancellation
            }
        });
    }

    private void startEmergencyCall() {
        dbHelper = new ContactsDatabaseHelper(context, null);
        contacts = dbHelper.getContactsFromDatabase();
        new EmergencyCallFunction().startCallFunction(1000, context, contacts);
    }
}