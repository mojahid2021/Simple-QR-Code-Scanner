package com.mojahid.simple_qr_code_scanner.history;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "scan_history")
public class ScanHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String data;
    public String type;
    public long timestamp;

    public ScanHistory(String data, String type, long timestamp) {
        this.data = data;
        this.type = type;
        this.timestamp = timestamp;
    }
}

