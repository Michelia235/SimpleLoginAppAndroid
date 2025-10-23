package com.example.helloworld;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText Username = (EditText) findViewById(R.id.editUsername);
                EditText Password = (EditText) findViewById(R.id.editPassword);

                String username = Username.getText().toString().trim();
                String password = Password.getText().toString().trim();

                if ("user".equals(username) && "password".equals(password)) {
                    Intent intent = new Intent(MainActivity.this, AccountActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnLogin.setText(R.string.btnLogin);
    };
}
