package com.example.TDMUSupport;

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
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

    private Spinner spinnerArea, spinnerRoom, spinnerDevice ;
    private EditText editTextNameDevice, editTextIssue;
    private Button btnSubmit;
    private ImageView btnBack, btnScan;
    private int selectedId;

    private final List<String> areaList = new ArrayList<>();
    private final List<String> roomList = new ArrayList<>();
    private final List<String> deviceList = new ArrayList<>();

    private final List<Integer> id_areaList = new ArrayList<>();
    private final List<Integer> id_roomList = new ArrayList<>();
    private final List<Integer> id_deviceList = new ArrayList<>();

    private static final String URL = "http://192.168.172.1/device_management.php";
    private static final String URL_ADD = "http://192.168.172.1/add_report.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notify);

        spinnerRoom = findViewById(R.id.spinner_device2);
        spinnerArea = findViewById(R.id.spinner_device);
        spinnerDevice = findViewById(R.id.spinner_device3);
        editTextNameDevice = findViewById(R.id.editTextRoom);
        editTextIssue = findViewById(R.id.editTextIssue);
        btnSubmit = findViewById(R.id.btn_submit);
        btnBack = findViewById(R.id.backbtn);
        btnScan = findViewById(R.id.btnScanBarcode);

        // Kiểm tra kết nối mạng
        if (isConnected()) {
            fetchData("area", "", "name", areaList, spinnerArea, id_areaList);
        } else {
            Toast.makeText(this, "Không có kết nối Internet!", Toast.LENGTH_SHORT).show();
        }

        areaList.add(0, "Chọn khu vực");
        roomList.add(0, "Chọn phòng");
        deviceList.add(0, "Chọn thiết bị");

        ArrayAdapter<String> areaAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerArea.setAdapter(areaAdapter);

        ArrayAdapter<String> roomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roomList);
        roomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(roomAdapter);

        ArrayAdapter<String> deviceAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, deviceList);
        deviceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDevice.setAdapter(deviceAdapter);

        spinnerArea.setSelection(0, false);
        spinnerRoom.setSelection(0, false);
        spinnerDevice.setSelection(0, false);

        btnBack.setOnClickListener(view -> finish());

        spinnerArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int selectedId = id_areaList.get(position - 1);
                    fetchData("room", "id_area=" + selectedId, "name", roomList, spinnerRoom, id_roomList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    selectedId = id_roomList.get(position - 1);
                    fetchData("device", "id_room=" + selectedId, "name", deviceList, spinnerDevice, id_deviceList);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSubmit.setOnClickListener(v -> {
            String NameDevice = editTextNameDevice.getText().toString().trim();
            String issue = editTextIssue.getText().toString().trim();
            String roomAndDescription;

            if (NameDevice.isEmpty() || issue.isEmpty()) {
                // can sua
                String DeviceName = spinnerDevice.getSelectedItem().toString();
                 roomAndDescription = DeviceName + " - " + issue;
                int selectedDeviceId = (spinnerDevice.getSelectedItemPosition() > 0)
                        ? id_deviceList.get(spinnerDevice.getSelectedItemPosition() - 1) : -1;
                sendReport(String.valueOf(selectedDeviceId), roomAndDescription, selectedId);
            } else if (!NameDevice.isEmpty()){
                roomAndDescription = NameDevice + " - " + issue;
                int selectedDeviceId= -1;
//                int selectedDeviceId = (spinnerDevice.getSelectedItemPosition() > 0)
//                        ? id_deviceList.get(spinnerDevice.getSelectedItemPosition() - 1) : -1;
                for (int i = 1; i < deviceList.size(); i++) { // Bỏ index 0 vì nó là "Chọn thiết bị"
                    if (deviceList.get(i).equals(NameDevice)) {
                        selectedDeviceId = id_deviceList.get(i - 1); // Lấy id tương ứng (vì id_deviceList chỉ lưu từ index 0)
                        break; // Đã tìm thấy thì thoát vòng lặp luôn
                    }
                }
                sendReport(String.valueOf(selectedDeviceId), roomAndDescription, selectedId);
            }
        });

        // Xử lý quét mã QR
        btnScan.setOnClickListener(v -> {
            Intent intent = new Intent(Notify.this, CaptureActivityPortrait.class);
            startActivityForResult(intent, 1001); // Mã requestCode tùy ý
        });
    }

    // Nhận kết quả từ màn hình quét
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1001 && resultCode == RESULT_OK && data != null) {
            String scanResult = data.getStringExtra("SCAN_RESULT");
            if (scanResult != null) {
                editTextNameDevice.setText(scanResult);
            }
        }
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    private void sendReport(String device, String description, int id_room) {
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject postData = new JSONObject();
        try {
            postData.put("id_device", device);
            postData.put("description", description);
            postData.put("id_room", id_room);
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

    private void fetchData(String table, String where, String value_get,
                           List<String> list, Spinner spinner, List<Integer> id_list) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = URL + "?table=" + table + (!where.isEmpty() ? "&where=" + where : "");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray(table);
                        list.clear();
                        list.add("Chọn mục");
                        id_list.clear();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            list.add(obj.getString(value_get));
                            id_list.add(obj.getInt("id"));
                        }

                        runOnUiThread(() -> {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                                    android.R.layout.simple_spinner_item, list);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinner.setAdapter(adapter);
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace);

        queue.add(jsonObjectRequest);
    }
}
