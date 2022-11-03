package com.example.yidianClock.activity;

import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.MyAdapter;
import com.example.yidianClock.MyPicker;
import com.example.yidianClock.MyUtils;
import com.example.yidianClock.databinding.ActivitySettingBinding;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.SleepAlarm;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import javax.security.auth.login.LoginException;

public class SettingActivity extends AppCompatActivity {
    private ActivitySettingBinding settingBinding;
    private MyPicker picker;
    private MyUtils utils;
    private final ContentValues values = new ContentValues();
    
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

        //以下代码在recyclerView绑定加载的时候会加载，所以，调用方法的对象在一开始就不能为空————————————————————————
        adapter.setOnListener((holder, position) -> {
            utils = new MyUtils(this);
            boolean isNight = holder.restType.getText().equals("晚睡");

            //蓝色更多，点击展开或收回
            holder.moreSetView.setOnClickListener(v -> {
                int vis;
                if (holder.moreSetLayout.getVisibility() == View.GONE) {
                    vis = View.VISIBLE;
                    //展开，收起软键盘
                    utils.hideSoftInput(holder.itemView);
                    //必须让它再执行一次状态改变的实现（实现不了，isChecked为true的话也应该显示）
                    if (holder.isSetShockButton.isChecked()) {
                        holder.shockSetLayout.setVisibility(View.VISIBLE);
                    }
                    //如果是展开晚睡的蓝色更多，则使recyclerView上移，底部可见
                    if (isNight) {
                        layoutManager.scrollToPositionWithOffset(1, -200);
                    }
                } else {
                    //只可能是visible
                    vis = View.GONE;
                }
                holder.moreSetLayout.setVisibility(vis);
            });

            //防息损震动提示，开启展开，关闭收缩（只有状态改变的时候才会执行）
            holder.isSetShockButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int vis;
                if (isChecked) {
                    vis = View.VISIBLE;
                    //展开，收起软键盘
                    utils.hideSoftInput(holder.itemView);
                    //如果是展开晚睡的震动提示，则使recyclerView上移，底部可见
                    if (isNight) {
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
                //TimePicker点击确认
                picker.setOnConfirm(() -> {
                    //更新TextView的值
                    holder.potView.setText(picker.getPOT());
                    //更新数据库中对应的数据
                    String[] potArr = picker.getPOT().split(" ~ ");
                    values.put("timeStart", potArr[0]);
                    values.put("timeEnd", potArr[1]);
                    updateData(holder, values);
                    Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
                });
            });


            //点击闹钟任务布局块，开启或关闭，并更新数据库中的数据
            holder.alarmTaskLayout.setOnClickListener(v -> {
                //改变ToggleButton的状态
                boolean isTaskChecked = holder.isSetTaskButton.isChecked();
                holder.isSetTaskButton.setChecked(!isTaskChecked);
                //更新值
                updateChecked(holder, holder.itemSB.isTaskSetButton, "isSetTask", false);
            });
            //点击闹钟任务ToggleButton，同样更新数据库的数据
            holder.itemSB.isTaskSetButton.setOnClickListener(v -> {
                updateChecked(holder, holder.itemSB.isTaskSetButton, "isSetTask", true);
            });


            //点击震动提示布局块，开启或关闭，并更新数据库中的数据
            holder.shockTipLayout.setOnClickListener(v -> {
                //改变ToggleButton的状态
                boolean isShockTipChecked = holder.isSetShockButton.isChecked();
                holder.isSetShockButton.setChecked(!isShockTipChecked);
                //更新值
                updateChecked(holder, holder.itemSB.isShockTipSetButton, "isSetShockTip", false);
            });
            //点击震动提示ToggleButton，更新数据库中的数据
            // ToggleButton点击后，其checked状态会立即改变，在其点击事件中获取到的是改变后的状态
            holder.itemSB.isShockTipSetButton.setOnClickListener(v -> {
                updateChecked(holder, holder.itemSB.isShockTipSetButton, "isSetShockTip", true);
            });


            //restTime失去焦点后更新数据库
            updateText(holder, holder.itemSB.restTimeEdit, "restTime");

            //alarmContent失去焦点后更新数据库
            updateText(holder, holder.itemSB.alarmContentEdit, "content");

            //shockInterval失去焦点后更新数据库
            updateText(holder, holder.itemSB.shockIntervalEdit, "shockInterval");

            //recyclerView的滑动监听
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //recyclerView在滑动
                    if (newState != 0) {
                        //这三个EditText，任一存在焦点，在滑动时就是它们安全失去
                        loseFocus(holder, holder.restTimeEdit);
                        loseFocus(holder, holder.alarmContentEdit);
                        loseFocus(holder, holder.shockIntervalEdit);
                    }
                }
            });

        });

        //——————————————————————————from MyAdapter——————————————————————————————————————————————————

    }

    /**
     * 使EditText失去焦点，并隐藏软键盘
     */
    private void loseFocus(MyAdapter.InnerHolder holder, EditText editText) {
        boolean hasFocus = editText.hasFocus();
        if (hasFocus) {
            editText.clearFocus();
            //如果写下下面这行代码，那么在失去焦点之后，马上又会获取到焦点（在界面上看起来就好像没有变一样）
//            holder.itemView.requestFocus();
            //隐藏软键盘
            utils.hideSoftInput(holder.itemView);
        }
    }

    /**
     *更新数据库中的checked型数据
     * @param isChanged ToggleButton的状态是否已经改变
     */
    private void updateChecked(MyAdapter.InnerHolder holder,
                                  ToggleButton button, String key, boolean isChanged) {
        boolean isChecked = button.isChecked();
        //更新数据库中对应的数据
        if (isChanged) {
            //针对ToggleButton的直接点击
            values.put(key, isChecked);
        } else {
            //针对块区的点击
            values.put(key, !isChecked);
        }
        updateData(holder, values);
        Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 监听EditText的焦点改变事件，当EditText失去焦点后核验text值，有异的话就更新数据库中相应的值
     * @param holder 所需引用
     * @param editText EditText
     * @param key 更新的key
     */
    private void updateText(MyAdapter.InnerHolder holder, EditText editText, String key) {
        //不能放在里边，里边都是已经改变过了的
        String valueBefore = editText.getText().toString();
        Log.i("Checked", "valueBefore = " + valueBefore);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Toast.makeText(this, "EditText 失去焦点！", Toast.LENGTH_SHORT).show();
                //在丧失焦点后在获取一次text值
                String value = editText.getText().toString();
                Log.i("Checked", "value = " + value);
                //不一样的话就更新数据库中相应的值
                if (!value.equals(valueBefore)) {
                    Log.i("Checked", "不一样，更新数据！");
                    values.put(key, value);
                    updateData(holder, values);
                    Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 根据类型获取数据模型对象
     * @param holder MyAdapter.InnerHolder对象，要通过它来获取类型
     * @return 返回LitePalSupport的子类，即LunchAlarm类或SleepAlarm类
     */
    public LitePalSupport getModel(MyAdapter.InnerHolder holder) {
        boolean isNight = holder.restType.getText().equals("晚睡");
        LitePalSupport support;
        if (isNight) {
            support = new SleepAlarm();
        } else {
            support = new LunchAlarm();
        }
        return support;
    }

    /**
     * 更新数据库中相应的数据
     */
    private void updateData(MyAdapter.InnerHolder holder, ContentValues values) {
        LitePal.update(getModel(holder).getClass(), values, 1);
    }
}