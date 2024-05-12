package chocolateam.fr.Utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import chocolateam.fr.ContactsDatabaseHelper;
import chocolateam.fr.R;

public class ContactsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> contacts;
    private final ContactsDatabaseHelper dbHelper;
    private boolean isFavorite = false;


    public ContactsAdapter(Context context, ArrayList<String> contacts, ContactsDatabaseHelper dbHelper) {
        super(context, R.layout.item_contact, contacts);
        this.context = context;
        this.contacts = contacts;
        this.dbHelper = dbHelper;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_contact, parent, false);
        TextView textViewContact = itemView.findViewById(R.id.textViewContact);
        ImageButton btnAddToFavorites = itemView.findViewById(R.id.btnAddToFavorites);
        ImageButton btnDeleteContact = itemView.findViewById(R.id.btnDeleteContact);

        final String phoneNumber = contacts.get(position);
        String formattedPhoneNumber = formatPhoneNumber(phoneNumber); // Formater le numéro de téléphone
        textViewContact.setText(formattedPhoneNumber);

        btnAddToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Basculer l'état du bouton favoris
                isFavorite = !isFavorite;

                // Mettre à jour la couleur du bouton
                updateFavoriteButtonColor(btnAddToFavorites);
            }
        });

        btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Supprimer le contact de la base de données
                dbHelper.deleteContactFromDatabase(phoneNumber, context);
            }
        });

        return itemView;
    }

    // Méthode pour mettre à jour la couleur du bouton favoris en fonction de l'état
    private void updateFavoriteButtonColor(ImageButton button) {
        if (isFavorite) {
            // Si le contact est favoris, définir la couleur du bouton sur jaune
            button.setColorFilter(context.getResources().getColor(R.color.yellow));
        } else {
            // Sinon, définir la couleur du bouton sur gris
            button.setColorFilter(context.getResources().getColor(R.color.grey));
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        StringBuilder formattedNumber = new StringBuilder();
        for (int i = 0; i < phoneNumber.length(); i++) {
            formattedNumber.append(phoneNumber.charAt(i));
            if ((i + 1) % 2 == 0 && (i + 1) != phoneNumber.length()) {
                formattedNumber.append(" "); // Ajouter un espace après chaque deux caractères
            }
        }
        return formattedNumber.toString();
    }
}