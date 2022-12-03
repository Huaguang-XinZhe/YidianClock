package com.example.yidianClock.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.activity.MainActivity;
import com.example.yidianClock.adapter.ReminderAdapter;
import com.example.yidianClock.databinding.FragmentReminderdayBinding;
import com.example.yidianClock.model.Reminder;

import java.util.ArrayList;
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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        frBinding = FragmentReminderdayBinding.inflate(inflater, container, false);

        //创建Adapter
        adapter = new ReminderAdapter(context, reminderList);
        //创建布局管理器
        layoutManager = new LinearLayoutManager(context);
        Log.i("getSongsList", "adapter和layoutManager创建");

        RecyclerView recyclerView = frBinding.recyclerViewReminderDay;
        //必须先设置布局管理器，要不然不会显示
        recyclerView.setLayoutManager(layoutManager);
        //在Fragment中必须在这里设置
        recyclerView.setAdapter(adapter);
        Log.i("getSongsList", "布局管理器和adapter设置");

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
