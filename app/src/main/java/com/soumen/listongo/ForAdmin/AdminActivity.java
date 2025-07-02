package com.soumen.listongo.ForAdmin;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForAdmin.viewfragment.ViewpagerAdminAdapter;
import com.soumen.listongo.MainActivity;
import com.soumen.listongo.R;
import com.soumen.listongo.SettingsUtil;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {


    String api_url;
    MaterialToolbar toolbarAdmin;

    TabLayout tab;
    ViewPager viewPager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api_url = getString(R.string.server_api);
        TextView marqueeText = findViewById(R.id.marqueeText);
        MaterialToolbar toolbar=findViewById(R.id.toolbarAdmin);

        toolbar.setNavigationOnClickListener(v->onBackPressed());

        marqueeText.setSelected(true);

        marqueeText.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Alert")
                    .setMessage("📢 Attention: Listing any product in the user list is entirely your responsibility, as it will be associated with your user ID.📢")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        });



        toolbarAdmin = findViewById(R.id.toolbarAdmin);

        tab = findViewById(R.id.adminTab);
        viewPager = findViewById(R.id.viewPager);

        toolbarAdmin.setNavigationOnClickListener(v -> {
            onBackPressed();
        });


        Long userId = getIntent().getLongExtra("UserId", 0);
        ViewpagerAdminAdapter viewpagerAdminAdapter = new ViewpagerAdminAdapter(getSupportFragmentManager(), userId);
        viewPager.setAdapter(viewpagerAdminAdapter);
        tab.setupWithViewPager(viewPager);


    }


}