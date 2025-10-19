package com.example.practice3ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.List;

public class UriStorage {
    private static final String PREFS = "storage";
    private static final String KEY_GALLERY = "gallery_uris";
    private static final String KEY_BG = "bg_uri";

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static void ensureInitialGallery(Context c) {
        List<Uri> current = getGalleryUris(c);
        if (current.isEmpty()) {
            ArrayList<Uri> init = new ArrayList<>();
            init.add(resource(c, "gallery_1"));
            init.add(resource(c, "gallery_2"));
            init.add(resource(c, "gallery_3"));
            init.add(resource(c, "gallery_4"));
            init.add(resource(c, "gallery_5"));
            saveGalleryUris(c, init);
        }
        if (getBackgroundUri(c) == null) {
            setBackgroundUri(c, resource(c, "bg_default"));
        }
    }

    private static Uri resource(Context c, String drawableName) {
        return Uri.parse("android.resource://" + c.getPackageName() + "/drawable/" + drawableName);
    }

    public static List<Uri> getGalleryUris(Context c) {
        String raw = prefs(c).getString(KEY_GALLERY, "");
        ArrayList<Uri> list = new ArrayList<>();
        if (raw == null || raw.isEmpty()) return list;
        try {
            JSONArray arr = new JSONArray(raw);
            for (int i = 0; i < arr.length(); i++) list.add(Uri.parse(arr.getString(i)));
        } catch (Exception ignored) {}
        return list;
    }

    public static void saveGalleryUris(Context c, List<Uri> uris) {
        JSONArray arr = new JSONArray();
        for (Uri u : uris) arr.put(u.toString());
        prefs(c).edit().putString(KEY_GALLERY, arr.toString()).apply();
    }

    public static void addToGallery(Context c, Uri uri) {
        List<Uri> list = getGalleryUris(c);
        list.add(uri);
        saveGalleryUris(c, list);
    }

    public static Uri getBackgroundUri(Context c) {
        String s = prefs(c).getString(KEY_BG, null);
        return (s == null) ? null : Uri.parse(s);
    }

    public static void setBackgroundUri(Context c, Uri uri) {
        prefs(c).edit().putString(KEY_BG, uri.toString()).apply();
    }
}
