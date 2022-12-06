package com.example.yidianClock;

import android.graphics.Canvas;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yidianClock.adapter.ReminderAdapter;
import com.example.yidianClock.model.Reminder;

import java.util.List;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    ReminderAdapter adapter;
    List<Reminder> reminderList;

    public SimpleItemTouchHelperCallback(ReminderAdapter adapter, List<Reminder> reminderList) {
        this.adapter = adapter;
        this.reminderList = reminderList;
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
        //从list中移除数据
        reminderList.remove(position);
        //通知adapter，item移除
        adapter.notifyItemRemoved(position);
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            //只对滑动作出处理
            //删除方块的宽度80dp


        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

    }
}
