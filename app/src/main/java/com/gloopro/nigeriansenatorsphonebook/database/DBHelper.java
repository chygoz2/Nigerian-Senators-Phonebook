package com.gloopro.nigeriansenatorsphonebook.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class DBHelper extends SQLiteOpenHelper {

    public static final String NAME = "KNOWYOURSENATOR";
    public static final int VERSION = 2;
//    public static final String STATES = "states";
    public static final String SENATORS = "senators";
    private Context context;

    public DBHelper (Context context) {
        super(context, NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    public void updateMyDatabase (SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
//            createStatesTable(db);
            createSenatorsTable(db);
        }
        populateSenatorsTable(db);
    }
//
//    private void createStatesTable(SQLiteDatabase db) {
//        String sql = "create table " + STATES + " (_id integer primary key autoincrement, " +
//                "name varchar(100) not null);";
//        db.execSQL(sql);
//    }

    private void createSenatorsTable(SQLiteDatabase db) {
        String sql = "create table " + SENATORS + " (_id integer primary key autoincrement, " +
                "name varchar(100) not null, " +
                "email varchar(100) default null, " +
                "phone_number varchar(30) default null, " +
                "state varchar(100) not null);";
        db.execSQL(sql);
    }

    private void populateSenatorsTable (SQLiteDatabase db) {
        String senatorsList = getSenatorsList();
        try {
            JSONArray senatorsListArray = new JSONArray(senatorsList);
            for (int i = 0; i < senatorsListArray.length(); i++) {
                JSONObject senatorObject = (JSONObject) senatorsListArray.get(i);
                insertSenator(db, senatorObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String getSenatorsList () {
        try {
            AssetManager assetManager = context.getAssets();
            InputStream input = assetManager.open("senators.json");
            byte [] buffer = new byte[input.available()];
            input.read(buffer);
            input.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void insertSenator (SQLiteDatabase db, JSONObject senatorObject) {
        try {
            if (doesSenatorAlreadyExist(db, senatorObject)) {
                return;
            }
            ContentValues cv = new ContentValues();
            cv.put("name", senatorObject.getString("name"));
            cv.put("phone_number", senatorObject.getString("phone_number"));
            cv.put("email", senatorObject.getString("email"));
            cv.put("state", senatorObject.getString("state"));
            db.insert(SENATORS, null, cv);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean doesSenatorAlreadyExist (SQLiteDatabase db, JSONObject senatorObject) {
        try {
            String name = senatorObject.getString("name");
            String email = senatorObject.getString("email");

            String sql = "select * from " + SENATORS + " where name = ? and email = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{name, email});
            return cursor.moveToFirst();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Cursor getAllSenators (SQLiteDatabase db) {
        String sql = "select * from " + SENATORS + " order by state asc";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor getAllStates (SQLiteDatabase db) {
        String sql = "select distinct(state) as state from " + SENATORS + " order by state asc";
        Cursor cursor = db.rawQuery(sql, null);
        return cursor;
    }

    public Cursor search (SQLiteDatabase db, String searchTerm) {
        String sql = "select * from " + SENATORS + " " +
                "where state like ? " +
                "or name like ? " +
                "or email like ? " +
                "or phone_number like ? " +
                "order by name asc";
        searchTerm = "%"+searchTerm.trim()+"%";
        Cursor cursor = db.rawQuery(sql, new String[]{ searchTerm, searchTerm, searchTerm, searchTerm});
        return cursor;
    }
}
