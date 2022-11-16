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
        fhBinding.imageHelp.setOnClickListener(v -> {
            Toast.makeText(context, "你点击了帮助图片", Toast.LENGTH_SHORT).show();
        });

        return fhBinding.getRoot();
    }
}