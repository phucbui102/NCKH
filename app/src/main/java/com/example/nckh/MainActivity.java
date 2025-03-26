package com.example.nckh;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btn_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Kiểm tra lại nếu file layout không phải activity_main.xml

        // Khởi tạo nút btn_request
        btn_request = findViewById(R.id.btn_request);

        // Xử lý khi bấm nút
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Notify.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
