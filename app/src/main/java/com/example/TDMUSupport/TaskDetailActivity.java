package com.example.TDMUSupport;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class TaskDetailActivity extends AppCompatActivity {

    private static final String UPDATE_URL = "http://192.168.172.1/update.php";
    private static final String SET_NULL_URL = "http://192.168.172.1/set_time_repair_null.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail); // Gắn layout XML với activity
        Button buttonBack = findViewById(R.id.buttonFinal);
        // Tìm TextView trong layout
        TextView textView = findViewById(R.id.textViewTaskDetail);

        // Nhận dữ liệu được truyền từ TaskAdapter thông qua Intent
        String taskInfo = getIntent().getStringExtra("describe");
        String reportId = getIntent().getStringExtra("reportId");
        String deviceId = getIntent().getStringExtra("deviceId");
        String NameButton = getIntent().getStringExtra("NameButton");


        // Kiểm tra dữ liệu và hiển thị
        if (taskInfo != null && !taskInfo.isEmpty()) {
            textView.setText(taskInfo);
        } else {
            textView.setText("Không có thông tin nhiệm vụ.");
        }
        buttonBack.setText(NameButton);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateReport(reportId);
            }
        });
        ImageButton backButton = findViewById(R.id.buttonBack);
        backButton.setOnClickListener(v -> finish());
    }

    private void updateReport(String reportId) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Chỉ gửi id_report
        Map<String, String> params = new HashMap<>();
        params.put("id_report", reportId);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPDATE_URL,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        if (jsonResponse.has("success")) {
                            Toast.makeText(this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Lỗi: " + jsonResponse.getString("error"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Lỗi xử lý dữ liệu!", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };

        queue.add(stringRequest);
    }

}
