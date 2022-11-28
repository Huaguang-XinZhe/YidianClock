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

import com.example.yidianClock.activity.MainActivity;
import com.example.yidianClock.databinding.FragmentReminderdayBinding;

public class ReminderDayFragment extends Fragment {
    Context context;
    FragmentReminderdayBinding frBinding;
//    OnListener onListener;
//
//    /**
//     * 内设接口，与Activity交互
//     */
//    public interface OnListener {
//        //把binding引用传出
//        void onExecute(FragmentReminderdayBinding binding);
//    }
//
//    public void setOnListener(OnListener onListener) {
//        this.onListener = onListener;
//    }

    public ReminderDayFragment(Context context) {
        this.context = context;
    }

    //onAttach先于onCreateView执行
//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        //下面两种方法获得的MainActivity对象都是一样的
//        MainActivity activity = (MainActivity) context;
////        MainActivity activity1 = (MainActivity) getActivity();
////        Log.i("getSongsList", "activity = " + activity);
////        Log.i("getSongsList", "activity1 = " + activity1);
//        Log.i("getSongsList", "frBinding（在Fragment里） = " + frBinding);
//        //为宿主Activity提供binding引用
//        activity.frBinding = frBinding;
//        //如果宿主Activity实现了InteractionCallBack接口，那么就此赋值，否则，抛出异常
////        if (context instanceof InteractionCallBack) {
////            Log.i("getSongsList", "实现了接口，赋值执行！");
////            callBack = (InteractionCallBack) context;
////            //将binding传出
////            callBack.process(frBinding);
////        } else {
////            throw new IllegalArgumentException("activity must implements FragmentInteraction");
////        }
//    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        frBinding = FragmentReminderdayBinding.inflate(inflater, container, false);
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

    @Override
    public void onDetach() {
        super.onDetach();
        //释放传进来的Activity
//        callBack = null;
    }

}
