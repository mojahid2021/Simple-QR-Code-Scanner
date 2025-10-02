package com.mojahid.simple_qr_code_scanner;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.IOException;
import java.io.OutputStream;

public class GenerateQRActivity extends AppCompatActivity {

    private Spinner spinnerType;
    private LinearLayout inputContainer;
    private EditText etInput, etWifiSSID, etWifiPassword, etWifiType;
    private EditText etContactName, etContactPhone, etContactEmail;
    private ImageView ivQRCode;
    private Button btnGenerate, btnSave;
    private Bitmap qrBitmap;

    private static final String[] QR_TYPES = {"Text", "URL", "WiFi", "Contact Info", "Email", "Phone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        spinnerType = findViewById(R.id.spinnerType);
        inputContainer = findViewById(R.id.inputContainer);
        ivQRCode = findViewById(R.id.ivQRCode);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnSave = findViewById(R.id.btnSave);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, QR_TYPES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);

        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateInputFields(QR_TYPES[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        btnGenerate.setOnClickListener(v -> generateQRCode());
        btnSave.setOnClickListener(v -> saveQRCode());

        updateInputFields(QR_TYPES[0]);
    }

    private void updateInputFields(String type) {
        inputContainer.removeAllViews();

        switch (type) {
            case "Text":
            case "URL":
            case "Email":
            case "Phone":
                etInput = new EditText(this);
                etInput.setHint("Enter " + type);
                inputContainer.addView(etInput);
                break;

            case "WiFi":
                etWifiSSID = new EditText(this);
                etWifiSSID.setHint("WiFi SSID");
                inputContainer.addView(etWifiSSID);

                etWifiPassword = new EditText(this);
                etWifiPassword.setHint("Password");
                inputContainer.addView(etWifiPassword);

                etWifiType = new EditText(this);
                etWifiType.setHint("Encryption Type (WPA/WEP/nopass)");
                etWifiType.setText("WPA");
                inputContainer.addView(etWifiType);
                break;

            case "Contact Info":
                etContactName = new EditText(this);
                etContactName.setHint("Name");
                inputContainer.addView(etContactName);

                etContactPhone = new EditText(this);
                etContactPhone.setHint("Phone");
                inputContainer.addView(etContactPhone);

                etContactEmail = new EditText(this);
                etContactEmail.setHint("Email");
                inputContainer.addView(etContactEmail);
                break;
        }
    }

    private void generateQRCode() {
        String data = getDataFromInputs();
        if (data == null || data.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            qrBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    qrBitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            ivQRCode.setImageBitmap(qrBitmap);
            btnSave.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to generate QR code", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDataFromInputs() {
        String selectedType = spinnerType.getSelectedItem().toString();

        switch (selectedType) {
            case "Text":
            case "URL":
                return etInput != null ? etInput.getText().toString() : "";

            case "Email":
                String email = etInput != null ? etInput.getText().toString() : "";
                return "mailto:" + email;

            case "Phone":
                String phone = etInput != null ? etInput.getText().toString() : "";
                return "tel:" + phone;

            case "WiFi":
                String ssid = etWifiSSID != null ? etWifiSSID.getText().toString() : "";
                String password = etWifiPassword != null ? etWifiPassword.getText().toString() : "";
                String type = etWifiType != null ? etWifiType.getText().toString() : "WPA";
                return "WIFI:T:" + type + ";S:" + ssid + ";P:" + password + ";;";

            case "Contact Info":
                String name = etContactName != null ? etContactName.getText().toString() : "";
                String phoneNum = etContactPhone != null ? etContactPhone.getText().toString() : "";
                String emailAddr = etContactEmail != null ? etContactEmail.getText().toString() : "";
                return "BEGIN:VCARD\nVERSION:3.0\nFN:" + name + "\nTEL:" + phoneNum + "\nEMAIL:" + emailAddr + "\nEND:VCARD";

            default:
                return "";
        }
    }

    private void saveQRCode() {
        if (qrBitmap == null) {
            Toast.makeText(this, "Generate a QR code first", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, "QRCode_" + System.currentTimeMillis() + ".png");
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QRCodes");

            Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (uri != null) {
                OutputStream outputStream = getContentResolver().openOutputStream(uri);
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                if (outputStream != null) {
                    outputStream.close();
                }
                Toast.makeText(this, "QR Code saved to Pictures/QRCodes", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
        }
    }
}
