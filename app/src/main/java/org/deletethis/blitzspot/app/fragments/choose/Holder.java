package org.deletethis.blitzspot.app.fragments.choose;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

class Holder extends RecyclerView.ViewHolder {
    private final ViewGroup viewGroup;

    public Holder(ViewGroup v) {
        super(v);
        viewGroup = v;
    }

    public ViewGroup getViewGroup() {
        return viewGroup;
    }

    public View getView() {
        return viewGroup;
    }
}