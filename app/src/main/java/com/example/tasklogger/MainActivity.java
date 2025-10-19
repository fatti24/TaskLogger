package com.example.tasklogger;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout welcomeContainer;
    private LinearLayout taskListContainer;
    private RecyclerView recyclerViewTasks;
    private TaskAdapter adapter;
    private List<Task> taskList;
    private TaskDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        welcomeContainer = findViewById(R.id.welcomeContainer);
        taskListContainer = findViewById(R.id.taskListContainer);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        Button btnLaunchForm = findViewById(R.id.btnLaunchForm);  // From welcome page
        Button btnAddTask = findViewById(R.id.btnAddTask);        // From task list page

        dbHelper = new TaskDatabaseHelper(this);
        taskList = new ArrayList<>();

        // RecyclerView setup
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Toast.makeText(MainActivity.this, "Tapped: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: open detail screen if needed
            }

            @Override
            public void onItemLongClick(Task task) {
                Toast.makeText(MainActivity.this, "Long pressed: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: implement deletion or editing
            }
        });
        recyclerViewTasks.setAdapter(adapter);

        // Click listeners for both buttons to launch NewTaskActivity
        btnLaunchForm.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));
        btnAddTask.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromDatabase(); // Refresh task list every time the activity comes to foreground
    }

    private void loadTasksFromDatabase() {
        taskList.clear();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("tasks", null, null, null, null, null, "id DESC");

        while (cursor.moveToNext()) {
            String title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
            String deadline = cursor.getString(cursor.getColumnIndexOrThrow("deadline"));
            String notes = cursor.getString(cursor.getColumnIndexOrThrow("notes"));

            taskList.add(new Task(title, deadline, notes));
        }
        cursor.close();

        adapter.notifyDataSetChanged();

        // Toggle visibility
        if (taskList.isEmpty()) {
            welcomeContainer.setVisibility(View.VISIBLE);
            taskListContainer.setVisibility(View.GONE);
        } else {
            welcomeContainer.setVisibility(View.GONE);
            taskListContainer.setVisibility(View.VISIBLE);
        }
    }
}
