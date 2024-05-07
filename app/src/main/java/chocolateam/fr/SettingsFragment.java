package chocolateam.fr;

import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class SettingsFragment extends Fragment {

    private EditText editTextContacts;
    private ContactsDatabaseHelper dbHelper;
    private ListView contactsContainer;
    private ContactsAdapter contactsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        editTextContacts = rootView.findViewById(R.id.editTextContact);
        ImageButton btnAddContact = rootView.findViewById(R.id.add_button);
        contactsContainer = rootView.findViewById(R.id.contactsContainer);
        dbHelper = new ContactsDatabaseHelper(getContext());
        Button btnDeleteAllContacts = rootView.findViewById(R.id.btn_delete_all_contacts);


        ArrayList<String> contacts = dbHelper.getContactsFromDatabase(); // Utilisez dbHelper pour obtenir les contacts

        // Créer un adaptateur personnalisé pour la liste
        contactsAdapter = new ContactsAdapter(getContext(), contacts);

        // Définir l'adaptateur sur la ListView
        contactsContainer.setAdapter(contactsAdapter);

        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextContacts.getText().toString();
                if (!phoneNumber.isEmpty() && phoneNumber.matches("^0[1-9]([-.\\s]?[0-9]{2}){4}$")) {
                    new ContactsDatabaseHelper(getContext()).addContactToDatabase(phoneNumber, getContext());
                    editTextContacts.setText(""); // Efface le texte après l'ajout
                } else {
                    Toast.makeText(getContext(), R.string.valid_phone_number, Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Définir le gestionnaire de clics pour le bouton de suppression de tous les contacts
        btnDeleteAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmPopup();
            }
        });

        contactsContainer.setAdapter(contactsAdapter);
        return rootView;
    }

    private void deleteConfirmPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_valid_delete_contact, null);
        builder.setView(popupView);
        final AlertDialog problem_popup = builder.create();
        Button buttonClose = popupView.findViewById(R.id.button_close);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problem_popup.dismiss();
            }
        });
        Button buttonConfirm = popupView.findViewById(R.id.button_confirm);
        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                problem_popup.dismiss();
                // Passer les contacts à la méthode
                deleteAllContacts();
            }

        });
        problem_popup.setCancelable(true);
        problem_popup.show();
    }

    private void deleteAllContacts() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("contacts", null, null);
        contactsAdapter.clear(); // Effacer les données de l'adaptateur
        Toast.makeText(getContext(), R.string.delete_contact, Toast.LENGTH_SHORT).show();
    }

    public void updateContactsList() {
        ContactsAdapter contactsAdapter = (ContactsAdapter) contactsContainer.getAdapter();
        if (contactsAdapter != null) {
            contactsAdapter.clear(); // Effacer les anciennes données
            ContactsDatabaseHelper dbHelper = new ContactsDatabaseHelper(getContext());
            contactsAdapter.addAll(dbHelper.getContactsFromDatabase()); // Ajouter les nouvelles données depuis la base de données
            contactsAdapter.notifyDataSetChanged(); // Notifier l'adaptateur du changement
        }
    }
}