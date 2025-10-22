package com.example.helloworld;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);

        // Lấy username từ Intent extra
        String username = getIntent().getStringExtra(MainActivity.EXTRA_USERNAME);
        if (username == null || username.isEmpty()) {
            username = "User";
        }

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        tvGreeting.setText("Hello " + username + "!");
    }
}
