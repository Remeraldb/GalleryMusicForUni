package com.example.practice3ui;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.practice3ui.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private MediaPlayer mediaPlayer;
    private boolean playing = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        UriStorage.ensureInitialGallery(this);
        updateBackground();

        binding.btnGallery.setOnClickListener(v ->
                startActivity(new Intent(this, GalleryActivity.class)));

        binding.btnMusic.setOnClickListener(v -> toggleMusic());

        binding.btnSettings.setOnClickListener(v ->
                startActivity(new Intent(this, SettingsActivity.class)));
    }

    private void updateBackground() {
        Uri bg = UriStorage.getBackgroundUri(this);
        if (bg != null) binding.bgImage.setImageURI(bg);
        else binding.bgImage.setImageResource(R.drawable.bg_default);
    }

    private void toggleMusic() {
        if (playing) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
            playing = false;
            binding.btnMusic.setText(getString(R.string.music_play));
        } else {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.music_sample);
                mediaPlayer.setOnCompletionListener(mp -> {
                    playing = false;
                    binding.btnMusic.setText(getString(R.string.music_play));
                });
            }
            mediaPlayer.start();
            playing = true;
            binding.btnMusic.setText(getString(R.string.music_pause));
        }
    }

    @Override protected void onResume() {
        super.onResume();
        updateBackground();
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) { mediaPlayer.release(); mediaPlayer = null; }
    }
}
