package com.example.hike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class HikeDao {
    private final DbHelper dbHelper;

    public HikeDao(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(Hike hike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", hike.name);
        cv.put("location", hike.location);
        cv.put("date", hike.date);
        cv.put("parking", hike.parking);
        cv.put("difficulty", hike.difficulty);
        cv.put("description", hike.description);
        cv.put("imageUri", hike.imageUri);
        long id = db.insert(DbHelper.TABLE_HIKES, null, cv);
        db.close();
        return id;
    }

    public int update(Hike hike) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("name", hike.name);
        cv.put("location", hike.location);
        cv.put("date", hike.date);
        cv.put("parking", hike.parking);
        cv.put("difficulty", hike.difficulty);
        cv.put("description", hike.description);
        cv.put("imageUri", hike.imageUri);
        int rows = db.update(DbHelper.TABLE_HIKES, cv, "id=?", new String[]{String.valueOf(hike.id)});
        db.close();
        return rows;
    }

    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DbHelper.TABLE_HIKES, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public int deleteAll() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DbHelper.TABLE_HIKES, null, null);
        db.close();
        return rows;
    }

    public Hike getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbHelper.TABLE_HIKES, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        Hike h = null;
        if (c != null) {
            if (c.moveToFirst()) {
                h = new Hike(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("location")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        c.getString(c.getColumnIndexOrThrow("parking")),
                        c.getString(c.getColumnIndexOrThrow("difficulty")),
                        c.getString(c.getColumnIndexOrThrow("description"))
                );
                h.imageUri = c.getString(c.getColumnIndexOrThrow("imageUri"));
            }
            c.close();
        }
        db.close();
        return h;
    }

    public List<Hike> getAll() {
        List<Hike> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbHelper.TABLE_HIKES, null, null, null, null, null, "name ASC");
        if (c != null) {
            while (c.moveToNext()) {
                Hike h = new Hike(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("location")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        c.getString(c.getColumnIndexOrThrow("parking")),
                        c.getString(c.getColumnIndexOrThrow("difficulty")),
                        c.getString(c.getColumnIndexOrThrow("description"))
                );
                h.imageUri = c.getString(c.getColumnIndexOrThrow("imageUri"));
                list.add(h);
            }
            c.close();
        }
        db.close();
        return list;
    }

    public List<Hike> search(String q) {
        List<Hike> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "name LIKE ? OR location LIKE ? OR description LIKE ?";
        String pattern = "%" + q + "%";
        Cursor c = db.query(DbHelper.TABLE_HIKES, null, where, new String[]{pattern, pattern, pattern}, null, null, "name ASC");
        if (c != null) {
            while (c.moveToNext()) {
                Hike h = new Hike(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getString(c.getColumnIndexOrThrow("name")),
                        c.getString(c.getColumnIndexOrThrow("location")),
                        c.getString(c.getColumnIndexOrThrow("date")),
                        c.getString(c.getColumnIndexOrThrow("parking")),
                        c.getString(c.getColumnIndexOrThrow("difficulty")),
                        c.getString(c.getColumnIndexOrThrow("description"))
                );
                h.imageUri = c.getString(c.getColumnIndexOrThrow("imageUri"));
                list.add(h);
            }
            c.close();
        }
        db.close();
        return list;
    }
}
