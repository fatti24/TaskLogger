package com.example.tasklogger;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity{
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Retrieve data passed through Intent extras
        ((TextView)findViewById(R.id.title)).setText(getIntent().getStringExtra("title"));
        ((TextView)findViewById(R.id.deadline)).setText(getIntent().getStringExtra("deadline"));
        ((TextView)findViewById(R.id.notes)).setText(getIntent().getStringExtra("notes"));
    }
}

