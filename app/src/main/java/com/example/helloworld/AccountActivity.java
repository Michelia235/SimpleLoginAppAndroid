package com.example.helloworld;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        String username = getIntent().getStringExtra(MainActivity.EXTRA_USERNAME);
        if (username == null || username.isEmpty()) username = "User";

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText("Hello " + username + "!");

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences("auth_prefs", MODE_PRIVATE);
            prefs.edit().remove("current_user").apply();
            Toast.makeText(this, getString(R.string.msg_logged_out), Toast.LENGTH_SHORT).show();
            Intent i = new Intent(AccountActivity.this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        });
    }
}
