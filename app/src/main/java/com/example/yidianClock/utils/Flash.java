package com.example.yidianClock.utils;

import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;

public class Flash {
    private final CameraManager manager;
    private Camera mCamera;
    private final Context context;
    private boolean status = false;     // 记录手机状态

    public Flash(Context context) {
        this.context = context;
        manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    /**
     * 打开闪光灯，闪烁指定次数
     * @param numberFlashes 闪烁的次数
     */
    public void openFlicker(int numberFlashes) {
        new Thread(() -> {
            for (int i = 0; i < numberFlashes; i++) {
                open();

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                close();

                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    // 打开手电筒
    public void open() {
        if (status) {   //如果是打开状态,不需要打开
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                manager.setTorchMode("0",true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            //支持闪光灯就打开
            PackageManager packageManager = context.getPackageManager();
            FeatureInfo[] features = packageManager.getSystemAvailableFeatures();
            for (FeatureInfo featureInfo : features) {
                // 判断设备是否支持闪光灯
                if(PackageManager.FEATURE_CAMERA_FLASH.equals(featureInfo.name)) {
                    mCamera = Camera.open();
                    Camera.Parameters parameters = mCamera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    mCamera.setParameters(parameters);
                    mCamera.startPreview();
                }
            }
        }
        status = true;      // 记录手电筒状态为打开
    }


    // 关闭手电筒
    public void close() {
        if (!status) {// 如果是已经关闭的状态 不需要再关闭
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                manager.setTorchMode("0",false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        } else {
            if (mCamera != null) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
        }
        status = false; // 记录手电筒为关闭
    }

}
