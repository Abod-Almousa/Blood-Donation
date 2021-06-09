package com.almousa.abod.blooddonation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StartActivity extends AppCompatActivity {

    private AppCompatButton btn_login, btn_register;
    private TextView tv_guest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
        if(sharedPreferences.getString("username",null) != null) {
            Intent intent = new Intent(StartActivity.this,ProfileActivity.class);
            startActivity(intent);
            finish();
        }

        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        tv_guest = findViewById(R.id.tv_guest);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        tv_guest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StartActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}