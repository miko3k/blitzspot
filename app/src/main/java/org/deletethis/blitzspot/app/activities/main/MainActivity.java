package org.deletethis.blitzspot.app.activities.main;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.deletethis.blitzspot.app.InstantState;
import org.deletethis.blitzspot.app.Intents;
import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.add.AddActivity;
import org.deletethis.blitzspot.app.activities.info.InfoActivity;
import org.deletethis.blitzspot.app.activities.search.SearchActivity;
import org.deletethis.blitzspot.app.activities.settings.SettingsActivity;
import org.deletethis.blitzspot.app.dao.DbConfig;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.InsertSearchPlugin;
import org.deletethis.blitzspot.app.dao.PluginWithId;
import org.deletethis.blitzspot.app.dao.SelectSearchPlugins;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.lib.Logging;
import org.deletethis.blitzspot.lib.SimpleTextWatcher;
import org.deletethis.blitzspot.lib.SharedPreferenceLiveData;

import java.nio.charset.StandardCharsets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.LiveData;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity implements ItemTouchListener {
    private ViewGroup popup;
    private FloatingActionButton fab;

    private ItemTouchHelper touchHelper;
    // kinda similar to popup.getVisibility, but it's true only when it's fading in, not fading out
    private boolean popupVisible = false;
    private PluginWithId currentPlugin;
    private MyAdapter.MyViewHolder currentEngineView;
    private MyAdapter adapter;
    private QueryRunner queryRunner;
    private CoordinatorLayout coordinatorLayout;
    private Button settings;

    private static final int REQUEST_CODE_ADD = 41;
    private static final int REQUEST_CODE_PICKER = 42;
    private static final int REQUEST_CODE_INFO = 43;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        if(InstantState.isEnabled(this)) {
            InstantState.get(this).start(this);
        }

        popup = findViewById(R.id.popup);
        coordinatorLayout = findViewById(R.id.activityRoot);

        RecyclerView recyclerView = findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        LiveData<Boolean> privateMode = SharedPreferenceLiveData.ofBoolean(
                PreferenceManager.getDefaultSharedPreferences(this),
                getString(R.string.pref_private_mode),
                false
        );
        privateMode.observe(this, pvtMode -> {
            int id = pvtMode ? R.drawable.main_private_mode : R.drawable.main_settings;
            settings.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, id, 0);
        });

        // specify an adapter (see also next example)
        adapter = new MyAdapter(this);
        ItemTouchHelper.Callback callback =
                new TouchHelperCallback(adapter, this);
        touchHelper = new ItemTouchHelper(callback);
        recyclerView.setAdapter(adapter);
        touchHelper.attachToRecyclerView(recyclerView);

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    return hidePopup();
                } else {
                    return false;
                }
            }
        });

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(v->{
                hidePopup();
                startActivityForResult(
                        new Intent(this, AddActivity.class),
                        REQUEST_CODE_ADD);
        });

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            overridePendingTransition(R.anim.view_transition_fade_in_long, R.anim.view_transition_fade_out_long);
        });



        TextView details = findViewById(R.id.popup_details);
        details.setOnClickListener(v -> {
            Intent intent = InfoActivity.createIntent(this, currentPlugin.serialize(), null);
            Logging.MAIN.dOnly("plugin:" + new String(currentPlugin.serialize(), StandardCharsets.ISO_8859_1));
            startActivity(intent);
            hidePopup();
        });
        TextView rename = findViewById(R.id.popup_rename);
        rename.setOnClickListener(this::showRenameDialog);

        TextView remove = findViewById(R.id.popup_remove);
        remove.setOnClickListener(this::removeItem);

        queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
    }

    private void doRename(PluginWithId plugin, String name) {
        Logging.MAIN.i("rename plugin " + plugin.getId() + ": '" + name + "'");

        name = name.trim();
        if(name.isEmpty())
            name = null;

        byte [] data = plugin.patch().name(name).build().serialize();
        System.out.println(new String(data, StandardCharsets.UTF_8));

        queryRunner.run((db, cancel) -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConfig.ENGINE_DATA, data);
            db.update(DbConfig.ENGINES, contentValues, DbConfig.ENGINE_ID + " = " + plugin.getId(), null);
            return SelectSearchPlugins.get(this).execute(db, cancel);
        }, adapter::setSearchEngines);
    }

    private void showRenameDialog(View ignored) {
        PluginWithId engine = currentPlugin;

        hidePopup();

        final EditText input = new EditText(this);

        int padding = (int) getResources().getDimension(R.dimen.mainRenamePadding);
        FrameLayout.LayoutParams params = new  FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(padding, padding, padding, padding);
        input.setLayoutParams(params);
        FrameLayout container = new FrameLayout(this);
        container.addView(input);

        InputMethodManager imm = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setView(container);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> doRename(engine, input.getText().toString()));
        builder.setNegativeButton(R.string.cancel, (dialog, which) -> dialog.cancel());
        builder.setNeutralButton(R.string.dflt, null);
        if(imm != null) {
            builder.setOnDismissListener(dialog -> {
                View currentFocus = getCurrentFocus();
                if(currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }
            });
        }
        AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(dialog -> {
            String originalName = engine.patch().clear().build().getName();
            String currentName = engine.getSearchPlugin().getName();

            input.requestFocus();
            if(imm != null) {
                imm.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
            }

            Button neutral = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            neutral.setOnClickListener(v -> input.setText(""));
            input.addTextChangedListener(new SimpleTextWatcher(
                    s-> neutral.setEnabled(!s.trim().isEmpty()))
            );

            input.setHint(originalName);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            // maybe we could just check if field in patch is null, this seems safer
            input.setText(originalName.equals(currentName) ? "" : currentName);
            input.selectAll();
        });

        alertDialog.show();
    }

    private void removeItem(View ignored) {
        final MyAdapter.MyViewHolder holder = this.currentEngineView;
        ViewGroup item = holder.getViewGroup();
        hidePopup();

        item.animate()
                .translationX(item.getWidth())
                .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        onRemoved(holder);
                    }
                });
    }


    @Override
    public void onRemoved(MyAdapter.MyViewHolder position) {
        PluginWithId pluginWithId = adapter.getEngineWithId(position);
        long id = pluginWithId.getId();
        adapter.removeItem(id);

        queryRunner.run((db, cancel) -> {
            db.delete(DbConfig.ENGINES, DbConfig.ENGINE_ID + " = " + id, null);
            return SelectSearchPlugins.get(this).execute(db, cancel);
        }, adapter::setSearchEngines);
    }

    @Override
    public void onMoved(long what, double ordering) {
        queryRunner.run((db, cancel) -> {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DbConfig.ENGINE_ORDERING, ordering);
            db.update(DbConfig.ENGINES, contentValues, DbConfig.ENGINE_ID + " = " + what, null);
            return SelectSearchPlugins.get(this).execute(db, cancel);
        }, adapter::setSearchEngines);
    }

    private PluginWithId getEngineWithId(MyAdapter.MyViewHolder viewHolder) {
        return adapter.getEngineWithId(viewHolder);
    }

    @Override
    public void onItemClick(MyAdapter.MyViewHolder viewHolder) {
        Intent intent = SearchActivity.createIntent(this, getEngineWithId(viewHolder).serialize(), null);
        //intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        //EditText editText = (EditText) findViewById(R.id.editText);
        //String message = editText.getText().toString();
        //intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
        overridePendingTransition(R.anim.view_transition_fade_in, 0);
        Logging.MAIN.i("item clicked");
    }

    private void showPopup(Rect selectedRect, MyAdapter.MyViewHolder viewHolder) {
        Logging.MAIN.i("showing popup");

        int popupHeight = popup.getHeight();
        // always recreate the drawable even when it's the same ...
        boolean above = popupHeight <= selectedRect.top;
        int y;
        if(above) {
            y = selectedRect.top - popupHeight;
        } else {
            y = selectedRect.bottom;
        }



        popup.setLayoutParams(MainActivityUtil.createCoordinatorLayoutParams(
                (int)getResources().getDimension(R.dimen.bubbleX),
                y
        ));

        MainActivityUtil.animatePopupAppearance(this, popup, above);
        popupVisible = true;
        this.currentPlugin = getEngineWithId(viewHolder);
        this.currentEngineView = viewHolder;
    }


    private boolean hidePopup() {
        if(!popupVisible)
            return false;

        if(popup.getVisibility() == View.INVISIBLE)
            return false;

        currentPlugin = null;
        currentEngineView = null;
        popupVisible = false;
        MainActivityUtil.animatePopupDisappearance(this, popup);
        return true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        fab.hide();
    }


    @Override
    protected void onResume() {
        super.onResume();
        fab.show();
        queryRunner.run(SelectSearchPlugins.get(this), adapter::setSearchEngines);
    }



    @Override
    public void onItemLongClick(MyAdapter.MyViewHolder viewHolder) {
        Rect offsetViewBounds = new Rect();
        viewHolder.getViewGroup().getDrawingRect(offsetViewBounds);
        coordinatorLayout.offsetDescendantRectToMyCoords(viewHolder.getViewGroup(), offsetViewBounds);
        Logging.MAIN.i("ItemLongClick: " + offsetViewBounds);

        showPopup(offsetViewBounds, viewHolder);

        touchHelper.startDrag(viewHolder);
    }


    @Override
    public void onFarDragged(MyAdapter.MyViewHolder viewHolder, int actionState) {
        Logging.MAIN.i("onFarDragged: " + actionState);

        hidePopup();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        super.onActivityResult(requestCode, resultCode, resultData);
        if (resultCode != Activity.RESULT_OK || resultData == null) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_ADD:
                if (AddActivity.LOCAL_FILE.equals(resultData.getData())) {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("*/*");

                    // only one which actually works is text/xml
                    String[] mimetypes = {
                            "text/xml",
                            "application/xml",
                            "application/opensearchdescription+xml",
                    };
                    intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                    startActivityForResult(intent, REQUEST_CODE_PICKER);
                }
                break;

            case REQUEST_CODE_PICKER:
                byte[] data = MainActivityUtil.readUri(this, resultData.getData());
                if (data == null) {
                    startActivity(InfoActivity.createMessageIntent(this,
                            getString(R.string.unable_to_read_file),
                            getString(R.string.add)));
                } else {
                    startActivityForResult(
                            InfoActivity.createIntent(this, data, getString(R.string.add)),
                            REQUEST_CODE_INFO);
                    // do not finish
                }
                break;

            case REQUEST_CODE_INFO:
                byte[] engineData = Intents.getPluginExtra(resultData);
                queryRunner.runUncancellable(new InsertSearchPlugin(engineData), unused ->
                        queryRunner.run(SelectSearchPlugins.get(this), adapter::setSearchEngines));
                break;
        }
    }

}
