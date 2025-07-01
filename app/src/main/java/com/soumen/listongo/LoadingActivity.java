package com.soumen.listongo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.widget.*;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.button.MaterialButton;

public class LoadingActivity extends AppCompatActivity {
    String api;
LottieAnimationView lottieAnimationView;
TextView txtServerOf;
MaterialButton btnRequest,btnRefresh;
ImageView serverError;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loading);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        lottieAnimationView=findViewById(R.id.lottieView);
        btnRefresh=findViewById(R.id.retry_button);
        btnRequest=findViewById(R.id.request_button);
        txtServerOf=findViewById(R.id.info_text);
        serverError=findViewById(R.id.info_image);
        api=getString(R.string.server_api);
        serverOnOrOf();

        btnRefresh.setOnClickListener(v->{
            lottieAnimationView.setVisibility(VISIBLE);
            btnRefresh.setVisibility(GONE);
            btnRequest.setVisibility(GONE);
            txtServerOf.setVisibility(GONE);
            serverError.setVisibility(GONE);
            serverOnOrOf();
        });

    }
    public void serverOnOrOf(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RequestQueue requestQueue= Volley.newRequestQueue(LoadingActivity.this);
                String url=api+"/isServerOn";

                StringRequest stringRequest=new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent intent=new Intent(LoadingActivity.this, OptionActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        lottieAnimationView.setVisibility(GONE);
                        btnRefresh.setVisibility(VISIBLE);
                        btnRequest.setVisibility(VISIBLE);
                        txtServerOf.setVisibility(VISIBLE);
                        serverError.setVisibility(VISIBLE);
                        Toast.makeText(LoadingActivity.this, "Server is offline", Toast.LENGTH_SHORT).show();
                    }
                });
                requestQueue.add(stringRequest);
            }
        },1000);
    }
}