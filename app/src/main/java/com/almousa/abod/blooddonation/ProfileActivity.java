package com.almousa.abod.blooddonation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView tv_username, tv_phone, tv_type, view_map;
    private AppCompatButton btn_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        tv_username = findViewById(R.id.tv_username);
        tv_phone = findViewById(R.id.tv_phone);
        tv_type = findViewById(R.id.tv_type);
        view_map = findViewById(R.id.view_map);
        btn_logout = findViewById(R.id.btn_logout);

        String username, phone, type;

        username = getSharedPreferences("USER", Context.MODE_PRIVATE).getString("fullname",null);
        phone = getSharedPreferences("USER", Context.MODE_PRIVATE).getString("phone",null);
        type = getSharedPreferences("USER", Context.MODE_PRIVATE).getString("type",null);

        tv_username.setText(username);
        tv_phone.setText(phone);
        tv_type.setText(type);

        view_map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences = getSharedPreferences("USER",Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                Intent intent = new Intent(ProfileActivity.this,StartActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}