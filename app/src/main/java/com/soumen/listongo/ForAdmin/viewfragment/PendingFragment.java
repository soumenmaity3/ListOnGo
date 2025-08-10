package com.soumen.listongo.ForAdmin.viewfragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForAdmin.AdminActivity;
import com.soumen.listongo.ForAdmin.AdminListAdapter;
import com.soumen.listongo.ForAdmin.AdminProductModel;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingFragment extends Fragment {
    RecyclerView recyclerView;
    ArrayList<AdminProductModel> arrayProduct;
    AdminListAdapter adapter;
    TextView txtDone;
    SwipeRefreshLayout pendingSwipe;

    public PendingFragment() {
        // Required empty public constructor
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        recyclerView = view.findViewById(R.id.adminPanel);
        txtDone = view.findViewById(R.id.txtDone);
        pendingSwipe=view.findViewById(R.id.pendingSwipe);
        arrayProduct = new ArrayList<>();

        String userEmail=getArguments().getString("email");
        String image_url=getString(R.string.server_api);

//         SET LAYOUT MANAGER & ADAPTER
        adapter = new AdminListAdapter(arrayProduct, getContext(),userEmail,image_url);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

// Call API
        getAdminProduct();
        pendingSwipe.setOnRefreshListener(()->getAdminProduct());
        return view;
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
                    adapter.notifyDataSetChanged();
                    pendingSwipe.setRefreshing(false);

                } else {
                    Toast.makeText(getContext(), "Faild to Fetch List", Toast.LENGTH_SHORT).show();
                }
                if (response.body() == null||response.body().isEmpty()) {
                    txtDone.setVisibility(VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<AdminProductModel>> call, Throwable throwable) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}