package com.soumen.listongo.ForAdmin.ReqForMakeAdmin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.soumen.listongo.R;

public class PlaneListActivity extends AppCompatActivity {
    MaterialButton btnSilver, btnBronze, btnGold, btnDiamond, btnPlatinum, btnTitanium;
    TextView coin100, coin200, coin400, coin800, coin1000, coin1500;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_plane_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnSilver = findViewById(R.id.btnSilver);
        btnBronze = findViewById(R.id.btnBronze);
        btnDiamond = findViewById(R.id.btnDiamond);
        btnGold = findViewById(R.id.btnGold);
        btnPlatinum = findViewById(R.id.btnPlatinum);
        btnTitanium = findViewById(R.id.btnTitanium);

        coin100=findViewById(R.id.coin100);
        coin200=findViewById(R.id.coin200);
        coin400=findViewById(R.id.coin400);
        coin800=findViewById(R.id.coin800);
        coin1000=findViewById(R.id.coin1000);
        coin1500=findViewById(R.id.coin1500);

        btnSilver.setOnClickListener(v -> planeSelect(btnSilver,coin100));
        btnBronze.setOnClickListener(v -> planeSelect(btnBronze,coin200));
        btnGold.setOnClickListener(v -> planeSelect(btnGold,coin400));
        btnDiamond.setOnClickListener(v -> planeSelect(btnDiamond,coin800));
        btnPlatinum.setOnClickListener(v -> planeSelect(btnPlatinum,coin1000));
        btnTitanium.setOnClickListener(v -> planeSelect(btnTitanium,coin1500));
    }

    public void planeSelect(MaterialButton btn,TextView coinValue) {
        String selectedPlan = btn.getText().toString();
        Intent resultIntent = new Intent(PlaneListActivity.this, ReqForAdminActivity.class);
        resultIntent.putExtra("selected_plan", selectedPlan);
        resultIntent.putExtra("coin_value",coinValue.getText().toString());
        setResult(RESULT_OK, resultIntent);
        finish();
    }

}