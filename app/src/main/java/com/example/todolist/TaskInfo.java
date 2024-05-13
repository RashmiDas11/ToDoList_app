package com.example.todolist;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class TaskInfo extends AppCompatActivity {
    ImageView imageView;
    TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_info);
        imageView = findViewById(R.id.imagetask);
        textview = findViewById(R.id.textTask);
    }
}