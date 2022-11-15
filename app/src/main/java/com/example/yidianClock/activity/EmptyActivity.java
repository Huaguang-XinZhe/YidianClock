package com.example.yidianClock.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.WindowManager;

import com.example.yidianClock.Flash;
import com.example.yidianClock.MyUtils;
import com.example.yidianClock.R;

public class EmptyActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        // TODO: 2022/11/9
        //下面这行代码必须放在setContentView()前边
//        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title
        //以下代码包括解锁（KeyguardManager.KeyguardLock）和PowerManager.SCREEN_DIM_WAKE_LOCK的作用
//        WindowManager.LayoutParams winParams = this.getWindow().getAttributes();
//        winParams.flags |= (WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
//                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
////                | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
//                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

//        以下代码包括KeyguardManager.KeyguardLock和PowerManager.SCREEN_DIM_WAKE_LOCK的作用（不会解锁）
        this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);

        Log.i("getSongsList", "onCreate执行！");

        //保持CPU持续运转
        MyUtils.getInstance(this).wakeUp(2);

        //在这里进行震动和闪光提示
        //震动一次
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
//        long[] pattern = {1500L, 1500L};//静、震
//        if (vibrator.hasVibrator()) {
//            Log.i("getSongsList", "有震动器，开始震动！");
//            vibrator.vibrate(1000);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.EFFECT_TICK),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
        } else {
            vibrator.vibrate(1000);
        }

        //闪光三次
        new Flash(this).openFlicker(3);

    }

}