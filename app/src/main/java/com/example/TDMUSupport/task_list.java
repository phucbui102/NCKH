package com.example.TDMUSupport;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class task_list extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TaskPagerAdapter pagerAdapter;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_list);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
//        btnBack = findViewById(R.id.btnBack);
        // Khởi tạo danh sách fragment và tiêu đề
        List<Fragment> fragments = new ArrayList<>();

        // Lấy idGroup từ Intent
        int idGroup = getIntent().getIntExtra("id_group", -1);

        // Tạo các fragment và truyền idGroup vào Bundle
        TaskPendingFragment pendingFragment = new TaskPendingFragment();
        TaskCompletedFragment completedFragment = new TaskCompletedFragment();

        // Tạo Bundle và truyền idGroup vào
        Bundle bundle = new Bundle();
        bundle.putInt("id_group", idGroup);

        // Gán Bundle cho các Fragment
        pendingFragment.setArguments(bundle);
        completedFragment.setArguments(bundle);

        // Thêm các Fragment vào danh sách
        fragments.add(pendingFragment);
        fragments.add(completedFragment);

        List<String> titles = new ArrayList<>();
        titles.add("Nhiệm vụ");
        titles.add("Đã xong");

        // Adapter cho ViewPager
        pagerAdapter = new TaskPagerAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);

        // Gắn tab với ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(titles.get(position))).attach();

        // Thiết lập sự kiện cho nút Avatar
        ImageButton btnAvatar = findViewById(R.id.btnAvatar);

//        btnBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
        btnAvatar.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v); // tạo popup tại vị trí nút avatar
            popupMenu.getMenuInflater().inflate(R.menu.avatar_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.menu_account) {
//                    startActivity(new Intent(this, Login.class));
//                    finish();
                    return true;
                } else if (id == R.id.menu_logout) {
                    // Xử lý khi bấm "Đăng xuất"
                    startActivity(new Intent(this, MainActivity.class));
                    finish();
                    Toast.makeText(this, "Đăng xuất thành công", Toast.LENGTH_SHORT).show();
                    return true;
                }
                return false;
            });

            popupMenu.show();
        });
    }
}
