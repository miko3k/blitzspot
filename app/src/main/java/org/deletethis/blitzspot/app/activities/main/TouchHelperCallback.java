package org.deletethis.blitzspot.app.activities.main;

import android.graphics.Canvas;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.ItemTouchHelper;

class TouchHelperCallback extends ItemTouchHelper.Callback {
    private static final int FAR_DRAG = 16;
    private final ItemTouchHelperAdapter adapter;
    private final ItemTouchListener listener;
    private MyAdapter.MyViewHolder draggedView = null;
    private boolean wasDraggedFar;

    TouchHelperCallback(ItemTouchHelperAdapter adapter, ItemTouchListener listener) {
        this.adapter = adapter;
        this.listener = listener;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(
            @NonNull RecyclerView recyclerView,
            @NonNull RecyclerView.ViewHolder viewHolder) {

        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(
            @NonNull RecyclerView recyclerView,
            @NonNull  RecyclerView.ViewHolder viewHolder,
            @NonNull RecyclerView.ViewHolder target) {

        adapter.onItemMove(viewHolder, target);
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.onItemDismiss(viewHolder);
    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);

        if(actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            // we need to remember this in member variable, coz it's null when IDLE
            if(draggedView != null) {
                draggedView.getViewGroup().setActivated(false);
            }
            draggedView = null;
        } else {
            if(viewHolder != null) {
                draggedView = (MyAdapter.MyViewHolder) viewHolder;
                draggedView.getViewGroup().setActivated(true);
            }
            wasDraggedFar = false;
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if(Math.abs(dX) + Math.abs(dY) > FAR_DRAG && !wasDraggedFar) {
            wasDraggedFar = true;
            listener.onFarDragged((MyAdapter.MyViewHolder) viewHolder, actionState);
        }

        //System.out.println("onchildraw: " + dX + ", " + dY + ", actionState: " + actionState + ", isA: " + isCurrentlyActive);
    }
}