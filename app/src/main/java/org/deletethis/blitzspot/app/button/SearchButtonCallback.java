package org.deletethis.blitzspot.app.button;

import android.graphics.Point;

interface SearchButtonCallback {
    void onButtonLongTouch();
    void onButtonClick();
    void preventButtonTimeout();
    void enableButtonTimeout();
    void onButtonDragged(int x, int y);
    Point getCurrentPosition();
}
