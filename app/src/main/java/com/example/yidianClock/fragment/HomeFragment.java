package com.example.yidianClock.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.yidianClock.activity.MainActivity;
import com.example.yidianClock.alarm.YDAlarm;
import com.example.yidianClock.databinding.FragmentHomeBinding;

public class HomeFragment extends Fragment {
    Context context;
    FragmentHomeBinding fhBinding;

    public HomeFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        fhBinding = FragmentHomeBinding.inflate(inflater, container, false);
//        return inflater.inflate(R.layout.fragment_home, container, false);
        //主页图片点击监听
        fhBinding.imageAlarmList.setOnClickListener(v -> {
            //跳转到系统闹钟列表
            YDAlarm.showAlarm(context);
        });

        return fhBinding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        //获取MainActivity的引用
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            //为activity传入相关引用
            activity.fhBinding = fhBinding;
        }
    }
}