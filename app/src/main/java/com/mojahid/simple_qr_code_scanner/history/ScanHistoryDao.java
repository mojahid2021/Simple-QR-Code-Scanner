package com.mojahid.simple_qr_code_scanner.history;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ScanHistoryDao {
    @Insert
    void insert(ScanHistory scanHistory);

    @Delete
    void delete(ScanHistory scanHistory);

    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    List<ScanHistory> getAllScans();

    @Query("DELETE FROM scan_history")
    void clearAll();
}
