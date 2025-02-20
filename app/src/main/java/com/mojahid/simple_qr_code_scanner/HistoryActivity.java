package com.mojahid.simple_qr_code_scanner;

import android.os.Bundle;
import android.os.Environment;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.mojahid.simple_qr_code_scanner.history.ScanDatabase;
import com.mojahid.simple_qr_code_scanner.history.ScanHistory;
import com.mojahid.simple_qr_code_scanner.history.ScanHistoryAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ScanHistoryAdapter adapter;
    private ScanDatabase database;
    private List<ScanHistory> scanHistoryList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        SearchView searchView = findViewById(R.id.searchView);
//        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                adapter.filterList(query);
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                adapter.filterList(newText);
//                return true;
//            }
//        });

        findViewById(R.id.btnExportCSV).setOnClickListener(v -> exportToCSV());
        findViewById(R.id.btnExportJSON).setOnClickListener(v -> exportToJSON());


        adapter = new ScanHistoryAdapter(scanHistoryList, this);
        recyclerView.setAdapter(adapter);

        database = Room.databaseBuilder(getApplicationContext(), ScanDatabase.class, "scan_db").build();
        loadHistory();
    }

    private void exportToCSV() {
        // Get the Downloads folder path (works on Android 10 and below)
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Magic QR");
        if (!folder.exists()) {
            folder.mkdirs(); // Create the folder if it doesn't exist
        }

        // Save the CSV file inside the "Magic QR" folder in Downloads
        File file = new File(folder, "scan_history.csv");
        try (FileWriter writer = new FileWriter(file)) {
            writer.append("Data,Timestamp\n");
            for (ScanHistory scan : scanHistoryList) {
                writer.append(scan.data).append(",").append(new Date(scan.timestamp).toString()).append("\n");
            }
            Toast.makeText(this, "CSV Exported: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void exportToJSON() {
        // Get the Downloads folder path (works on Android 10 and below)
        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Magic QR");
        if (!folder.exists()) {
            folder.mkdirs(); // Create the folder if it doesn't exist
        }

        // Save the JSON file inside the "Magic QR" folder in Downloads
        File file = new File(folder, "scan_history.json");
        try (FileWriter writer = new FileWriter(file)) {
            JSONArray jsonArray = new JSONArray();
            for (ScanHistory scan : scanHistoryList) {
                JSONObject json = new JSONObject();
                json.put("data", scan.data);
                json.put("timestamp", scan.timestamp);
                jsonArray.put(json);
            }
            writer.write(jsonArray.toString());
            Toast.makeText(this, "JSON Exported: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadHistory() {
        new Thread(() -> {
            List<ScanHistory> history = database.scanHistoryDao().getAllScans();
            runOnUiThread(() -> {
                scanHistoryList.clear();
                scanHistoryList.addAll(history);
                adapter.notifyDataSetChanged();
            });
        }).start();
    }
}
