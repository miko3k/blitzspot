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

package org.deletethis.blitzspot.app.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.deletethis.blitzspot.app.R;
import org.deletethis.blitzspot.app.builtin.PluginFactory;
import org.deletethis.blitzspot.lib.db.Database;
import org.deletethis.blitzspot.lib.db.DatabaseProvider;
import org.deletethis.blitzspot.lib.db.DatabaseProviderImpl;
import org.deletethis.blitzspot.lib.db.openhelper.ScriptOpenHelper;

public class DbOpenHelper extends ScriptOpenHelper implements Database {
    public static final DatabaseProvider PROVIDER = new DatabaseProviderImpl(DbOpenHelper::new);

    private DbOpenHelper(Context context) {
        super(context, DbConfig.FILE, null, DbConfig.VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys = ON");
        db.execSQL("PRAGMA strict=ON");
        db.execSQL("PRAGMA case_sensitive_like=ON");
    }

    @SuppressWarnings("SwitchStatementWithTooFewBranches")
    @Override
    protected void upgrade(SQLiteDatabase db, int toVersion) {
        switch (toVersion) {
            case 1:
                executeScript(db, R.raw.database_v1);
                PluginFactory pluginFactory = PluginFactory.get(context);
                InsertSearchPlugin isp = new InsertSearchPlugin(pluginFactory.getOnStart());
                isp.execute(db, null);
                break;

            default:
                throw new IllegalStateException("unsupported version: " + toVersion);
        }
    }

    @Override
    protected void downgrade(SQLiteDatabase db, int fromVersion) {
        throw new UnsupportedOperationException("downgrade not supported");
    }
}