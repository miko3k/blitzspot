package org.deletethis.blitzspot.app.fragments.info;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.Browser;
import org.deletethis.blitzspot.lib.icon.IconView;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.PropertyName;
import org.deletethis.search.parser.PropertyValue;
import org.deletethis.search.parser.SearchPlugin;

import java.util.Map;
import java.util.function.Consumer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class InfoFragment extends Fragment {
    private SearchPlugin plugin;
    private String message;
    private LinearLayout mainBody;
    private TextView pluginName;
    private IconView icon;

    private void addItem(Consumer<TextView> key, Consumer<TextView> value) {
        View inflate = LayoutInflater.from(getContext()).inflate(R.layout.info_item, mainBody, false);
        TextView k = inflate.findViewById(R.id.key);
        TextView v = inflate.findViewById(R.id.value);
        key.accept(k);
        value.accept(v);
        mainBody.addView(inflate);
    }

    private void showPropertyValue(PropertyValue value, TextView v) {
        value.accept(new PropertyValue.Visitor<Void>() {
            @Override
            public Void visitLiteral(PropertyValue.Literal pv) {
                v.setText(pv.toString());
                return null;
            }

            @Override
            public Void visitPredefined(PropertyValue.Predefined pv) {
                v.setText(ParserTranslations.getPropertyValue(pv));
                return null;
            }

            @Override
            public Void visitUrl(PropertyValue.Url url) {
                Context ctx = getContext();
                if(ctx == null)
                    throw new NullPointerException();

                v.setTextColor(getResources().getColor(R.color.colorAccent, null));
                v.setClickable(true);
                v.setFocusable(true);
                v.setText(url.getValue());
                v.setOnClickListener(v1 -> Browser.openBrowser(ctx, Uri.parse(url.getHref())));
                return null;
            }
        });
    }

    public boolean setPluginData(byte [] data) {
        SearchPlugin plugin;
        try {
            plugin = PluginFactory.get(getContext()).load(data);
        } catch (PluginParseException e) {
            Logging.INFO.e("parse error", e);
            setMessage(getString(ParserTranslations.getErrorCode(e.getErrorCode())));
            return false;
        }
        setPlugin(plugin);
        return true;
    }



    public void setPlugin(SearchPlugin plugin) {
        this.message = null;
        this.plugin = plugin;

        updateView();

    }

    private void updateView() {
        if(mainBody == null) {
            // this might be called before the fragment is actually displayed
            return;
        }

        mainBody.removeAllViews();

        if(message != null) {
            pluginName.setVisibility(View.INVISIBLE);
            icon.setVisibility(View.INVISIBLE);

            View inflate = LayoutInflater.from(getContext()).inflate(R.layout.info_message, mainBody, false);
            TextView viewById = inflate.findViewById(R.id.message);
            viewById.setText(message);
            mainBody.addView(inflate);
        } else if(plugin != null) {
            pluginName.setVisibility(View.VISIBLE);
            icon.setVisibility(View.VISIBLE);

            pluginName.setText(plugin.getName());
            icon.setAddress(plugin.getIcon());

            if(plugin.supportsSuggestions()) {
                addItem(v -> v.setVisibility(View.GONE),
                        v -> v.setText(R.string.suggestion_supported));
            }
            for (Map.Entry<PropertyName, PropertyValue> entry : plugin.getProperties().entrySet()) {
                addItem(v -> v.setText(ParserTranslations.getPropertyName(entry.getKey())),
                        v -> showPropertyValue(entry.getValue(), v));
            }
        }
    }

    public void setMessage(String message) {
        this.message = message;
        this.plugin = null;

        updateView();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.info_fragment, container, false);

        mainBody = view.findViewById(R.id.main_body);
        pluginName = view.findViewById(R.id.plugin_name);
        icon = view.findViewById(R.id.icon);

        updateView();

        return view;
    }
}
