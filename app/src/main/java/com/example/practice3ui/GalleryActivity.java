package com.example.practice3ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.practice3ui.databinding.ActivityGalleryBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GalleryActivity extends AppCompatActivity {

    private ActivityGalleryBinding binding;
    private final List<Uri> images = new ArrayList<>();
    private ImageAdapter adapter;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                Intent data = result.getData();
                                int inserted = 0;

                                if (data.getClipData() != null) {
                                    int count = data.getClipData().getItemCount();
                                    for (int i = 0; i < count; i++) {
                                        Uri uri = data.getClipData().getItemAt(i).getUri();
                                        persist(uri);
                                        images.add(uri);
                                        UriStorage.addToGallery(GalleryActivity.this, uri);
                                        inserted++;
                                    }
                                    adapter.notifyItemRangeInserted(images.size() - inserted, inserted);
                                } else if (data.getData() != null) {
                                    Uri uri = data.getData();
                                    persist(uri);
                                    images.add(uri);
                                    UriStorage.addToGallery(GalleryActivity.this, uri);
                                    adapter.notifyItemInserted(images.size() - 1);
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

        adapter = new ImageAdapter(this, images,
                new ImageAdapter.OnSelectionChangeListener() {
                    @Override
                    public void onSelectionModeChanged(boolean enabled) {
                        binding.deleteBar.setVisibility(enabled ? android.view.View.VISIBLE : android.view.View.GONE);
                        binding.fabAdd.setVisibility(enabled ? android.view.View.GONE : android.view.View.VISIBLE);
                    }

                    @Override
                    public void onSelectionCountChanged(int count) {
                        binding.btnDelete.setText("Delete (" + count + ")");
                    }
                },
                pos -> openFull(images, pos) // normal click callback
        );

        binding.recycler.setAdapter(adapter);

        binding.fabAdd.setOnClickListener(v -> launchPicker());

        // ← UPDATED: show confirmation dialog before deleting
        binding.btnDelete.setOnClickListener(v -> {
            Set<Integer> sel = adapter.getSelectedPositions();
            if (sel == null || sel.isEmpty()) {
                Toast.makeText(this, "Nothing selected", Toast.LENGTH_SHORT).show();
                return;
            }

            // Build readable message (1 item vs N items)
            String msg = sel.size() == 1 ? "Видалити 1 зображення?" : "Видалити " + sel.size() + " зображення(ь)?";

            new AlertDialog.Builder(this)
                    .setTitle("Підтвердження видалення")
                    .setMessage(msg)
                    .setPositiveButton("Видалити", (dialog, which) -> {
                        // perform delete
                        adapter.deleteSelected();
                        // persist the updated gallery
                        UriStorage.saveGalleryUris(this, images);
                        Toast.makeText(this, "Видалено", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Скасувати", null)
                    .show();
        });
    }

    private void openFull(List<Uri> list, int start) {
        Intent i = new Intent(this, FullscreenActivity.class);
        ArrayList<String> payload = new ArrayList<>();
        for (Uri u : list) payload.add(u.toString());
        i.putStringArrayListExtra("uris", payload);
        i.putExtra("start", start);
        startActivity(i);
    }

    private void launchPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pickImageLauncher.launch(intent);
    }

    private void persist(Uri uri) {
        try {
            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception ignored) {}
    }
}
