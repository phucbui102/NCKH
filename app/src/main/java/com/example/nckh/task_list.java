package com.example.nckh;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class task_list extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private TaskPagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_list);

        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        // Khởi tạo danh sách fragment và tiêu đề
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(new TaskPendingFragment());      // Nhiệm vụ
        fragments.add(new TaskCompletedFragment());    // Đã xong

        List<String> titles = new ArrayList<>();
        titles.add("Nhiệm vụ");
        titles.add("Đã xong");

        // Adapter cho ViewPager
        pagerAdapter = new TaskPagerAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);



        // Gắn tab với ViewPager
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(titles.get(position))).attach();
    }
}
