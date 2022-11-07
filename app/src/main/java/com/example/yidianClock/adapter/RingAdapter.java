package com.example.yidianClock.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.databinding.ItemRingBinding;
import com.example.yidianClock.model.Song;

import java.util.List;

public class RingAdapter extends RecyclerView.Adapter<RingAdapter.InnerHolder> {
    private final Context context;
    private final List<Song> songsList;

    public RingAdapter(Context context, List<Song> songsList) {
        this.context = context;
        this.songsList = songsList;
    }

    //在类里边声明自己的接口
    public interface OnListener {
        //这个接口必须实现一个方法，注意！这里没有方法体
        void onPerform(RingAdapter.InnerHolder holder, int position);
    }

    private RingAdapter.OnListener listener;

    /**
     * 设置点击监听器
     * @param listener OnListener对象，由外界提供（其实质也就是一段逻辑）
     */
    public void setOnListener(RingAdapter.OnListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRingBinding itemRingBinding = ItemRingBinding.inflate(
                LayoutInflater.from(context), parent, false);
        return new InnerHolder(itemRingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //视图数据显示
        holder.itemRB.songNameTv.setText(songsList.get(position).getSongName());
        holder.itemRB.selectedRb.setChecked(songsList.get(position).isSelected());
        holder.itemRB.artistTv.setText(songsList.get(position).getArtist());

        if (listener != null) {
            listener.onPerform(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return songsList.size();
    }

    public static class InnerHolder extends RecyclerView.ViewHolder {
        public ItemRingBinding itemRB;

        public InnerHolder(@NonNull ItemRingBinding itemRingBinding) {
            super(itemRingBinding.getRoot());
            itemRB = itemRingBinding;
        }
    }

    //重写此方法，告诉RecyclerView每个item都是不同的
    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
