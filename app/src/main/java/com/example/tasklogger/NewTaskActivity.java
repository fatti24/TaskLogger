package com.example.tasklogger;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Locale;

public class NewTaskActivity extends AppCompatActivity {

    // Declare variables
    private EditText editTextTitle;
    private EditText editTextDeadline;
    private EditText editTextNotes;
    private CheckBox checkboxStorageMethod;
    private Button btnSubmitTask;
    private Button btnCancel;

    private TaskDatabaseHelper dbHelper;   // SQLite helper

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // Initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDeadline = findViewById(R.id.editTextDeadline);
        editTextNotes = findViewById(R.id.editTextNotes);
        checkboxStorageMethod = findViewById(R.id.checkboxStorageMethod);
        btnSubmitTask = findViewById(R.id.btnSubmitTask);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new TaskDatabaseHelper(this);

        editTextDeadline.setOnClickListener(v -> showDatePickerDialog());
        btnSubmitTask.setOnClickListener(v -> handleFormSubmission());
        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    String date = String.format(Locale.US, "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    editTextDeadline.setText(date);
                },
                year, month, day);

        datePickerDialog.show();
    }

    private void handleFormSubmission() {
        String title = editTextTitle.getText().toString().trim();
        String deadline = editTextDeadline.getText().toString().trim();

        if (title.isEmpty()) {
            editTextTitle.setError("Task Title is required!");
            Toast.makeText(this, "Please enter a Task Title.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (deadline.isEmpty()) {
            editTextDeadline.setError("Deadline is required!");
            Toast.makeText(this, "Please select a Deadline.", Toast.LENGTH_SHORT).show();
            return;
        }

        String notes = editTextNotes.getText().toString().trim();
        boolean useSQLite = checkboxStorageMethod.isChecked();

        if (useSQLite) {
            // ✅ Save to SQLite
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("deadline", deadline);
            values.put("notes", notes);

            long result = db.insert("tasks", null, values);
            if (result != -1) {
                Toast.makeText(this, "Task saved in SQLite ✅", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save task to SQLite ❌", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // ✅ Save to SharedPreferences (simple example)
            SharedPreferences prefs = getSharedPreferences("TaskPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("title", title);
            editor.putString("deadline", deadline);
            editor.putString("notes", notes);
            editor.apply();

            Toast.makeText(this, "Task saved in SharedPreferences ✅", Toast.LENGTH_SHORT).show();
        }

        clearFormFields();
        finish();
    }

    private void clearFormFields() {
        editTextTitle.setText("");
        editTextDeadline.setText("");
        editTextNotes.setText("");
    }
}
