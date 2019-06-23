package com.example.neighbourapplication.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.example.neighbourapplication.model.Incident;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;


/**
 * Created by user on 3/20/2019.
 */

public class IncidentDB extends SQLiteOpenHelper {
    public static final String dbName = "dbIncident";
    public static final String tblNameIncident = "incident";
    public static final String tblNameIncidentToUpload = "incident";
    public static final String colIncidentName = "incidentName";
    public static final String colIncidentPhoto = "photo";
    public static final String colIncidentDescription = "description";
    public static final String colIncidentDate = "date";
    public static final String colIncidentTime = "time";
    public static final String colIncidentLong = "longitude";
    public static final String colIncidentLat = "latitude";
    public static final String colIncidentId = "incidentId";

    public static final String strCrtTblIncidentToUpload = "CREATE TABLE IF NOT EXISTS " + tblNameIncident +
            " (" + colIncidentId + " INTEGER PRIMARY KEY, " + colIncidentPhoto + " TEXT, " + colIncidentName + " TEXT, " + colIncidentDate + " TEXT, " +
            colIncidentLong + " TEXT, " + colIncidentLat + " TEXT, " + colIncidentDescription + " TEXT, " + colIncidentTime + " REAL)";
    public static final String strDropTblIncidentToUpload = "DROP TABLE IF EXISTS " + tblNameIncidentToUpload;

    public IncidentDB(Context context) {
        super(context, dbName, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(strCrtTblIncidentToUpload);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(strDropTblIncidentToUpload);
        onCreate(sqLiteDatabase);
    }

    public float fnInsertIncident(Incident incident, Uri uri, String tableName) {
        float retResult = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (tableName == tblNameIncidentToUpload) {
            if (uri != null)
                values.put(colIncidentPhoto, uri.toString());
        } else
            values.put(colIncidentPhoto, incident.getPhoto());
        values.put(colIncidentName, incident.getIncidentName());
        values.put(colIncidentDate, incident.getDate());
        values.put(colIncidentDescription, incident.getDescription());
        values.put(colIncidentTime, incident.getTime());
        values.put(colIncidentLong, incident.getLocation().getLongitude());
        values.put(colIncidentLat, incident.getLocation().getLatitude());

        retResult = db.insert(tableName, null, values);
        return retResult;
    }

    public float fnDeleteIncident(String incidentId, String tableName) {
        float retResult;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        System.out.println(incidentId);
        values.put(colIncidentId, incidentId);
        retResult = db.delete(tableName, colIncidentId + " = ?", new String[]{incidentId});
        return retResult;
    }

    public float fnEditIncident(Incident incident, String tableName) {
        float retResult;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(colIncidentName, incident.getIncidentName());
        values.put(colIncidentDate, incident.getDate());
        values.put(colIncidentPhoto, incident.getPhoto());
        values.put(colIncidentDescription, incident.getDescription());
        values.put(colIncidentTime, incident.getTime());
        values.put(colIncidentLong, incident.getLocation().getLongitude());
        values.put(colIncidentLat, incident.getLocation().getLatitude());

        retResult = db.update(tableName, values, colIncidentId + " = ?", new String[]{incident.getIncidentId()});
        return retResult;
    }

    public void fnGetIncidents(String tableName, HashMap<String, Incident> list) {
        String strSelAll = "Select * from " + tableName;

        Cursor cursor = this.getReadableDatabase().rawQuery(strSelAll, null);
        if (cursor.moveToFirst()) {
            do {
                //System.out.println(cursor.getDouble(cursor.getColumnIndex(colSubjHour))+ " "+ cursor.getDouble(cursor.getColumnIndex(colSubjHour)));
                Incident model = new Incident();
                model.setDate(cursor.getString(cursor.getColumnIndex(colIncidentDate)));
                model.setDescription(cursor.getString(cursor.getColumnIndex(colIncidentDescription)));
                model.setIncidentId(cursor.getString(cursor.getColumnIndex(colIncidentId)));
                model.setIncidentName(cursor.getString(cursor.getColumnIndex(colIncidentName)));
                model.setTime(cursor.getString(cursor.getColumnIndex(colIncidentTime)));
                model.setLocation(new GeoPoint(cursor.getDouble(cursor.getColumnIndex(colIncidentLat)),
                        cursor.getDouble(cursor.getColumnIndex(colIncidentLong))));
                model.setPhoto(cursor.getString(cursor.getColumnIndex(colIncidentPhoto)));
                list.put(model.getIncidentId(), model);
            } while (cursor.moveToNext());
        }
    }
}