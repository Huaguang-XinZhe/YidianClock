package com.example.yidianClock;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.adapter.ReminderAdapter;
import com.example.yidianClock.model.Reminder;
import com.example.yidianClock.utils.MyUtils;
import com.google.android.material.snackbar.Snackbar;

import org.litepal.LitePal;

import java.util.List;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    Context context;
    ReminderAdapter adapter;
    List<Reminder> reminderList;
    /**
     * 是否点击了撤销按钮
     */
    boolean isRevoke = false;

    public SimpleItemTouchHelperCallback(ReminderAdapter adapter) {
        //由于adapter实例里本身就有context和reminderList引用，而且这个引用也是MainActivity提供的，所以这里就不重复要求外界传入引用了
        this.context = adapter.context;
        this.adapter = adapter;
        this.reminderList = adapter.reminderList;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //禁止拖动，允许左滑
        return makeMovementFlags(0, ItemTouchHelper.START);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        //该方法只有item真正移除后才会调用，item复原会去不会执行
        Log.i("getSongsList", "onSwiped执行！");
        //获取滑动处的position
        int position = viewHolder.getAbsoluteAdapterPosition();
        //获取滑动处的Reminder实例
        Reminder deletedReminder = reminderList.get(position);
        //必须在获取完id后才能在list中把该项移除
        int id = deletedReminder.getId();
        //从list中移除数据
        reminderList.remove(position);
        //通知adapter，item移除
        adapter.notifyItemRemoved(position);
        if (position == 0) {
            //如果删除的是第一项，就通知第二项改变刷新
            //注意，因为OnSwipe方法是移除后调用，此时的第二项的索引已经实时更新为第一项了，所以position = 0
            adapter.notifyItemChanged(0);
        }
        Snackbar.make(viewHolder.itemView, "是否撤销删除操作？", Snackbar.LENGTH_SHORT).setAction("撤销", v -> {
            isRevoke = true;
            //在原来删除的位置恢复Reminder实例
            reminderList.add(position, deletedReminder);
            //通知插入刷新
            adapter.notifyItemInserted(position);
        }).show();
        //没撤销就彻底删除
        if (!isRevoke) {
            //从数据库中移除该条数据
            LitePal.delete(Reminder.class, id);
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //注意，这里没有重写父类的方法
        //只对滑动作出处理
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //删除布局的宽度
            int deleteLayoutWidth = 180*3;
//            //RecyclerView滑动删除的最小删除宽度
//            int minDeleteWidth = recyclerView.getWidth()/2;
            //限制整体布局左移的宽度
            if (-dX < deleteLayoutWidth) {
                //跟随dX，整体布局左移（显示删除布局）
                viewHolder.itemView.scrollTo((int) -dX,0);
                //只处理普通viewHolder，不处理脚部viewHolder
                if (viewHolder instanceof ReminderAdapter.InnerHolder) {
                    ReminderAdapter.InnerHolder holder = (ReminderAdapter.InnerHolder) viewHolder;
                    if (dX < 0) {
                        //左滑
                        //分割线不可见但占位
                        holder.itemReminderBinding.viewLine.setVisibility(View.INVISIBLE);
                    } else {
                        //右滑
                        //分割线再现
                        holder.itemReminderBinding.viewLine.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }


    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        //太棒了，这个方法能够很好的实现我的目的，它只会在选中时执行一次，并且恢复后在选中依然能够执行！
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //震动200毫秒
            MyUtils.getInstance(context).vibrate(200);
            Log.i("getSongsList", "选中，震动执行！");
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
        //重置改变，防止由于复用而导致的显示问题
        //使整个item复位
        viewHolder.itemView.setScrollX(0);
        if (viewHolder instanceof ReminderAdapter.InnerHolder) {
            ReminderAdapter.InnerHolder holder = (ReminderAdapter.InnerHolder) viewHolder;
            //滑动item的position
            int position = viewHolder.getAbsoluteAdapterPosition();
            //分割线再现，不过第一项就算了
            if (position == 0) {
                Log.i("getSongsList", "clearView，position = 0执行");
                //这里不管怎么搞都没有，还是直接刷新吧！
                adapter.notifyItemChanged(0);
                return;
            }
            holder.itemReminderBinding.viewLine.setVisibility(View.VISIBLE);
        }
    }
}
