package com.mojahid.simple_qr_code_scanner.history;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ScanHistory.class}, version = 1, exportSchema = false)
public abstract class ScanDatabase extends RoomDatabase {
    public abstract ScanHistoryDao scanHistoryDao();
}
