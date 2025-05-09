package com.example.TDMUSupport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btn_request ,btn_login;

//test
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Kiểm tra lại nếu file layout không phải activity_main.xml

        // Khởi tạo nút btn_request
        btn_request = findViewById(R.id.btn_request);
        btn_login = findViewById(R.id.btn_login);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(MainActivity.this, Login.class);
                startActivity(intent1);
            }
        });
        // Xử lý khi bấm nút
        btn_request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, Notify.class);
                startActivity(intent);

            }
        });
    }
}
