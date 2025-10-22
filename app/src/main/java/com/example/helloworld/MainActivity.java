package com.example.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Key dùng để gửi username sang AccountActivity
    public static final String EXTRA_USERNAME = "com.example.helloworld.EXTRA_USERNAME";

    // Danh sách người dùng (username -> password)
    private final Map<String, String> users = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Khởi tạo tài khoản mẫu
        users.put("nva", "123456");
        users.put("user", "password");
        users.put("admin", "admin123");

        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Username = (EditText) findViewById(R.id.editUsername);
                EditText Password = (EditText) findViewById(R.id.editPassword);

                String username = Username.getText().toString().trim();
                String password = Password.getText().toString().trim();

                // Kiểm tra theo danh sách users
                if (users.containsKey(username) && users.get(username).equals(password)) {
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    intent.putExtra(EXTRA_USERNAME, username);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLogin.setText(R.string.btnLogin);
    };
}
