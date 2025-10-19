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
import androidx.appcompat.app.AppCompatActivity;
import com.example.practice3ui.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {
    private ActivitySettingsBinding binding;

    private final ActivityResultLauncher<Intent> pickBgLauncher =
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

                                    UriStorage.setBackgroundUri(SettingsActivity.this, uri);
                                    binding.preview.setImageURI(uri);
                                    Toast.makeText(SettingsActivity.this,
                                            getString(R.string.bg_updated), Toast.LENGTH_SHORT).show();
                                }
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

        binding.btnChoose.setOnClickListener(v -> chooseBackground());
        binding.btnReset.setOnClickListener(v -> {
            UriStorage.setBackgroundUri(this,
                    Uri.parse("android.resource://" + getPackageName() + "/drawable/bg_default"));
            binding.preview.setImageResource(R.drawable.bg_default);
            Toast.makeText(this, getString(R.string.bg_updated), Toast.LENGTH_SHORT).show();
        });
    }

    private void chooseBackground() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        pickBgLauncher.launch(intent);
    }
}
