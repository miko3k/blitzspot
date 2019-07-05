/*
 * blitzspot
 * Copyright (C) 2018-2019 Peter Hanula
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.deletethis.blitzspot.app.activities.main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.dao.PluginWithId;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.icon.IconView;
import org.deletethis.search.parser.SearchPlugin;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class MyAdapter
        extends RecyclerView.Adapter<MyAdapter.MyViewHolder>
        implements ItemTouchHelperAdapter {

    private List<PluginWithId> dataSet = new ArrayList<>();
    private final ItemTouchListener itemTouchListener;

    MyAdapter(ItemTouchListener itemTouchListener) {
        this.itemTouchListener = itemTouchListener;
    }

    PluginWithId getEngineWithId(MyAdapter.MyViewHolder holder) {
        int adapterPosition = holder.getAdapterPosition();
        return dataSet.get(adapterPosition);
    }

    // this compares only the visible fields
    private boolean different(SearchPlugin a, SearchPlugin b) {
        if(!a.getName().equals(b.getName()))
            return true;

        if(!a.getIcon().equals(b.getIcon()))
            return true;

        return false;
    }

    private boolean different(List<PluginWithId> a, List<PluginWithId> b) {
        if(a.size() != b.size()) {
            Logging.MAIN.i("Difference in size");
            return true;
        }

        for(int i=0;i<a.size();++i) {
            if(a.get(i).getId() != b.get(i).getId()) {
                Logging.MAIN.i("Difference at position " + i +
                        ": ID " + a.get(i).getId() + " != " + b.get(i).getId());
                return true;
            }
            if(different(a.get(i).getSearchPlugin(), b.get(i).getSearchPlugin())) {
                Logging.MAIN.i("Difference at position " + i +
                        ": plugin " + a.get(i).getSearchPlugin().getName() +
                        " != " + b.get(i).getSearchPlugin().getName());
                return true;
            }

        }
        return false;
    }

    void setSearchEngines(List<PluginWithId> mDataset) {
        if(different(this.dataSet, mDataset)) {
            this.dataSet = mDataset;
            notifyDataSetChanged();
            Logging.MAIN.i("Data set changed");
        } else {
            Logging.MAIN.i("Data set not changed");
            // ordering might still have changed, that is not checked above
            // (not order, but precise values).. this happens when we moving items around
            this.dataSet = mDataset;
        }
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private final ViewGroup viewGroup;

        MyViewHolder(ViewGroup v) {
            super(v);
            viewGroup = v;
        }

        ViewGroup getViewGroup() {
            return viewGroup;
        }
    }

    @Override
    public void onItemDismiss(RecyclerView.ViewHolder holder) {
        itemTouchListener.onRemoved((MyViewHolder) holder);
    }

    void removeItem(long id) {
        int found = -1;
        for(int i = 0; i< dataSet.size(); ++i) {
            if(dataSet.get(i).getId() == id) {
                found = i;
                break;
            }
        }
        if(found < 0)
            throw new AssertionError();

        dataSet.remove(found);
        notifyItemRemoved(found);
    }

    private Double getOrdering(int position) {
        if(position < 0 ||position >= dataSet.size())
            return null;
        else
            return dataSet.get(position).getOrdering();
    }

    @Override
    public void onItemMove(RecyclerView.ViewHolder view, RecyclerView.ViewHolder target) {
        int viewPos = view.getAdapterPosition();
        int targetPos = target.getAdapterPosition();

        long idView = dataSet.get(viewPos).getId();
        Double ordering1, ordering2;

        if (viewPos < targetPos) {
            ordering1 = getOrdering(targetPos);
            ordering2 = getOrdering(targetPos +1);

            for (int i = viewPos; i < targetPos; i++) {
                Collections.swap(dataSet, i, i + 1);
            }
        } else {
            ordering1 = getOrdering(targetPos -1);
            ordering2 = getOrdering(targetPos);

            for (int i = viewPos; i > targetPos; i--) {
                Collections.swap(dataSet, i, i - 1);
            }
        }
        if(ordering1 == null && ordering2 == null)
            throw new IllegalStateException();

        double ordering;
        if(ordering1 == null) {
            ordering = ordering2 - 10000;
        } else if(ordering2 == null){
            ordering = ordering1 + 10000;
        } else {
            ordering = (ordering1 + ordering2) / 2;
        }

        Logging.MAIN.i("Move view ID " + idView + " at " + viewPos + " to " + targetPos + ", ordering: " + ordering1 + ".." + ordering2 + "=" + ordering);

        notifyItemMoved(viewPos, targetPos);
        itemTouchListener.onMoved(idView, ordering);
    }


    // Create new views (invoked by the layout manager)
    @Override
    @NonNull
    public MyAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder vh;

            // create a new view
            ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.main_item, parent, false);

            vh = new MyViewHolder(v);
            v.setOnClickListener(x -> itemTouchListener.onItemClick(vh));

            v.setOnLongClickListener(x -> {
                itemTouchListener.onItemLongClick(vh);
                return true;
            });
            //v.setOnTouchListener((v1, event) -> itemTouchListener.onTouch(vh, event));

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        // - get element from your data set at this position
        // - replace the contents of the view with that element
        ViewGroup convertView = holder.viewGroup;
        TextView textView = convertView.findViewById(R.id.label);
        IconView icon = convertView.findViewById(R.id.icon);
        SearchPlugin engine = dataSet.get(position).getSearchPlugin();
        icon.setAddress(engine.getIcon());
        textView.setText(engine.getName());
        // this is animated when removing the view using the button
        convertView.setTranslationX(0);
    }

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}