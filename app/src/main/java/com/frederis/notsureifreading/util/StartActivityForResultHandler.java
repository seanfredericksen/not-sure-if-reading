package com.frederis.notsureifreading.util;

import android.content.Intent;
import android.net.Uri;

public interface StartActivityForResultHandler {
    void startActivityForResult(Intent intent, int requestCode);
}
