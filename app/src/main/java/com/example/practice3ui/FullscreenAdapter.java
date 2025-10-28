package com.example.practice3ui;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.List;

public class FullscreenAdapter extends RecyclerView.Adapter<FullscreenAdapter.VH> {
    public interface OnTap { void onTap(); }
    private final Context ctx; private final List<Uri> data; private final OnTap onTap;

    public FullscreenAdapter(Context c, List<Uri> d, OnTap t){ ctx=c; data=d; onTap=t; }

    static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        VH(@NonNull ImageView v) { super(v); img = v; }
    }

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int vt) {
        PhotoView iv = new PhotoView(ctx); // Якщо без zoom — можна звичайний ImageView
        iv.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setOnClickListener(v -> { if (onTap!=null) onTap.onTap(); });
        return new VH(iv);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) { h.img.setImageURI(data.get(pos)); }
    @Override public int getItemCount() { return data.size(); }
}
