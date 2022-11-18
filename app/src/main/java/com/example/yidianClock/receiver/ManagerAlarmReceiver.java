package com.example.yidianClock.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.yidianClock.utils.Flash;

public class ManagerAlarmReceiver extends BroadcastReceiver {

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("getSongsList", "收到广播！");

//        Intent arouseIntent = new Intent(context, EmptyActivity.class);
//        arouseIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(arouseIntent);

        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.EFFECT_TICK),
                    new AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_ALARM)
                            .build());
        } else {
            vibrator.vibrate(500);
        }

        //闪光三次
        new Flash(context).openFlicker(3);

    }
}