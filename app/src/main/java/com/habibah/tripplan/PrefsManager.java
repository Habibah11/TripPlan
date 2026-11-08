package com.habibah.tripplan;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

/**
 * Pengelola SharedPreferences. Setiap preferensi punya fungsi simpan dan ambil sendiri.
 * Penyimpanan memakai apply(), pembacaan memakai getSharedPreferences().
 */
public class PrefsManager {
    private static final String FILE = "tripplan_prefs";

    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_EMAIL = "user_email";
    private static final String KEY_DARK_MODE = "dark_mode";
    private static final String KEY_CURRENCY = "currency";
    private static final String KEY_ONBOARDING_DONE = "onboarding_done";
    private static final String KEY_CONFIRM_DELETE = "confirm_delete";
    private static final String KEY_SHOW_TIPS = "show_tips";

    private final Context context;

    public PrefsManager(Context context) {
        this.context = context.getApplicationContext();
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(FILE, Context.MODE_PRIVATE);
    }

    // ---- Nama pengguna ----
    public void saveUserName(String name) {
        prefs().edit().putString(KEY_USER_NAME, name).apply();
    }

    public String getUserName() {
        return prefs().getString(KEY_USER_NAME, "Mahasiswa");
    }

    // ---- Email pengguna ----
    public void saveUserEmail(String email) {
        prefs().edit().putString(KEY_USER_EMAIL, email).apply();
    }

    public String getUserEmail() {
        return prefs().getString(KEY_USER_EMAIL, "");
    }

    // ---- Tema terang / gelap ----
    public void saveDarkMode(boolean dark) {
        prefs().edit().putBoolean(KEY_DARK_MODE, dark).apply();
    }

    public boolean isDarkMode() {
        return prefs().getBoolean(KEY_DARK_MODE, false);
    }

    /** Menerapkan tema tersimpan ke seluruh aplikasi. */
    public void applyTheme() {
        AppCompatDelegate.setDefaultNightMode(isDarkMode()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

    // ---- Mata uang ----
    public void saveCurrency(String code) {
        prefs().edit().putString(KEY_CURRENCY, code).apply();
    }

    public String getCurrency() {
        return prefs().getString(KEY_CURRENCY, "IDR");
    }

    // ---- Status onboarding ----
    public void setOnboardingDone(boolean done) {
        prefs().edit().putBoolean(KEY_ONBOARDING_DONE, done).apply();
    }

    public boolean isOnboardingDone() {
        return prefs().getBoolean(KEY_ONBOARDING_DONE, false);
    }

    // ---- Konfirmasi sebelum hapus ----
    public void saveConfirmDelete(boolean value) {
        prefs().edit().putBoolean(KEY_CONFIRM_DELETE, value).apply();
    }

    public boolean isConfirmDelete() {
        return prefs().getBoolean(KEY_CONFIRM_DELETE, true);
    }

    // ---- Tampilkan tips di dashboard ----
    public void saveShowTips(boolean value) {
        prefs().edit().putBoolean(KEY_SHOW_TIPS, value).apply();
    }

    public boolean isShowTips() {
        return prefs().getBoolean(KEY_SHOW_TIPS, true);
    }
}
