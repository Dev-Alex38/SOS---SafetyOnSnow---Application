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

public class ContactsAdapter extends ArrayAdapter<Contact> {
    private final Context context;
    private final ArrayList<Contact> contacts;
    private final ContactsDatabaseHelper dbHelper;

    public ContactsAdapter(Context context, ArrayList<Contact> contacts, ContactsDatabaseHelper dbHelper) {
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

        final Contact contact = contacts.get(position);
        String formattedPhoneNumber = formatPhoneNumber(contact.getPhoneNumber());
        textViewContact.setText(formattedPhoneNumber);

        updateFavoriteButtonColor(btnAddToFavorites, contact.isFavorite());

        btnAddToFavorites.setOnClickListener(v -> {
            contact.setFavorite(!contact.isFavorite());
            dbHelper.updateContactFavoriteStatus(contact.getPhoneNumber(), contact.isFavorite());
            updateFavoriteButtonColor(btnAddToFavorites, contact.isFavorite());
        });

        btnDeleteContact.setOnClickListener(v -> {
            dbHelper.deleteContactFromDatabase(contact.getPhoneNumber(), context);
        });

        return itemView;
    }

    // Méthode pour mettre à jour la couleur du bouton favoris en fonction de l'état
    private void updateFavoriteButtonColor(ImageButton button, boolean isFavorite) {
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