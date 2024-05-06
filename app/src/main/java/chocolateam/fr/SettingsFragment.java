package chocolateam.fr;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private EditText editTextContacts;
    private ContactsDatabaseHelper dbHelper;
    private ListView contactsContainer;
    private ArrayAdapter<String> contactsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        editTextContacts = rootView.findViewById(R.id.editTextContact);
        ImageButton btnAddContact = rootView.findViewById(R.id.add_button);
        contactsContainer = rootView.findViewById(R.id.contactsContainer);
        dbHelper = new ContactsDatabaseHelper(getContext());

        ArrayList<String> contacts = getContactsFromDatabase();
        contactsAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, contacts);
        contactsContainer.setAdapter(contactsAdapter);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextContacts.getText().toString();
                if (!phoneNumber.isEmpty()) {
                    addContactToDatabase(phoneNumber);
                    editTextContacts.setText(""); // Efface le texte après l'ajout
                } else {
                    Toast.makeText(getContext(), "Veuillez saisir un numéro de téléphone", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    private void addContactToDatabase(String phoneNumber) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        long newRowId = db.insert("contacts", null, values);
        if (newRowId != -1) {
            Toast.makeText(getContext(), "Contact ajouté à la base de données", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Erreur lors de l'ajout du contact à la base de données", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<String> getContactsFromDatabase() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        ArrayList<String> contactsList = new ArrayList<>();
        Cursor cursor = db.query("contacts", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
            contactsList.add(phoneNumber);
        }
        cursor.close();
        return contactsList;
    }
}