package org.deletethis.blitzspot.app.activities.jump;

import org.deletethis.blitzspot.app.dao.PluginWithId;
import org.deletethis.blitzspot.app.fragments.choose.DataSet;
import org.deletethis.search.parser.IconAddress;

import java.util.List;
import java.util.function.Consumer;

class MyDataSet implements DataSet {
    private final List<PluginWithId> plugins;
    private final Consumer<byte[]> consumer;

    MyDataSet(List<PluginWithId> plugins, Consumer<byte[]> consumer) {
        this.plugins = plugins;
        this.consumer = consumer;
    }


    @Override
    public IconAddress getIcon(int position) {
        return plugins.get(position).getSearchPlugin().getIcon();
    }

    @Override
    public String getName(int position) {
        return plugins.get(position).getSearchPlugin().getName();
    }

    @Override
    public void selected(int position) {
        consumer.accept(plugins.get(position).getSearchPlugin().serialize());
    }

    @Override
    public int getCount() {
        return plugins.size();
    }
}
