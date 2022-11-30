package com.example.yidianClock.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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

    public ReminderDayFragment(Context context) {
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        List<Reminder> reminderList = new ArrayList<>();

        //自备数据
        Reminder reminder1 = new Reminder("我自己", "生日", "2001-11-09");
        Reminder reminder2 = new Reminder("老妈", "生日", "1977-04-09");
        Reminder reminder3 = new Reminder("师父", "生日", "1981-03-24");
        Reminder reminder4 = new Reminder("强哥", "生日", "1998-10-07");
        Reminder reminder5 = new Reminder("母亲节", "节日", "2023-05-08");
        Reminder reminder6 = new Reminder("结婚 3 周年", "纪念日", "2022-12-19");
        Reminder reminder7 = new Reminder("高考", "倒计时", "2023-06-07");
        Reminder reminder8 = new Reminder("期末考试", "倒计时", "2022-12-30");
        reminderList.add(reminder1);
        reminderList.add(reminder2);
        reminderList.add(reminder3);
        reminderList.add(reminder4);
        reminderList.add(reminder5);
        reminderList.add(reminder6);
        reminderList.add(reminder7);
        reminderList.add(reminder8);
        //不能加入空 item，必须在底部填充一个不同类型的 item
//        //加入空 item
//        reminderList.add(new Reminder("", "", ""));
        //创建Adapter
        adapter = new ReminderAdapter(context, reminderList);
        //创建布局管理器
        layoutManager = new LinearLayoutManager(context);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        frBinding = FragmentReminderdayBinding.inflate(inflater, container, false);

        RecyclerView recyclerView = frBinding.recyclerViewReminderDay;
        //必须先设置布局管理器，要不然不会显示
        recyclerView.setLayoutManager(layoutManager);
        //在Fragment中必须在这里设置
        recyclerView.setAdapter(adapter);

        return frBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        //onStart的时候Activity已经启动完成，可以传入binding了
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.frBinding = frBinding;
        }
    }

}
