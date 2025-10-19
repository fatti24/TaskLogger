package com.example.tasklogger;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class TaskDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "tasklogger.db";
    private static final int DATABASE_VERSION = 1;

    public TaskDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE tasks (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "deadline TEXT," +
                "notes TEXT" +
                ")";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tasks");
        onCreate(db);
    }

    // Insert a task
    public long insertTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO tasks (title, deadline, notes) VALUES (?, ?, ?)",
                new Object[]{task.getTitle(), task.getDeadline(), task.getNotes()});
        return 0;
    }

    // **Get all tasks**
    public List<Task> getAllTasks() {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query("tasks", null, null, null, null, null, "id DESC");

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"));

            Task task = new Task(id, title, deadline, notes);
            tasks.add(task);
        }
        cursor.close();
        return tasks;
    }


    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("tasks", "id = ?", new String[]{String.valueOf(taskId)});
    }
}