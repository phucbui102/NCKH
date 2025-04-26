package com.example.nckh;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TaskDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail); // Gắn layout XML với activity
        Button buttonBack = findViewById(R.id.buttonFinal);
        // Tìm TextView trong layout
        TextView textView = findViewById(R.id.textViewTaskDetail);

        // Nhận dữ liệu được truyền từ TaskAdapter thông qua Intent
        String taskInfo = getIntent().getStringExtra("describe");

        // Kiểm tra dữ liệu và hiển thị
        if (taskInfo != null && !taskInfo.isEmpty()) {
            textView.setText(taskInfo);
        } else {
            textView.setText("Không có thông tin nhiệm vụ.");
        }
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
