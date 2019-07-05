package org.deletethis.blitzspot.app.builtin;

import android.content.Context;
import android.content.res.Resources;

import com.google.common.collect.ImmutableList;

import org.deletethis.blitzspot.app.builtin.list.BuiltinDefinitions;
import org.deletethis.blitzspot.app.builtin.list.BuiltinRegistry;
import org.deletethis.blitzspot.app.builtin.list.GroupItem;
import org.deletethis.blitzspot.lib.RawResource;
import org.deletethis.blitzspot.lib.icon.IconView;
import org.deletethis.search.parser.ErrorCode;
import org.deletethis.search.parser.HttpMethod;
import org.deletethis.search.parser.PatchBuilder;
import org.deletethis.search.parser.PluginAdapter;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;
import org.deletethis.search.parser.SearchPluginFactory;
import org.deletethis.search.parser.IconAddress;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class PluginFactory {
    private final static String PREFIX = "builtin:";
    private final Map<String, BuiltinPluginInfo> plugins = new HashMap<>();
    private final List<BuiltinItem> builtinItems = new ArrayList<>();
    private final List<BuiltinPluginInfo> onStart = new ArrayList<>();

    private final Resources resources;

    private PluginFactory(Resources resources) {
        this.resources = resources;

        BuiltinPluginsVerifier verifier = new BuiltinPluginsVerifier(resources);

        BuiltinDefinitions.initialize(new BuiltinRegistry() {
            private BuiltinPluginInfo add(String identifier, int icon, int source) {
                BuiltinPluginInfo bpi = new BuiltinPluginInfo(identifier, source, icon);
                BuiltinPluginInfo put = plugins.put(identifier, bpi);
                if(put != null)
                    throw new IllegalArgumentException("duplicate identifier: " + identifier);

                verifier.verify(bpi);
                verifier.verifyIcon(icon);

                return bpi;
            }

            @Override
            public void plugin(String identifier, int icon, int source) {
                add(identifier, icon, source);
                builtinItems.add(new BuiltinItem.Plugin(() -> createBuiltin(identifier)));
            }

            @Override
            public void onStart(String identifier) {
                BuiltinPluginInfo builtinPluginInfo = plugins.get(identifier);
                Objects.requireNonNull(builtinPluginInfo);
                onStart.add(builtinPluginInfo);
            }

            @SuppressWarnings("UnstableApiUsage")
            @Override
            public void group(int title, int icon, GroupItem... items) {
                ImmutableList.Builder<BuiltinItem> bld = ImmutableList.builderWithExpectedSize(items.length);

                for(GroupItem itm: items) {
                    add(itm.getIdentifier(), icon, itm.getSource());
                    bld.add(new BuiltinItem.NamedPlugin(
                            ()->createBuiltin(itm.getIdentifier()),
                            itm.getTitle(),
                            itm.getTitleIcon()));

                    verifier.verifyIcon(itm.getTitleIcon());
                }
                builtinItems.add(new BuiltinItem.Group(title, icon, bld.build()));
            }
        });
    }

    private static class BuiltinPlugin extends PluginAdapter {
        private final BuiltinPluginInfo info;

        BuiltinPlugin(BuiltinPluginInfo info, SearchPlugin target) {
            super(target);
            this.info = info;
        }

        public BuiltinPluginInfo getInfo() {
            return info;
        }

        @Override
        public byte[] serialize() {
            return (PREFIX + info.getIdentifier()).getBytes(StandardCharsets.US_ASCII);
        }

        @Override
        public String getIdentifier() {
            return info.getIdentifier();
        }

        @Override
        public IconAddress getIcon() {
            return IconView.getResourceAddress(info.getIcon());
        }

        @Override
        public PatchBuilder patch() {
            return new PatchBuilder().plugin(this);
        }
    }

    private Optional<BuiltinPluginInfo> getBuiltinPluginInfo(byte[] bytes) {
        String str = new String(bytes, StandardCharsets.ISO_8859_1);
        if(str.startsWith(PREFIX)) {
            String name = str.substring(PREFIX.length());
            return Optional.ofNullable(plugins.get(name));
        } else {
            return Optional.empty();
        }
    }

    public List<BuiltinItem> getBuiltinItems() {
        return builtinItems;
    }

    private final SearchPluginFactory factory = new SearchPluginFactory() {
        @Override
        public SearchPlugin loadSearchPlugin(byte[] bytes) throws PluginParseException {
            Optional<BuiltinPluginInfo> builtinPluginInfo = getBuiltinPluginInfo(bytes);
            if(builtinPluginInfo.isPresent()) {
                BuiltinPluginInfo info = builtinPluginInfo.get();
                byte [] source = RawResource.cachedLoad(resources, info.getSource());
                SearchPlugin actual = super.loadSearchPlugin(source);
                return new BuiltinPlugin(info, actual);
            } else {
                return super.loadSearchPlugin(bytes);
            }
        }
    };

    static SearchPlugin verify(SearchPlugin plugin) throws PluginParseException {
        if(plugin.getSearchMethod() != HttpMethod.GET)
            throw new PluginParseException(ErrorCode.INVALID_METHOD, "this app does not support specified method");

        if(plugin.supportsSuggestions() && plugin.getSuggestionMethod() != HttpMethod.GET)
            throw new PluginParseException(ErrorCode.INVALID_METHOD, "suggestions use an support specified method");

        return plugin;
    }

    public SearchPlugin load(byte [] bytes) throws PluginParseException {
        return verify(factory.loadSearchPlugin(bytes));
    }

    public List<byte[]> getOnStart() {
        List<byte[]> result = new ArrayList<>();
        for(BuiltinPluginInfo bpi: onStart) {
            result.add(createBuiltin(bpi.getIdentifier()).serialize());
        }
        return result;
    }

    private SearchPlugin createBuiltin(String identifier) {
        try {
            byte [] source = (PREFIX + identifier).getBytes(StandardCharsets.UTF_8);
            return factory.loadSearchPlugin(source);
        } catch (PluginParseException e) {
            // parsing of a builtin plugin should succeed
            throw new IllegalStateException(e);
        }
    }

    private static PluginFactory instance;

    public static PluginFactory get(Context context) {
        context = context.getApplicationContext();

        if (instance == null) {
            instance = new PluginFactory(context.getResources());
        }
        return instance;
    }
}
