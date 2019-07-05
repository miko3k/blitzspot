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

package org.deletethis.blitzspot.app.builtin;

import android.content.Context;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;

import org.deletethis.blitzspot.lib.icon.IconView;
import org.deletethis.search.parser.IconAddress;
import org.deletethis.search.parser.SearchPlugin;

import java.util.List;
import java.util.Optional;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

public interface BuiltinItem {
    String getName(Context context);

    IconAddress getIcon(Context context);

    Optional<List<BuiltinItem>> getChildren();

    Optional<SearchPlugin> getPlugin();

    class Plugin implements BuiltinItem {
        private final Supplier<SearchPlugin> supplier;

        public Plugin(Supplier<SearchPlugin> supplier) {
            this.supplier = Suppliers.memoize(supplier);
        }

        @Override
        public String getName(Context context) {
            return supplier.get().getName();
        }

        @Override
        public IconAddress getIcon(Context context) {
            return supplier.get().getIcon();
        }

        @Override
        public Optional<List<BuiltinItem>> getChildren() {
            return Optional.empty();
        }

        @Override
        public Optional<SearchPlugin> getPlugin() {
            return Optional.of(supplier.get());
        }
    }

    class NamedPlugin implements BuiltinItem {
        private final Supplier<SearchPlugin> supplier;
        private @StringRes
        final int name;
        private final IconAddress icon;

        public NamedPlugin(Supplier<SearchPlugin> supplier, @StringRes int name, @DrawableRes int icon) {
            this.supplier = Suppliers.memoize(supplier);
            this.name = name;
            this.icon = IconView.getResourceAddress(icon);
        }

        @Override
        public String getName(Context context) {
            return context.getString(name);
        }

        @Override
        public IconAddress getIcon(Context context) {
            return icon;
        }

        @Override
        public Optional<List<BuiltinItem>> getChildren() {
            return Optional.empty();
        }

        @Override
        public Optional<SearchPlugin> getPlugin() {
            return Optional.of(supplier.get());
        }
    }

    class Group implements BuiltinItem {
        private @StringRes
        final int name;
        private final IconAddress icon;
        private final ImmutableList<BuiltinItem> items;

        public Group(@StringRes int name, @DrawableRes int icon, ImmutableList<BuiltinItem> items) {
            this.name = name;
            this.icon = IconView.getResourceAddress(icon);
            this.items = items;
        }

        @Override
        public String getName(Context context) {
            return context.getString(name);
        }

        @Override
        public IconAddress getIcon(Context context) {
            return icon;
        }

        @Override
        public Optional<List<BuiltinItem>> getChildren() {
            return Optional.of(items);
        }

        @Override
        public Optional<SearchPlugin> getPlugin() {
            return Optional.empty();
        }
    }
}
