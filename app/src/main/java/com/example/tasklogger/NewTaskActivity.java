package com.example.tasklogger;

import android.os.Bundle;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import com.example.tasklogger.Task;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class NewTaskActivity extends AppCompatActivity {

    //declare variables to have references
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
        setContentView(R.layout.activity_new_task);     //links java to XML

        //initialize views
        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDeadline = findViewById(R.id.editTextDeadline);
        editTextNotes = findViewById(R.id.editTextNotes);
        checkboxStorageMethod = findViewById(R.id.checkboxStorageMethod);
        btnSubmitTask = findViewById(R.id.btnSubmitTask);
        btnCancel = findViewById(R.id.btnCancel);

        dbHelper = new TaskDatabaseHelper(this);

        editTextDeadline.setOnClickListener(v -> showDatePickerDialog());   //set up DatePicker dialog on deadline

        btnSubmitTask.setOnClickListener(v -> handleFormSubmission()); //set Click listener on submit button

        btnCancel.setOnClickListener(v -> {     //set click listener for cancel button
            finish();       //dismiss NewTaskActivity and return to previous screen (Welcome Page)
        });
    }

    //shows DatePickerDialog when deadline field is clicked
    private void showDatePickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year1, monthOfYear, dayOfMonth) -> {     //set date selected by the user to EditText field
                    String date = String.format(Locale.US, "%d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
                    editTextDeadline.setText(date);
                },
                year, month, day);

        datePickerDialog.show();
    }

    //logic for form submission for validation
    private void handleFormSubmission() {
        //get user input and trim whitespace
        String title = editTextTitle.getText().toString().trim();
        String deadline = editTextDeadline.getText().toString().trim();

        if(title.isEmpty()) {        //check if title is empty
            editTextTitle.setError("Task Title is required!");
            Toast.makeText(this, "Please enter a Task Title.", Toast.LENGTH_SHORT).show(); //provide user feedback toast
            return; //stop and prevent submission
        }

        if (deadline.isEmpty()) {       //check if deadline is empty
            editTextDeadline.setError("Deadline is required!");
            Toast.makeText(this, "Please select a Deadline.", Toast.LENGTH_SHORT).show();  //provide user feedback toast
            return; //stop and prevent submission
        }

        String notes = editTextNotes.getText().toString().trim();
        boolean useSQLite = checkboxStorageMethod.isChecked();



        if (useSQLite) {
            // --- Save to SQLite ---
            Task newTask = new Task(title, deadline, notes);
            long rowId = dbHelper.insertTask(newTask);  // returns -1 on failure
            if (rowId != -1) {
                Toast.makeText(this, "Task saved in SQLite ✅", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to save task to SQLite ❌", Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            // --- Save to SharedPreferences: append to JSON array "tasks_json" ---
            SharedPreferences prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);
            String json = prefs.getString("tasks_json", "[]");
            try {
                JSONArray arr = new JSONArray(json);
                JSONObject obj = new JSONObject();
                obj.put("title", title);
                obj.put("deadline", deadline);
                obj.put("notes", notes);
                arr.put(obj);
                prefs.edit().putString("tasks_json", arr.toString()).apply();
                Toast.makeText(this, "Task saved in SharedPreferences ✅", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                Toast.makeText(this, "Failed to save task to SharedPreferences ❌", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        clearFormFields();  //clear form

        finish();   //finish activity & return to MainActivity, which will refresh the list
    }

    private void clearFormFields() {        //clear form fields after submission
        editTextTitle.setText("");
        editTextDeadline.setText("");
        editTextNotes.setText("");
    }
}