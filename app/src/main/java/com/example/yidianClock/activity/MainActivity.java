package com.example.yidianClock.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.yidianClock.R;
import com.example.yidianClock.alarm.YDAlarm;
import com.example.yidianClock.adapter.MyFSAdapter;
import com.example.yidianClock.databinding.ActivityMainBinding;
import com.example.yidianClock.fragment.HomeFragment;
import com.example.yidianClock.fragment.ReminderDayFragment;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;
import com.example.yidianClock.receiver.UnlockReceiver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private final YDAlarm alarm = new YDAlarm(this);
    private final UnlockReceiver unlockReceiver = new UnlockReceiver();
    DrawerLayout drawerLayout;
    SharedPreferences sp;
    ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        drawerLayout = mainBinding.getRoot();
        setContentView(drawerLayout);

        FloatingActionButton fab = mainBinding.fab;
        sp = getSharedPreferences("sp", MODE_PRIVATE);

        //ActionBar左侧设置
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            Log.i("getSongsList", "actionBar里边执行");
            //这是返回箭头
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        //ActionBar点击home图标打开/关闭抽屉的动画效果
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.drawer_open, R.string.drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);

        //创建数据库，并初始化数据。注意！这是耗时操作！！！
        initDBData();
        alarm.setFinally();

        //数据初始化
        //fragment数据
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment(this));
        fragmentList.add(new ReminderDayFragment(this));
        //tab title数据
        List<String> tabTitleList = new ArrayList<>();
        tabTitleList.add("主页");
        tabTitleList.add("提醒日");

        //创建ViewPager2和其Adapter，并绑定
        ViewPager2 viewPager = mainBinding.viewpagerHome;
        MyFSAdapter fsAdapter = new MyFSAdapter(this, fragmentList);
        viewPager.setAdapter(fsAdapter);

        //通过TabLayoutMediator来关联TabLayout和ViewPager2
        //注意，这个position只能用来关联TabLayout，不能用来执行其他和position相关的逻辑（根本不会执行）
        new TabLayoutMediator(mainBinding.tabLayoutNav, viewPager, (tab, position) -> {
            tab.setText(tabTitleList.get(position));
            Log.i("getSongsList", "现在position是 " + position);
//            if (position == 1) {
//                Toast.makeText(this, "你是一只猪，该换换图标了", Toast.LENGTH_SHORT).show();
//                mainBinding.fab.setImageResource(R.drawable.add);
//            }
        }).attach();

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.i("getSongsList", "你选中了 " + position);
                if (position == 1) {
                    fab.setImageResource(R.drawable.add);
                } else {
                    fab.setImageResource(R.drawable.alarm);
                }
                // TODO: 2022/11/16 fab监听有待区分position 
                //fab短按监听
                fab.setOnClickListener(v -> {
                    alarm.setFinally();
                });
                //fab长按监听
                fab.setOnLongClickListener(v -> {
                    //始终开启
                    alarm.setLimitAlarm();
                    //将设置闲娱限止的状态存入sp中
                    sp.edit().putBoolean("isLimitAlarmSet", true).apply();
                    return true;
                });
                
            }
        });

        //震光提示点击取消
//        mainBinding.shockLightTipTV.setOnClickListener(v -> {
////            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                new ManagerAlarm(this).cancel();
////            }
//            MyUtils.suicide();
//        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册解锁广播
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_USER_PRESENT);
        registerReceiver(unlockReceiver, filter);

        //test
//        SensorManager manager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        List<Sensor> list = manager.getSensorList(Sensor.TYPE_ALL);
//        for (Sensor sensor : list) {
//            Log.i("Test", sensor.getName());
//        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(unlockReceiver);
    }

    /**
     * 这是首页ActionBar右侧的设置按钮
     */
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        //必须加上下面这两句
        MenuInflater menuInflater = new MenuInflater(this);
        menuInflater.inflate(R.menu.menu_home, menu);
        //由于直接在menu_home.xml中定义无法显示，故在代码中指定（解决图标不正常显示问题）
        menu.findItem(R.id.setting).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }

    /**
     * 在这里处理ActionBar图标的点选事件
     */
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                //跳转到设置界面
                Intent intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
            case android.R.id.home:
                //打开/关闭抽屉
                if (drawerLayout.isOpen()) {
                    drawerLayout.close();
                } else {
                    drawerLayout.open();
                }
                break;
        }
        return true;
    }

    // TODO: 2022/11/16 这个方法是干啥的？
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //ActionBar中home图标动画切换效果所需
        drawerToggle.syncState();
    }

    private void initDBData() {
        if (sp.getBoolean("myDBInit_JustDoOnce", true)) {
            new LunchAlarm().save();
            new SleepAlarm().save();
            sp.edit().putBoolean("myDBInit_JustDoOnce", false).apply();
        }
    }
}