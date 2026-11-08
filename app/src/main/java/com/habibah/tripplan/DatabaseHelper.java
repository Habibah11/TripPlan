package com.habibah.tripplan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/** SQLiteOpenHelper: tabel trips, destinations, itineraries, expenses + operasi CRUD. */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "tripplan.db";
    public static final int DB_VERSION = 1;

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE trips (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "name TEXT NOT NULL, destination TEXT NOT NULL, "
                + "start_date TEXT NOT NULL, end_date TEXT NOT NULL, "
                + "budget REAL DEFAULT 0, notes TEXT, status INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE destinations (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "trip_id INTEGER NOT NULL, name TEXT NOT NULL, location TEXT, "
                + "category TEXT, notes TEXT, visited INTEGER DEFAULT 0, "
                + "FOREIGN KEY(trip_id) REFERENCES trips(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE itineraries (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "trip_id INTEGER NOT NULL, day INTEGER NOT NULL, time TEXT NOT NULL, "
                + "activity TEXT NOT NULL, place TEXT, "
                + "FOREIGN KEY(trip_id) REFERENCES trips(id) ON DELETE CASCADE)");
        db.execSQL("CREATE TABLE expenses (id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "trip_id INTEGER NOT NULL, title TEXT NOT NULL, category TEXT, "
                + "amount REAL NOT NULL, date TEXT, "
                + "FOREIGN KEY(trip_id) REFERENCES trips(id) ON DELETE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS expenses");
        db.execSQL("DROP TABLE IF EXISTS itineraries");
        db.execSQL("DROP TABLE IF EXISTS destinations");
        db.execSQL("DROP TABLE IF EXISTS trips");
        onCreate(db);
    }

    // ------------------------------------------------------------ helpers
    private static String[] args(long id) {
        return new String[]{String.valueOf(id)};
    }

    private static String str(Cursor c, String col) {
        String v = c.getString(c.getColumnIndexOrThrow(col));
        return v == null ? "" : v;
    }

    private static long lng(Cursor c, String col) {
        return c.getLong(c.getColumnIndexOrThrow(col));
    }

    private static int num(Cursor c, String col) {
        return c.getInt(c.getColumnIndexOrThrow(col));
    }

    private static double dbl(Cursor c, String col) {
        return c.getDouble(c.getColumnIndexOrThrow(col));
    }

    public double scalar(String sql, String[] a) {
        Cursor c = getReadableDatabase().rawQuery(sql, a);
        try {
            if (c.moveToFirst() && !c.isNull(0)) return c.getDouble(0);
        } finally {
            c.close();
        }
        return 0;
    }

    // ------------------------------------------------------------ TRIPS
    private ContentValues tripValues(Models.Trip t) {
        ContentValues v = new ContentValues();
        v.put("name", t.name);
        v.put("destination", t.destination);
        v.put("start_date", t.startDate);
        v.put("end_date", t.endDate);
        v.put("budget", t.budget);
        v.put("notes", t.notes);
        v.put("status", t.status);
        return v;
    }

    private Models.Trip readTrip(Cursor c) {
        Models.Trip t = new Models.Trip();
        t.id = lng(c, "id");
        t.name = str(c, "name");
        t.destination = str(c, "destination");
        t.startDate = str(c, "start_date");
        t.endDate = str(c, "end_date");
        t.budget = dbl(c, "budget");
        t.notes = str(c, "notes");
        t.status = num(c, "status");
        return t;
    }

    public long insertTrip(Models.Trip t) {
        return getWritableDatabase().insert("trips", null, tripValues(t));
    }

    public int updateTrip(Models.Trip t) {
        return getWritableDatabase().update("trips", tripValues(t), "id=?", args(t.id));
    }

    public int updateTripStatus(long id, int status) {
        ContentValues v = new ContentValues();
        v.put("status", status);
        return getWritableDatabase().update("trips", v, "id=?", args(id));
    }

    public int deleteTrip(long id) {
        return getWritableDatabase().delete("trips", "id=?", args(id));
    }

    public Models.Trip getTrip(long id) {
        Cursor c = getReadableDatabase().query("trips", null, "id=?", args(id), null, null, null);
        try {
            if (c.moveToFirst()) return readTrip(c);
        } finally {
            c.close();
        }
        return null;
    }

    /** history=false: perjalanan aktif/akan datang. history=true: selesai atau tanggal sudah lewat. */
    public List<Models.Trip> getTrips(boolean history, String today) {
        String where = history ? "status=1 OR end_date<?" : "status=0 AND end_date>=?";
        String order = history ? "end_date DESC" : "start_date ASC";
        List<Models.Trip> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("trips", null, where, new String[]{today}, null, null, order);
        try {
            while (c.moveToNext()) list.add(readTrip(c));
        } finally {
            c.close();
        }
        return list;
    }

    public int countActiveTrips(String today) {
        return (int) scalar("SELECT COUNT(*) FROM trips WHERE status=0 AND end_date>=?", new String[]{today});
    }

    public int countHistoryTrips(String today) {
        return (int) scalar("SELECT COUNT(*) FROM trips WHERE status=1 OR end_date<?", new String[]{today});
    }

    public int countAllTrips() {
        return (int) scalar("SELECT COUNT(*) FROM trips", null);
    }

    public double totalBudget() {
        return scalar("SELECT SUM(budget) FROM trips", null);
    }

    // ------------------------------------------------------------ DESTINATIONS
    private ContentValues destValues(Models.Destination d) {
        ContentValues v = new ContentValues();
        v.put("trip_id", d.tripId);
        v.put("name", d.name);
        v.put("location", d.location);
        v.put("category", d.category);
        v.put("notes", d.notes);
        v.put("visited", d.visited ? 1 : 0);
        return v;
    }

    public long insertDestination(Models.Destination d) {
        return getWritableDatabase().insert("destinations", null, destValues(d));
    }

    public int updateDestination(Models.Destination d) {
        return getWritableDatabase().update("destinations", destValues(d), "id=?", args(d.id));
    }

    public int deleteDestination(long id) {
        return getWritableDatabase().delete("destinations", "id=?", args(id));
    }

    public List<Models.Destination> getDestinations(long tripId) {
        List<Models.Destination> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("destinations", null, "trip_id=?", args(tripId),
                null, null, "visited ASC, name ASC");
        try {
            while (c.moveToNext()) {
                Models.Destination d = new Models.Destination();
                d.id = lng(c, "id");
                d.tripId = lng(c, "trip_id");
                d.name = str(c, "name");
                d.location = str(c, "location");
                d.category = str(c, "category");
                d.notes = str(c, "notes");
                d.visited = num(c, "visited") == 1;
                list.add(d);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public int countDestinations() {
        return (int) scalar("SELECT COUNT(*) FROM destinations", null);
    }

    // ------------------------------------------------------------ ITINERARIES
    private ContentValues itinValues(Models.Itinerary i) {
        ContentValues v = new ContentValues();
        v.put("trip_id", i.tripId);
        v.put("day", i.day);
        v.put("time", i.time);
        v.put("activity", i.activity);
        v.put("place", i.place);
        return v;
    }

    public long insertItinerary(Models.Itinerary i) {
        return getWritableDatabase().insert("itineraries", null, itinValues(i));
    }

    public int updateItinerary(Models.Itinerary i) {
        return getWritableDatabase().update("itineraries", itinValues(i), "id=?", args(i.id));
    }

    public int deleteItinerary(long id) {
        return getWritableDatabase().delete("itineraries", "id=?", args(id));
    }

    public List<Models.Itinerary> getItineraries(long tripId) {
        List<Models.Itinerary> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("itineraries", null, "trip_id=?", args(tripId),
                null, null, "day ASC, time ASC");
        try {
            while (c.moveToNext()) {
                Models.Itinerary i = new Models.Itinerary();
                i.id = lng(c, "id");
                i.tripId = lng(c, "trip_id");
                i.day = num(c, "day");
                i.time = str(c, "time");
                i.activity = str(c, "activity");
                i.place = str(c, "place");
                list.add(i);
            }
        } finally {
            c.close();
        }
        return list;
    }

    // ------------------------------------------------------------ EXPENSES
    private ContentValues expValues(Models.Expense e) {
        ContentValues v = new ContentValues();
        v.put("trip_id", e.tripId);
        v.put("title", e.title);
        v.put("category", e.category);
        v.put("amount", e.amount);
        v.put("date", e.date);
        return v;
    }

    public long insertExpense(Models.Expense e) {
        return getWritableDatabase().insert("expenses", null, expValues(e));
    }

    public int updateExpense(Models.Expense e) {
        return getWritableDatabase().update("expenses", expValues(e), "id=?", args(e.id));
    }

    public int deleteExpense(long id) {
        return getWritableDatabase().delete("expenses", "id=?", args(id));
    }

    public List<Models.Expense> getExpenses(long tripId) {
        List<Models.Expense> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query("expenses", null, "trip_id=?", args(tripId),
                null, null, "date DESC, id DESC");
        try {
            while (c.moveToNext()) {
                Models.Expense e = new Models.Expense();
                e.id = lng(c, "id");
                e.tripId = lng(c, "trip_id");
                e.title = str(c, "title");
                e.category = str(c, "category");
                e.amount = dbl(c, "amount");
                e.date = str(c, "date");
                list.add(e);
            }
        } finally {
            c.close();
        }
        return list;
    }

    public double expensesForTrip(long tripId) {
        return scalar("SELECT SUM(amount) FROM expenses WHERE trip_id=?", args(tripId));
    }

    public double totalExpenses() {
        return scalar("SELECT SUM(amount) FROM expenses", null);
    }

    public double expensesForHistory(String today) {
        return scalar("SELECT SUM(amount) FROM expenses WHERE trip_id IN "
                + "(SELECT id FROM trips WHERE status=1 OR end_date<?)", new String[]{today});
    }

    // ------------------------------------------------------------ RESET
    public void clearAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.delete("expenses", null, null);
        db.delete("itineraries", null, null);
        db.delete("destinations", null, null);
        db.delete("trips", null, null);
    }
}
