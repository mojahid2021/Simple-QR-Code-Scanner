package com.mojahid.simple_qr_code_scanner.history;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.mojahid.simple_qr_code_scanner.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ScanHistoryAdapter extends RecyclerView.Adapter<ScanHistoryAdapter.ViewHolder> {
    private List<ScanHistory> historyList;
    private Context context;

    public ScanHistoryAdapter(List<ScanHistory> historyList, Context context) {
        this.historyList = historyList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_scan_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanHistory scan = historyList.get(position);

        if (scan.type.equals("7")) {
            holder.typeTextView.setText("Text Type");
        }

        holder.dataTextView.setText(scan.data);
        holder.timestampTextView.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date(scan.timestamp)));

        holder.itemView.setOnClickListener(v -> {
            copyToClipboard(scan.data);
            openData(scan.data);
        });

        holder.deleteButton.setOnClickListener(v -> {
            deleteItem(position);
        });
    }

    private void copyToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Scanned QR Code", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show();
    }

    private void openData(String data) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, "Cannot open this data", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem(int position) {
        ScanHistory scan = historyList.get(position);
        new Thread(() -> {
            ScanDatabase db = Room.databaseBuilder(context, ScanDatabase.class, "scan_db").build();
            db.scanHistoryDao().delete(scan);
            ((Activity) context).runOnUiThread(() -> {
                historyList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, historyList.size());
            });
        }).start();
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dataTextView, timestampTextView, typeTextView;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            typeTextView = itemView.findViewById(R.id.typeTextView);
            dataTextView = itemView.findViewById(R.id.dataTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
