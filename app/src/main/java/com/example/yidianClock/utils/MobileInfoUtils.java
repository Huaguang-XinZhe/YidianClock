package com.example.yidianClock.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.RequiresApi;

public class MobileInfoUtils {
    Context context;
//    private SettingDialogPermision dialog_per;


    public MobileInfoUtils(Context context) {
        this.context = context;
    }

    //获取手机类型
    private String getMobileType() {
        return Build.MANUFACTURER;
    }

    //跳转至授权页面
    public void jumpStartInterface() {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri myAppUri = Uri.fromParts("package", context.getPackageName(), null);
        try {
            Log.i("getSongsList", "******************当前手机型号为：" + getMobileType());
            ComponentName componentName = null;
            switch (getMobileType()) {
                case "Xiaomi":  // 红米Note4测试通过
                    componentName = new ComponentName("com.miui.securitycenter",
                            "com.miui.permcenter.autostart.AutoStartManagementActivity");
                    break;
                case "Letv":  // 乐视2测试通过
                    intent.setAction("com.letv.android.permissionautoboot");
                    break;
                case "samsung":  // 三星Note5测试通过
                    //componentName = new ComponentName("com.samsung.android.sm_cn", "com.samsung.android.sm.ui.ram.AutoRunActivity");
                    //componentName = ComponentName.unflattenFromString("com.samsung.android.sm/.ui.ram.RamActivity");// Permission Denial not exported from uid 1000，不允许被其他程序调用
                    componentName = ComponentName.unflattenFromString(
                            "com.samsung.android.sm/.app.dashboard.SmartManagerDashBoardActivity");
                    break;
                case "HUAWEI":  // 华为测试通过
                    //componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity");//锁屏清理
                    componentName = ComponentName.unflattenFromString(
                            "com.huawei.systemmanager/.startupmgr.ui.StartupNormalAppListActivity");//跳自启动管理

                    //SettingOverlayView.show(context);
                    break;
                case "vivo":  // VIVO测试通过
                    componentName = ComponentName.unflattenFromString(
                            "com.iqoo.secure/.safeguard.PurviewTabActivity");
                    break;
                case "Meizu":  //万恶的魅族
                    //componentName = ComponentName.unflattenFromString("com.meizu.safe/.permission.PermissionMainActivity");//跳转到手机管家
                    componentName = ComponentName.unflattenFromString(
                            "com.meizu.safe/.permission.SmartBGActivity");//跳转到后台管理页面

                    break;
                case "OPPO":
//                    componentName = ComponentName.unflattenFromString("com.oplus.battery/com.oplus.powermanager.fuelgaue.PowerControlActivity");
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(myAppUri);
                    break;
                case "ulong":  // 360手机 未测试
                    componentName = new ComponentName("com.yulong.android.coolsafe",
                            ".ui.activity.autorun.AutoRunListActivity");
                    break;
                default:
                    // 将用户引导到系统设置页面
                    Log.e("getSongsList", "APPLICATION_DETAILS_SETTINGS");
                    intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                    intent.setData(Uri.fromParts("package", context.getPackageName(), null));
                    break;
            }

            if (componentName != null) {
                intent.setComponent(componentName);
            }
            context.startActivity(intent);

//            if (getMobileType().equals("Xiaomi")) {
//                showtip();//显示弹窗（**特别注意**）
//            }
//            if (getMobileType().equals("samsung")){
//                new SettingOverlayView().show(context);//显示悬浮窗
//            }

        } catch (Exception e) {//抛出异常就直接应用详情界面
            Log.e("getSongsList", e.getLocalizedMessage());
            //此处必须用全新的intent，否则还有可能会抛出异常
            Intent mIntent = new Intent(Settings.ACTION_SETTINGS);
            context.startActivity(mIntent);
        }
    }

//    //小米手机显示弹窗
//    private void showtip() {
//        try {
//            dialog_per=new SettingDialogPermision(context, R.style.CustomDialog4);
//            dialog_per.getWindow().setType(WindowManager.LayoutParams.TYPE_TOAST);//注意这里改成吐司类型
//            dialog_per.show();
//            Log.e("HLQ_Struggle","显示弹窗");
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("HLQ_Struggle", "没有显示弹窗"+e.getMessage());
//        }
//    }
}
