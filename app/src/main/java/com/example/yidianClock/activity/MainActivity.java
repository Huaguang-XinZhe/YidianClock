package com.example.yidianClock.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.yidianClock.matches.KeyWord;
import com.example.yidianClock.R;
import com.example.yidianClock.matches.RegexMatches;
import com.example.yidianClock.TextBGSpan;
import com.example.yidianClock.alarm.YDAlarm;
import com.example.yidianClock.adapter.MyFSAdapter;
import com.example.yidianClock.databinding.ActivityMainBinding;
import com.example.yidianClock.databinding.FragmentReminderdayBinding;
import com.example.yidianClock.fragment.HomeFragment;
import com.example.yidianClock.fragment.ReminderDayFragment;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;
import com.example.yidianClock.receiver.UnlockReceiver;
import com.example.yidianClock.utils.MyUtils;
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
    //用的多，就立个变量
    EditText remindInput;
    //引用ReminderDayFragment中的binding对象
    public FragmentReminderdayBinding frBinding;
    String sourceText;
    TextWatcher textWatcher;
    String timeStr;
    //String对象在类中如果一开始不初始化，那么他的初始值将默认为null
    //为防止空指针异常（因为之后要调用String对象的contains方法），必须赋值
    String oldSourceText = "";

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
                    if (frBinding.layoutInput.getVisibility() == View.VISIBLE) {
                        //隐藏后来的底部输入框
                        frBinding.layoutInput.setVisibility(View.GONE);
                    }
                    fab.setImageResource(R.drawable.add);
                } else {
                    if (fab.getVisibility() == View.GONE) {
                        //原来底部复现（延迟一下，让软键盘先下去）
                        new Handler().postDelayed(() -> {
                            fab.setVisibility(View.VISIBLE);
                            mainBinding.tabLayoutNav.setVisibility(View.VISIBLE);
                        }, 300);
                    }
                    fab.setImageResource(R.drawable.alarm);
                }
                // TODO: 2022/11/16 fab监听有待区分position 
                //fab短按监听
                fab.setOnClickListener(v -> {
                    if (position == 0) {
                        //主页点击
                        alarm.setFinally();
                    } else {
                        //提醒日点击
                        //在此对EditText初次实例化
                        remindInput = frBinding.editRemindInput;
                        //隐藏光标下面的水滴
                        ColorDrawable colorDrawable = new ColorDrawable(Color.TRANSPARENT);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            remindInput.setTextSelectHandle(colorDrawable);
                        }// TODO: 2022/11/22 对更低的版本如何处理？
                        //隐藏底部原来的布局
                        mainBinding.tabLayoutNav.setVisibility(View.GONE);
                        fab.setVisibility(View.GONE);
                        //显示输入框布局
                        frBinding.layoutInput.setVisibility(View.VISIBLE);
                        //使EditText获取焦点，并弹出软键盘
                        remindInput.requestFocus();
                        MyUtils.getInstance(MainActivity.this).showSoftInput(remindInput);
                        //获取焦点（底线变绿了，但是EditText中没用光标，软键盘也没有弹出）
                        //注意，在这里获取焦点，焦点改变事件不会被触发（一定要先注册，在改变才行，故将其放入delay块中）
//                        remindInput.requestFocus();
                        //得等会儿，要不然弹不出来（等待的时间要把握好，要不然弹出不稳定，有时不会弹，等长了又会有顿挫感）
//                        new Handler().postDelayed(() -> {
//                            //执行，但EditText必须获取到焦点
//                            remindInput.requestFocus();
//                            MyUtils.getInstance(dialog.getContext()).showSoftInput(remindInput);
//                        }, 150);
//                        dialog.show();

                        //撤销图标点击监听
                        frBinding.imageRevoke.setOnClickListener(v1 -> {
                            //使撤销按钮消失
                            frBinding.imageRevoke.setVisibility(View.GONE);
                            Log.i("getSongsList", "timeStr（撤销）= " + timeStr);
                            setSpan();
                        });

                        //EditText焦点改变监听
                        remindInput.setOnFocusChangeListener((v1, hasFocus) -> {
                            Log.i("getSongsList", "焦点改变监听");
                            if (hasFocus) {
                                //设置了这个后，Span文本无法点击了，但却修复了水滴隐现异常，也有了光标
//                                remindInput.setTextIsSelectable(true);
                                //下面这个没用
//                                remindInput.setCursorVisible(true);
                                Log.i("getSongsList", "有焦点了！！！");
                            }
                        });

                        //EditText文本改变监听
                        textWatcher = new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {}

                            @Override
                            public void afterTextChanged(Editable s) {
//                                Log.i("getSongsList", "editable前 = " + s);
                                sourceText = s.toString();
                                // TODO: 2022/11/20 缺一个方法，传入源字符串，得到一个表示时间的字符串
                                timeStr = RegexMatches.getFirstMatchedStr(RegexMatches.TIME_REGEX, sourceText);
                                Log.i("getSongsList", "timeStr = " + timeStr);
                                if (!timeStr.isEmpty() && RegexMatches.getNewMatchedStr(oldSourceText, sourceText) != null) {
                                    setSpan();
                                    //设置了Span后更新旧的源文本
                                    oldSourceText = sourceText;
                                    Log.i("getSongsList", "oldSourceText = " + oldSourceText);
                                }
                                Log.i("getSongsList", "输入改变");
                            }
                        };
                        remindInput.addTextChangedListener(textWatcher);

                    }
                });
                //fab长按监听
                fab.setOnLongClickListener(v -> {
                    if (position == 0) {
                        //主页点击
                        //始终开启
                        alarm.setLimitAlarm();
                        //将设置闲娱限止的状态存入sp中
                        sp.edit().putBoolean("isLimitAlarmSet", true).apply();
                    } else {
                        //提醒日点击


                    }
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

    /**
     * 为EditText设置Span
     */
    private void setSpan() {
        //为适配Span，必须+1
        int end = KeyWord.getEnd(sourceText, timeStr) + 1;
        int start = end - timeStr.length();
        Log.i("getSongsList", "start = " + start);
        Log.i("getSongsList", "end = " + end);
//        Editable editable = Editable.Factory.getInstance().newEditable(sourceText);
//        Log.i("getSongsList", "editable后 = " + editable);
        SpannableString spannableStr = new SpannableString(sourceText);
        //注意，span的索引就看end，end就是第几个字符，至于start，就往前推它们的差额就行了
        //使用Spannable.SPAN_EXCLUSIVE_EXCLUSIVE标志，在其后插入效果不会顺延，删除的时候一键就会全部删除。
        //SPAN_INCLUSIVE_INCLUSIVE标志，前后都可以插入，但也是一键删除；
        //SPAN_INTERMEDIATE标志、SPAN_COMPOSING标志、SPAN_MARK_MARK标志会在前边插入，后边不会，也是一键删除；
        //SPAN_POINT_POINT标志，后边可插，前边不行，一键删除；
        //单出来是为了移除方便，当然，这样写起来也简洁一点
        //由于之前做过判断，一定存在时间关键词
        TextBGSpan bgSpan = new TextBGSpan(getResources().getColor(R.color.green_set_value));
        spannableStr.setSpan(bgSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                        spannableStr.setSpan(new RelativeSizeSpan(1.5f),
//                                0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //为Span设置点击监听
        spannableStr.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //先移除文本改变监听，要不然又会触发文本改变，重新来一遍，使点击效果失效
                remindInput.removeTextChangedListener(textWatcher);
                //EditText移除Span，使用源文本
                remindInput.setText(sourceText);
                //EditText移除完Span后再加上文本改变监听
                remindInput.addTextChangedListener(textWatcher);
                showRevokeShort();
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //先移除文本改变监听，要不然会反复触发，导致无限循环而崩溃
        remindInput.removeTextChangedListener(textWatcher);
        //为EditText绑定span，并设置点击事件（必须）
        remindInput.setText(spannableStr, TextView.BufferType.SPANNABLE);
        //将光标移动到文本最后边，不能影响输入
        remindInput.setSelection(sourceText.length());
        //EditText设置完Span后再加上文本改变监听
        remindInput.addTextChangedListener(textWatcher);
        remindInput.setMovementMethod(LinkMovementMethod.getInstance());//点击事件必须添加这一句
    }

    /**
     * 显示撤销按钮，并在3秒后移除
     */
    private void showRevokeShort() {
        frBinding.imageRevoke.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            frBinding.imageRevoke.setVisibility(View.GONE);
        }, 3000);
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