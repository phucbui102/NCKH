package com.example.TDMUSupport;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TaskCompletedFragment extends Fragment {

    private ListView listView;
    private TaskAdapter adapter;
    private List<TaskAdapter.Task> taskList = new ArrayList<>();
    private static final String URL = "http://192.168.172.1/device_management.php";
    private int idGroup; // Thêm biến idGroup
    private Button repay;
    public TaskCompletedFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task_list, container, false);


        // Lấy idGroup từ arguments
        if (getArguments() != null) {
            idGroup = getArguments().getInt("id_group", -1); // Nhận id_group từ Bundle
        }

        listView = view.findViewById(R.id.listView);
        adapter = new TaskAdapter(getContext(), taskList, null); // Không cần update cho đã hoàn thành
        listView.setAdapter(adapter);

        fetchTasks();

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            TaskAdapter.Task task = taskList.get(position);
            Intent intent = new Intent(getContext(), TaskDetailActivity.class);
            intent.putExtra("describe", task.all_attribute); // truyền nội dung nhiệm vụ
            intent.putExtra("reportId", task.id); // truyền nội dung nhiệm vụ
            intent.putExtra("deviceId", task.id_device); // truyền id thiết bị
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Gọi lại fetchTasks để chắc chắn dữ liệu luôn được tải lại khi quay lại Fragment
        fetchTasks();

        // Đảm bảo thiết lập lại listener khi quay lại Fragment
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            TaskAdapter.Task task = taskList.get(position);
            Intent intent = new Intent(getContext(), TaskDetailActivity.class);
            intent.putExtra("describe", task.all_attribute);
            intent.putExtra("NameButton", "Thu hồi");
            intent.putExtra("reportId", task.id);
            intent.putExtra("deviceId", task.id_device);
            startActivity(intent);
        });
    }

    private void fetchTasks() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = URL + "?table=joined&where=r.id_group=" + idGroup + " AND r.time_repair IS NOT NULL";
        // Sử dụng idGroup thay cho 2

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("joined");
                        taskList.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            StringBuilder result = new StringBuilder();
                            result.append("Tên: ").append(obj.getString("device_name")).append("\n")
                                    .append("Mô tả: ").append(obj.getString("report_description")).append("\n")
                                    .append("Phòng: ").append(obj.getString("room_name")).append("\n")
                                    .append("Thời gian: ").append(obj.getString("report_time_report"));

                            taskList.add(new TaskAdapter.Task(result.toString(), obj.getString("id"), obj.getString("id_device"), obj.getString("report_description")));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace);

        queue.add(request);
    }
}

