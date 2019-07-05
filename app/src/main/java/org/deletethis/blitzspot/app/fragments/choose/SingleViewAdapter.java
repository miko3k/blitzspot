package org.deletethis.blitzspot.app.fragments.choose;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.function.Consumer;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

class SingleViewAdapter extends RecyclerView.Adapter<Holder> {
    @LayoutRes private final int layoutId;
    private final Consumer<View> bind;

    public SingleViewAdapter(@LayoutRes int layoutId, Consumer<View> bind) {
        this.layoutId = layoutId;
        this.bind = bind;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(layoutId, parent, false);

        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        if (bind != null)
            bind.accept(holder.getView());
    }

    @Override
    public int getItemCount() {
        return 1;
    }
}
