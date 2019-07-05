package org.deletethis.blitzspot.app.activities.jump;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.activities.LaunchSearchActivity;
import org.deletethis.blitzspot.app.dao.DbOpenHelper;
import org.deletethis.blitzspot.app.dao.SelectSearchPlugins;
import org.deletethis.blitzspot.lib.db.QueryRunner;
import org.deletethis.blitzspot.app.fragments.choose.ChooseFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

public class JumpActivity extends AppCompatActivity {
    public static final String QUERY = JumpActivity.class.getName() + ".QUERY";

    private ChooseFragment fragment;
    private String query;
    private CheckBox editableCheckbox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jump_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragment = (ChooseFragment)fragmentManager.findFragmentById(R.id.fragment);
        editableCheckbox = findViewById(R.id.editable_select);

        findViewById(R.id.cancel).setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });


        query = getIntent().getStringExtra(QUERY);
        if(query == null)
            throw new IllegalStateException();

        // we leave the checkbox initially visible, it's much more common case than
        // invisible, and it looks bad, when it appears later. We modify visibility,
        // not the enabled state, because the button has color overrides, which
        // do not respond well to changing of enabled state. We could also write proper
        // selectors but we are lazy.
        QueryRunner queryRunner = new QueryRunner(DbOpenHelper.PROVIDER, this, this);
        queryRunner.run(SelectSearchPlugins.get(this), items -> {
            if(items.isEmpty()) {
                fragment.setMessage(getString(R.string.no_plugins_message));
                editableCheckbox.setVisibility(View.INVISIBLE);
            } else {
                fragment.setDataSet(new MyDataSet(items, this::onItemClicked));
                editableCheckbox.setVisibility(View.VISIBLE);
            }
        });
    }

    private void onItemClicked(byte [] plugin) {

        boolean edit = editableCheckbox.isChecked();
        LaunchSearchActivity.launch(this, query, plugin, edit, false);
        finish();
    }
}
