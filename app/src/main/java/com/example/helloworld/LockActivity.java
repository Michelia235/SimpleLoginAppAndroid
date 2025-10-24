package com.example.helloworld;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class LockActivity extends AppCompatActivity {

    private CountDownTimer timer;
    private String lockedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        String u = getIntent().getStringExtra(MainActivity.EXTRA_LOCKED_USERNAME);
        lockedUser = (u == null || u.isEmpty()) ? getString(R.string.this_account) : u;

        long until = getIntent().getLongExtra(MainActivity.EXTRA_LOCK_UNTIL, 0L);
        long remain = Math.max(0, until - System.currentTimeMillis());

        TextView tv = findViewById(R.id.tvLockMsg);
        Button btn = findViewById(R.id.btnTryAnother);
        btn.setOnClickListener(v -> finish());

        tv.setText(getString(R.string.lock_msg_with_remaining, lockedUser, humanize(remain)));
        timer = new CountDownTimer(remain, 1000) {
            @Override public void onTick(long ms) {
                tv.setText(getString(R.string.lock_msg_with_remaining, lockedUser, humanize(ms)));
            }
            @Override public void onFinish() { finish(); }
        };
        timer.start();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) timer.cancel();
        super.onDestroy();
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
