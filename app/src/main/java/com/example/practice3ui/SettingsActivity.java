package com.example.practice3ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.practice3ui.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    // SAF launcher
    private final ActivityResultLauncher<Intent> pickBgLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                applyBackgroundUri(result.getData().getData());
                            }
                        }
                    });

    // Gallery picker launcher
    private final ActivityResultLauncher<Intent> galleryPickLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                                applyBackgroundUri(result.getData().getData());
                            }
                        }
                    });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Uri current = UriStorage.getBackgroundUri(this);
        if (current != null) binding.preview.setImageURI(current);
        else binding.preview.setImageResource(R.drawable.bg_default);

        binding.btnChoose.setOnClickListener(v -> showChooseSourceDialog());
        binding.btnReset.setOnClickListener(v -> {
            Uri def = Uri.parse("android.resource://" + getPackageName() + "/drawable/bg_default");
            UriStorage.setBackgroundUri(this, def);
            binding.preview.setImageResource(R.drawable.bg_default);
            Toast.makeText(this, getString(R.string.bg_updated), Toast.LENGTH_SHORT).show();
        });
    }

    private void showChooseSourceDialog() {
        String[] options = {"З галереї програми", "З файлової системи"};
        new AlertDialog.Builder(this)
                .setTitle("Вибір джерела")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) { // gallery
                        Intent i = new Intent(this, GalleryPickerActivity.class);
                        galleryPickLauncher.launch(i);
                    } else { // SAF
                        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                        intent.addCategory(Intent.CATEGORY_OPENABLE);
                        intent.setType("image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                        pickBgLauncher.launch(intent);
                    }
                })
                .show();
    }

    private void applyBackgroundUri(Uri uri) {
        if (uri == null) return;
        try {
            getContentResolver().takePersistableUriPermission(uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } catch (Exception ignored) {}

        UriStorage.setBackgroundUri(this, uri);
        binding.preview.setImageURI(uri);
        Toast.makeText(this, getString(R.string.bg_updated), Toast.LENGTH_SHORT).show();
    }
}
