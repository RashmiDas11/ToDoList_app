package com.example.todolist;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "toDoList.db";

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "ToDo_Table";
    public static final String COL1 = "ID";
    private static final String COL2 = "Name";
    private static final String COL3 = "Date";
    private static final String COL4 = "Time";
    public static  final String COL5 = "Archieved";
    private static final String COL6 = "ImageUri";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "("
                + COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT , "
                + COL2 + " TEXT , "
                + COL3 + " TEXT , "
                + COL4 + " TEXT , "
                + COL5 + " INTEGER DEFAULT 0 ,"
                + COL6 + " TEXT  )";

        Log.d(TAG, "Creating table " + createTable);
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//
    }

    //Insert data into database
    public boolean insertData(String item, String date, String time, String image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, item);
        contentValues.put(COL3, date);
        contentValues.put(COL4, time);
        contentValues.put(COL6, image);
        Log.d(TAG, "insertData: Inserting " + item + " to " + TABLE_NAME);
        long result = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return result != -1;
    }


    public void deleteData(String name, String date, String time,String imagePath) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            String query = "DELETE FROM " + TABLE_NAME + " WHERE " +
                    COL2 + "=? AND " + COL3 + "=? AND " + COL4 + "=? AND " + COL6 + "=?";
            db.execSQL(query, new String[]{name, date, time,imagePath});
            Log.d(TAG, "deleteData: Deleted " + name + " from database");
        } catch (SQLiteException e) {
            Log.e(TAG, "Error deleting data from database", e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }



    // Update task in the database
    public boolean updateTask(String Id ,String newTitle, String newDate, String newTime,String imagePath) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL1, newId);
        contentValues.put(COL2, newTitle);
        contentValues.put(COL3, newDate);
        contentValues.put(COL4, newTime);
        contentValues.put(COL6,imagePath);
        int rowsAffected = db.update(TABLE_NAME, contentValues, COL1 + "=?", new String[]{(Id)});
        db.close();
        return rowsAffected > 0;
    }





    public ArrayList<ItemInfo> getAllData() {
        ArrayList<ItemInfo> arrayList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COL5 + " = 0";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range") int id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(COL1)));
            @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COL2));
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COL3));
            @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(COL4));
            @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(COL6));

            ItemInfo itemInfo = new ItemInfo(id,title,date,time,path);
            arrayList.add(itemInfo);
        }
        db.close();
        cursor.close();
        return arrayList ;
    }
    public boolean setArchiveOrUnArchieveInDb(ItemInfo itemInfo, int val) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL5, val); // Set Archived column to 1 (archived)
        int rowsAffected = db.update(TABLE_NAME, contentValues,
                COL1 + " = ?", new String[]{String.valueOf(itemInfo.id)});
        db.close();
        return rowsAffected > 0;
    }

    // Retrieve archived items from database
    public ArrayList<ItemInfo> getArchivedItems() {
        ArrayList<ItemInfo> archivedItems = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, COL5 + "=?", new String[]{"1"}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(COL1));
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex(COL2));
                @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex(COL3));
                @SuppressLint("Range") String time = cursor.getString(cursor.getColumnIndex(COL4));
                @SuppressLint("Range") String imagePath = cursor.getString(cursor.getColumnIndex(COL6));

                archivedItems.add(new ItemInfo(id, name, date, time,imagePath));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return archivedItems;
    }




}
