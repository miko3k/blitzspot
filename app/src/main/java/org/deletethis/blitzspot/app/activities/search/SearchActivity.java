package org.deletethis.blitzspot.app.activities.search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.deletethis.blitzspot.app.Intents;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.lib.Browser;
import org.deletethis.blitzspot.lib.volley.VolleySingleton;
import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProvider;
import org.deletethis.blitzspot.app.activities.search.suggest.ChoiceProviderImpl;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.InsertHistory;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.SimpleTextWatcher;
import org.deletethis.search.parser.PluginParseException;
import org.deletethis.search.parser.SearchPlugin;

import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import com.google.common.collect.ImmutableList;

import org.deletethis.search.parser.SearchQuery;

public class SearchActivity extends AppCompatActivity implements ChoiceProvider.Listener, AdapterListener {

    private final static int MIN_CHOICES = 4;
    private MyAdapter adapter;
    private SearchPlugin searchPlugin;
    private SearchEditText query;
    private ImageButton action;
    private ChoiceProvider choiceProvider;

    private ChoiceProvider.Choice defaultUrl;
    private int visibleChoicesCount = 0;
    private ChoiceProvider.Choice lastSelectedChoice;
    private QueryRunner queryRunner;

    private final static String QUERY_EXTRA = SearchActivity.class.getName() + ".QUERY";

    public static Intent createIntent(Context context, byte [] source, String query) {
        Intent intent = new Intent(context, SearchActivity.class);
        Intents.putPluginExtra(intent, source);
        if(query != null) {
            intent.putExtra(QUERY_EXTRA, query);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
        getLifecycle().addObserver(queryRunner);

        query = findViewById(R.id.query);
        action = findViewById(R.id.action);
        RecyclerView recyclerView = findViewById(R.id.suggestions);

        query.setOnBack(this::finish);
        action.setOnClickListener((v) -> query.setText(""));
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnLayoutChangeListener((v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            int oldH = oldBottom - oldTop;
            int h = bottom - top;

            if(oldH != h) {
                visibleChoicesCount = h / (int)getResources().getDimension(R.dimen.suggestionItemHeight);
                choiceProvider.setCount(Math.max(visibleChoicesCount, MIN_CHOICES));
            }
        });
        adapter = new MyAdapter(this);
        recyclerView.setAdapter(adapter);

        query.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH || event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                if(defaultUrl != null)
                    onSearchSuggestionClicked(defaultUrl);
                return true;
            } else {
                return false;
            }
        });
        Intent intent = getIntent();
        try {
            searchPlugin = PluginFactory.get(this).load(Intents.getPluginExtra(intent));
        } catch (PluginParseException e) {
            Logging.SEARCH.e("unable to parse plugin", e);
            finish();
        }
        query.setHint(searchPlugin.getName());

        String extraQuery = intent.getStringExtra(QUERY_EXTRA);
        if(extraQuery == null)
            extraQuery = "";

        choiceProvider = new ChoiceProviderImpl(
                VolleySingleton.getInstance(this),
                searchPlugin,
                queryRunner,
                this,
                SearchQuery.of(extraQuery),
                0);

        query.setText(extraQuery);
        // listener is the last, we do not want to trigger suggestion refresh
        // it will be triggered by first setCound
        query.addTextChangedListener(new SimpleTextWatcher(str -> {
            updateAction(str);
            if (choiceProvider != null) {
                if (lastSelectedChoice != null && lastSelectedChoice.getValue().equals(str)) {
                    choiceProvider.setQueryFromSuggestion(lastSelectedChoice);
                } else {
                    choiceProvider.setQuery(str);
                }
            }
        }));
    }

    @Override
    public void onSearchChoices(ImmutableList<ChoiceProvider.Choice> choiceList, ChoiceProvider.Choice defaultUrl) {
        adapter.setSuggestions(choiceList);
        adapter.notifyDataSetChanged();
        this.defaultUrl = defaultUrl;
    }

    private void updateAction(String str) {
        if(str.isEmpty()) {
            action.setVisibility(View.INVISIBLE);
        } else {
            action.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.view_transition_fade_out);
    }

    @Override
    protected void onStart() {
        Logging.SEARCH.i("activity started");
        super.onStart();
    }

    @Override
    protected void onResume() {
        Logging.SEARCH.i("activity resumed");
        super.onResume();

        // there were cases when we came back to this activity without keyboard. This is an
        // attempt to prevent it. No idea why.
        if (query.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) {
                imm.showSoftInput(query, InputMethodManager.SHOW_IMPLICIT);
            }
        }

    }


    @Override
    protected void onStop() {
        Logging.SEARCH.i("activity stopped");
        super.onStop();
        choiceProvider.cancelAll();
    }

    @Override
    public void onSearchSuggestionUsed(ChoiceProvider.Choice choice) {
        this.lastSelectedChoice = choice;
        this.query.setText(choice.getValue());
    }

    @Override
    public void onSearchSuggestionClicked(ChoiceProvider.Choice choice) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if(!sharedPreferences.getBoolean(getString(R.string.pref_private_mode), false)) {
            queryRunner.runUncancellable(new InsertHistory(searchPlugin.getIdentifier(), choice));
        }

        String url = choice.getUri();
        if(url == null) {
            SearchQuery searchQuery = choice.getSearchQuery();
            url = searchPlugin.getSearchRequest(searchQuery).getUrl();
        }

        Browser.openBrowser(this, url);
    }

    @Override
    public void onSearchSuggestionRemoved(ChoiceProvider.Choice choice) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.suggestion_removal_confirm)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> choice.remove())
                .setNegativeButton(android.R.string.no, null).show();
    }
}
