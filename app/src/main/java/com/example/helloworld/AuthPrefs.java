package com.example.helloworld;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public final class AuthPrefs {
    private static SharedPreferences instance;

    public static synchronized SharedPreferences get(Context ctx) {
        if (instance == null) {
            try {
                MasterKey key = new MasterKey.Builder(ctx)
                        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                        .build();

                instance = EncryptedSharedPreferences.create(
                        ctx,
                        "auth_prefs_secure",
                        key,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                );

                migrateFromPlain(ctx, instance);
            } catch (GeneralSecurityException | IOException e) {
                instance = ctx.getSharedPreferences("auth_prefs_secure_fallback", Context.MODE_PRIVATE);
            }
        }
        return instance;
    }

    private static void migrateFromPlain(Context ctx, SharedPreferences encrypted) {
        SharedPreferences plain = ctx.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE);
        if (plain.getBoolean("MIGRATED_V1", false)) return;

        Map<String, ?> all = plain.getAll();
        if (all != null && !all.isEmpty()) {
            SharedPreferences.Editor ed = encrypted.edit();
            for (Map.Entry<String, ?> e : all.entrySet()) {
                String k = e.getKey();
                Object v = e.getValue();
                if (v instanceof String) ed.putString(k, (String) v);
                else if (v instanceof Integer) ed.putInt(k, (Integer) v);
                else if (v instanceof Long) ed.putLong(k, (Long) v);
                else if (v instanceof Boolean) ed.putBoolean(k, (Boolean) v);
                else if (v instanceof Float) ed.putFloat(k, (Float) v);
            }
            ed.apply();
            plain.edit().clear().apply();
        }
        plain.edit().putBoolean("MIGRATED_V1", true).apply();
    }

    private AuthPrefs() {}
}
