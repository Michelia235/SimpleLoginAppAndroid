package com.example.helloworld;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AccountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account);


        TextView tv = new TextView(this);
        tv.setText("Hello Android!");
        tv.setGravity(Gravity.CENTER);
        setContentView(tv);
        };
}
