package cs65.edu.dartmouth.cs.gifto;

import android.annotation.TargetApi;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Oliver on 2/24/2018.
 *
 */

public class MySQLiteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "gifto.db";
    private static final int DATABASE_VERSION = 1;

    static final String FRIEND_TITLE = "friends";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FRIEND_NAME = "name";
    private static final String COLUMN_FRIEND_NICKNAME = "nickname";
    private static final String COLUMN_FIREBASE_ID = "firebaseId";

    static final String GIFT_TITLE = "gifts";
    private static final String COLUMN_GIFT = "gift";
    private static final String COLUMN_SENT = "sent";
    private static final String COLUMN_TOFROM = "toFrom";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_LOCATION = "location";

    static final String ANIMAL_TITLE = "animals";
    private static final String COLUMN_ANIMAL_NAME = "name";
    private static final String COLUMN_RARITY = "rarity";
    private static final String COLUMN_SEEN = "seen";
    private static final String COLUMN_VISITS = "visits";

    static final String INVENTORY_TITLE = "inventory";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_AMOUNT = "amount";


    private String[] friends_columns = { COLUMN_ID, COLUMN_FRIEND_NAME,
            COLUMN_FRIEND_NICKNAME, COLUMN_FIREBASE_ID };

    private String[] gifts_columns = { COLUMN_ID, COLUMN_GIFT, COLUMN_SENT, COLUMN_TOFROM,
            COLUMN_TIME, COLUMN_LOCATION, COLUMN_FIREBASE_ID };

    private String [] animals_columns = { COLUMN_ID, COLUMN_ANIMAL_NAME, COLUMN_RARITY, COLUMN_SEEN,
            COLUMN_VISITS, COLUMN_FIREBASE_ID };

    private String[] inventory_columns = { COLUMN_ID, COLUMN_TYPE, COLUMN_AMOUNT, COLUMN_FIREBASE_ID };

    private static final String CREATE_FRIENDS_TABLE = "create table " + FRIEND_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_FRIEND_NAME + " TEXT, " +
            COLUMN_FRIEND_NICKNAME + " TEXT, " +
            COLUMN_FIREBASE_ID + " STRING );";

    private static final String CREATE_GIFTS_TABLE = "create table " + GIFT_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_GIFT + " TEXT, " +
            COLUMN_SENT + " INTEGER NOT NULL, " +
            COLUMN_TOFROM + " TEXT, " +
            COLUMN_TIME + " DATETIME NOT NULL, " +
            COLUMN_LOCATION + " BLOB, " +
            COLUMN_FIREBASE_ID + " STRING );";

    private static final String CREATE_ANIMAL_TABLE = "create table " + ANIMAL_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_ANIMAL_NAME + " TEXT, " +
            COLUMN_RARITY + " INTEGER NOT NULL, " +
            COLUMN_SEEN + " INTEGER NOT NULL, " +
            COLUMN_VISITS + " INTEGER NOT NULL, " +
            COLUMN_FIREBASE_ID + " STRING );";

    private static final String CREATE_INVENTORY_TABLE = "create table " + INVENTORY_TITLE +
            "(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_TYPE + " TEXT, " +
            COLUMN_SENT + " INTEGER NOT NULL, " +
            COLUMN_FIREBASE_ID + " STRING );";


    // Constructor
    MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create table schema if not exists
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_FRIENDS_TABLE);
        db.execSQL(CREATE_GIFTS_TABLE);
        db.execSQL(CREATE_ANIMAL_TABLE);
        db.execSQL(CREATE_INVENTORY_TABLE);
    }

    // Insert a friend
    /*** username must be unique! ***/
    void insertFriend(Friend friend) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        DatabaseReference ref = Util.databaseReference.child("users").
                child(Util.userID).child("friends").child(friend.getName());
        String key = ref.getKey();
        ref.setValue(friend);

        // now insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_FRIEND_NAME, friend.getName());
        values.put(COLUMN_FRIEND_NICKNAME, friend.getNickname());
        values.put(COLUMN_FIREBASE_ID, key);

        database.insert(FRIEND_TITLE, null, values);
        database.close();
    }

    // Insert a gift
    void insertGift(Gift gift) throws IOException {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        DatabaseReference ref = Util.databaseReference.child("users").
                child(Util.userID).child("gifts").child(gift.getGiftName());
        String key = ref.getKey();
        ref.setValue(gift);

        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_GIFT, gift.getGiftName());
        values.put(COLUMN_SENT, gift.isSent());
        values.put(COLUMN_TOFROM, gift.getFriendName());
        values.put(COLUMN_TIME, gift.getTime());
        values.put(COLUMN_LOCATION, toByte(gift.getLocation()));        // converting to byte throws IOException
        values.put(COLUMN_FIREBASE_ID, key);

        database.insert(GIFT_TITLE, null, values);
        database.close();
    }

    // Insert an animal
    void insertAnimal(Animal animal) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        DatabaseReference ref = Util.databaseReference.child("users").
                child(Util.userID).child("animals").child(animal.getAnimalName());
        String key = ref.getKey();
        ref.setValue(animal);

        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ANIMAL_NAME, animal.getAnimalName());
        values.put(COLUMN_RARITY, animal.getRarity());
        values.put(COLUMN_SEEN, animal.isSeen());
        values.put(COLUMN_VISITS, animal.getNumVisits());
        values.put(COLUMN_FIREBASE_ID, key);

        database.insert(ANIMAL_TITLE, null, values);
        database.close();
    }

    // increment the number of times an animal has been seen
    void incrementVisits(final Animal animal) {
        final DatabaseReference ref = Util.databaseReference.child("users")
                .child(Util.userID).child("animals");

        // need to check if they have seen the animal before first
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // if animal exists, then increment the counter and add to both databases
                if (dataSnapshot.hasChild(animal.getAnimalName())) {
                    animal.setNumVisits(animal.getNumVisits() + 1);
                    insertAnimal(animal);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    // insert inventory item
    // also use this if you want to change the amount of an item
    void insertInventory(InventoryItem item) {
        // first try to insert into Firebase
        // if user is offline, Firebase will automatically cache the data and upload it once
        //   user is back online
        DatabaseReference ref = Util.databaseReference.child("users").
                child(Util.userID).child("items").child(item.getItemType());
        String key = ref.getKey();
        ref.setValue(item);

        // insert into SQL
        SQLiteDatabase database = getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TYPE, item.getItemType());
        values.put(COLUMN_AMOUNT, item.getItemAmount());
        values.put(COLUMN_FIREBASE_ID, key);

        database.insert(INVENTORY_TITLE, null, values);
        database.close();
    }

    // Remove an entry by giving its index
    // title is the title of the database you want to use
    void removeEntry(String title, long rowIndex) {
        SQLiteDatabase database = getWritableDatabase();
        String whereClause = "_id=?";
        String[] whereArgs = new String[] { String.valueOf(rowIndex) };
        database.delete(title, whereClause, whereArgs);
        database.close();
    }

    ArrayList<Animal> fetchAllAnimals() {
        ArrayList<Animal> animals = new ArrayList<>(0);
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.query(ANIMAL_TITLE, animals_columns,
                null, null, null, null, null);

        cursor.moveToFirst(); //Move the cursor to the first row.
        while (!cursor.isAfterLast()) {
            Animal animal = cursorToAnimal(cursor);
            animals.add(animal);
            cursor.moveToNext();
        }

        // Make sure to close the cursor
        cursor.close();
        database.close();

        return animals;
    }

    //TODO return all rows in the tables

    // delete every row in every table (probably so you can remake a new one)
    void deleteAll() {
        SQLiteDatabase database = getWritableDatabase();
        database.delete(MySQLiteHelper.GIFT_TITLE, null, null);
        database.delete(MySQLiteHelper.FRIEND_TITLE, null, null);
        database.delete(MySQLiteHelper.ANIMAL_TITLE, null, null);
        database.delete(MySQLiteHelper.INVENTORY_TITLE, null, null);
    }

    private Animal cursorToAnimal(Cursor cursor) {
        Animal animal = new Animal();
        animal.setAnimalName(cursor.getString(1));
        animal.setRarity(cursor.getInt(2));
        if (cursor.getInt(3) == 0) {
            animal.setSeen(false);
        } else {
            animal.setSeen(true);
        }
        animal.setNumVisits(cursor.getInt(4));

        return animal;
    }
    //TODO cursorToGift

    // convert a LatLng to a byte array to be stored as a BLOB
    @TargetApi(19)
    private byte[] toByte(LatLng latLng) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            try (DataOutputStream out = new DataOutputStream(baos)) {
                out.writeDouble(latLng.latitude);
                out.writeDouble(latLng.longitude);
            }
            return baos.toByteArray();
        }
    }

    // convert a BLOB back to a LatLng
    @TargetApi(19)
    private static LatLng toList(byte[] bytes) throws IOException, ClassNotFoundException {
        ArrayList<Double> list = new ArrayList<>(0);
        try(ByteArrayInputStream bais = new ByteArrayInputStream(bytes)){
            try(DataInputStream in = new DataInputStream(bais)){
                while (in.available() > 0) {
                    list.add(in.readDouble());
                }
            }
        }

        return new LatLng(list.get(0), list.get(1));
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + FRIEND_TITLE);
        db.execSQL("DROP TABLE IF EXISTS " + GIFT_TITLE);
        db.execSQL("DROP TABLE IF EXISTS " + ANIMAL_TITLE);
        db.execSQL("DROP TABLE IF EXISTS " + INVENTORY_TITLE);
        onCreate(db);
    }
}
