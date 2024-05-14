package chocolateam.fr.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import chocolateam.fr.ContactsDatabaseHelper;
import chocolateam.fr.R;
import chocolateam.fr.Utils.ContactsAdapter;

public class SettingsFragment extends Fragment {

    private static final int CONTACT_PICK_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CONTACT_PICK_REQUEST_CODE = 2;
    private EditText editTextContacts;
    private ContactsDatabaseHelper dbHelper;
    private ListView contactsContainer;
    private ContactsAdapter contactsAdapter;
    private ImageButton btnAddContact;
    private ImageButton btnAddFromApp;
    private ImageButton btnClose;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        contactsContainer = rootView.findViewById(R.id.contactsContainer);
        dbHelper = new ContactsDatabaseHelper(getContext(), this);
        ImageButton btnDeleteAllContacts = rootView.findViewById(R.id.btn_delete_all_contacts);
        ImageButton btnAddContactPopup = rootView.findViewById(R.id.btn_add_contact_popup);

        ArrayList<String> contacts = dbHelper.getContactsFromDatabase();

        contactsAdapter = new ContactsAdapter(getContext(), contacts, dbHelper);
        contactsContainer.setAdapter(contactsAdapter);

        btnAddContactPopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContactPopup();
            }
        });

        btnDeleteAllContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteConfirmPopup();
            }
        });

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
                deleteAllContacts();
            }
        });
        problem_popup.setCancelable(false);
        problem_popup.show();
    }

    private void deleteAllContacts() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("contacts", null, null);
        contactsAdapter.clear();
        Toast.makeText(getContext(), R.string.delete_contact, Toast.LENGTH_SHORT).show();
    }

    public void updateContactsList() {
        contactsAdapter.clear();
        contactsAdapter.addAll(dbHelper.getContactsFromDatabase());
        contactsAdapter.notifyDataSetChanged();
    }

    private void addContactPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View popupView = inflater.inflate(R.layout.popup_add_contact, null);
        builder.setView(popupView);
        final AlertDialog add_contact_popup = builder.create();
        btnAddContact = popupView.findViewById(R.id.add_button);
        editTextContacts = popupView.findViewById(R.id.editTextContact);
        btnAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editTextContacts.getText().toString();
                // Supprimer les espaces du numéro de téléphone
                phoneNumber = phoneNumber.replaceAll("\\s+", "");
                if (!phoneNumber.isEmpty() && phoneNumber.matches("^0[1-9]([-.\\s]?[0-9]{2}){4}$")) {
                    dbHelper.addContactToDatabase(phoneNumber, getContext());
                    editTextContacts.setText("");
                    add_contact_popup.dismiss();
                } else {
                    Toast.makeText(getContext(), R.string.valid_phone_number, Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnAddFromApp = popupView.findViewById(R.id.add_from_contact_app);
        btnAddFromApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickContact();
                add_contact_popup.dismiss();
            }
        });
        btnClose = popupView.findViewById(R.id.contact_button_close);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add_contact_popup.dismiss();
            }
        });

        add_contact_popup.setCancelable(false);
        add_contact_popup.show();
    }

    private void pickContact() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            // La permission n'est pas accordée, demandez-la à l'utilisateur
            ActivityCompat.requestPermissions(requireActivity(), new String[]{android.Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_CONTACT_PICK_REQUEST_CODE);
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
            startActivityForResult(intent, CONTACT_PICK_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = null;
            if (contactUri != null) {
                cursor = requireActivity().getContentResolver().query(contactUri, null, null, null, null);
            }
            if (cursor != null && cursor.moveToFirst()) {
                String phoneNumber = retrieveContactPhoneNumber(cursor);
                if (phoneNumber != null && !phoneNumber.isEmpty()) {
                    dbHelper.addContactToDatabase(phoneNumber, getContext());
                    cursor.close();
                } else {
                    // Gérer le cas où le numéro de téléphone n'a pas pu être récupéré
                    Toast.makeText(getContext(), "Impossible de récupérer le numéro de téléphone du contact sélectionné", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String retrieveContactPhoneNumber(Cursor cursor) {
        String phoneNumber = null;
        int hasPhoneNumber = cursor.getInt(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
        if (hasPhoneNumber > 0) {
            // Le contact a un numéro de téléphone, rechercher dans les données de contact
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phoneCursor = requireActivity().getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                    new String[]{contactId},
                    null);
            if (phoneCursor != null) {
                if (phoneCursor.moveToFirst()) {
                    // Il peut y avoir plusieurs numéros de téléphone associés à un contact,
                    // mais pour simplifier, nous prenons juste le premier numéro ici
                    phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    // Supprimer les espaces du numéro de téléphone
                    phoneNumber = phoneNumber.replaceAll("\\s+", "");
                    phoneNumber = phoneNumber.replaceAll("^\\+33|^\\+\\d{2}", "0");
                }
                phoneCursor.close();
            } else {
                // Gérer le cas où phoneCursor est null
                Toast.makeText(getContext(),"Impossible de récupérer le numéro de téléphone du contact sélectionné", Toast.LENGTH_SHORT).show();
            }
        }
        return phoneNumber;
    }
}