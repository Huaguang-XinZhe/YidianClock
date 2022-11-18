package com.example.yidianClock.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.utils.MyUtils;
import com.example.yidianClock.R;
import com.example.yidianClock.databinding.ItemSettingBinding;
import com.example.yidianClock.model.MyAlarm;

public class SettingAdapter extends RecyclerView.Adapter<SettingAdapter.InnerHolder> {
    private final Context context;

    public SettingAdapter(Context context) {
        this.context = context;
    }

    //在类里边声明自己的接口
    public interface OnListener {
        //这个接口必须实现一个方法，注意！这里没有方法体
        void onPerform(InnerHolder holder, int position);
    }

    private OnListener listener;

    /**
     * 设置点击监听器
     * @param listener OnListener对象，由外界提供（其实质也就是一段逻辑）
     */
    public void setOnListener(OnListener listener) {
        this.listener = listener;
    }

    /**
     * 将xml文件加载到java代码中
     * @return 返回包含itemView的ViewHolder对象
     */
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewBinding写法
        ItemSettingBinding itemBinding = ItemSettingBinding.inflate(
                LayoutInflater.from(context), parent, false);
        //同步修改
        return new InnerHolder(itemBinding);
        //一般写法
//        View root_view = LayoutInflater.from(context)
//                .inflate(R.layout.item_setting, parent, false);
//        return new InnerHolder(root_view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //数据初始化————————————————————————————————————————————
        boolean isNight;
        if (position == 1) {
            //晚睡item加载所需
            holder.restType.setText("晚睡");
            holder.timeUnit.setText("小时");
            holder.potTitle.setText("晚睡一般时段");
            holder.potIntro.setText("晚睡一般入睡点所在时段");
            holder.restTimeEdit.setHint("默认 7.5");
            holder.shockIntervalEdit.setHint("默认 45");

            holder.itemSB.noRingBeforeLayout.setVisibility(View.VISIBLE);
            holder.itemSB.layoutDonGetUp.setVisibility(View.VISIBLE);
            holder.itemSB.lineView.setVisibility(View.VISIBLE);
            holder.itemSB.lineView2.setVisibility(View.VISIBLE);
            isNight = true;
        } else {
            holder.itemSB.noRingBeforeLayout.setVisibility(View.GONE);
            holder.itemSB.layoutDonGetUp.setVisibility(View.GONE);
            holder.itemSB.lineView.setVisibility(View.GONE);
            holder.itemSB.lineView2.setVisibility(View.GONE);
            isNight = false;
        }

        //构建myAlarm实例的时候会自动从数据库中取值然后设定到该类的实例变量中
        MyAlarm myAlarm = new MyAlarm(isNight);
//        myAlarm.getDataFromDB();
        holder.restTimeEdit.setText(MyUtils.getRoundDotStr(myAlarm.getRestTime()));
        holder.shockIntervalEdit.setText(String.valueOf(myAlarm.getShockInterval()));
        holder.alarmContentEdit.setText(myAlarm.getAlarmContent());
        holder.potView.setText(myAlarm.getPotStr());
        holder.isSetShockButton.setChecked(myAlarm.isShockTipSet());
        holder.isSetTaskButton.setChecked(myAlarm.isTaskSet());
        holder.itemSB.noRingBeforeButton.setChecked(myAlarm.isJustShockOn());
        holder.itemSB.buttonDonGetUp.setChecked(myAlarm.isDelayGetUp());

        //几点前不响铃
        if (myAlarm.isJustShockOn()) {
            holder.itemSB.titleBellowTV.setText(myAlarm.getBeforeTimeStr_noRingBefore());
            holder.itemSB.titleBellowTV.setTextColor(context.getResources().getColor(R.color.green_set_value));
        }
        //不到几点不起床
        if (myAlarm.isDelayGetUp()) {
            holder.itemSB.tvIntroDonGetUp.setText(myAlarm.getBeforeTimeStr_donGetUp());
            holder.itemSB.tvIntroDonGetUp.setTextColor(context.getResources().getColor(R.color.green_set_value));
        }
        //铃声图标
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.bellImage.getLayoutParams();
        if (myAlarm.isRing()) {
            holder.bellImage.setImageResource(R.drawable.bell);
            layoutParams.width = 90;
            layoutParams.height = 90;
        } else {
            holder.bellImage.setImageResource(R.drawable.shock);
            //图标略显大了些，调小一点（注意，这里的类型必须是 “父类.LayoutParams”）
            layoutParams.width = 75;
            layoutParams.height = 75;
        }
        holder.bellImage.setLayoutParams(layoutParams);

        //传入相关引用，构建环境
        // 这段代码一定能执行，因为SettingActivity的onCreate会先于onBindViewHolder执行
        if (listener != null) {
            listener.onPerform(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    /**
     * 声明item中的控件，通过findViewById()
     */
    public static class InnerHolder extends RecyclerView.ViewHolder {
        public ItemSettingBinding itemSB;
        // TODO: 2022/11/2 用了viewBinding，这里可以精简代码
        //直接显示文本，根据item的类型变化
        public TextView restType;
        TextView potTitle;
        TextView potIntro;
        TextView timeUnit;
        //用户设置数据，含默认值
        public EditText restTimeEdit;
        public EditText alarmContentEdit;
        public TextView potView;
        public ToggleButton isSetShockButton;
        public ToggleButton isSetTaskButton;
        public EditText shockIntervalEdit;
        //布局
        public LinearLayoutCompat moreSetLayout;
        public RelativeLayout potLayout;
        public RelativeLayout alarmTaskLayout;
        public RelativeLayout shockTipLayout;
        //其他
        public ImageView bellImage;
        public TextView moreSetView;

        //注意：使用viewBinding的时候，修改了InnerHolder的构造函数
        public InnerHolder(@NonNull ItemSettingBinding binding) {
            super(binding.getRoot());
            itemSB = binding;

            View itemView = binding.getRoot();
            restType = itemView.findViewById(R.id.restType_show);
            potTitle = itemView.findViewById(R.id.potTitle_show);
            potIntro = itemView.findViewById(R.id.potIntro_show);
            timeUnit = itemView.findViewById(R.id.timeUnit_tv);
            restTimeEdit = itemView.findViewById(R.id.restTimeEdit);
            alarmContentEdit = itemView.findViewById(R.id.alarmContentEdit);
            potView = itemView.findViewById(R.id.period_of_time_tv);
            isSetShockButton = itemView.findViewById(R.id.isShockTipSet_button);
            isSetTaskButton = itemView.findViewById(R.id.isTaskSet_button);
            shockIntervalEdit = itemView.findViewById(R.id.shockIntervalEdit);
            bellImage = itemView.findViewById(R.id.bell_image);
            moreSetView = itemView.findViewById(R.id.moreSet_tv);
            moreSetLayout = itemView.findViewById(R.id.moreSet_layout);
            potLayout = itemView.findViewById(R.id.pot_layout);
            alarmTaskLayout = itemView.findViewById(R.id.alarmTask_layout);
            shockTipLayout = itemView.findViewById(R.id.shockTip_layout);
        }
    }

}
