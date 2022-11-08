package com.example.yidianClock.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.MyHashMap;
import com.example.yidianClock.MyPeriodPicker;
import com.example.yidianClock.MyPicker;
import com.example.yidianClock.MyUtils;
import com.example.yidianClock.R;
import com.example.yidianClock.adapter.SettingAdapter;
import com.example.yidianClock.databinding.ActivitySettingBinding;
import com.example.yidianClock.databinding.DialogRingSelectedBinding;
import com.example.yidianClock.model.LunchAlarm;
import com.example.yidianClock.model.MyAlarm;
import com.example.yidianClock.model.SleepAlarm;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.permissionx.guolindev.PermissionX;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.Map;

public class SettingActivity extends AppCompatActivity {
    private MyPeriodPicker picker;
    private MyUtils utils;
    String currentRingTitle;
    Uri currentRingUri;
    Ringtone currentRingtone;
    DialogRingSelectedBinding dialogBinding;
    private final ContentValues values = new ContentValues();
    SharedPreferences sp;
    LinearLayoutManager layoutManager;
    
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingBinding settingBinding = ActivitySettingBinding.inflate(getLayoutInflater());
        setContentView(settingBinding.getRoot());

        RecyclerView recyclerView = settingBinding.settingRv;

        //sp初始化，不能放在实例变量处初始化，会引发空指针异常（可能是context当时还为空）
        sp = getSharedPreferences("sp", MODE_PRIVATE);
        //当前铃声Ringtone的Map（获取Ringtone单例的手段）
        Map<Uri, Ringtone> ringtoneMap = new MyHashMap<>();

        //创建并设置布局管理器
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //创建并设置adapter
        SettingAdapter adapter = new SettingAdapter(this);
        recyclerView.setAdapter(adapter);

