package com.soumen.listongo.ForCart;

import android.os.Bundle;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.soumen.listongo.R;
import com.soumen.listongo.SettingsUtil;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView cartList;
    private CartItemAdapter adapter;
    private ArrayList<CartModel> cartItems = new ArrayList<>();
    TextView totalPriceText;

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

        MaterialToolbar toolbar = findViewById(R.id.cartToolbar);
        totalPriceText = findViewById(R.id.text_total_price);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        cartList = findViewById(R.id.recycler_cart_items);
        adapter = new CartItemAdapter(this, cartItems,image_url);
        cartList.setLayoutManager(new LinearLayoutManager(this));
        cartList.setAdapter(adapter);

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


}
