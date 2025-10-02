package com.mojahid.simple_qr_code_scanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.InputStream;
import java.util.List;

public class ScanFromImageActivity extends AppCompatActivity {

    private ImageView ivSelectedImage;
    private TextView tvResult;
    private Button btnSelectImage;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_from_image);

        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        tvResult = findViewById(R.id.tvResult);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri imageUri = result.getData().getData();
                    if (imageUri != null) {
                        processImage(imageUri);
                    }
                }
            }
        );

        btnSelectImage.setOnClickListener(v -> openImagePicker());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        imagePickerLauncher.launch(intent);
    }

    private void processImage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ivSelectedImage.setImageBitmap(bitmap);

            InputImage image = InputImage.fromBitmap(bitmap, 0);
            BarcodeScanner scanner = BarcodeScanning.getClient();

            scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    if (barcodes.isEmpty()) {
                        tvResult.setText("No QR code found in the image");
                    } else {
                        StringBuilder resultText = new StringBuilder();
                        for (Barcode barcode : barcodes) {
                            String rawValue = barcode.getRawValue();
                            resultText.append("Detected: ").append(rawValue).append("\n\n");
                            
                            int valueType = barcode.getValueType();
                            String typeStr = getTypeString(valueType);
                            resultText.append("Type: ").append(typeStr).append("\n");
                        }
                        tvResult.setText(resultText.toString());
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to scan image: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                });

        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private String getTypeString(int type) {
        switch (type) {
            case Barcode.TYPE_URL: return "URL";
            case Barcode.TYPE_EMAIL: return "Email";
            case Barcode.TYPE_PHONE: return "Phone";
            case Barcode.TYPE_SMS: return "SMS";
            case Barcode.TYPE_TEXT: return "Text";
            case Barcode.TYPE_WIFI: return "WiFi";
            case Barcode.TYPE_GEO: return "Location";
            case Barcode.TYPE_CONTACT_INFO: return "Contact";
            case Barcode.TYPE_CALENDAR_EVENT: return "Calendar Event";
            case Barcode.TYPE_DRIVER_LICENSE: return "Driver License";
            case Barcode.TYPE_ISBN: return "ISBN";
            case Barcode.TYPE_PRODUCT: return "Product";
            default: return "Unknown";
        }
    }
}
