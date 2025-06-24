package com.soumen.listongo.ForAdmin;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.*;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.MainActivity;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<AdminProductModel> arrayProduct;
    AdminListAdapter adapter;
    TextView txtDone;
    String api_url;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        api_url = getString(R.string.server_api);

        recyclerView = findViewById(R.id.adminPanel);
        arrayProduct = new ArrayList<>();
        txtDone = findViewById(R.id.txtDone);

// SET LAYOUT MANAGER & ADAPTER
        adapter = new AdminListAdapter(arrayProduct, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

// Call API
        getAdminProduct();

    }

    public void getAdminProduct() {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<AdminProductModel>> call = apiService.AdminList();
        call.enqueue(new Callback<List<AdminProductModel>>() {
            @Override
            public void onResponse(Call<List<AdminProductModel>> call, Response<List<AdminProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    txtDone.setVisibility(GONE);
                    arrayProduct.clear();
                    arrayProduct.addAll(response.body());
                    Log.d("AdminResponse", response.body().toString());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(AdminActivity.this, "Faild to Fetch List", Toast.LENGTH_SHORT).show();
                }
                if (response.body() == null||response.body().isEmpty()) {
                    txtDone.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AdminProductModel>> call, Throwable throwable) {
                Toast.makeText(AdminActivity.this, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

}