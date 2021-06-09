package com.almousa.abod.blooddonation;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                Constants.VIEW_USERS_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONArray array = new JSONArray(response);

                            for (int i = 0; i < array.length(); i++) {

                                //getting user object from json array
                                JSONObject user = array.getJSONObject(i);

                                String latitude = user.getString("latitude");
                                String longitude = user.getString("longitude");
                                String fullname = user.getString("fullname");
                                String phone = user.getString("phone");
                                String type = user.getString("type");

                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(latitude) , Double.parseDouble(longitude)));

                                switch (type) {

                                    case "A+":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_a_p));
                                        break;

                                    case "A-":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_a_m));
                                        break;

                                    case "B+":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_b_p));
                                        break;

                                    case "B-":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_b_m));
                                        break;

                                    case "O+":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_o_p));
                                        break;

                                    case "O-":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_o_m));
                                        break;

                                    case "AB+":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_ab_p));
                                        break;

                                    case "AB-":
                                        markerOptions.icon(bitmapDescriptorFromVector(MainActivity.this, R.drawable.ic_ab_m));
                                        break;
                                }

                                Marker marker = mMap.addMarker(markerOptions);

                                marker.showInfoWindow();

                                MarkerTags tags = new MarkerTags();
                                tags.setUsername(fullname);
                                tags.setPhone(phone);
                                tags.setType(type);
                                marker.setTag(tags);

                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(28.4055764,45.9822522), 12.51f));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {

        };

        int socketTimeout = 10000;
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(policy);

        // With singleton
        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                String username = null;
                String phone = null;
                String bloodType = null;

                if(null != marker.getTag()){
                    if(marker.getTag() instanceof MarkerTags){
                        MarkerTags tag = ((MarkerTags)marker.getTag());
                        username = tag.getUsername();
                        phone = tag.getPhone();
                        bloodType = tag.getType();
                    }
                }


                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                        MainActivity.this,
                        R.style.BottomSheetDialogTheme);

                View bottomSheetView = LayoutInflater.from(getApplicationContext())
                        .inflate(R.layout.bottom_sheet, (RelativeLayout) findViewById(R.id.bottomSheetContainer));

                TextView type = bottomSheetView.findViewById(R.id.tv_type);
                type.setText(bloodType);

                TextView user_name = bottomSheetView.findViewById(R.id.tv_username);
                user_name.setText(username);

                String finalPhone = phone;
                bottomSheetView.findViewById(R.id.btn_contact).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:"+ finalPhone));
                        startActivity(intent);
                    }
                });

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                return false;
            }
        });

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

}