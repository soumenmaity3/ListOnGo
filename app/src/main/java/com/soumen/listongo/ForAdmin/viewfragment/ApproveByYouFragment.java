package com.soumen.listongo.ForAdmin.viewfragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForAdmin.AdminListAdapter;
import com.soumen.listongo.ForAdmin.AdminProductModel;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApproveByYouFragment extends Fragment {
    RecyclerView recycleApprove;
    ArrayList<AdminProductModel> arrayList;
    AdminListAdapter adapter;
    public ApproveByYouFragment() {
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_approve_by_you, container, false);
        recycleApprove=view.findViewById(R.id.recycleApprove);
        arrayList=new ArrayList<>();
        Long userId = getArguments().getLong("UserId");
        String image_url=getString(R.string.server_api);
        adapter = new AdminListAdapter(arrayList, getContext(),userId,image_url);
        recycleApprove.setLayoutManager(new LinearLayoutManager(getContext()));
        recycleApprove.setAdapter(adapter);

        if (userId != -1) {
            approveList(userId);
        } else {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
        }
        return view;
    }


    private void approveList(Long userId){
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<AdminProductModel>> approveByU=apiService.approveProduct(userId);
        approveByU.enqueue(new Callback<List<AdminProductModel>>() {
            @Override
            public void onResponse(Call<List<AdminProductModel>> call, Response<List<AdminProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    arrayList.clear();
                    arrayList.addAll(response.body());
                    adapter.notifyDataSetChanged();
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