        //ActivityResultLauncher对象创建，并实现点击本地铃声或系统铃声的回调监听
        ActivityResultLauncher<Intent> mARLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), this::parseResult);

        //以下代码在recyclerView绑定加载的时候会加载，所以，调用方法的对象在一开始就不能为空————————————————————————
        adapter.setOnListener((holder, position) -> {
            utils = new MyUtils(this);
            boolean isNight = holder.restType.getText().equals("晚睡");

            //蓝色更多，点击展开或收回
            holder.moreSetView.setOnClickListener(v -> {
                int vis;
                if (holder.moreSetLayout.getVisibility() == View.GONE) {
                    vis = View.VISIBLE;
                    //展开，收起软键盘
                    utils.hideSoftInput(holder.itemView);
                    //必须让它再执行一次状态改变的实现（实现不了，isChecked为true的话也应该显示）
                    if (holder.isSetShockButton.isChecked()) {
                        holder.itemSB.shockInterValSetLayout.setVisibility(View.VISIBLE);
                    }
                    //如果是展开晚睡的蓝色更多，则使recyclerView上移，底部可见
                    if (isNight) {
                        layoutManager.scrollToPositionWithOffset(1, -200);
                    }
                } else {
                    //只可能是visible
                    vis = View.GONE;
                }
                holder.moreSetLayout.setVisibility(vis);
            });

            //防息损震动提示，开启展开，关闭收缩（只有状态改变的时候才会执行）
            holder.isSetShockButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int vis;
                if (isChecked) {
                    vis = View.VISIBLE;
                    //展开，收起软键盘
                    utils.hideSoftInput(holder.itemView);
                    //如果是展开晚睡的震动提示，则使recyclerView上移，底部可见
                    if (isNight) {
                        //下面两种代码对上移都是有用的，没有这两行代码就不会上移
//                        Objects.requireNonNull(recyclerView.getLayoutManager()).scrollToPosition(1);
//                        recyclerView.scrollToPosition(1);
                        //只有下面这行代码能实现最终的效果，注意：这里将offset设为0不能拉到最底边！故设为负值
                        layoutManager.scrollToPositionWithOffset(1, -100);
                    }
                } else {
                    vis = View.GONE;
                }
                holder.itemSB.shockInterValSetLayout.setVisibility(vis);
            });

            //点击一般时段布局块，弹出时段选择器
            holder.potLayout.setOnClickListener(v -> {
                picker = new MyPeriodPicker(this, isNight);
                picker.setAndShow();
                //TimePicker点击确认
                picker.setOnConfirm(() -> {
                    //更新TextView的值
                    holder.potView.setText(picker.getPOT());
                    //更新数据库中对应的数据
                    String[] potArr = picker.getPOT().split(" ~ ");
                    values.put("timeStart", potArr[0]);
                    values.put("timeEnd", potArr[1]);
                    updateData(holder, values);
                    Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
                });
            });


            //点击闹钟任务布局块，开启或关闭，并更新数据库中的数据
            holder.alarmTaskLayout.setOnClickListener(v -> {
                //改变ToggleButton的状态
                boolean isTaskChecked = holder.isSetTaskButton.isChecked();
                holder.isSetTaskButton.setChecked(!isTaskChecked);
                //更新值
                updateChecked(holder, holder.itemSB.isTaskSetButton, "isSetTask", false);
            });
            //点击闹钟任务ToggleButton，同样更新数据库的数据
            holder.itemSB.isTaskSetButton.setOnClickListener(v -> {
                updateChecked(holder, holder.itemSB.isTaskSetButton, "isSetTask", true);
            });


            //点击震动提示布局块，开启或关闭，并更新数据库中的数据
            holder.shockTipLayout.setOnClickListener(v -> {
                //改变ToggleButton的状态
                boolean isShockTipChecked = holder.isSetShockButton.isChecked();
                holder.isSetShockButton.setChecked(!isShockTipChecked);
                //更新值
                updateChecked(holder, holder.itemSB.isShockTipSetButton, "isSetShockTip", false);
            });
            //点击震动提示ToggleButton，更新数据库中的数据
            // ToggleButton点击后，其checked状态会立即改变，在其点击事件中获取到的是改变后的状态
            holder.itemSB.isShockTipSetButton.setOnClickListener(v -> {
                updateChecked(holder, holder.itemSB.isShockTipSetButton, "isSetShockTip", true);
            });


            //点击几点前不响铃布局块，开启或关闭
            // TODO: 2022/11/9
            holder.itemSB.noRingBeforeLayout.setOnClickListener(v -> {
                boolean isChecked = holder.itemSB.noRingBeforeButton.isChecked();
                holder.itemSB.noRingBeforeButton.setChecked(!isChecked);
            });
            //点击几点前不响铃的RadioButton
            holder.itemSB.noRingBeforeButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    //开启状态
                    //启动时间选择器
                    MyPicker myPicker = new MyPicker(this);
                    myPicker.setAndShow();

                    //TimePicker点击确认
                    myPicker.setOnConfirm(() -> {
                        //使RadioButton开启
                        holder.itemSB.noRingBeforeButton.setChecked(true);
                        //替换titleBelow的值
                        holder.itemSB.titleBellowTV.setText(myPicker.getTime());
                        holder.itemSB.titleBellowTV.setTextColor(getResources().getColor(R.color.green_set_value));
                        //更新到数据库
                        values.put("beforeTimeStr", myPicker.getTime());
                        updateData(holder, values);
                        Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
                    });

                    //TimePicker点击取消
                    myPicker.setOnCancel(() -> closeOrCancel(holder));
                } else {
                    closeOrCancel(holder);
                }
            });


            //restTime失去焦点后更新数据库
            updateText(holder, holder.itemSB.restTimeEdit, "restTime");

            //alarmContent失去焦点后更新数据库
            updateText(holder, holder.itemSB.alarmContentEdit, "content");

            //shockInterval失去焦点后更新数据库
            updateText(holder, holder.itemSB.shockIntervalEdit, "shockInterval");

            //recyclerView的滑动监听
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    //recyclerView在滑动
                    if (newState != 0) {
                        //这三个EditText，任一存在焦点，在滑动时就是它们安全失去
                        loseFocus(holder, holder.restTimeEdit);
                        loseFocus(holder, holder.alarmContentEdit);
                        loseFocus(holder, holder.shockIntervalEdit);
                    }
                }
            });

            //铃声图像点击监听
            holder.bellImage.setOnClickListener(v -> {
                //创建BottomSheetDialog，并将布局加载到其中
                BottomSheetDialog dialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                dialogBinding = DialogRingSelectedBinding.inflate(getLayoutInflater());
                dialog.setContentView(dialogBinding.getRoot());

                //在显示之前预先从数据库取值，设置当前铃声的名称（uri取不到值的话就设为本机默认的闹钟铃声uri）
                getCurrentRingData(position == 1);
                dialogBinding.currentRingtoneTV.setText(currentRingTitle);

                //解决BottomSheetDialog设置圆角后带来的底部导航栏透明问题
//                dialog.getWindow().setNavigationBarColor(getResources().getColor(R.color.white));

                //显示
                dialog.show();

                //请求存储空间权限（READ_EXTERNAL_STORAGE）
                requestPermission();

                //本地铃声的点击监听，跳转到AlarmRingActivity
                dialogBinding.localRingtoneTv.setOnClickListener(v1 -> {
                    //如果当前铃声在播放，停止它然后再执行本项目的操作
                    if (currentRingtone != null && currentRingtone.isPlaying()) {
                        currentRingtone.stop();
                    }
                    Intent intent = new Intent(this, AlarmRingActivity.class);
                    intent.putExtra("title", "本地铃声");
                    mARLauncher.launch(intent);
                    Toast.makeText(this, "本铃声戴耳机也会外放，请关注音量", Toast.LENGTH_SHORT).show();
                });

                //系统铃声的点击监听
                dialogBinding.systemRingtoneTv.setOnClickListener(v1 -> {
                    //如果当前铃声在播放，停止它然后再执行本项目的操作
                    if (currentRingtone != null && currentRingtone.isPlaying()) {
                        currentRingtone.stop();
                    }
                    Intent intent = new Intent(this, AlarmRingActivity.class);
                    intent.putExtra("title", "系统铃声");
                    mARLauncher.launch(intent);
                    Toast.makeText(this, "此处厂商似乎做了限制，打开略有延迟", Toast.LENGTH_SHORT).show();
                    Toast.makeText(this, "本铃声戴耳机也会外放，请关注音量", Toast.LENGTH_SHORT).show();
//                    //下面这段代码能直接跳转到系统铃声选择界面（包括本地音乐和在线铃声）
//                    Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
//                    //加了这句还是有用的，虽然跳转到的界面都一样，但调节音量的时候会显示闹钟图标（表明这是闹钟渠道，由闹钟渠道控制）
//                    intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALARM);
//                    startActivity(intent);
                });

                //当前铃声布局块的点击监听，播放、停止铃声
                dialogBinding.currentRingtoneLayout.setOnClickListener(v1 -> {
                    if (!dialogBinding.currentRingtoneTV.getText().equals("无")) {
                        //由于点击铃声图标的时候就从sp中取currentRingUri的值（没有就默认系统闹钟uri），所以不可能为空
                        ringtoneMap.put(currentRingUri, RingtoneManager.getRingtone(this, currentRingUri));
                        Log.i("getSongsList", "ringtoneMap = " + ringtoneMap);
                        currentRingtone = ringtoneMap.get(currentRingUri);
                        Log.i("getSongsList", "currentRingtone = " + currentRingtone);
                        assert currentRingtone != null;
//                        //使用闹钟渠道来控制音量。注意！这行代码竟然会影响下面if-else块的执行！会是if部分无法执行，直接跳到else块！！！！
//                        MyUtils.setAlarmControl(SettingActivity.this, currentRingtone);
                        if (currentRingtone.isPlaying()) {
                            Log.i("getSongsList", "当前铃声正在播放，停止它！");
                            currentRingtone.stop();
                            dialogBinding.displayIV.setImageResource(R.drawable.play);
                        } else {
                            //该方法调用一次就会重新播放一次，原播放不会受到影响，多铃声同时进行。
                            //播放会在后台进行，只有当调用程序的进程结束时播放才会停止，Activity销毁不受影响。
                            //仅会完整的播放一次。
                            currentRingtone.play();
                            dialogBinding.displayIV.setImageResource(R.drawable.pause);
                        }
                    }

                });

                //无区块的点击监听
                dialogBinding.noBellTv.setOnClickListener(v1 -> {
                    //如果当前铃声在播放，停止它然后再执行本项目的操作
                    if (currentRingtone != null && currentRingtone.isPlaying()) {
                        currentRingtone.stop();
                    }
                    dialogBinding.currentRingtoneTV.setText("无");
                    currentRingTitle = "无";
                    //数据库也得同步更新
                    values.put("isRing", false);
                    updateData(holder, values);
                    dialog.dismiss();
                });


//                //BottomSheetDialog的行为监听
//                //先获取BottomSheetBehavior对象
//                assert dialogBinding != null;
//                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from((View) dialogBinding.getRoot().getParent());
////                BottomSheetBehavior<LinearLayoutCompat> behavior = BottomSheetBehavior.from(dialogBinding.bottomLayout);
//                behavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//                    //以下两个回调只有在BottomSheetDialog滑动隐藏的时候才会执行，直接点击灰黑处隐藏不会执行
//                    @Override
//                    public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                        if (newState == BottomSheetBehavior.STATE_HIDDEN) {
//                            stopAndSaveValue(holder);
//                        }
//                    }
//
//                    @Override
//                    public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//                        //滑动回调
//                    }
//                });

                //BottomSheetDialog点击灰黑处或点击返回键使dialog消失的事件监听
                dialog.setOnDismissListener(dialog1 -> {
                    stopAndSaveValue(holder);
                });

            });


        });//——————————————————————————from MyAdapter——————————————————————————————————————————————————

    }


    /**
     * 几点前不响铃部分
     */
    @SuppressLint("SetTextI18n")
    private void closeOrCancel(SettingAdapter.InnerHolder holder) {
        Log.i("getSongsList", "消极执行！");
        holder.itemSB.noRingBeforeButton.setChecked(false);
        holder.itemSB.titleBellowTV.setText("如您的入睡点不稳定，\n且不想在某个时点前的响铃吵醒身边的人，" +
                "可以开启此项设置，时间到了会长震提醒。");
        holder.itemSB.titleBellowTV.setTextColor(getResources().getColor(R.color.hintColor));
        layoutManager.scrollToPositionWithOffset(1, -100);
    }

    /**
     * 根据类型设置当前铃声的名称和uri，取不到就为系统默认
     * @param isNight 是否点击了晚上那个item中的铃声图标
     */
    private void getCurrentRingData(boolean isNight) {
        MyAlarm myAlarm = new MyAlarm(isNight);
//        Uri alarmDefaultUri = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM);
        //这里虽然为ringtoneUriStr设定了空字符串的初始值，但取出来的时候依然是null
        Log.i("getSongsList", "myAlarm.getRingtoneUriStr() = " + myAlarm.getRingtoneUriStr());
        Log.i("getSongsList", "myAlarm.getRingtoneTitle() = " + myAlarm.getRingtoneTitle());
        currentRingUri = Uri.parse(myAlarm.getRingtoneUriStr());
        currentRingTitle = myAlarm.getRingtoneTitle();
    }

    /**
     * 停止当前音乐的播放，并将其title、uri保存起来
     */
    private void stopAndSaveValue(SettingAdapter.InnerHolder holder) {
        //把currentRingTitle和currentRingUriStr更新到数据库中
        values.put("ringtoneTitle", currentRingTitle);
        values.put("ringtoneUriStr", currentRingUri + "");
        updateData(holder, values);
        Toast.makeText(this, "铃声信息更新成功", Toast.LENGTH_SHORT).show();
        //关闭当前铃声的播放
        if (currentRingtone != null) {
            currentRingtone.stop();
        }
    }

    /**
     * 解析处理返回的ActivityResult，更新当前铃声的ui，并得到其Uri
     */
    private void parseResult(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            //获取title和uri
            Intent returnIntent = result.getData();
            if (returnIntent != null) {
                currentRingTitle = returnIntent.getStringExtra("title");
                currentRingUri = Uri.parse(returnIntent.getStringExtra("songsUriStr"));
                String[] positionMapStrArr = returnIntent.getStringExtra("positionMapStr").split("_");
                //返回过来就存进去
                sp.edit().putInt("currentRingPosition", Integer.parseInt(positionMapStrArr[1])).apply();
                sp.edit().putString("currentRingFrom", positionMapStrArr[0]).apply();
                //更新当前铃声的title
                dialogBinding.currentRingtoneTV.setText(currentRingTitle);
            }
        }
    }

    private void requestPermission() {
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .explainReasonBeforeRequest()
                .onExplainRequestReason((scope, deniedList) -> {
                    scope.showRequestReasonDialog(deniedList, "即将申请的权限是获取本地铃声所必需的条件", "我已明白");
                })
                .onForwardToSettings((scope, deniedList) -> {
                    scope.showForwardToSettingsDialog(deniedList, "您需要去应用程序设置当中手动开启权限", "我已明白");
                })
                .request((allGranted, grantedList, deniedList) -> {
                    if (allGranted) {
                        //只会执行一次！
                        if (sp.getBoolean("isRequested", true)) {
                            Toast.makeText(this, "权限获取成功！", Toast.LENGTH_SHORT).show();
                            sp.edit().putBoolean("isRequested", false).apply();
                        }
                    } else {
                        Toast.makeText(this, "您已拒绝此权限，无法获取本地铃声！", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * 使EditText失去焦点，并隐藏软键盘
     */
    private void loseFocus(SettingAdapter.InnerHolder holder, EditText editText) {
        boolean hasFocus = editText.hasFocus();
        if (hasFocus) {
            editText.clearFocus();
            //如果写下下面这行代码，那么在失去焦点之后，马上又会获取到焦点（在界面上看起来就好像没有变一样）
//            holder.itemView.requestFocus();
            //隐藏软键盘
            utils.hideSoftInput(holder.itemView);
        }
    }

    /**
     *更新数据库中的checked型数据
     * @param isChanged ToggleButton的状态是否已经改变
     */
    private void updateChecked(SettingAdapter.InnerHolder holder,
                               ToggleButton button, String key, boolean isChanged) {
        boolean isChecked = button.isChecked();
        //更新数据库中对应的数据
        if (isChanged) {
            //针对ToggleButton的直接点击
            values.put(key, isChecked);
        } else {
            //针对块区的点击
            values.put(key, !isChecked);
        }
        updateData(holder, values);
        Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
    }

    /**
     * 监听EditText的焦点改变事件，当EditText失去焦点后核验text值，有异的话就更新数据库中相应的值
     * @param holder 所需引用
     * @param editText EditText
     * @param key 更新的key
     */
    private void updateText(SettingAdapter.InnerHolder holder, EditText editText, String key) {
        //不能放在里边，里边都是已经改变过了的
        String valueBefore = editText.getText().toString();
        Log.i("Checked", "valueBefore = " + valueBefore);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                Toast.makeText(this, "EditText 失去焦点！", Toast.LENGTH_SHORT).show();
                //在丧失焦点后在获取一次text值
                String value = editText.getText().toString();
                Log.i("Checked", "value = " + value);
                //不一样的话就更新数据库中相应的值
                if (!value.equals(valueBefore)) {
                    Log.i("Checked", "不一样，更新数据！");
                    values.put(key, value);
                    updateData(holder, values);
                    Toast.makeText(this, "更新成功！", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    /**
     * 根据类型获取数据模型对象
     * @param holder MyAdapter.InnerHolder对象，要通过它来获取类型
     * @return 返回LitePalSupport的子类，即LunchAlarm类或SleepAlarm类
     */
    public LitePalSupport getModel(SettingAdapter.InnerHolder holder) {
        boolean isNight = holder.restType.getText().equals("晚睡");
        LitePalSupport support;
        if (isNight) {
            support = new SleepAlarm();
        } else {
            support = new LunchAlarm();
        }
        return support;
    }

    /**
     * 更新数据库中相应的数据
     * @param holder holder里包含position，它能区分item
     */
    private void updateData(SettingAdapter.InnerHolder holder, ContentValues values) {
        LitePal.update(getModel(holder).getClass(), values, 1);
    }
}