package htp.mapsplantravelapplication.datasource;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.PointF;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import htp.mapsplantravelapplication.lib.CenterPoint;
import htp.mapsplantravelapplication.model.ObjectPlan;

/**
 * Created by phuchtgc60244 on 5/3/2016.
 */
public class DatabaseHander extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    protected static final String DATABASE_NAME = "TravelDatabase.db";
    protected static final String DATABASE_TABLE_LIST = "listsTable";
    protected static final String LIST_ID = "id";
    protected static final String LIST_TITLE = "title";
    protected static final String LIST_CONTENT = "content";
    protected static final String LIST_PLACE = "place";
    protected static final String LIST_LNG = "lng";
    protected static final String LIST_LAT = "lat";
    protected static final String LIST_DATE = "date";
    protected static final String LIST_DELETE = "isDelete";

    SQLiteDatabase db;

    public DatabaseHander(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String queryAddList = "CREATE TABLE " + DATABASE_TABLE_LIST + "("
                + LIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + LIST_TITLE + " TEXT,"
                + LIST_CONTENT + " TEXT,"
                + LIST_PLACE + " TEXT,"
                + LIST_LAT + " REAL,"
                + LIST_LNG + " REAL,"
                + LIST_DATE + " INTEGER,"
                + LIST_DELETE + " INTEGER DEFAULT 0" + ");";
        db.execSQL(queryAddList);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sqlList = "DROP TABLE IF EXISTS" + DATABASE_TABLE_LIST;

        db.execSQL(sqlList);
        onCreate(db);
    }

    public boolean addNewList(ObjectPlan objectPlan) {
        db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LIST_TITLE, objectPlan.getTitle());
        values.put(LIST_CONTENT, objectPlan.getContent());
        values.put(LIST_PLACE, objectPlan.getPlace());
        values.put(LIST_LAT, objectPlan.getLat());
        values.put(LIST_LNG, objectPlan.getLng());
        long time = objectPlan.getDate().getTime();
        int dateInt = (int) (time / 1000);
        values.put(LIST_DATE, dateInt);
        long result = db.insert(DATABASE_TABLE_LIST, null, values);
        return result != -1;
    }

    public ArrayList<ObjectPlan> getAllPlan(double lat, double lng) {
        ArrayList<ObjectPlan> objectPlanList = new ArrayList<ObjectPlan>();
        String query = "SELECT * FROM " + DATABASE_TABLE_LIST;

        CenterPoint centerPoint = new CenterPoint();
        PointF center = new PointF((float) lat, (float) lng);
        double mult = 1; // mult = 1.1; is more reliable
        double radius = 1000;
        PointF p1 = centerPoint.calculateDerivedPosition(center, mult * radius, 0);
        PointF p2 = centerPoint.calculateDerivedPosition(center, mult * radius, 90);
        PointF p3 = centerPoint.calculateDerivedPosition(center, mult * radius, 180);
        PointF p4 = centerPoint.calculateDerivedPosition(center, mult * radius, 270);

        query += " WHERE "
                + LIST_LAT + " > " + String.valueOf(p3.x) + " AND "
                + LIST_LAT + " < " + String.valueOf(p1.x) + " AND "
                + LIST_LNG + " < " + String.valueOf(p2.y) + " AND "
                + LIST_LNG + " > " + String.valueOf(p4.y);


        db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                ObjectPlan objectPlan = new ObjectPlan();
                objectPlan.setId(Integer.parseInt(cursor.getString(0)));
                objectPlan.setTitle(cursor.getString(1));
                objectPlan.setContent(cursor.getString(2));
                objectPlan.setPlace(cursor.getString(3));
                objectPlan.setLat(Double.parseDouble(cursor.getString(4)));
                objectPlan.setLng(Double.parseDouble(cursor.getString(5)));
                int date = cursor.getInt(6);
                Date databaseDate = new Date(((long) date) * 1000L);
                objectPlan.setDate(new Timestamp(databaseDate.getTime()));
                objectPlan.setIs_delete(Integer.parseInt(cursor.getString(7)));

                // Adding contact to list
                objectPlanList.add(objectPlan);
            } while (cursor.moveToNext());
        }
        return objectPlanList;
    }


}


