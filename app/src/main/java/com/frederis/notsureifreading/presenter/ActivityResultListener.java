package com.frederis.notsureifreading.presenter;

import android.content.Intent;

public interface ActivityResultListener {
    boolean onActivityResult(int requestCode, int resultCode, Intent data);
}
