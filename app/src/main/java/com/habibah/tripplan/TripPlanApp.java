package com.habibah.tripplan;

import android.app.Application;

public class TripPlanApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Terapkan tema tersimpan sebelum Activity pertama dibuat
        new PrefsManager(this).applyTheme();
    }
}
