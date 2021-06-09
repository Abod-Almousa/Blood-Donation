package com.almousa.abod.blooddonation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_username, et_fullname, et_password, et_type, et_phone;
    private Button btn_location, btn_register;
    private TextView tv_login;

    private String latitude;
    private String longitude;

    int PLACE_PICKER_REQ = 1;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = findViewById(R.id.et_username);
        et_fullname = findViewById(R.id.et_fullname);
        et_password = findViewById(R.id.et_password);
        et_type = findViewById(R.id.et_type);
        et_phone = findViewById(R.id.et_phone);
        btn_location = findViewById(R.id.btn_location);
        btn_register = findViewById(R.id.btn_register);
        tv_login = findViewById(R.id.tv_login);

        et_type.setError("A+, A-, B+, B-, O+, O-, AB+, AB-");
        et_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                et_type.setError("A+, A-, B+, B-, O+, O-, AB+, AB-");
            }
        });

        progressDialog = new ProgressDialog(this);

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser();
            }
        });

        tv_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    Intent intent = builder.build(RegisterActivity.this);
                    startActivityForResult(intent,PLACE_PICKER_REQ);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void registerUser() {

        String username = et_username.getText().toString().trim();
        String fullname = et_fullname.getText().toString().trim();
        String password = et_password.getText().toString().trim();
        String type = et_type.getText().toString().trim();
        String phone = et_phone.getText().toString().trim();

        progressDialog.setMessage("إنتظر قليلا ...");
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.REGISTER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(RegisterActivity.this, "تم إنشاء الحساب. سجل الدخول الآن", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(RegisterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("username",username);
                params.put("fullname",fullname);
                params.put("password",password);
                params.put("type",type);
                params.put("phone",phone);
                params.put("latitude",latitude);
                params.put("longitude",longitude);

                return params;
            }
        };

        /* RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest); */

        // With singleton
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PLACE_PICKER_REQ) {
            if(resultCode == RESULT_OK) {

                Place place = PlacePicker.getPlace(data,this);
                latitude = String.valueOf(place.getLatLng().latitude);
                longitude = String.valueOf(place.getLatLng().longitude);
            }
        }
    }
}