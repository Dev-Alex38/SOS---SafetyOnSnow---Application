package chocolateam.fr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class ContactsAdapter extends ArrayAdapter<String> {

    private final Context context;
    private final ArrayList<String> contacts;
    private final ContactsDatabaseHelper dbHelper;

    public ContactsAdapter(Context context, ArrayList<String> contacts) {
        super(context, R.layout.item_contact, contacts);
        this.context = context;
        this.contacts = contacts;
        this.dbHelper = new ContactsDatabaseHelper(context);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.item_contact, parent, false);
        TextView textViewContact = itemView.findViewById(R.id.textViewContact);
        Button btnAddToFavorites = itemView.findViewById(R.id.btnAddToFavorites);
        Button btnDeleteContact = itemView.findViewById(R.id.btnDeleteContact);

        final String phoneNumber = contacts.get(position);
        textViewContact.setText(phoneNumber);

        btnAddToFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ajouter le contact aux favoris (à implémenter)
            }
        });

        btnDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Supprimer le contact de la base de données
                new ContactsDatabaseHelper(context).deleteContactFromDatabase(phoneNumber,context);
            }
        });

        return itemView;
    }
}