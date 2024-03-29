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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.yidianClock.GlobalLayoutListener;
import com.example.yidianClock.OnKeyboardChangedListener;
import com.example.yidianClock.R;
import com.example.yidianClock.TextBGSpan;
import com.example.yidianClock.adapter.MyFSAdapter;
import com.example.yidianClock.adapter.ReminderAdapter;
import com.example.yidianClock.alarm.YDAlarm;
import com.example.yidianClock.databinding.ActivityMainBinding;
import com.example.yidianClock.databinding.FragmentHomeBinding;
import com.example.yidianClock.databinding.FragmentReminderdayBinding;
import com.example.yidianClock.fragment.HomeFragment;
import com.example.yidianClock.fragment.ReminderDayFragment;
import com.example.yidianClock.matches.KeyWord;
import com.example.yidianClock.matches.RegexMatches;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.Reminder;
import com.example.yidianClock.model.SleepAlarm;
import com.example.yidianClock.receiver.UnlockReceiver;
import com.example.yidianClock.time_conversions.MatchStandardization;
import com.example.yidianClock.utils.MyUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayoutMediator;
import com.loper7.date_time_picker.DateTimeConfig;
import com.loper7.date_time_picker.dialog.CardDatePickerDialog;

import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mainBinding;
    private final YDAlarm alarm = new YDAlarm(this);
    private final UnlockReceiver unlockReceiver = new UnlockReceiver();
    DrawerLayout drawerLayout;
    SharedPreferences sp;
    ActionBarDrawerToggle drawerToggle;
    //用的多，就立个变量
    EditText remindInput;
    //引用ReminderDayFragment中的binding对象，交给Fragment赋值
    public FragmentReminderdayBinding frBinding;
    //持有ReminderDayFragment的reminderList的引用
    public List<Reminder> reminderList;
    //持有ReminderDayFragment的adapter的引用
    public ReminderAdapter adapter;
    //引用ReminderDayFragment的layoutManager
    public RecyclerView.LayoutManager layoutManager;
    //引用HomeFragment的Binding
    public FragmentHomeBinding fhBinding;
    String sourceText;
    String timeStr;
    //String对象在类中如果一开始不初始化，那么他的初始值将默认为null
    //为防止空指针异常（因为之后要调用String对象的contains方法），必须赋值
    String oldSourceText = "";
    /**
     * 是否允许触发文本改变
     */
    boolean isTriggerTextChange = true;
    /**
     * 标准化后的时间
     */
    String standardTime;
    /**
     * 时间输入是否来自输入框
     */
    boolean isFromInput = true;
    /**
     * 提醒日到期闹铃提示文本，没到期为空字符串
     */
    String content;

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

        //Toasty配置，使之居中显示
            Toasty.Config.getInstance()
                    .allowQueue(false)//禁止排队
                    .setGravity(Gravity.CENTER_VERTICAL)
                    .apply();

        //创建数据库，并初始化数据。注意！这是耗时操作！！！
        initDBData();
        //设置提醒——————————————————————————————————————————————————————————————————————————
        //设置午休、晚睡闹钟（含震光提示）
        alarm.setFinally();
        // TODO: 2022/12/7 将这里计算得到的最近目标日传给fragment引用
        //获取数据库中离现在最近的目标日
        Date latestGoalDate = MyUtils.getTheLatestGoalDate();
        content = setReminder(latestGoalDate);
        Log.i("getSongsList", "content = " + content);

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

        //监听软键盘的状态改变
        mainBinding.getRoot().getViewTreeObserver().addOnGlobalLayoutListener(
                new GlobalLayoutListener(mainBinding.getRoot(), (isShow, keyboardHeight, screenWidth, screenHeight) -> {
                    if (isShow) {
                        Log.i("getSongsList", "软键盘弹出！");
                        Log.i("getSongsList", "keyboardHeight = " + keyboardHeight);
                        //存入延迟（划回主页，底部布局的复现延迟）时间，300毫秒
                        sp.edit().putLong("delayMillis", 300).apply();
                    } else {
                        Log.i("getSongsList", "软键盘隐藏！");
                        Log.i("getSongsList", "keyboardHeight = " + keyboardHeight);
                        //几乎不延迟
                        sp.edit().putLong("delayMillis", 10).apply();
                    }
                }));

        //ViewPager2滑动回调
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                Log.i("getSongsList", "你选中了 " + position);
                if (position == 1) {
//                    Log.i("getSongsList", "onPageSelected：position = 1 -> frBinding = " + frBinding);
                    //必须在这里判空，要不然点击切换过去之前Fragment还没加载好，为null
                    //注意：滑动切换Fragment会先加载，在执行onPageSelected逻辑！
                    if (frBinding != null && frBinding.layoutInput.getVisibility() == View.VISIBLE) {
                        //隐藏后来的底部输入框
                        frBinding.layoutInput.setVisibility(View.GONE);
                        //此时adapter不为null，重新实现OnListener接口，调回list尾部布局的高度
                        adapter.setOnListener((holder, layoutParams) -> {
                            layoutParams.height = 60*3;
                            holder.itemView.setLayoutParams(layoutParams);
                            Log.i("getSongsList", "底部布局已恢复！");
                        });
                        //必须更新UI才能执行实现
                        adapter.notifyItemChanged(reminderList.size()-1);
                    }
                    fab.setImageResource(R.drawable.add);
                } else {
                    //以下代码启动后就会执行，但此时OnCreate和主页Fragment已经加载好了
                    //延迟一下，让软键盘先下去
                    long delayMillis = sp.getLong("delayMillis", 200);
                    //原来底部复现（只有fab没显示才执行）
                    if (fab.getVisibility() == View.GONE) {
                        new Handler().postDelayed(() -> {
                            fab.setVisibility(View.VISIBLE);
                            mainBinding.tabLayoutNav.setVisibility(View.VISIBLE);
                        }, delayMillis);
                    }
                    fab.setImageResource(R.drawable.alarm);
                    //注意，这行代码不能放在OnCreate中，fhBinding为null
                    //设置主页的今日提示（数据库操作，耗时）
                    setHomeTipToday();
                }

                //fab短按监听
                fab.setOnClickListener(v -> {
                    if (position == 0) {
                        //主页点击
                        alarm.setFinally();


                    } else {
                        //提醒日点击
                        //在此对EditText初次实例化
                        remindInput = frBinding.editRemindInput;
//                        //一开始就设置发送图标不可点击（不管用，不管在哪里设置都不管用）
//                        remindInput.setText("");
                        //模拟触发文本改变(100ms)，使sourceText不为null，发送按钮一开始就不可点击，并显示光标，移至行首
                        remindInput.setText(".");
                        new Handler().postDelayed(() -> remindInput.setText(""), 100);
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

                        //输入框的点击事件
                        remindInput.setOnClickListener(v1 -> {
                            Log.i("getSongsList", "输入框点击执行！");
                            //必须为空才能执行，要不然每次点击都会回到行首，便无法定点编辑
                            if (sourceText.isEmpty()) {
                                //显示光标，并移至行首，防止有时点击失去光标
                                remindInput.setSelection(0);
                            }
                        });

                        //撤销图标点击监听
                        frBinding.imageRevoke.setOnClickListener(v1 -> {
                            //使撤销按钮消失
                            frBinding.imageRevoke.setVisibility(View.GONE);
                            Log.i("getSongsList", "timeStr（撤销）= " + timeStr);
                            setSpan();
                        });

                        //日历图标点击监听
                        frBinding.imageCalendar.setOnClickListener(v1 -> {
                            new CardDatePickerDialog.Builder(MainActivity.this)
                                    .setTitle("日期选择")
                                    .setThemeColor(getResources().getColor(R.color.green_set_value))
                                    .setDisplayType(DateTimeConfig.YEAR, DateTimeConfig.MONTH, DateTimeConfig.DAY)
                                    .showBackNow(false)
                                    .setChooseDateModel(DateTimeConfig.DATE_LUNAR)
                                    .setOnChoose("确认", aLong -> {
                                        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
                                        String dateStr = sdFormat.format(new Date(aLong));
                                        //日历选择弹窗中的确认，时间就不来自于输入框了
                                        isFromInput = false;
                                        //变更标准后的时间
                                        standardTime = dateStr;
                                        //执行发送按钮的逻辑
                                        frBinding.imageSend.performClick();
                                        return null;
                                    })
                                    .build().show();

                        });

                        //发送按钮点击监听
                        frBinding.imageSend.setOnClickListener(v1 -> {
                            //输入时间文本的类型
                            String type;
                            Log.i("getSongsList", "发送点击！");
//                            //为防止没输入（sourceText为null，不是空串）就点击发送，而执行崩溃，故先判空
//                            if (sourceText != null) {
//                                Log.i("getSongsList", "输入框非空执行！");
                            //隐藏输入部分（切换到其他场景），之后再回来，sourceText为空串，不为null，且行首的光标又没了
                            if (timeStr.isEmpty()) {
                                String tip = "您还没有输入或选择时间！";
                                Toasty.warning(MainActivity.this, tip, Toasty.LENGTH_SHORT).show();
//                                    //显示光标，并移至行首
//                                    remindInput.setSelection(0);
//                                    //禁止点击发送按钮
//                                    frBinding.imageSend.setClickable(false);
                                return;
                            }
                            //时间标准化
                            //如果时间来自输入框，就执行转换逻辑
                            if (isFromInput) {
                                String[] dpArr = MatchStandardization.conversions(timeStr);
                                standardTime = dpArr[0];
                                type = dpArr[1];
                            } else {
                                //来自日历选择
                                type = "directly";
                            }
                            Log.i("getSongsList", "standardTime = " + standardTime);
                            //判断输入的时间是否远超当前时间（至少是后年）
                            String goalDay = MatchStandardization.getGoalDay(timeStr, standardTime, type);
                            if (goalDay.isEmpty()) {
                                Toasty.error(MainActivity.this, "您的输入远超当前时间，暂不支持", Toasty.LENGTH_SHORT).show();
                                return;
                            }
                            //正常，可发送于列表中显示
                            //获取title和标签
                            String[] displayArr = RegexMatches.getDisplay(sourceText, timeStr, standardTime);
                            String title = displayArr[0];
                            String label = displayArr[1];
                            //输入如：33年12月9日去吃饭，解析成：1933-12-09倒计时，这不妥，故警示
                            if (standardTime.contains("19") && label.equals("倒计时")) {
                                Toasty.error(MainActivity.this, "您的输入远超当前时间，暂不支持", Toasty.LENGTH_SHORT).show();
                                return;
                            }
                            //判断输入title列表中是否已经存在，已经存在便不重复输入
                            if (Reminder.containsTitle(reminderList, title)) {
                                Toasty.error(MainActivity.this, "该提醒已存在！", Toasty.LENGTH_SHORT).show();
                                return;
                            }
                            //return之后下面的代码就不会执行！
                            Reminder reminder = new Reminder(title, label, MyUtils.getDate(goalDay), type, timeStr);
                            //将数据存储到数据库
                            reminder.save();
                            reminderList.add(0, reminder);
                            Log.i("getSongsList", "reminderList = " + reminderList);
                            //新增刷新（放在第一位），这个方法默认会从list的position位取数据，放在position位
                            adapter.notifyItemInserted(0);
                            //改变刷新第二项（因为第二项之前是第一项，而第一项上面的分割线占位隐藏了，但现在它是第二项，第二项的分割线必须显示）
                            adapter.notifyItemChanged(1);
                            layoutManager.scrollToPosition(0);
                            //必须是目标日
                            String tip = label + "提醒设置成功！\n目标日：" + goalDay;
                            Toasty.success(MainActivity.this, tip, Toasty.LENGTH_LONG).show();
                            //清空文本
                            remindInput.setText("");
//                            } else {
//                                Log.i("getSongsList", "sourceText为null执行！");
//                                //刚开始未输入时，发送按钮禁止点击
//                                frBinding.imageSend.setClickable(false);
//                            }
                        });

                        //删除按钮点击监听
                        frBinding.imageDelete.setOnClickListener(v1 -> {
                            //直接这么干是没用的，必须对EditText操作，改变源头
//                            sourceText = "";
                            remindInput.setText("");
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
                        remindInput.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {}

                            @Override
                            public void afterTextChanged(Editable s) {
                                Log.i("getSongsList", "isTriggerTextChange = " + isTriggerTextChange);
                                //允许触发才能执行以下逻辑
                                if (isTriggerTextChange) {
                                    sourceText = s.toString();
                                    timeStr = MatchStandardization.getDeepMatchedStr(RegexMatches.TIME_REGEX, sourceText);
                                    Log.i("getSongsList", "timeStr = " + timeStr);

                                    //更新UI————————————————————————————————————————————————————————————
                                    if (!sourceText.isEmpty()) {
                                        //显示删除按钮
                                        frBinding.imageDelete.setVisibility(View.VISIBLE);
                                        //发送图标实化，变黑
                                        frBinding.imageSend.setImageResource(R.drawable.upload_black);
                                        //发送图标允许点击
                                        frBinding.imageSend.setClickable(true);
                                    } else {
                                        //隐藏删除按钮
                                        frBinding.imageDelete.setVisibility(View.GONE);
                                        //发送图标虚化，变灰
                                        frBinding.imageSend.setImageResource(R.drawable.upload_gray);
                                        //发送图标禁止点击
                                        frBinding.imageSend.setClickable(false);
                                        //光标显示，并移动到行首
                                        remindInput.setSelection(0);
                                    }
                                    //为匹配到的时间文本设置Span，以示区分
                                    if (!timeStr.isEmpty() && RegexMatches.getNewMatchedStr(oldSourceText, sourceText) != null) {
                                        setSpan();
                                        //设置了Span后更新旧的源文本
                                        oldSourceText = sourceText;
                                        Log.i("getSongsList", "oldSourceText = " + oldSourceText);
                                    }
                                    //数据处理———————————————————————————————————————————————————————————

                                    Log.i("getSongsList", "输入改变后的操作全部完成");
                                }
                            }
                        });

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
                //禁止触发文本改变
                isTriggerTextChange = false;
                //EditText移除Span，使用源文本
                remindInput.setText(sourceText);
                //显示撤销按钮，并延迟三秒后移除
                showRevokeShort();
                //允许触发文本改变
                isTriggerTextChange = true;
            }
        }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        //禁止触发文本改变
        isTriggerTextChange = false;
        //为EditText绑定span，并设置点击事件（必须）
        remindInput.setText(spannableStr, TextView.BufferType.SPANNABLE);
        //将光标移动到文本最后边，不能影响输入
        remindInput.setSelection(sourceText.length());
        remindInput.setMovementMethod(LinkMovementMethod.getInstance());//点击事件必须添加这一句
        //允许触发文本改变
        isTriggerTextChange = true;
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

    /**
     * 判断数据库中的目标日是否到期（当天或提前指定天数），如果到期就设置闹钟提醒，默认9：00
     * @param latestGoalDate 离现在最近的目标日
     * @return 闹铃提示文本；返回空串表示目标日当天或其前一天都没到期
     */
    private String setReminder(Date latestGoalDate) {
        Log.i("getSongsList", "setReminder执行");
        String content = "";
        String s1 = "";
        String s2 = "";
        int dueDays = MyUtils.dueAFewDaysEarly(latestGoalDate);
        //提前一天、当天才会执行
        //构建提醒字符串的第一部分
        if (dueDays == 1) {
            s1 = "明天是：";
        } else if (dueDays == 0) {
            s1 = "今天是：";
        }
        Calendar calendar = Calendar.getInstance();
        //今天是在今年的第几天
        int today = calendar.get(Calendar.DAY_OF_YEAR);
        Log.i("getSongsList", "today = " + today);
        //是新的一天就执行（默认值为-1，保证第一次一定能够执行）
        if (today != sp.getInt("today", -1)) {
            //第一次允许执行
            //以下代码，一天只能执行一次

            if (!LitePal.isExist(Reminder.class)) {
                //在没有添加提醒事项之前，直接退出
                return content;
            }
            //数据库中存在数据时才需要执行以下逻辑
            //通过目标日到数据库中获取该行其他列的数据
            List<Reminder> reminderList = LitePal.select("label", "title")//只查找label和title列
                    .where("goalDate = ?", latestGoalDate.getTime() + "")//找到值等于goalDate的那一行
                    .find(Reminder.class);
            //不出意外reminderList里面只有一个元素
            String label = reminderList.get(0).getLabel();
            if (label.equals("生日")) {
                s2 = "的生日";
            } else if (label.equals("纪念日")) {
                s2 = label;
            }
            String title = reminderList.get(0).getTitle();
            content = s1 + title + s2;
            // TODO: 2022/12/7 这里目前是默认9：00提醒，之后要根据当天的起床时间来设定提醒时间
            alarm.set("9:00", content);
            Toasty.success(this, "提醒设置成功", Toasty.LENGTH_SHORT).show();

            sp.edit().putInt("today", today).apply();
        }

        return content;
    }

    /**
     * 设置主页的今日提示
     */
    private void setHomeTipToday() {
        String text = "";
        if (content.isEmpty()) {
            Log.i("getSongsList", "content.isEmpty执行！");
            Calendar calendar = Calendar.getInstance();
            //今天是在今年的第几天
            int today2 = calendar.get(Calendar.DAY_OF_YEAR);
            //是新的一天就执行（默认值为-1，保证第一次一定能够执行）
            if (today2 != sp.getInt("today2", -1)) {
                //第一次允许执行
                //以下代码，一天只能执行一次

                //获取今天的节日、节气名（第一次执行要进行数据库操作，注意耗时和执行位置）
                String name = MyUtils.getFestivalOrTermName(this);
                Log.i("getSongsList", "name = " + name);
                if (name.isEmpty()) {
                    return;
                }
                text = "今天是：" + name;
                //有text才让它显现
                fhBinding.cardHome.setVisibility(View.VISIBLE);

                sp.edit().putInt("today2", today2).apply();
            }

        } else {
            text = content;
            fhBinding.cardHome.setVisibility(View.VISIBLE);
        }
        fhBinding.tipHome.setText(text);
    }

}