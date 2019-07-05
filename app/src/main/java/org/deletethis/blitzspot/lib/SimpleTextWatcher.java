package org.deletethis.blitzspot.lib;

import android.text.Editable;
import android.text.TextWatcher;

import java.util.function.Consumer;

public class SimpleTextWatcher implements TextWatcher {
    private final Consumer<String> function;

    public SimpleTextWatcher(Consumer<String> function) {
        this.function = function;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        function.accept(s.toString());
    }
}
