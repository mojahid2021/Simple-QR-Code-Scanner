package com.mojahid.simple_qr_code_scanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class SettingsActivity extends AppCompatActivity {

    private Switch switchDarkMode;
    private Switch switchVibration;
    private Switch switchSound;
    private Switch switchAutoOpenUrl;
    private Switch switchCopyToClipboard;
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchVibration = findViewById(R.id.switchVibration);
        switchSound = findViewById(R.id.switchSound);
        switchAutoOpenUrl = findViewById(R.id.switchAutoOpenUrl);
        switchCopyToClipboard = findViewById(R.id.switchCopyToClipboard);

        loadSettings();

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? 
                AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("vibration", isChecked).apply();
        });

        switchSound.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("sound", isChecked).apply();
        });

        switchAutoOpenUrl.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("auto_open_url", isChecked).apply();
        });

        switchCopyToClipboard.setOnCheckedChangeListener((buttonView, isChecked) -> {
            preferences.edit().putBoolean("copy_to_clipboard", isChecked).apply();
        });

        Button btnAbout = findViewById(R.id.btnAbout);
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(intent);
        });
    }

    private void loadSettings() {
        switchDarkMode.setChecked(preferences.getBoolean("dark_mode", false));
        switchVibration.setChecked(preferences.getBoolean("vibration", true));
        switchSound.setChecked(preferences.getBoolean("sound", true));
        switchAutoOpenUrl.setChecked(preferences.getBoolean("auto_open_url", false));
        switchCopyToClipboard.setChecked(preferences.getBoolean("copy_to_clipboard", true));
    }
}
