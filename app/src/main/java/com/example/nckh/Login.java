package com.example.nckh;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login);

//        // Chuyển trang sau 3 giây
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(Login.this, MainActivity.class);
//                startActivity(intent);
//                finish(); // Đóng Activity hiện tại để không quay lại bằng nút Back
//            }
//        }, 3000); // 3000ms = 3 giây
    }
}