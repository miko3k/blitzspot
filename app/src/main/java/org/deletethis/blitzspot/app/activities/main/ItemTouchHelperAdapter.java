package org.deletethis.blitzspot.app.activities.main;

import androidx.recyclerview.widget.RecyclerView;

interface ItemTouchHelperAdapter {

    void onItemMove(RecyclerView.ViewHolder view, RecyclerView.ViewHolder target);

    void onItemDismiss(RecyclerView.ViewHolder view);
}
