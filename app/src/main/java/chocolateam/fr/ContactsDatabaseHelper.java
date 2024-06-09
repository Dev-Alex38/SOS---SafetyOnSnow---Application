package chocolateam.fr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

import chocolateam.fr.Fragments.SettingsFragment;
import chocolateam.fr.Utils.Contact;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 2;
    private SettingsFragment settingsFragment;

    public ContactsDatabaseHelper(Context context, SettingsFragment settingsFragment) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.settingsFragment = settingsFragment;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, phone_number TEXT, is_favorite INTEGER DEFAULT 0);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE contacts ADD COLUMN is_favorite INTEGER DEFAULT 0;");
        }
    }

    public ArrayList<Contact> getContactsFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<Contact> contactsList = new ArrayList<>();
        Cursor cursor = db.query("contacts", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
            boolean isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow("is_favorite")) > 0;
            contactsList.add(new Contact(phoneNumber, isFavorite));
        }
        cursor.close();
        return contactsList;
    }

    public void deleteContactFromDatabase(String phoneNumber, Context context) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("contacts", "phone_number = ?", new String[]{phoneNumber});
        settingsFragment.updateContactsList();
        Toast.makeText(context, R.string.delete_one_contact, Toast.LENGTH_SHORT).show();
    }

    public void addContactToDatabase(String phoneNumber, Context context) {
        ArrayList<Contact> currentContacts = getContactsFromDatabase();
        for (Contact contact : currentContacts) {
            if (contact.getPhoneNumber().equals(phoneNumber)) {
                Toast.makeText(context, R.string.contact_already_exists, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        long newRowId = db.insert("contacts", null, values);
        if (newRowId != -1) {
            settingsFragment.updateContactsList();
        } else {
            Toast.makeText(context, R.string.error_add_contact, Toast.LENGTH_SHORT).show();
        }
    }

    public void updateContactFavoriteStatus(String phoneNumber, boolean isFavorite) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("is_favorite", isFavorite ? 1 : 0);
        db.update("contacts", values, "phone_number = ?", new String[]{phoneNumber});
    }
}