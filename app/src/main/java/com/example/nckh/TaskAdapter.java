package com.example.nckh;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TaskAdapter extends BaseAdapter {
    private Context context;
    private List<Task> taskList;
    private OnUpdateListener updateListener; // Callback interface

    public TaskAdapter(Context context, List<Task> taskList, OnUpdateListener updateListener) {
        this.context = context;
        this.taskList = taskList;
        this.updateListener = updateListener; // Gán callback
    }

    @Override
    public int getCount() {
        return taskList.size();
    }

    @Override
    public Object getItem(int position) {
        return taskList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_item_listview, parent, false);
        }

        TextView txtTaskName = convertView.findViewById(R.id.NameTask);
        Button btnComplete = convertView.findViewById(R.id.btnComplete);

        Task task = taskList.get(position);
        txtTaskName.setText(task.describe);

        btnComplete.setOnClickListener(v -> {
            // Gọi hàm updateReport từ activity thông qua callback
            Toast.makeText(context, "đã hoàn thành nhiệm vụ", Toast.LENGTH_SHORT).show();
            if (updateListener != null) {
                updateListener.onUpdate(task.id, task.id_device);
            }
        });

        return convertView;
    }

    public interface OnUpdateListener {
        void onUpdate(String taskId, String taskAttributes);
    }

    public static class Task {
        String all_attribute;
        String id;
        String describe;
        String id_device;

        public Task(String all_attribute, String id,String id_device,String describe) {
            this.all_attribute = all_attribute;
            this.id = id;
            this.describe = describe;
            this.id_device = id_device;
        }
    }
}
