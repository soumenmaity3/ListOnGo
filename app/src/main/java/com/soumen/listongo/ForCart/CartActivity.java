package com.soumen.listongo.ForCart;

import android.os.Build;
import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.R;
import com.soumen.listongo.SettingActivity.SettingsUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartList;
    private CartItemAdapter adapter;
    private List<CartModel> cartItems = new ArrayList<>();
    TextView totalPriceText;
    String email;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SettingsUtil.applyTheme(this);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        String image_url=getString(R.string.server_api);
        Long id= getIntent().getLongExtra("userId",0);
        email=getIntent().getStringExtra("email");

        MaterialToolbar toolbar = findViewById(R.id.cartToolbar);
        MaterialButton btnAddList=findViewById(R.id.button_cart);
        totalPriceText = findViewById(R.id.text_total_price);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        cartList = findViewById(R.id.recycler_cart_items);
        adapter = new CartItemAdapter(this, cartItems,image_url);
        cartList.setLayoutManager(new LinearLayoutManager(this));
        cartList.setAdapter(adapter);

        int credit=getIntent().getIntExtra("credit",0);

        btnAddList.setOnClickListener(v -> {
            if (credit>=15) {
                List<ForAllListModel> allListModels = new ArrayList<>();
                TextInputEditText edtListName = findViewById(R.id.edtListName);
                String listName = edtListName.getText().toString();


                for (CartModel item : cartItems) {
                    ForAllListModel model = new ForAllListModel();
                    model.setTitle(item.getTitle());
                    model.setPrice(item.getPrice());
                    model.setQuantity(item.getQuantity());
                    model.setList_name(listName);
                    allListModels.add(model);
                }
                if (allListModels.isEmpty()) {
                    Toast.makeText(this, "Add Some product", Toast.LENGTH_SHORT).show();
                    return;
                }

                ApiService apiService = ApiClient.getInstance().create(ApiService.class);
                Call<ResponseBody> createList = apiService.addList(allListModels, id);
                createList.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(CartActivity.this, "List uploaded successfully", Toast.LENGTH_SHORT).show();
                            AppDatabase db = AppDatabaseClient.getInstance(CartActivity.this);
                            btnAddList.setEnabled(false);
                            new Thread(() -> {
                                db.cartDao().delete();
                                runOnUiThread(() -> {
                                    cartItems.clear();
                                    edtListName.setText("");
                                    adapter.notifyDataSetChanged();
                                    totalPriceText.setText("₹0.00");
                                });
                            }).start();
                            cost();
                        } else {
                            Toast.makeText(CartActivity.this, "Upload failed: " + response.code(), Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                    }
                });
            }else {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("Insufficient Credits")
                        .setMessage("You don’t have enough credit points to perform this action.\nPlease buy credits to continue.")
                        .setPositiveButton("OK", null)
                        .show();
            }


        });
        observeCartItems();

    }

    private void observeCartItems() {
        AppDatabase db = AppDatabaseClient.getInstance(this);
        db.cartDao().getAll().observe(this, items -> {
            cartItems.clear();
            cartItems.addAll(items);
            adapter.notifyDataSetChanged();

            double sum = 0;
            for (CartModel item : cartItems) {
                sum += item.getPrice() * item.getQuantity();
            }
            totalPriceText.setText("₹" + String.format("%.2f", sum));
        });
    }

    public void cost(){
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<ResponseBody> costCredit=apiService.costCredit(email,15);
        costCredit.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {

            }
        });
    }

}
