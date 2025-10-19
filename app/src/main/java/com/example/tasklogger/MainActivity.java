package com.example.tasklogger;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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
    private LinearLayout taskListContainer;
    private RecyclerView recyclerViewTasks;
    private TaskDatabaseHelper dbHelper;
    private TaskAdapter adapter;
    private List<Task> taskList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeContainer = findViewById(R.id.welcomeContainer);
        taskListContainer = findViewById(R.id.taskListContainer);
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);

        Button btnLaunchForm = findViewById(R.id.btnLaunchForm);
        Button btnAddTask = findViewById(R.id.btnAddTask);

        dbHelper = new TaskDatabaseHelper(this);
        taskList = new ArrayList<>();

        // RecyclerView setup
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                Toast.makeText(MainActivity.this, "Tapped: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemLongClick(Task task) {
                Toast.makeText(MainActivity.this, "Long pressed: " + task.getTitle(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDelete(Task task) {
                // Delete task from database
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete("tasks", "title = ?", new String[]{task.getTitle()});
                Toast.makeText(MainActivity.this, "Deleted: " + task.getTitle(), Toast.LENGTH_SHORT).show();
                loadTasksFromDatabase();
            }
        });
        recyclerViewTasks.setAdapter(adapter);

        btnLaunchForm.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));
        btnAddTask.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromDatabase();
    }

    private void loadTasksFromDatabase() {
        taskList.clear();
        taskList.addAll(dbHelper.getAllTasks()); // assuming you have a method that returns all tasks
        adapter.notifyDataSetChanged();

        // Toggle visibility
        if (taskList.isEmpty()) {
            welcomeContainer.setVisibility(LinearLayout.VISIBLE);
            taskListContainer.setVisibility(LinearLayout.GONE);
        } else {
            welcomeContainer.setVisibility(LinearLayout.GONE);
            taskListContainer.setVisibility(LinearLayout.VISIBLE);
        }
    }
}
