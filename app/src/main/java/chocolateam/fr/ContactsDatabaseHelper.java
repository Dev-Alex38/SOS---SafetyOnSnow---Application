package chocolateam.fr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

import chocolateam.fr.Fragments.SettingsFragment;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;
    private SettingsFragment settingsFragment;

    public ContactsDatabaseHelper(Context context, SettingsFragment settingsFragment) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.settingsFragment = settingsFragment;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, phone_number TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS contacts;");
        onCreate(db);
    }

    public ArrayList<String> getContactsFromDatabase() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<String> contactsList = new ArrayList<>();
        Cursor cursor = db.query("contacts", null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("phone_number"));
            contactsList.add(phoneNumber);
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
        ArrayList<String> currentContacts = getContactsFromDatabase();
        if (currentContacts.contains(phoneNumber)) {
            Toast.makeText(context, R.string.contact_already_exists, Toast.LENGTH_SHORT).show();
            return;
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
}