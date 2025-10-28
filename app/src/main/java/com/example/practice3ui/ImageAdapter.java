package com.example.practice3ui;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.VH> {

    private final Context context;
    private final List<Uri> data;

    private boolean selectionMode = false;
    private final Set<Integer> selectedPositions = new HashSet<>();

    private final OnSelectionChangeListener selectionListener;
    private final OnItemClick normalClick;

    public interface OnSelectionChangeListener {
        void onSelectionModeChanged(boolean enabled);
        void onSelectionCountChanged(int count);
    }

    public interface OnItemClick {
        void onClick(int pos);
    }

    public ImageAdapter(@NonNull Context context,
                        @NonNull List<Uri> data,
                        OnSelectionChangeListener selectionListener,
                        OnItemClick normalClick) {
        this.context = context;
        this.data = data;
        this.selectionListener = selectionListener;
        this.normalClick = normalClick;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_image, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Uri uri = data.get(position);
        holder.img.setImageURI(uri);

        // Show overlay if selected
        holder.overlay.setVisibility(selectedPositions.contains(position) ? View.VISIBLE : View.GONE);

        // Click
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            if (selectionMode) {
                toggleSelection(pos);
            } else {
                if (normalClick != null) normalClick.onClick(pos);
            }
        });

        // Long click to enter selection mode
        holder.itemView.setOnLongClickListener(v -> {
            int pos = holder.getBindingAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return false;

            if (!selectionMode) {
                selectionMode = true;
                toggleSelection(pos);
                if (selectionListener != null)
                    selectionListener.onSelectionModeChanged(true);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private void toggleSelection(int position) {
        if (selectedPositions.contains(position)) selectedPositions.remove(position);
        else selectedPositions.add(position);

        notifyItemChanged(position);

        if (selectionListener != null)
            selectionListener.onSelectionCountChanged(selectedPositions.size());

        if (selectionMode && selectedPositions.isEmpty()) {
            selectionMode = false;
            if (selectionListener != null)
                selectionListener.onSelectionModeChanged(false);
        }
    }

    public Set<Integer> getSelectedPositions() {
        return new HashSet<>(selectedPositions);
    }

    public void clearSelection() {
        selectedPositions.clear();
        selectionMode = false;
        notifyDataSetChanged();
        if (selectionListener != null)
            selectionListener.onSelectionModeChanged(false);
    }

    public void deleteSelected() {
        selectedPositions.stream()
                .sorted((a, b) -> b - a)
                .forEach(pos -> data.remove((int) pos));
        clearSelection();
        notifyDataSetChanged();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ImageView img;
        final View overlay;

        VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            overlay = itemView.findViewById(R.id.overlay);
        }
    }
}
