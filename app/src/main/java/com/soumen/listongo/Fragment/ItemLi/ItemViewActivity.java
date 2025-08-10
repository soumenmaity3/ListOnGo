package com.soumen.listongo.Fragment.ItemLi;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForCart.AppDatabase;
import com.soumen.listongo.ForCart.AppDatabaseClient;
import com.soumen.listongo.ForCart.CartActivity;
import com.soumen.listongo.ForCart.CartModel;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ItemViewActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    ArrayList<ProductListModel> productList;
    SuggestedProductAdapter adapter;
    MaterialButton btnAddCart,btnGoCart;
    private ProductListModel selectedProduct;
    MaterialToolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);

        Window window = getWindow();
        window.setStatusBarColor(Color.TRANSPARENT);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ImageView productImage = findViewById(R.id.productImage);
        TextView title = findViewById(R.id.productTitle);
        TextView price = findViewById(R.id.productPrice);
        TextView description = findViewById(R.id.productDescription);
        btnAddCart=findViewById(R.id.btnAddToCart);
        btnGoCart=findViewById(R.id.btnGoToCart);
        toolbar=findViewById(R.id.itemToolbar);
        toolbar.setNavigationOnClickListener(view -> onBackPressed());




        // Get data from Intent
        Long id=getIntent().getLongExtra("id",0);
        String imageUrl = getIntent().getStringExtra("image_url");
        String productTitle = getIntent().getStringExtra("title");
        String productDesc = getIntent().getStringExtra("description");
        String productCat = getIntent().getStringExtra("category");
        int credit=getIntent().getIntExtra("credit",0);
        double productPrice = getIntent().getDoubleExtra("price", 0.0);
        String apiUrl=getString(R.string.server_api);

        selectedProduct=new ProductListModel(id,productTitle,productDesc,imageUrl,productPrice,productCat, true);

        btnGoCart.setOnClickListener(v->{
            Intent intent=new Intent(ItemViewActivity.this, CartActivity.class);
            intent.putExtra("credit",credit);
            startActivity(intent);
        });

        btnAddCart.setOnClickListener(v->{
            ProductListModel product =selectedProduct;
            AppDatabase db = AppDatabaseClient.getInstance(ItemViewActivity.this);
            CartModel cartModel = new CartModel(product.getId(), product.getTitle(), product.getPrice(), product.getCategory(), product.getImageUrl(), product.getQuantity());
            new Thread(() -> {
                CartModel existing = db.cartDao().getItemById(product.getId());

                if (existing != null) {
                    existing.setQuantity(existing.getQuantity() + 1);
                    db.cartDao().updateItem(existing);
                } else {
                    cartModel.setQuantity(1);
                    db.cartDao().insert(cartModel);
                }
                ((Activity) ItemViewActivity.this).runOnUiThread(() -> {
                    if (existing == null) {
                        Toast.makeText(ItemViewActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                    }
                });

            }).start();
        });

        title.setText(productTitle);
        price.setText("₹ " + productPrice);
        description.setText(productDesc);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.puja)
                .error(R.drawable.fruit)
                .into(productImage);


         recyclerView = findViewById(R.id.suggestedRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        productList = new ArrayList<>();
        adapter = new SuggestedProductAdapter(ItemViewActivity.this,  productList, apiUrl);
fetchByCat(productCat);
        recyclerView.setAdapter(adapter);

    }

    public void fetchByCat(String category){
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<ProductListModel>> getProductByCat=apiService.getProductByCategory(category);
        getProductByCat.enqueue(new Callback<List<ProductListModel>>() {
            @Override
            public void onResponse(Call<List<ProductListModel>> call, Response<List<ProductListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ItemViewActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductListModel>> call, Throwable throwable) {

            }
        });
    }
}
