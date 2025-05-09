package com.example.TDMUSupport;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

public class TaskPendingFragment extends Fragment {

    private ListView listView;
    private TaskAdapter adapter;
    private List<TaskAdapter.Task> taskList = new ArrayList<>();
    private static final String URL = "http://192.168.172.1/device_management.php";
    private int idGroup; // Thêm biến idGroup

    public TaskPendingFragment() {
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
        adapter = new TaskAdapter(getContext(), taskList, (taskId, taskAttributes) -> {
            // Khi nhấn "Hoàn thành", bạn có thể cập nhật API ở đây nếu muốn
            Toast.makeText(getContext(), "Nhiệm vụ đang xử lý", Toast.LENGTH_SHORT).show();
        });
        listView.setAdapter(adapter);

        // Gọi hàm tải dữ liệu ban đầu
        fetchTasks();

        // Thiết lập OnItemClickListener trong onCreateView
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            TaskAdapter.Task task = taskList.get(position);
            Intent intent = new Intent(getContext(), TaskDetailActivity.class);
            intent.putExtra("describe", task.all_attribute); // truyền nội dung nhiệm vụ
            intent.putExtra("reportId", task.id); // truyền nội dung nhiệm vụ
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
            intent.putExtra("NameButton", "Hoàn thành");

            startActivity(intent);
        });
    }

    private void fetchTasks() {
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = URL + "?table=joined&where=r.id_group=" + idGroup + " AND r.time_repair IS NULL"; // Thay thế 2 bằng idGroup

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        JSONArray jsonArray = response.getJSONArray("joined");
                        taskList.clear(); // Xóa dữ liệu cũ trong danh sách task
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);

                            StringBuilder result = new StringBuilder();
                            result.append("Tên: ").append(obj.getString("device_name")).append("\n")
                                    .append("Mô tả: ").append(obj.getString("report_description")).append("\n")
                                    .append("Phòng: ").append(obj.getString("room_name")).append("\n")
                                    .append("Thời gian: ").append(obj.getString("report_time_report"));

                            taskList.add(new TaskAdapter.Task(result.toString(), obj.getString("id"), obj.getString("id_device"), obj.getString("report_description")));
                        }
                        adapter.notifyDataSetChanged(); // Cập nhật lại dữ liệu trong adapter
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                Throwable::printStackTrace);

        queue.add(request);
    }
}

