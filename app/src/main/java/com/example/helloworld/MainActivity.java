package com.example.helloworld;

import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_USERNAME = "com.example.helloworld.EXTRA_USERNAME";
    public static final String EXTRA_LOCKED_USERNAME = "com.example.helloworld.EXTRA_LOCKED_USERNAME";
    public static final String EXTRA_LOCK_UNTIL = "com.example.helloworld.EXTRA_LOCK_UNTIL";

    private static final int MAX_ATTEMPTS = 5;
    private static final long ONE_DAY_MS = 24L * 60L * 60L * 1000L;

    private final Map<String, String> builtInUsers = new HashMap<>();
    private SharedPreferences prefs;

    private TextView tvAttempts;
    private TextInputLayout tilUsername;
    private TextInputLayout tilPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = AuthPrefs.get(this);
        String current = prefs.getString("current_user", null);
        if (current != null) {
            Intent i = new Intent(this, AccountActivity.class);
            i.putExtra(EXTRA_USERNAME, current);
            startActivity(i);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        builtInUsers.put("user", "password");
        builtInUsers.put("nva", "123456");

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView btnForgot = findViewById(R.id.btnForgot);
        tvAttempts = findViewById(R.id.tvAttempts);
        tilUsername = findViewById(R.id.tilUsername);
        tilPassword = findViewById(R.id.tilPassword);

        btnLogin.setText(R.string.btnLogin);
        tvAttempts.setText(getString(R.string.lblAttempts, 0, MAX_ATTEMPTS));

        EditText etUser = findViewById(R.id.editUsername);
        EditText etPass = findViewById(R.id.editPassword);

        etUser.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                String u = s.toString().trim();
                if (!u.isEmpty()) updateAttemptsLabelFor(u);
                else tvAttempts.setText(getString(R.string.lblAttempts, 0, MAX_ATTEMPTS));
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnLogin.setOnClickListener(v -> {
            String username = etUser.getText().toString().trim();
            String password = etPass.getText().toString().trim();

            boolean ok = true;
            if (username.isEmpty()) { tilUsername.setError(getString(R.string.err_required)); ok = false; } else tilUsername.setError(null);
            if (password.isEmpty()) { tilPassword.setError(getString(R.string.err_required)); ok = false; } else tilPassword.setError(null);
            if (!ok) return;

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
        });

        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class)));

        btnForgot.setOnClickListener(v -> showResetPasswordDialog());
    }

    private void showResetPasswordDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_reset_password, null, false);
        TextInputEditText etU = dialogView.findViewById(R.id.editForgotUsername);
        TextInputEditText etP = dialogView.findViewById(R.id.editNewPassword);
        TextInputEditText etC = dialogView.findViewById(R.id.editConfirmNewPassword);
        TextInputLayout tilU = dialogView.findViewById(R.id.tilForgotUsername);
        TextInputLayout tilP = dialogView.findViewById(R.id.tilNewPassword);
        TextInputLayout tilC = dialogView.findViewById(R.id.tilConfirmNewPassword);

        AlertDialog dlg = new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.title_reset_password)
                .setView(dialogView)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(R.string.btnReset, null)
                .create();

        dlg.setOnShowListener(d -> {
            Button p = dlg.getButton(DialogInterface.BUTTON_POSITIVE);
            p.setOnClickListener(x -> {
                String u = etU.getText() == null ? "" : etU.getText().toString().trim();
                String np = etP.getText() == null ? "" : etP.getText().toString().trim();
                String cp = etC.getText() == null ? "" : etC.getText().toString().trim();

                boolean ok = true;
                if (u.isEmpty()) { tilU.setError(getString(R.string.err_required)); ok = false; } else tilU.setError(null);
                if (np.isEmpty()) { tilP.setError(getString(R.string.err_required)); ok = false; } else tilP.setError(null);
                if (!np.equals(cp)) { tilC.setError(getString(R.string.msg_password_mismatch)); ok = false; } else tilC.setError(null);
                if (!ok) return;

                String existed = prefs.getString("pw_" + u, null);
                if (existed == null && !builtInUsers.containsKey(u)) {
                    tilU.setError(getString(R.string.msg_username_not_found));
                    return;
                }

                prefs.edit()
                        .putString("pw_" + u, np)
                        .putInt(keyAttempts(u), 0)
                        .remove(keyLockUntil(u))
                        .apply();

                Toast.makeText(this, getString(R.string.msg_password_reset_success), Toast.LENGTH_SHORT).show();
                dlg.dismiss();
            });
        });

        dlg.show();
    }

    private void updateAttemptsLabelFor(String user) {
        int a = prefs.getInt(keyAttempts(user), 0);
        tvAttempts.setText(getString(R.string.lblAttempts, a, MAX_ATTEMPTS));
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
