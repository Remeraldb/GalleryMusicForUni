package com.example.practice3ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.practice3ui.databinding.ActivityGalleryBinding;
import java.util.ArrayList;
import java.util.List;

public class GalleryPickerActivity extends AppCompatActivity {
    private ActivityGalleryBinding binding;
    private final List<Uri> images = new ArrayList<>();
    private ImageAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Load gallery images (both default and user-added)
        images.addAll(UriStorage.getGalleryUris(this));
        binding.recycler.setLayoutManager(new GridLayoutManager(this, 3));

        adapter = new ImageAdapter(this, images, null, null);
        binding.recycler.setAdapter(adapter);

        // Hide "add image" button — not needed in picker mode
        binding.fabAdd.hide();

        // Handle taps on image items
        binding.recycler.addOnItemTouchListener(
                new RecyclerItemClickListener(this, (view, position) -> {
                    Uri selected = images.get(position);
                    Intent result = new Intent();
                    result.setData(selected);
                    setResult(Activity.RESULT_OK, result);
                    finish();
                })
        );
    }
}
