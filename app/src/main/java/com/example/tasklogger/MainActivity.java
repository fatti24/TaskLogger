package com.example.tasklogger;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout welcomeContainer;
    private RecyclerView recyclerViewTasks;
    private TaskDatabaseHelper dbHelper;
    private TaskAdapter adapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        Button btnLaunchForm = findViewById(R.id.btnLaunchForm);
        welcomeContainer = findViewById(R.id.welcomeContainer);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        dbHelper = new TaskDatabaseHelper(this);
        taskList = new ArrayList<>();

        // RecyclerView setup
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Toast.makeText(MainActivity.this, "Tapped: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                // Here you can open a detail screen if you want
            }

            @Override
            public void onItemLongClick(Task task) {
                Toast.makeText(MainActivity.this, "Long pressed: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                // Here you can implement deletion or editing
            }
        });
        recyclerViewTasks.setAdapter(adapter);

        btnLaunchForm.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromDatabase();
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

        // Toggle welcome message vs RecyclerView
        if (taskList.isEmpty()) {
            welcomeContainer.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
        } else {
            welcomeContainer.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);
        }
    }
}
