package com.example.yidianClock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.R;
import com.example.yidianClock.databinding.ItemReminderBinding;
import com.example.yidianClock.model.Reminder;
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
        return new InnerHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        /*
        普通设置
         */
        String label = reminderList.get(position).getLabel();
        holder.itemReminderBinding.tvLabel.setText(label);
        holder.itemReminderBinding.tvName.setText(reminderList.get(position).getName());
        /*
        根据数据映射、计算、判断
         */
        //设置头像
        String birthDate = reminderList.get(position).getDate();
        String theConstellation = ZodiacConstellation.getArr(birthDate)[1];
        Integer imageResource = imageMap.get(theConstellation);//此处通过星座取值一定不为空
        if (imageResource != null) {
            holder.itemReminderBinding.imageHead.setImageResource(imageResource);
        }
        //设置年龄
        String ageStr = Age.calculateRealYears(birthDate) + "周岁";
        holder.itemReminderBinding.tvAge.setText(ageStr);
        //设置生肖
        String chineseZodiac = ZodiacConstellation.getArr(birthDate)[0];
        holder.itemReminderBinding.tvChineseZodiac.setText(chineseZodiac);
        //设置星座
        holder.itemReminderBinding.tvTheConstellation.setText(theConstellation);
        //设置目标日离现在的天数
        // TODO: 2022/11/30 这里计算生日之间的间距要改为目标日
        String daysDiffStr = Age.getDaysDiff(birthDate) + "";
        //往setText()方法中传值要尤为谨慎，非资源的int型值一定要先转为String才行！！！
        holder.itemReminderBinding.tvDaysNum.setText(daysDiffStr);
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

}
