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

package org.deletethis.blitzspot.app.activities.search;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProvider;


class MyAdapter
        extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ImmutableList<ChoiceProvider.Choice> mDataset;
    private final AdapterListener listener;

    MyAdapter(AdapterListener listener) {
        this.mDataset = ImmutableList.of();
        this.listener = listener;
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
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // create a new view
        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_choice, parent, false);


        return new MyViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChoiceProvider.Choice src = mDataset.get(position);

        ViewGroup root = holder.viewGroup;
        TextView value = root.findViewById(R.id.value);
        TextView description = root.findViewById(R.id.description);
        ImageView delete = root.findViewById(R.id.delete);
        ViewGroup suggestion = root.findViewById(R.id.suggestion);
        ImageView use = root.findViewById(R.id.use);

        value.setText(src.getValue());

        if(src.isRemovable()) {
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener((v)->listener.onSearchSuggestionRemoved(src));
        } else {
            delete.setVisibility(View.GONE);
        }

        if(src.getDescription() != null) {
            description.setVisibility(View.VISIBLE);
            description.setText(src.getDescription());
        } else {
            description.setVisibility(View.GONE);
        }

        if(src.isUsable()) {
            use.setVisibility(View.VISIBLE);
            use.setOnClickListener((v)->listener.onSearchSuggestionUsed(src));
        } else {
            use.setVisibility(View.GONE);
        }
        suggestion.setOnClickListener((v)->listener.onSearchSuggestionClicked(src));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public void setSuggestions(ImmutableList<ChoiceProvider.Choice> mDataset) {
        this.mDataset = mDataset;
    }
}