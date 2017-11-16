package com.ramson.mobilesmachines.easytodolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DB extends SQLiteOpenHelper {


    // db variables
    private static final String NAME = "to.do.list";
    private static final int VERSION = 1;


    // table variables
    private static final String TABLE = "tasks";
    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_DATE = "date";
    private static final String KEY_STATUS = "status"; // KEY_STATUS will 0 by default for new / incomplete task
    private static final String[] COLUMNS = {KEY_ID, KEY_TITLE, KEY_DESCRIPTION, KEY_DATE, KEY_STATUS};


    // will be called while creating DB object
    public DB(Context context) {
        super(context, NAME, null, VERSION);
    }

    // will be called on creating DB object
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "create table " + TABLE + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_TITLE + " TEXT, "
                + KEY_DESCRIPTION + " TEXT, "
                + KEY_DATE + " TEXT, "
                + KEY_STATUS + " INTEGER) ";
        db.execSQL(createTable);

    }


    // call if required to update, pass old and new Version no.
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        String dropTable = "drop table if exists " + TABLE;
        db.execSQL(dropTable);
        this.onCreate(db);
    }


    //    to be called when user tap + icon and save
    public void insertNewTask(String title, String desc, String date) {
        // 1. Initialize WritableDatabase object
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        // 2. Initialize ContentValues obj
        ContentValues contentValues = new ContentValues();
        // put values in ContentValues obj
        contentValues.put(KEY_TITLE, title);
        contentValues.put(KEY_DESCRIPTION, desc);
        contentValues.put(KEY_DATE, date);
        contentValues.put(KEY_STATUS, 0); // KEY_STATUS will 0 by default for new / incomplete task

        // 3. Insert the ContentValues to table
        sqLiteDatabase.insert(TABLE, null, contentValues);

        // 4. close db
        sqLiteDatabase.close();
    }


    //    get all InComplete tasks queries -> to be called to display in the list in Main Activity
    public ArrayList<Task> getAllInCompleteTasks() {

        String selectTasksWithStatusAs1 = "select * from " + TABLE + " where " + KEY_STATUS + " = 0";
        return getTasks(selectTasksWithStatusAs1);
    }

    //    get all Completed tasks queries -> to be called to display in the list in Main Activity
    public ArrayList<Task> getAllCompletedTasks() {

        String selectTasksWithStatusAs1 = "select * from " + TABLE + " where " + KEY_STATUS + " = 1";
        return getTasks(selectTasksWithStatusAs1);
    }

    //    get all tasks queries -> to be called to display in the list in Main Activity
    public ArrayList<Task> getAllTasks() {

        String selectAll = "select * from " + TABLE;
        return getTasks(selectAll);
    }

    private ArrayList<Task> getTasks(String query) {
        ArrayList<Task> tasks = new ArrayList<>();

        // 1. Initialize WritableDatabase object
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();

        // 2. Initialize Cursor
        Cursor cursor = sqLiteDatabase.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getInt(cursor.getColumnIndex(KEY_ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndex(KEY_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndex(KEY_DESCRIPTION)));
                task.setDueDate(cursor.getString(cursor.getColumnIndex(KEY_DATE)));
                task.setStatus(cursor.getInt(cursor.getColumnIndex(KEY_STATUS)));

                tasks.add(task);

            } while (cursor.moveToNext());
        }

        sqLiteDatabase.close();
        return tasks;
    }

    //    to be called when user select a task from list, changes and save
    public int updateTask(Task task) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, task.getTitle());
        values.put(KEY_DESCRIPTION, task.getDescription());
        values.put(KEY_DATE, task.getDueDate());
        values.put(KEY_STATUS, task.getStatus());

        int count = sqLiteDatabase.update(TABLE, values, KEY_ID + " = ? ", new String[]{String.valueOf(task.getId())});

        sqLiteDatabase.close();
        return count;
    }

    public int updateTaskStatus (int id, int status) {
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);

        int count = sqLiteDatabase.update(TABLE, values, KEY_ID+"=?", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();

        return count;
    }

    public boolean deleteTask(int id) {

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.delete(TABLE, KEY_ID + " = ? ", new String[]{String.valueOf(id)});
        sqLiteDatabase.close();
        return true;
    }
}