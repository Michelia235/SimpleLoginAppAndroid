package com.example.helloworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "com.example.helloworld.EXTRA_USERNAME";
    public static final String EXTRA_LOCKED_USERNAME = "com.example.helloworld.EXTRA_LOCKED_USERNAME";
    public static final String EXTRA_LOCK_UNTIL = "com.example.helloworld.EXTRA_LOCK_UNTIL";

    private static final String PREFS = "auth_prefs";
    private static final int MAX_ATTEMPTS = 5;
    private static final long ONE_DAY_MS = 24L * 60L * 60L * 1000L;

    private final Map<String, String> builtInUsers = new HashMap<>();
    private SharedPreferences prefs;

    private TextView tvAttempts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        builtInUsers.put("user", "password");
        builtInUsers.put("nva", "123456");

        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        tvAttempts = findViewById(R.id.tvAttempts);

        btnLogin.setText(R.string.btnLogin);
        tvAttempts.setText(getString(R.string.lblAttempts, 0, MAX_ATTEMPTS));

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                EditText etUser = findViewById(R.id.editUsername);
                EditText etPass = findViewById(R.id.editPassword);

                String username = etUser.getText().toString().trim();
                String password = etPass.getText().toString().trim();

                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, getString(R.string.msg_enter_both), Toast.LENGTH_SHORT).show();
                    return;
                }

                long now = System.currentTimeMillis();
                long until = getLockUntil(username);
                if (now < until) {
                    String remain = humanize(until - now);
                    Toast.makeText(MainActivity.this, getString(R.string.msg_locked_remaining, remain), Toast.LENGTH_LONG).show();
                    Intent i = new Intent(MainActivity.this, LockActivity.class);
                    i.putExtra(EXTRA_LOCKED_USERNAME, username);
                    i.putExtra(EXTRA_LOCK_UNTIL, until);
                    startActivity(i);
                    return;
                }

                if (isValidCredential(username, password)) {
                    prefs.edit()
                            .putInt(keyAttempts(username), 0)
                            .putString("current_user", username)
                            .apply();
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    intent.putExtra(EXTRA_USERNAME, username);
                    startActivity(intent);
                } else {
                    int a = prefs.getInt(keyAttempts(username), 0) + 1;
                    prefs.edit().putInt(keyAttempts(username), a).apply();
                    tvAttempts.setText(getString(R.string.lblAttempts, a, MAX_ATTEMPTS));
                    if (a > MAX_ATTEMPTS) {
                        long lockUntil = now + ONE_DAY_MS;
                        prefs.edit().putLong(keyLockUntil(username), lockUntil).apply();
                        Intent i = new Intent(MainActivity.this, LockActivity.class);
                        i.putExtra(EXTRA_LOCKED_USERNAME, username);
                        i.putExtra(EXTRA_LOCK_UNTIL, lockUntil);
                        startActivity(i);
                    } else {
                        Toast.makeText(MainActivity.this, getString(R.string.msg_incorrect), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnRegister.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
    }

    private boolean isValidCredential(String username, String password) {
        if (builtInUsers.containsKey(username) && builtInUsers.get(username).equals(password)) return true;
        String saved = prefs.getString("pw_" + username, null);
        return saved != null && saved.equals(password);
    }

    private String keyAttempts(String user) { return "attempts_" + user; }
    private String keyLockUntil(String user) { return "lock_until_" + user; }
    private long getLockUntil(String user) { return prefs.getLong(keyLockUntil(user), 0L); }

    private String humanize(long ms) {
        long s = Math.max(0, ms / 1000);
        long m = s / 60, h = m / 60, d = h / 24;
        if (d > 0) return d + "d " + (h % 24) + "h";
        if (h > 0) return h + "h " + (m % 60) + "m";
        if (m > 0) return m + "m " + (s % 60) + "s";
        return s + "s";
    }
}
