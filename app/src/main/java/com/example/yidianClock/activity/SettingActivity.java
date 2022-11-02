package com.example.yidianClock.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.yidianClock.MyAdapter;
import com.example.yidianClock.MyPicker;
import com.example.yidianClock.MyUtils;
import com.example.yidianClock.R;
import com.example.yidianClock.databinding.ActivitySettingBinding;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;

import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding settingBinding;
    private MyPicker picker;
    private MyUtils utils;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(settingBinding.getRoot());

        RecyclerView recyclerView = settingBinding.settingRv;

        //创建并设置布局管理器
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //创建并设置adapter
        MyAdapter adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);

        //以下代码在recyclerView绑定加载的时候会加载，所以，调用方法的对象在一开始就不能为空
        adapter.setOnListener((holder, position) -> {
            utils = new MyUtils(this);
            boolean isNight = holder.restType.getText().equals("晚睡");

            //公用逻辑——————————————————————————————————————————————————
            //蓝色更多，点击展开或收回
            holder.moreSetView.setOnClickListener(v -> {
                int vis;
                if (holder.moreSetLayout.getVisibility() == View.GONE) {
                    vis = View.VISIBLE;
                    //展开，收起软键盘
                    utils.hideSoftInput(holder.itemView);
                } else {
                    //只可能是visible
                    vis = View.GONE;
                }
                holder.moreSetLayout.setVisibility(vis);
            });

            //防息损震动提示，开启展开，关闭收缩
            holder.isSetShockButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int vis;
                if (isChecked) {
                    vis = View.VISIBLE;
                    //展开，收起软键盘
                    utils.hideSoftInput(holder.itemView);
                    //如果是展开晚睡的震动提示，则使recyclerView上移，底部可见
                    if (isNight) {
                        Toast.makeText(this, "执行！", Toast.LENGTH_SHORT).show();
                        //下面两种代码对上移都是有用的，没有这两行代码就不会上移
//                        Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(1);
//                        recyclerView.scrollToPosition(1);
                        //只有下面这行代码能实现最终的效果，注意：这里将offset设为0不能拉到最底边！故设为负值
                        layoutManager.scrollToPositionWithOffset(1, -100);
                    }
                } else {
                    vis = View.GONE;
                }
                holder.shockSetLayout.setVisibility(vis);
            });

            //点击一般时段布局块，弹出时段选择器
            holder.potLayout.setOnClickListener(v -> {
                picker = new MyPicker(this, isNight);
                picker.setAndShow();
                //TimePicker点击确认时更新TextView的值
                picker.setOnConfirm(() -> holder.potView.setText(picker.getPOT()));
            });

            //点击闹钟任务布局块，开启或关闭
            holder.alarmTaskLayout.setOnClickListener(v -> holder.isSetTaskButton.
                    setChecked(!holder.isSetTaskButton.isChecked()));

            //点击震动提示布局块，开启或关闭
            holder.shockTipLayout.setOnClickListener(v -> holder.isSetShockButton.
                    setChecked(!holder.isSetShockButton.isChecked()));


        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("SettingActivity", "onDestroy: 执行！");
        //用户数据存档
        Toast.makeText(this, "设置已更新", Toast.LENGTH_SHORT).show();
    }
}