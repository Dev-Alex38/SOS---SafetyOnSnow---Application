package chocolateam.fr.Network;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import chocolateam.fr.MainActivity;
import chocolateam.fr.Utils.ReceiveFallFunction;

public class HttpServerListener extends AsyncTask<Void, Void, Void> {
    public static final String SERVER_IP = "192.168.1.130"; // Adresse IP du serveur sur la carte du boîtier
    private static final int SERVER_PORT = 8000; // Le port sur lequel écouter
    private static final String EXPECTED_URL = "/fall?=1"; // URL attendue
    private final MainActivity activity;
    public HttpServerListener(MainActivity activity) {
        this.activity = activity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                InputStream inputStream = clientSocket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line = reader.readLine();
                if (line != null && line.startsWith("GET " + SERVER_IP + ":" + SERVER_PORT + EXPECTED_URL)) {
                    // Si la requête correspond à l'URL attendue, exécutez la fonction spécifique
                    new ReceiveFallFunction(activity).handleEmergencyNotification();
                    Toast.makeText(activity, "Test APP", Toast.LENGTH_SHORT).show();
                }
                clientSocket.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}