package com.example.yidianClock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.databinding.ItemSettingBinding;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.InnerHolder> {
    private final Context context;
    private OnListener listener;

    //在类里边声明自己的接口
    public interface OnListener {
        //这个接口必须实现一个方法，注意！这里没有方法体
        void onPerform(InnerHolder holder, int position);
    }

    public MyAdapter(Context context) {
        this.context = context;
    }

    /**
     * 设置点击监听器
     * @param listener OnListener对象，由外界提供（其实质也就是一段逻辑）
     */
    public void setOnListener(OnListener listener) {
        this.listener = listener;
    }

    /**
     * 将xml文件加载到java代码中
     * @param parent
     * @param viewType
     * @return 返回包含itemView的ViewHolder对象
     */
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //viewBinding写法
        ItemSettingBinding itemBinding = ItemSettingBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new InnerHolder(itemBinding.getRoot());
        //一般写法
//        View root_view = LayoutInflater.from(context)
//                .inflate(R.layout.item_setting, parent, false);
//        return new InnerHolder(root_view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //数据初始化————————————————————————————————————————————
        if (position == 1) {
            // TODO: 2022/11/1 此处有待精简
            holder.restType.setText("晚睡");
            holder.potTitle.setText("晚睡一般时段");
            holder.potIntro.setText("晚睡一般入睡点所在时段");
            //为晚睡模块设置默认值
            holder.restTimeEdit.setHint("默认 7");
            holder.alarmContentEdit.setHint("又是元气满满的一天");
            holder.potView.setText("默认：21:30 ~ 2:30");
            holder.shockIntervalEdit.setHint("默认 50");
        }

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
    public class InnerHolder extends RecyclerView.ViewHolder {
        //直接显示文本，根据item的类型变化
        public TextView restType;
        TextView potTitle;
        TextView potIntro;
        //用户设置文本，含默认值
        public EditText restTimeEdit;
        public EditText alarmContentEdit;
        public TextView potView;
        public ToggleButton isSetShockButton;
        public ToggleButton isSetTaskButton;
        public EditText shockIntervalEdit;
        public EditText shockContentEdit;
        //布局
        public LinearLayoutCompat moreSetLayout;
        public LinearLayoutCompat shockSetLayout;
        public RelativeLayout potLayout;
        public RelativeLayout alarmTaskLayout;
        public RelativeLayout shockTipLayout;
        //其他
        public ImageView bellImage;
        public TextView moreSetView;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
            restType = itemView.findViewById(R.id.restType_show);
            potTitle = itemView.findViewById(R.id.potTitle_show);
            potIntro = itemView.findViewById(R.id.potIntro_show);
            restTimeEdit = itemView.findViewById(R.id.restTimeEdit);
            alarmContentEdit = itemView.findViewById(R.id.alarmContentEdit);
            potView = itemView.findViewById(R.id.period_of_time_tv);
            isSetShockButton = itemView.findViewById(R.id.isSetShockButton);
            isSetTaskButton = itemView.findViewById(R.id.isSetTaskButton);
            shockIntervalEdit = itemView.findViewById(R.id.shockIntervalEdit);
            shockContentEdit = itemView.findViewById(R.id.shockContentEdit);
            bellImage = itemView.findViewById(R.id.bell_image);
            moreSetView = itemView.findViewById(R.id.moreSet_tv);
            moreSetLayout = itemView.findViewById(R.id.moreSet_layout);
            shockSetLayout = itemView.findViewById(R.id.shockSet_layout);
            potLayout = itemView.findViewById(R.id.pot_layout);
            alarmTaskLayout = itemView.findViewById(R.id.alarmTask_layout);
            shockTipLayout = itemView.findViewById(R.id.shockTip_layout);
        }
    }
}
