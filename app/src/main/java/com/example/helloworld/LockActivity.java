package com.example.helloworld;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LockActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        String user = getIntent().getStringExtra(MainActivity.EXTRA_LOCKED_USERNAME);
        long until = getIntent().getLongExtra(MainActivity.EXTRA_LOCK_UNTIL, 0L);
        long remain = Math.max(0, until - System.currentTimeMillis());

        TextView tv = findViewById(R.id.tvLockMsg);
        if (user == null || user.isEmpty()) user = "this account";
        tv.setText("Your account (" + user + ") is locked. Try again in " + humanize(remain) + ".");

        Button btn = findViewById(R.id.btnTryAnother);
        btn.setOnClickListener(v -> finish()); // quay lại màn login
    }

    private String humanize(long ms) {
        long s = Math.max(0, ms / 1000);
        long m = s / 60, h = m / 60, d = h / 24;
        if (d > 0) return d + "d " + (h % 24) + "h";
        if (h > 0) return h + "h " + (m % 60) + "m";
        if (m > 0) return m + "m " + (s % 60) + "s";
        return s + "s";
    }
}
