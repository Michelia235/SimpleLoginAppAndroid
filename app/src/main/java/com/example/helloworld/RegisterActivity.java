package com.example.helloworld;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private static final String PREFS = "auth_prefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText edtU = findViewById(R.id.editRegUsername);
        EditText edtP = findViewById(R.id.editRegPassword);
        EditText edtC = findViewById(R.id.editRegConfirm);
        Button btnCreate = findViewById(R.id.btnCreateAccount);
        Button btnLogin = findViewById(R.id.btnBackToLogin);

        btnCreate.setOnClickListener(v -> {
            String u = edtU.getText().toString().trim();
            String p = edtP.getText().toString().trim();
            String c = edtC.getText().toString().trim();

            if (u.isEmpty() || p.isEmpty() || c.isEmpty()) {
                Toast.makeText(this, getString(R.string.msg_enter_both), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!p.equals(c)) {
                Toast.makeText(this, getString(R.string.msg_password_mismatch), Toast.LENGTH_SHORT).show();
                return;
            }
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            if (prefs.getString("pw_" + u, null) != null) {
                Toast.makeText(this, getString(R.string.msg_username_exists), Toast.LENGTH_SHORT).show();
                return;
            }
            prefs.edit().putString("pw_" + u, p).apply();
            Toast.makeText(this, getString(R.string.msg_account_created), Toast.LENGTH_SHORT).show();
            finish();
        });

        btnLogin.setOnClickListener(v -> finish());
    }
}
