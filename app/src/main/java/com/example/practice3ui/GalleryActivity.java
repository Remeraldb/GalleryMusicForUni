package com.example.practice3ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import com.example.practice3ui.databinding.ActivityGalleryBinding;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private ActivityGalleryBinding binding;
    private final List<Uri> images = new ArrayList<>();
    private ImageAdapter adapter;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Uri uri = result.getData().getData();
                                if (uri != null) {
                                    int granted = result.getData().getFlags()
                                            & (Intent.FLAG_GRANT_READ_URI_PERMISSION
                                            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                    if ((granted & Intent.FLAG_GRANT_READ_URI_PERMISSION) == 0) {
                                        granted |= Intent.FLAG_GRANT_READ_URI_PERMISSION;
                                    }
                                    try {
                                        getContentResolver().takePersistableUriPermission(uri, granted);
                                    } catch (Exception ignored) {}

                                    images.add(uri);
                                    adapter.notifyItemInserted(images.size() - 1);
                                    UriStorage.addToGallery(GalleryActivity.this, uri);
                                }
                            }
                        }
                    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGalleryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        images.addAll(UriStorage.getGalleryUris(this));
        binding.recycler.setLayoutManager(new GridLayoutManager(this, 3));
        adapter = new ImageAdapter(this, images);
        binding.recycler.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(v -> launchImagePicker());
    }

    private void launchImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pickImageLauncher.launch(intent);
    }
}
