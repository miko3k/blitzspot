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
