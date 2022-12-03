package com.example.yidianClock.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.R;
import com.example.yidianClock.databinding.ItemReminderBinding;
import com.example.yidianClock.model.Reminder;
import com.example.yidianClock.time_conversions.MatchStandardization;
import com.example.yidianClock.utils.MyUtils;
import com.example.yidianClock.utils.timeUtils.Age;
import com.example.yidianClock.utils.timeUtils.ZodiacConstellation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.InnerHolder> {
    Context context;
    List<Reminder> reminderList;
    /**
     * 星座和头像的映射map
     */
    static final Map<String, Integer> imageMap = new HashMap<>();
    /**
     * 星座
     */
    String theConstellation;

    static {
        imageMap.put("天蝎座", R.drawable.tian_xie);
        imageMap.put("天秤座", R.drawable.tian_ping);
        imageMap.put("水瓶座", R.drawable.shui_ping);
        imageMap.put("金牛座", R.drawable.jin_niu);
        imageMap.put("处女座", R.drawable.chu_nv);
        imageMap.put("摩羯座", R.drawable.mo_jie);
        imageMap.put("双鱼座", R.drawable.shuang_yu);
        imageMap.put("双子座", R.drawable.shuang_zi);
        imageMap.put("巨蟹座", R.drawable.ju_xie);
        imageMap.put("狮子座", R.drawable.shi_zi);
        imageMap.put("射手座", R.drawable.she_shou);
        imageMap.put("白羊座", R.drawable.bai_yang);
    }

    public ReminderAdapter(Context context, List<Reminder> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReminderBinding binding = ItemReminderBinding.inflate(LayoutInflater.from(context), parent, false);

        //只执行一次的代码可以放在此处（避免在onBindViewHolder中重复执行）
        //不能采用下面这个方法去获取Drawable对象，颜色设置的时候会出问题（和位置无关）
//        Drawable drawable = ContextCompat.getDrawable(context, R.drawable.shape_label);//获取drawable对象
//        gradientDrawable = (GradientDrawable) drawable;//强转为GradientDrawable对象

        return new InnerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        /*
        普通设置
         */
        String label = reminderList.get(position).getLabel();
        holder.itemReminderBinding.tvLabel.setText(label);
        holder.itemReminderBinding.tvName.setText(reminderList.get(position).getTitle());
        /*
        根据数据映射、计算、判断
         */
        //这里取的是原始日期，不是目标日
        String priDate = reminderList.get(position).getPriDate();
        theConstellation = ZodiacConstellation.getArr(priDate)[1];
        //根据label更新头像和label背景颜色
        updateWithLabel(label, holder);
        //只有生日才计算年龄、生肖和星座，不是生日就不计算，并隐藏View
        if (label.equals("生日")) {
            //设置年龄
            String ageStr = Age.calculateRealYears(priDate) + "周岁";
            holder.itemReminderBinding.tvAge.setText(ageStr);
            //设置生肖
            String chineseZodiac = ZodiacConstellation.getArr(priDate)[0];
            holder.itemReminderBinding.tvChineseZodiac.setText(chineseZodiac);
            //设置星座
            holder.itemReminderBinding.tvTheConstellation.setText(theConstellation);
            //View可见
            holder.itemReminderBinding.tvAge.setVisibility(View.VISIBLE);
            holder.itemReminderBinding.layoutSxXz.setVisibility(View.VISIBLE);
        } else {
            //View不可见
            holder.itemReminderBinding.tvAge.setVisibility(View.GONE);
            holder.itemReminderBinding.layoutSxXz.setVisibility(View.GONE);
        }
        //设置目标日离现在的天数
        String goalDay = MatchStandardization.getGoalDay(reminderList.get(position).getTimeStr(),
                priDate, reminderList.get(position).getType());//根据原始日计算目标日
        int daysDiff = MyUtils.getDaysDiff(goalDay);//根据目标日得到相差天数，此处的goalDay不为空串，远超当前日期的情况会在发送时判断避免
        //往setText()方法中传值要尤为谨慎，非资源的int型值一定要先转为String才行！！！
        holder.itemReminderBinding.tvDaysNum.setText(String.valueOf(daysDiff));
        //设置天数下面的提示
        String tip = "还有";
        if (label.equals("生日")) {
            tip = "离生日还有";
        }
        holder.itemReminderBinding.tvTip.setText(tip);

    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {
        public ItemReminderBinding itemReminderBinding;

        public InnerHolder(@NonNull ItemReminderBinding binding) {
            super(binding.getRoot());
            itemReminderBinding = binding;
        }
    }

    /**
     * 跟随标签更新设置：
     * 1. 头像上生日、纪念日等标签的背景颜色；
     * 2. 头像
     * @param label 标签
     * @param holder InnerHolder对象，持有View
     */
    private void updateWithLabel(String label, InnerHolder holder) {
        int colorRes;
        Integer imageRes;
        switch (label) {
            case "生日":
                colorRes = context.getResources().getColor(R.color.orange);
                imageRes = imageMap.get(theConstellation);//此处通过星座取值一定不为空
                break;
            case "纪念日":
                colorRes = context.getResources().getColor(R.color.pink);
                imageRes = R.drawable.day_of_remembrance;
                break;
            case "节日":
                colorRes = context.getResources().getColor(R.color.red);
                imageRes = R.drawable.festival;
                break;
            case "节气":
                colorRes = context.getResources().getColor(R.color.brown);
                imageRes = R.drawable.solar_terms;
                break;
            case "倒计时":
                colorRes = context.getResources().getColor(R.color.blue);
                imageRes = R.drawable.countdown;
                break;
            default:
                colorRes = context.getResources().getColor(R.color.gray_time_length);
                imageRes = R.drawable.add_black;
        }
        //根据label改变shape背景颜色
        GradientDrawable gradientDrawable = (GradientDrawable) holder.itemReminderBinding.tvLabel.getBackground();
        if (gradientDrawable != null) {
//            gradientDrawable.mutate();//加了这行代码颜色变化就不管用了，依然是原来的颜色
            gradientDrawable.setColor(colorRes);

        }
        //设置头像
        if (imageRes != null) {
            holder.itemReminderBinding.imageHead.setImageResource(imageRes);
        }
    }


}
