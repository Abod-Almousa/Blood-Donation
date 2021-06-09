package com.almousa.abod.blooddonation;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText et_username, et_password;
    private Button btn_login;
    private TextView tv_register;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        btn_login = findViewById(R.id.btn_login);
        tv_register = findViewById(R.id.tv_register);

        progressDialog = new ProgressDialog(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });

        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void userLogin() {

        String username = et_username.getText().toString().trim();
        String password = et_password.getText().toString().trim();

        progressDialog.setMessage("إنتظر قليلا ...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject json = new JSONObject(response);
                            if (!json.getBoolean("error")) {

                                SharedPreferences sharedPreferences = getSharedPreferences("USER", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putInt("id",json.getInt("id"));
                                editor.putString("username",json.getString("username"));
                                editor.putString("fullname",json.getString("fullname"));
                                editor.putString("type",json.getString("type"));
                                editor.putString("phone",json.getString("phone"));
                                editor.putString("latitude",json.getString("latitude"));
                                editor.putString("longitude",json.getString("longitude"));
                                editor.apply();

                                //Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(LoginActivity.this,ProfileActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast.makeText(LoginActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };

        // With singleton
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }
}