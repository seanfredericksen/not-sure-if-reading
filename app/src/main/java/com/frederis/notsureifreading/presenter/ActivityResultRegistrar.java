package com.frederis.notsureifreading.presenter;

import mortar.MortarScope;

public interface ActivityResultRegistrar {
    void register(MortarScope scope, ActivityResultListener listener);

}
