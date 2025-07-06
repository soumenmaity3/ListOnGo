package com.soumen.listongo.Fragment.ItemLi;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForCart.CartActivity;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ItemFragment extends Fragment {

    private RecyclerView recyclerView;
    private ItemListAdapter adapter;
    private List<ProductListModel> productList = new ArrayList<>();
    List<SidebarItem> menuItems;
    String api_url;
    TextInputEditText searchLayout;
    MaterialToolbar cart_tool;
    ProgressBar itemProgress;

    public ItemFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);
        api_url = getString(R.string.server_api);
        searchLayout = view.findViewById(R.id.searchEditText);
        itemProgress = view.findViewById(R.id.itemProgress);
        Long userId= getArguments().getLong("UserId");
        // Sidebar
        RecyclerView sidebar = view.findViewById(R.id.sidebarRecyclerView);
        sidebar.setLayoutManager(new LinearLayoutManager(getContext()));
        cart_tool = view.findViewById(R.id.cart_tool);

        cart_tool.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CartActivity.class);
            intent.putExtra("userId",userId);
            startActivity(intent);
        });

        menuItems = Arrays.asList(
                new SidebarItem(R.drawable.fruit, "All"),
                new SidebarItem(R.drawable.soft_drink, "Soft Drinks"),
                new SidebarItem(R.drawable.medicine,"Medicine"),
                new SidebarItem(R.drawable.snacks, "Sweets & Chips"),
                new SidebarItem(R.drawable.brand, "Fashion"),
                new SidebarItem(R.drawable.vegetables, "Fresh Vegetable"),
                new SidebarItem(R.drawable.husk,"Husk Store"),
                new SidebarItem(R.drawable.spices, "Spices"),
                new SidebarItem(R.drawable.fresh_fruits, "Fresh Fruits"),
                new SidebarItem(R.drawable.dried_fruits, "Dry Fruits"),
                new SidebarItem(R.drawable.roses, "Flowers & Leaves"),
                new SidebarItem(R.drawable.soap, "Body Care"),
                new SidebarItem(R.drawable.exotics, "Exotics"),
                new SidebarItem(R.drawable.coriander, "Coriander & Others"),
                new SidebarItem(R.drawable.milk_honey, "Dairy, Brade & Eggs"),
                new SidebarItem(R.drawable.electrical, "Electronics"),
                new SidebarItem(R.drawable.atta_rice, "Atta, Rice & Dal"),
                new SidebarItem(R.drawable.bread, "Bakery & Brade"),
                new SidebarItem(R.drawable.puja, "Puja Store")
        );

        SidebarAdapter sidebarAdapter = new SidebarAdapter(menuItems, position -> {
            switch (position) {
                case 0:
                    fetchProducts();
                    break;
                case 1:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 2:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 3:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 4:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 5:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 6:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 7:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 8:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 9:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 10:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 11:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 12:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 13:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 14:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 15:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 16:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 17:
                    itemByCategory(menuItems.get(position).title);
                    break;
                case 18:
                    itemByCategory(menuItems.get(position).title);
                    break;

            }
        });
        searchLayout.setOnEditorActionListener((v, id, event) -> {
            if (id == EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String inputText = searchLayout.getText().toString().trim().toLowerCase();

                if (!inputText.isEmpty()) {
                    Toast.makeText(getContext(), "You searched: " + inputText, Toast.LENGTH_SHORT).show();
                    getProductByTitle(inputText);
                } else {
                    Toast.makeText(getContext(), "Please enter something", Toast.LENGTH_SHORT).show();
                }

                return true; // consume the event
            }
            return false;
        });
        sidebar.setAdapter(sidebarAdapter);


        // RecyclerView for product list
        recyclerView = view.findViewById(R.id.itemRecycleList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ItemListAdapter(getContext(), productList, api_url);
        recyclerView.setAdapter(adapter);

        fetchProducts();

        return view;
    }

    private void fetchProducts() {
        itemProgress.setVisibility(VISIBLE);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<ProductListModel>> call = apiService.getAllProducts();

        call.enqueue(new Callback<List<ProductListModel>>() {
            @Override
            public void onResponse(Call<List<ProductListModel>> call, Response<List<ProductListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    itemProgress.setVisibility(GONE);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductListModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void itemByCategory(String category) {
        itemProgress.setVisibility(VISIBLE);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<ProductListModel>> listItem = apiService.getProductByCategory(category);
        listItem.enqueue(new Callback<List<ProductListModel>>() {
            @Override
            public void onResponse(Call<List<ProductListModel>> call, Response<List<ProductListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    itemProgress.setVisibility(GONE);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductListModel>> call, Throwable throwable) {
                Toast.makeText(getContext(), "Fail", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getProductByTitle(String title) {
        itemProgress.setVisibility(VISIBLE);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<ProductListModel>> productByTitle = apiService.getProductByTitle(title);
        productByTitle.enqueue(new Callback<List<ProductListModel>>() {
            @Override
            public void onResponse(Call<List<ProductListModel>> call, Response<List<ProductListModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    itemProgress.setVisibility(GONE);
                } else {
                    getItemByNickName(title);
                }
            }

            @Override
            public void onFailure(Call<List<ProductListModel>> call, Throwable throwable) {
                itemProgress.setVisibility(GONE);
                Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getItemByNickName(String nickName) {
        itemProgress.setVisibility(VISIBLE);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<ProductListModel>> getItemByNick = apiService.getProductByNickname(nickName);
        getItemByNick.enqueue(new Callback<List<ProductListModel>>() {
            @Override
            public void onResponse(Call<List<ProductListModel>> call, Response<List<ProductListModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    itemProgress.setVisibility(GONE);
                } else {
                    Toast.makeText(getContext(), "Failed to fetch products", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ProductListModel>> call, Throwable throwable) {

            }
        });
    }
}
