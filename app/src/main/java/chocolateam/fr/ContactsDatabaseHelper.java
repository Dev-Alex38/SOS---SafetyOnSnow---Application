package chocolateam.fr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactsDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "contacts.db";
    private static final int DATABASE_VERSION = 1;
    private SettingsFragment settingsFragment;

    public ContactsDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Créez la table pour stocker les contacts
        db.execSQL("CREATE TABLE contacts (_id INTEGER PRIMARY KEY AUTOINCREMENT, phone_number TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Mettez à jour la base de données si nécessaire (pour les futures versions de l'application)
        // Par exemple, vous pouvez supprimer la table existante et la recréer
        db.execSQL("DROP TABLE IF EXISTS contacts;");
        onCreate(db);
    }

    public ArrayList<String> getContactsFromDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
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
        settingsFragment.updateContactsList(); // Mettre à jour la liste des contacts
        Toast.makeText(context, R.string.delete_one_contact, Toast.LENGTH_SHORT).show();
    }

    public void addContactToDatabase(String phoneNumber, Context context) {
        // Récupérer la liste des contacts depuis la base de données
        ArrayList<String> currentContacts = getContactsFromDatabase();
        // Vérifier d'abord si le contact existe déjà dans la base de données
        if (currentContacts.contains(phoneNumber)) {
            Toast.makeText(context, R.string.contact_already_exists, Toast.LENGTH_SHORT).show();
            return; // Sortir de la méthode si le contact existe déjà
        }

        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("phone_number", phoneNumber);
        long newRowId = db.insert("contacts", null, values);
        if (newRowId != -1) {
            Toast.makeText(context, R.string.add_contact, Toast.LENGTH_SHORT).show();
            settingsFragment.updateContactsList(); // Mettre à jour la liste des contacts
        } else {
            Toast.makeText(context, R.string.error_add_contact, Toast.LENGTH_SHORT).show();
        }
    }
}