package com.mojahid.simple_qr_code_scanner;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;
import com.mojahid.simple_qr_code_scanner.history.ScanDatabase;
import com.mojahid.simple_qr_code_scanner.history.ScanHistory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BatchScanActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private PreviewView previewView;
    private ExecutorService cameraExecutor;
    private RecyclerView recyclerView;
    private BatchScanAdapter adapter;
    private List<String> scannedCodes;
    private Set<String> uniqueCodes;
    private TextView tvCount;
    private ScanDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_batch_scan);

        previewView = findViewById(R.id.previewView);
        recyclerView = findViewById(R.id.recyclerViewBatch);
        tvCount = findViewById(R.id.tvCount);
        
        scannedCodes = new ArrayList<>();
        uniqueCodes = new HashSet<>();
        adapter = new BatchScanAdapter(scannedCodes);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        database = Room.databaseBuilder(getApplicationContext(), ScanDatabase.class, "scan_db").build();
        cameraExecutor = Executors.newSingleThreadExecutor();

        Button btnClear = findViewById(R.id.btnClearBatch);
        btnClear.setOnClickListener(v -> clearScannedCodes());

        Button btnSaveAll = findViewById(R.id.btnSaveAll);
        btnSaveAll.setOnClickListener(v -> saveAllToHistory());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = 
            ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                CameraSelector cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build();

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build();

                imageAnalysis.setAnalyzer(cameraExecutor, this::scanQRCode);

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, 
                    cameraSelector, preview, imageAnalysis);
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (Exception e) {
                Log.e("CameraX", "Failed to bind camera use cases", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void scanQRCode(ImageProxy imageProxy) {
        @SuppressWarnings("UnsafeOptInUsageError")
        Image mediaImage = imageProxy.getImage();
        
        if (mediaImage != null) {
            InputImage image = InputImage.fromMediaImage(mediaImage, 
                imageProxy.getImageInfo().getRotationDegrees());
            BarcodeScanner scanner = BarcodeScanning.getClient();

            scanner.process(image)
                .addOnSuccessListener(barcodes -> {
                    for (Barcode barcode : barcodes) {
                        String scannedData = barcode.getRawValue();
                        if (scannedData != null && !uniqueCodes.contains(scannedData)) {
                            uniqueCodes.add(scannedData);
                            scannedCodes.add(scannedData);
                            runOnUiThread(() -> {
                                adapter.notifyItemInserted(scannedCodes.size() - 1);
                                recyclerView.smoothScrollToPosition(scannedCodes.size() - 1);
                                updateCount();
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("MLKit", "QR Code scanning failed", e))
                .addOnCompleteListener(task -> imageProxy.close());
        } else {
            imageProxy.close();
        }
    }

    private void clearScannedCodes() {
        scannedCodes.clear();
        uniqueCodes.clear();
        adapter.notifyDataSetChanged();
        updateCount();
        Toast.makeText(this, "Cleared all scanned codes", Toast.LENGTH_SHORT).show();
    }

    private void saveAllToHistory() {
        if (scannedCodes.isEmpty()) {
            Toast.makeText(this, "No codes to save", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            for (String code : scannedCodes) {
                database.scanHistoryDao().insert(
                    new ScanHistory(code, "7", System.currentTimeMillis())
                );
            }
            runOnUiThread(() -> {
                Toast.makeText(this, "Saved " + scannedCodes.size() + 
                    " codes to history", Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    private void updateCount() {
        tvCount.setText("Scanned: " + scannedCodes.size());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }
}
