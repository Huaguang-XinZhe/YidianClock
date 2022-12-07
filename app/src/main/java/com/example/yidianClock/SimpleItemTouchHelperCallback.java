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

import org.litepal.LitePal;

import java.util.List;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    Context context;
    ReminderAdapter adapter;
    List<Reminder> reminderList;

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
        //该方法滑动完成后才会调用
        /*
        在这里要实现：
            1. 将滑动item处的数据从list中删除
            2. 通知adapter移除滑动item
        这需要position，需要list，需要adapter，而这里可以获取position和adapter，但不能获取list；
        故可创建一个接口，交由Adapter类（能提供list）去实现；
        创建接口的目的就是为了减少某处的引用，将实现交由引用提供者来完成（它们能轻松提供所需引用）；
        如果这里不创建接口，那么就必须增加构造函数的参数，交由构造处去实现。这就要求构造实例的地方能提供相关引用。
        另外，交给谁去引用，还得看实现的长度。如果长度过长，那就要考虑实现者的负担了，不能让实现者的代码过长。
         */

        //获取滑动处的position
        int position = viewHolder.getAbsoluteAdapterPosition();
        //必须在获取完id后才能在list中把该项移除
        int id = reminderList.get(position).getId();
        //从list中移除数据
        reminderList.remove(position);
        //通知adapter，item移除
        adapter.notifyItemRemoved(position);
        //从数据库中移除该条数据
        LitePal.delete(Reminder.class, id);
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
//            //滑动item的position
//            int position = viewHolder.getAbsoluteAdapterPosition();
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
            //分割线再现
            holder.itemReminderBinding.viewLine.setVisibility(View.VISIBLE);
        }
    }
}
