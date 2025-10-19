package com.example.tasklogger;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout welcomeContainer;
    private LinearLayout taskListContainer;
    private RecyclerView recyclerViewTasks;
    private TaskDatabaseHelper dbHelper;
    private TaskAdapter adapter;
    private final List<Task> taskList = new ArrayList<>();

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

        // RecyclerView setup
        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TaskAdapter(taskList, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                // TAP => open details screen
                Intent i = new Intent(MainActivity.this, TaskDetailActivity.class);
                i.putExtra("title", task.getTitle());
                i.putExtra("deadline", task.getDeadline());
                i.putExtra("notes", task.getNotes());
                startActivity(i);
            }

            @Override
            public void onItemLongClick(Task task) {
                // LONG PRESS => Delete / Update
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Modify task")
                        .setItems(new CharSequence[]{"Delete", "Update"}, (d, which) -> {
                            if (which == 0) {
                                handleDelete(task); // delete from the correct storage
                            } else {
                                // Minimal update flow: open the form (prefill optional later)
                                startActivity(new Intent(MainActivity.this, NewTaskActivity.class));
                            }
                        })
                        .show();
            }

            @Override
            public void onItemDelete(Task task) {
                handleDelete(task);
            }
        });
        recyclerViewTasks.setAdapter(adapter);

        btnLaunchForm.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));
        btnAddTask.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NewTaskActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasksFromBothStores();
    }

    //Loads tasks from both SQLite and SharedPreferences: SQLite rows have id> 0 and SharedPreferences tasks have id = 0
    private void loadTasksFromBothStores() {
        taskList.clear();

        // Load from SQLite
        taskList.addAll(dbHelper.getAllTasks());

        //Load from SharedPreferences (JSON array)
        SharedPreferences prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);
        String json = prefs.getString("tasks_json", "[]");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject o = arr.getJSONObject(i);
                String title = o.optString("title", "");
                String deadline = o.optString("deadline", "");
                String notes = o.optString("notes", "");
                // For SP items, use the 3-arg constructor (id defaults to 0)
                taskList.add(new Task(title, deadline, notes));
            }
        } catch (JSONException ignore) { }

        adapter.notifyDataSetChanged();

        // Toggle welcome vs list
        if (taskList.isEmpty()) {
            welcomeContainer.setVisibility(LinearLayout.VISIBLE);
            taskListContainer.setVisibility(LinearLayout.GONE);
        } else {
            welcomeContainer.setVisibility(LinearLayout.GONE);
            taskListContainer.setVisibility(LinearLayout.VISIBLE);
        }
    }

    /**
     * Deletes a task from the correct storage:If task has an id (>0), it came from SQLite: delete via DB helper. Else, try to remove a matching entry from SharedPreferences JSON array.Finally, refresh the list.
     */
    private void handleDelete(Task task) {
        boolean deleted = false;

        // SQLite-backed (has id)
        if (task.getId() > 0) {
            dbHelper.deleteTask(task.getId());
            deleted = true;
        } else {
            // SharedPreferences-backed (no id). Match by fields.
            SharedPreferences prefs = getSharedPreferences("TaskPrefs", MODE_PRIVATE);
            String json = prefs.getString("tasks_json", "[]");
            try {
                JSONArray arr = new JSONArray(json);
                JSONArray out = new JSONArray();
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject o = arr.getJSONObject(i);
                    boolean same =
                            task.getTitle().equals(o.optString("title")) &&
                                    task.getDeadline().equals(o.optString("deadline")) &&
                                    task.getNotes().equals(o.optString("notes"));
                    if (!same) out.put(o);
                }
                prefs.edit().putString("tasks_json", out.toString()).apply();
                deleted = true;
            } catch (JSONException ignore) { }
        }

        Toast.makeText(this, deleted ? "Task deleted" : "Delete failed", Toast.LENGTH_SHORT).show();
        loadTasksFromBothStores(); // refresh list after deletion
    }


}
