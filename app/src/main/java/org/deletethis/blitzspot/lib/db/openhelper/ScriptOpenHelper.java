package org.deletethis.blitzspot.lib.db.openhelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.deletethis.blitzspot.lib.Logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.annotation.RawRes;

public abstract class ScriptOpenHelper extends SQLiteOpenHelper {
    protected final Context context;
    private final int version;

    protected ScriptOpenHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = Objects.requireNonNull(context);
        this.version = version;
    }

    protected void executeScript(SQLiteDatabase db, @RawRes int resourceId) {
        StringBuilder command = new StringBuilder();

        try(BufferedReader br = new BufferedReader(new InputStreamReader(
                context.getResources().openRawResource(resourceId), StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("--"))
                    continue;
                if (line.isEmpty())
                    continue;

                command.append(' ');
                command.append(line);
                if (line.endsWith(";")) {
                    String sql = command.toString();
                    Logging.DB.i("executing SQL: " + sql);
                    db.execSQL(sql);
                    command.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException();
        }
        if(command.length() > 0) {
            throw new IllegalStateException("unterminated query at the end of the script: '" + command + "'");
        }
    }

    abstract protected void upgrade(SQLiteDatabase db, int toVersion);
    abstract protected void downgrade(SQLiteDatabase db, int fromVersion);

    public void onCreate(SQLiteDatabase db) {
        for(int i=1;i<=version;++i) {
            upgrade(db, i);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i=oldVersion+1;i<=newVersion;++i) {
            upgrade(db, i);
        }
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for(int i=newVersion;i>oldVersion;--i) {
            downgrade(db, i);
        }
    }
}
