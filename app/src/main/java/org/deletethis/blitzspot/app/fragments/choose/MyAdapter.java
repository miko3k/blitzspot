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

package org.deletethis.blitzspot.app.fragments.choose;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.lib.icon.IconView;

import androidx.recyclerview.widget.RecyclerView;

class MyAdapter extends RecyclerView.Adapter<Holder> {

    private DataSet dataSet = null;

    public <T> void setData(DataSet dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        Holder vh;

        ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.choose_item, parent, false);

        vh = new Holder(v);
        v.setOnClickListener(x -> dataSet.selected(vh.getAdapterPosition()));
        return vh;
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        ViewGroup convertView = holder.getViewGroup();
        TextView textView = convertView.findViewById(R.id.label);
        IconView icon = convertView.findViewById(R.id.icon);
        ImageView fancyArrow = convertView.findViewById(R.id.fancy_arrow);
        icon.setAddress(dataSet.getIcon(position));
        textView.setText(dataSet.getName(position));
        fancyArrow.setVisibility(dataSet.isFancyArrowPresent(position) ? View.VISIBLE : View.GONE);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(dataSet == null) {
            return 0;
        } else {
            return dataSet.getCount();
        }
    }

}