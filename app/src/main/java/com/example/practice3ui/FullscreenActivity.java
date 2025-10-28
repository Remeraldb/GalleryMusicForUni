package com.example.practice3ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.example.practice3ui.databinding.ActivityFullscreenBinding;
import java.util.ArrayList;

public class FullscreenActivity extends AppCompatActivity {
    private ActivityFullscreenBinding b;
    private boolean uiVisible = false;

    @Override protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        EdgeToEdge.enable(this);
        b = ActivityFullscreenBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        ArrayList<String> raw = getIntent().getStringArrayListExtra("uris");
        int start = getIntent().getIntExtra("start", 0);
        ArrayList<Uri> uris = new ArrayList<>();
        if (raw != null) for (String x: raw) uris.add(Uri.parse(x));

        FullscreenAdapter.OnTap tap = this::toggleUI;
        b.pager.setAdapter(new FullscreenAdapter(this, uris, tap));
        b.pager.setCurrentItem(start, false);

        b.btnClose.setOnClickListener(v -> finish());
        enterImmersive();
    }

    private void toggleUI() {
        uiVisible = !uiVisible;
        b.topBar.setVisibility(uiVisible ? View.VISIBLE : View.GONE);
        if (uiVisible) exitImmersive(); else enterImmersive();
    }

    private void enterImmersive() {
        WindowInsetsController c = getWindow().getDecorView().getWindowInsetsController();
        if (c != null) {
            c.hide(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
            c.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
        }
    }
    private void exitImmersive() {
        WindowInsetsController c = getWindow().getDecorView().getWindowInsetsController();
        if (c != null) c.show(WindowInsets.Type.statusBars() | WindowInsets.Type.navigationBars());
    }
}
