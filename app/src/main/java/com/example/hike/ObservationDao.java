package com.example.hike;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class ObservationDao {
    private final DbHelper dbHelper;

    public ObservationDao(Context context) {
        dbHelper = new DbHelper(context);
    }

    public long insert(HikeObservation obs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("hike_id", obs.hikeId);
        cv.put("description", obs.description);
        cv.put("time", obs.time);
        long id = db.insert(DbHelper.TABLE_OBS, null, cv);
        db.close();
        return id;
    }

    public int update(HikeObservation obs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("hike_id", obs.hikeId);
        cv.put("description", obs.description);
        cv.put("time", obs.time);
        int rows = db.update(DbHelper.TABLE_OBS, cv, "id=?", new String[]{String.valueOf(obs.id)});
        db.close();
        return rows;
    }

    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rows = db.delete(DbHelper.TABLE_OBS, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows;
    }

    public HikeObservation getById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbHelper.TABLE_OBS, null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        HikeObservation o = null;
        if (c != null) {
            if (c.moveToFirst()) {
                o = new HikeObservation(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getLong(c.getColumnIndexOrThrow("hike_id")),
                        c.getString(c.getColumnIndexOrThrow("description")),
                        c.getString(c.getColumnIndexOrThrow("time"))
                );
            }
            c.close();
        }
        db.close();
        return o;
    }

    public List<HikeObservation> getByHikeId(long hikeId) {
        List<HikeObservation> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c = db.query(DbHelper.TABLE_OBS, null, "hike_id=?", new String[]{String.valueOf(hikeId)}, null, null, "time DESC");
        if (c != null) {
            while (c.moveToNext()) {
                HikeObservation o = new HikeObservation(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getLong(c.getColumnIndexOrThrow("hike_id")),
                        c.getString(c.getColumnIndexOrThrow("description")),
                        c.getString(c.getColumnIndexOrThrow("time"))
                );
                list.add(o);
            }
            c.close();
        }
        db.close();
        return list;
    }

    public List<HikeObservation> search(String q) {
        List<HikeObservation> list = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String where = "description LIKE ?";
        String pattern = "%" + q + "%";
        Cursor c = db.query(DbHelper.TABLE_OBS, null, where, new String[]{pattern}, null, null, "time DESC");
        if (c != null) {
            while (c.moveToNext()) {
                HikeObservation o = new HikeObservation(
                        c.getLong(c.getColumnIndexOrThrow("id")),
                        c.getLong(c.getColumnIndexOrThrow("hike_id")),
                        c.getString(c.getColumnIndexOrThrow("description")),
                        c.getString(c.getColumnIndexOrThrow("time"))
                );
                list.add(o);
            }
            c.close();
        }
        db.close();
        return list;
    }
}
