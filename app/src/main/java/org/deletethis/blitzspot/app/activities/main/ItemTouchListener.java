package org.deletethis.blitzspot.app.activities.main;

interface ItemTouchListener {

    void onItemClick(MyAdapter.MyViewHolder viewHolder);

    void onItemLongClick(MyAdapter.MyViewHolder viewHolder);

    void onFarDragged(MyAdapter.MyViewHolder viewHolder, int actionState);

    void onRemoved(MyAdapter.MyViewHolder  position);

    void onMoved(long what, double ordering);
}
