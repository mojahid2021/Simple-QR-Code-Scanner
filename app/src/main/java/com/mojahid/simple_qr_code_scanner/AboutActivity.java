package com.mojahid.simple_qr_code_scanner;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        TextView tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText("Version 2.0.0");

        Button btnGithub = findViewById(R.id.btnGithub);
        btnGithub.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, 
                Uri.parse("https://github.com/mojahid2021/Simple-QR-Code-Scanner"));
            startActivity(intent);
        });

        Button btnRate = findViewById(R.id.btnRate);
        btnRate.setOnClickListener(v -> {
            // Open Play Store rating page
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent);
            } catch (Exception e) {
                Intent intent = new Intent(Intent.ACTION_VIEW, 
                    Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                startActivity(intent);
            }
        });

        Button btnShare = findViewById(R.id.btnShare);
        btnShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "QR Scanner Pro");
            shareIntent.putExtra(Intent.EXTRA_TEXT, 
                "Check out QR Scanner Pro - A powerful QR code scanner! " +
                "https://github.com/mojahid2021/Simple-QR-Code-Scanner");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });
    }
}
