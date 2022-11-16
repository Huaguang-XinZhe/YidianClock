package com.example.yidianClock.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.yidianClock.databinding.FragmentReminderdayBinding;

public class ReminderDayFragment extends Fragment {
    Context context;
    FragmentReminderdayBinding frBinding;

    public ReminderDayFragment(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        frBinding = FragmentReminderdayBinding.inflate(inflater, container, false);

        frBinding.imageShock.setOnClickListener(v -> {
            Toast.makeText(context, "你点击了震动图片", Toast.LENGTH_SHORT).show();
        });


        return frBinding.getRoot();
    }
}
