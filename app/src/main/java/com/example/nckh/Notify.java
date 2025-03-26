package com.example.nckh;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notify extends AppCompatActivity {

    private Spinner spinnerDevice, spinnerRoom;
    private EditText editTextRoom, editTextIssue;
    private Button btnSubmit;
    private TextView textViewData;
    private String selectedDevice = "";

    private final List<String> areaList = new ArrayList<>();
    private final List<String> roomList = new ArrayList<>();

    private static final String URL = "http://192.168.172.1/device_management.php";
    private static final String URL_ADD = "http://192.168.172.1/add_report.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        // Ánh xạ View
        spinnerRoom = findViewById(R.id.spinner_device2);
        spinnerDevice = findViewById(R.id.spinner_device);
        editTextRoom = findViewById(R.id.editTextRoom);
        editTextIssue = findViewById(R.id.editTextIssue);
        btnSubmit = findViewById(R.id.btn_submit);
        textViewData = findViewById(R.id.textView_1);

        // Kiểm tra kết nối Internet trước khi tải dữ liệu
        if (isConnected()) {
            fetchData("area", "", "name", areaList, spinnerDevice);
        } else {
            Toast.makeText(this, "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
        }

        // Adapter cho Spinner thiết bị
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(adapter);

        // Adapter cho Spinner phòng
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(adapter2);

        // Xử lý chọn thiết bị từ Spinner
        spinnerDevice.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDevice = areaList.get(position);
                fetchData("room", "id_area=" + position, "name", roomList, spinnerRoom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedDevice = "";
            }
        });

        // Xử lý khi bấm nút gửi
        btnSubmit.setOnClickListener(v -> {
            String room = editTextRoom.getText().toString().trim();
            String issue = editTextIssue.getText().toString().trim();

            if (room.isEmpty() || issue.isEmpty()) {
                Toast.makeText(Notify.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                sendReport(selectedDevice, room, issue);
            }
        });
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void sendReport(String device, String room, String description) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("id_device", device);
            postData.put("room", room);
            postData.put("description", description);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL_ADD, postData,
                response -> {
                    Toast.makeText(Notify.this, "Báo cáo thành công!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Notify.this, Thankyou.class));
                    finish();
                },
                error -> Toast.makeText(Notify.this, "Lỗi khi gửi báo cáo!", Toast.LENGTH_SHORT).show());

        queue.add(jsonObjectRequest);
    }

    private void fetchData(String table, String where, String value_get, List<String> list, Spinner spinner) {
        RequestQueue queue = Volley.newRequestQueue(this);

        // Tạo URL hợp lệ
        String url = URL + "?table=" + table;
        if (!where.isEmpty()) {
            url += "&where=" + where;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        if (response.has(table)) {
                            JSONArray jsonArray = response.getJSONArray(table);
                            StringBuilder dataText = new StringBuilder("🔹 Danh sách:\n");

                            // Xóa dữ liệu cũ và thêm giá trị mặc định
                            list.clear();
                            list.add("Chọn dãy");

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                if (obj.has("id") && obj.has(value_get)) {
                                    dataText.append("ID: ").append(obj.getInt("id"))
                                            .append(", Tên: ").append(obj.getString(value_get))
                                            .append("\n");
                                    list.add(obj.getString(value_get));
                                }
                            }

                            textViewData.setText(dataText.toString());

                            // Cập nhật Spinner với danh sách tương ứng
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, list);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);

                        } else {
                            textViewData.setText("Không có dữ liệu!");
                        }
                    } catch (JSONException e) {
                        textViewData.setText("Lỗi phân tích dữ liệu!");
                        e.printStackTrace();
                    }
                },
                error -> textViewData.setText("Lỗi kết nối: " + error.getMessage()));

        queue.add(jsonObjectRequest);
    }
}
