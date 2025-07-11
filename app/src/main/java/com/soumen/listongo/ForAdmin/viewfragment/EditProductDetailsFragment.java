package com.soumen.listongo.ForAdmin.viewfragment;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForAdmin.AdminProductModel;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductDetailsFragment extends Fragment {

    public EditProductDetailsFragment() {
        // Required empty public constructor
    }

    RecyclerView edtRecycler;
    ArrayList<AdminProductModel> arrayList;
    EditProductAdapter adapter;
    SwipeRefreshLayout editSwipe;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_product_details, container, false);
        edtRecycler = view.findViewById(R.id.edtRecycler);
        editSwipe=view.findViewById(R.id.editSwipe);
        Long userId = getArguments().getLong("UserId");
        String image_url = getString(R.string.server_api);
        arrayList = new ArrayList<>();
        adapter = new EditProductAdapter(getContext(), arrayList, image_url);
        edtRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        edtRecycler.setAdapter(adapter);
        if (userId != -1) {
            fetchForEdit(userId);
            editSwipe.setOnRefreshListener(()->fetchForEdit(userId));
        } else {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void fetchForEdit(Long userId) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<AdminProductModel>> approveByU = apiService.approveProduct(userId);
        approveByU.enqueue(new Callback<List<AdminProductModel>>() {
            @Override
            public void onResponse(Call<List<AdminProductModel>> call, Response<List<AdminProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    arrayList.clear();
                    arrayList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    editSwipe.setRefreshing(false);
                } else {
                    Toast.makeText(getContext(), "Failed to Fetch List", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<AdminProductModel>> call, Throwable throwable) {
                Toast.makeText(getContext(), "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}