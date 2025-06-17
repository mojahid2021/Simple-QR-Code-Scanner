package com.mojahid.simple_qr_code_scanner;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.room.Room;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.*;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.mojahid.simple_qr_code_scanner.history.ScanDatabase;
import com.mojahid.simple_qr_code_scanner.history.ScanHistory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private boolean hasScanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previewView = findViewById(R.id.previewView);
        cameraExecutor = Executors.newSingleThreadExecutor();

        Button historyButton = findViewById(R.id.historyButton);
        historyButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
            startActivity(intent);
        });

        Button generateQRButton = findViewById(R.id.generateQRButton);
        generateQRButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GenerateQRActivity.class);
            startActivity(intent);
        });


        checkCameraPermission();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void showPermissionDeniedDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Camera Permission Required")
                .setMessage("This app needs camera access to scan QR codes. Please enable it in settings.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                    intent.setData(uri);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> finish())
                .setCancelable(false)
                .show();
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::scanQRCode);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (Exception e) {
                Log.e("CameraX", "Failed to bind camera use cases", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void scanQRCode(ImageProxy imageProxy) {
        if (hasScanned) {
            imageProxy.close();
            return;
        }
        @SuppressWarnings("UnsafeOptInUsageError")
        Image mediaImage = imageProxy.getImage();
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanner scanner = BarcodeScanning.getClient();

            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            hasScanned = true;
                        }
                        for (Barcode barcode : barcodes) {
                            int valueType = barcode.getValueType();
                            String scannedData = barcode.getRawValue();
                            saveToHistory(scannedData, valueType);
                            copyToClipboard(scannedData);

                            switch (valueType) {
                                case Barcode.TYPE_URL:
                                    String url = barcode.getUrl().getUrl();
                                    showToast("Website: " + url);
                                    openWebPage(url);
                                    break;

                                case Barcode.TYPE_PHONE:
                                    String phoneNumber = barcode.getPhone().getNumber();
                                    showToast("Call: " + phoneNumber);
                                    dialPhoneNumber(phoneNumber);
                                    break;

                                case Barcode.TYPE_EMAIL:
                                    String email = barcode.getEmail().getAddress();
                                    showToast("Email: " + email);
                                    sendEmail(email);
                                    break;

                                case Barcode.TYPE_SMS:
                                    String smsNumber = barcode.getSms().getPhoneNumber();
                                    String smsMessage = barcode.getSms().getMessage();
                                    showToast("SMS to: " + smsNumber + "\nMessage: " + smsMessage);
                                    sendSMS(smsNumber, smsMessage);
                                    break;

                                case Barcode.TYPE_TEXT:
                                    String text = barcode.getRawValue();
                                    showToast("Text: " + text);
                                    break;

                                case Barcode.TYPE_WIFI:
                                    Barcode.WiFi wifi = barcode.getWifi();
                                    String ssid = wifi.getSsid();
                                    String password = wifi.getPassword();
                                    int encryptionType = wifi.getEncryptionType();

                                    autoConnectWiFi(barcode.getWifi());

                                    showToast("WiFi SSID: " + ssid + "\nPassword: " + password);
                                    break;

                                case Barcode.TYPE_GEO:
                                    Barcode.GeoPoint geo = barcode.getGeoPoint();
                                    showToast("Location: Lat " + geo.getLat() + ", Long " + geo.getLng());
                                    openGoogleMaps(geo.getLat(), geo.getLng());
                                    break;

                                case Barcode.TYPE_CONTACT_INFO:
                                    Barcode.ContactInfo contact = barcode.getContactInfo();
                                    showToast("Contact: " + contact.getName().getFormattedName());
                                    break;

                                case Barcode.TYPE_CALENDAR_EVENT:
                                    Barcode.CalendarEvent event = barcode.getCalendarEvent();
                                    showToast("Event: " + event.getDescription() + "\nAt: " + event.getStart());
                                    break;

                                case Barcode.TYPE_DRIVER_LICENSE:
                                    Barcode.DriverLicense license = barcode.getDriverLicense();
                                    showToast("Driver License No: " + license.getLicenseNumber());
                                    break;

                                case Barcode.TYPE_ISBN:
                                    String isbn = barcode.getRawValue();
                                    showToast("Book ISBN: " + isbn);
                                    break;

                                case Barcode.TYPE_PRODUCT:
                                    String productCode = barcode.getRawValue();
                                    showToast("Product Barcode: " + productCode);
                                    break;

                                default:
                                    showToast("Unknown QR Code");
                            }
                        }
                    })
                    .addOnFailureListener(e -> Log.e("MLKit", "QR Code scanning failed", e))
                    .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }
    private void saveToHistory(String data, int type) {
        new Thread(() -> {
            ScanDatabase db = Room.databaseBuilder(getApplicationContext(), ScanDatabase.class, "scan_db").build();
            db.scanHistoryDao().insert(new ScanHistory(data, String.valueOf(type), System.currentTimeMillis()));
        }).start();
    }


    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Scanned QR Code", text);
        clipboard.setPrimaryClip(clip);
        showToast("Copied to clipboard!");
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void openWebPage(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
    }

    private void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        startActivity(intent);
    }

    private void sendEmail(String email) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:" + email));
        startActivity(intent);
    }

    private void sendSMS(String phoneNumber, String message) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("smsto:" + phoneNumber));
        intent.putExtra("sms_body", message);
        startActivity(intent);
    }

    private void autoConnectWiFi(Barcode.WiFi wifi) {
        String ssid = wifi.getSsid();
        String password = wifi.getPassword();
        int encryptionType = wifi.getEncryptionType();

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = "\"" + ssid + "\"";

        if (encryptionType == Barcode.WiFi.TYPE_WPA) {
            wifiConfig.preSharedKey = "\"" + password + "\"";
        } else if (encryptionType == Barcode.WiFi.TYPE_WEP) {
            wifiConfig.wepKeys[0] = "\"" + password + "\"";
            wifiConfig.wepTxKeyIndex = 0;
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        } else {
            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        showToast("Connected to WiFi: " + ssid);
    }


    private void openGoogleMaps(double lat, double lng) {
        String uri = "geo:" + lat + "," + lng;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                showPermissionDeniedDialog();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}