package com.mojahid.simple_qr_code_scanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class BatchScanAdapter extends RecyclerView.Adapter<BatchScanAdapter.ViewHolder> {
    
    private List<String> codesList;

    public BatchScanAdapter(List<String> codesList) {
        this.codesList = codesList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_batch_scan, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String code = codesList.get(position);
        holder.tvCode.setText(code);
        holder.tvIndex.setText("#" + (position + 1));
        
        holder.itemView.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) v.getContext()
                .getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Scanned Code", code);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(v.getContext(), "Copied: " + code, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return codesList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode;
        TextView tvIndex;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvBatchCode);
            tvIndex = itemView.findViewById(R.id.tvBatchIndex);
        }
    }
}
