package com.example.yidianClock.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.SimpleItemTouchHelperCallback;
import com.example.yidianClock.activity.MainActivity;
import com.example.yidianClock.adapter.ReminderAdapter;
import com.example.yidianClock.databinding.FragmentReminderdayBinding;
import com.example.yidianClock.model.Reminder;
import com.example.yidianClock.utils.MyUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReminderDayFragment extends Fragment {
    Context context;
    FragmentReminderdayBinding frBinding;
    ReminderAdapter adapter;
    RecyclerView.LayoutManager layoutManager;
    List<Reminder> reminderList = new ArrayList<>();

    public ReminderDayFragment(Context context) {
        this.context = context;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        frBinding = FragmentReminderdayBinding.inflate(inflater, container, false);

        //reminderList数据，从数据库中取值（全部），必须在这里取值，因为reminderList的引用是这里传过去的
        //刚开始还没输入任何数据的时候，reminder表是不存在的，存储数据后才存在，故在此判断，防止崩溃
        if (LitePal.isExist(Reminder.class)) {
            //获取离现在最近的目标日
            Date latestGoalDate = MyUtils.getTheLatestGoalDate();
            //在最近目标日之后（目标日当天计算出来的目标日依然是当天，故往后推）执行更新操作
            //如果条件满足，执行完更新后在排序查找，这样能保证排序始终正确
            if (new Date().after(latestGoalDate)) {
                Log.i("getSongsList", "在最近目标日之后，更新执行！");
                MyUtils.updateGoalDate(latestGoalDate);
            }
            Log.i("getSongsList", "从数据库中取提醒日数据，执行！");
            //这里的查询方法是从原列表的末端往前端查（相当于列表顺序颠倒了）
            //按第一次记录时的目标日正序排列（即靠近今天的目标日放在前边）
            reminderList = LitePal.order("goalDate asc").find(Reminder.class);
            //往list的尾部加一个对象，占位，以显示尾部布局
            reminderList.add(new Reminder());
        }
        Log.i("getSongsList", "OnCreateView: reminderList = " + reminderList);
        //创建Adapter
        adapter = new ReminderAdapter(context, reminderList);
        Log.i("getSongsList", "OnCreateView: adapter = " + adapter);
        //创建布局管理器
        layoutManager = new LinearLayoutManager(context);

        RecyclerView recyclerView = frBinding.recyclerViewReminderDay;
        //通过回调实现创建ItemHelper对象
        ItemTouchHelper helper = new ItemTouchHelper(new SimpleItemTouchHelperCallback(adapter));
        //绑定RecyclerView
        helper.attachToRecyclerView(recyclerView);
        //必须先设置布局管理器，要不然不会显示
        recyclerView.setLayoutManager(layoutManager);
        //在Fragment中必须在这里设置
        recyclerView.setAdapter(adapter);
        //初始化提醒日列表UI
        adapter.notifyDataSetChanged();

        //recyclerView滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutCompat layoutInput = frBinding.layoutInput;
                //上滑为正，下滑为负
                if (Math.abs(dy) > 20) {
                    //隐藏软键盘————————————————————————————————————————————————————————————
                    MyUtils.getInstance(context).hideSoftInput(layoutInput);

                    //软键盘还在，故调高尾部布局的高度，避免遮挡——————————————————————————————————
                    if (layoutInput.getVisibility() == View.GONE) {
                        //不可见不作处理
                        return;
                    }
                    //可见才执行以下逻辑
                    adapter.setOnListener((holder, layoutParams) -> {
                        layoutParams.height = 100*3;
                        holder.itemView.setLayoutParams(layoutParams);
                        Log.i("getSongsList", "底部布局已调高！");
                    });
                    //通知UI更新，这样才会执行实现
                    adapter.notifyItemChanged(reminderList.size()-1);
                }
            }
        });

        return frBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("getSongsList", "fragment：onStart 执行！");
        //onStart的时候Activity已经启动完成，可以传入binding了
        MainActivity activity = (MainActivity) getActivity();
        Log.i("getSongsList", "fragment：activity = " + activity);
        if (activity != null) {
            //以下是将fragment中的值初始化后传给宿主Activity
            //只要在fragment的onStart之前不使用以下变量，就能正常运行，不为null
            activity.frBinding = frBinding;
            activity.adapter = adapter;
            activity.reminderList = reminderList;
            activity.layoutManager = layoutManager;
        }
    }

//    private void initData() {
//        Reminder reminder1 = new Reminder("我自己", "生日", "2001-11-09");
//        Reminder reminder2 = new Reminder("老妈", "生日", "1977-04-09");
//        Reminder reminder3 = new Reminder("师父", "生日", "1981-03-24");
//        Reminder reminder4 = new Reminder("强哥", "生日", "1998-10-07");
//        Reminder reminder5 = new Reminder("母亲节", "节日", "2023-05-08");
//        Reminder reminder6 = new Reminder("结婚 3 周年", "纪念日", "2022-12-19");
//        Reminder reminder7 = new Reminder("高考", "倒计时", "2023-06-07");
//        Reminder reminder8 = new Reminder("期末考试", "倒计时", "2022-12-30");
//        Reminder reminder9 = new Reminder("其他", "未知", "2011-11-12");
//        Reminder reminder10 = new Reminder("小满", "节气", "2022-12-11");
//        reminderList.add(reminder1);
//        reminderList.add(reminder2);
//        reminderList.add(reminder3);
//        reminderList.add(reminder4);
//        reminderList.add(reminder5);
//        reminderList.add(reminder6);
//        reminderList.add(reminder7);
//        reminderList.add(reminder8);
//        reminderList.add(reminder9);
//        reminderList.add(reminder10);
//    }

}
