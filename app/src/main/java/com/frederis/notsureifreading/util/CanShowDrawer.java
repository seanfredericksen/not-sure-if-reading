package com.frederis.notsureifreading.util;

import mortar.Blueprint;

public interface CanShowDrawer<S extends Blueprint> {
    void showDrawer(S screen);
}
