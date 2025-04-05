package com.example.nckh;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class task_list extends AppCompatActivity {

    private ListView listViewTasks;
    private TaskAdapter adapter;
    private List<TaskAdapter.Task> taskList;
    private TextView tvGroupName;
    private static final String URL = "http://192.168.172.1/device_management.php";
    private static final String UPDATE_URL = "http://192.168.172.1/update.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_list);

        listViewTasks = findViewById(R.id.listViewTasks);
        Button btnLogout = findViewById(R.id.btnLogout);

        tvGroupName = findViewById(R.id.tvGroupName);
        taskList = new ArrayList<>();

        // Khởi tạo adapter với callback
        adapter = new TaskAdapter(this, taskList, new TaskAdapter.OnUpdateListener() {
            @Override
            public void onUpdate(String taskId, String taskAttributes) {
                // Gọi hàm updateReport khi người dùng click vào một task
                updateReport(taskId, taskAttributes, 1); // Cập nhật báo cáo với id_report và id_device, status mới
            }
        });

        listViewTasks.setAdapter(adapter);

        
        if (isConnected()) {
            fetchData("joined", "");
        } else {
            Toast.makeText(this, "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
        }

        // Xử lý sự kiện đăng xuất
        btnLogout.setOnClickListener(v -> Toast.makeText(this, "Đăng xuất thành công!", Toast.LENGTH_SHORT).show());
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void fetchData(String table, String where) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL + "?table=" + table + (!where.isEmpty() ? "&where=" + where : "");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray(table);
                        taskList.clear(); // Xóa dữ liệu cũ trước khi cập nhật

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            // Sử dụng StringBuilder để gộp chuỗi
                            StringBuilder result = new StringBuilder();
                            result.append("Tên: ").append(obj.getString("device_name")).append("\n")
                                    .append("Mô tả: ").append(obj.getString("report_description")).append("\n")
                                    .append("Phòng: ").append(obj.getString("room_name")).append("\n")
                                    .append("Thời gian nhận thông báo: ").append(obj.getString("report_time_report"));

                            String finalString = result.toString();

                            // Thêm task mới vào danh sách
                            taskList.add(new TaskAdapter.Task(finalString, obj.getString("id"),obj.getString("id_device")));
                        }

                        // Cập nhật adapter mà không cần tạo lại
                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace);

        queue.add(jsonObjectRequest);
    }

    private void updateReport(String reportId, String deviceId, int newStatus) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Construct the parameters to send
        Map<String, String> params = new HashMap<>();
        params.put("id_report", reportId);
        params.put("id_device", deviceId);
        params.put("status", String.valueOf(newStatus)); // Status value to update

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

        // Add the request to the queue for execution
        queue.add(stringRequest);
    }
}
