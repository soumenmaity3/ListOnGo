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
import android.widget.Toast;

import com.google.gson.Gson;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForAdmin.AdminListAdapter;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReqAdminFragment extends Fragment {

    ArrayList<RequestAdminModel> arrayAdmin;
    RequestAdminAdapter adapter;
    RecyclerView adminRecycle;
    String adEmail;
    SwipeRefreshLayout swipeReqAdmin;

    public ReqAdminFragment() {
        // Required empty public constructor
    }


    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_req_admin, container, false);
        adEmail = getArguments().getString("email");
        adminRecycle = view.findViewById(R.id.recycleApprove);
        swipeReqAdmin = view.findViewById(R.id.swipeReqAdmin);
        arrayAdmin = new ArrayList<>();
        adapter = new RequestAdminAdapter(getContext(), adEmail, arrayAdmin);
        adminRecycle.setLayoutManager(new LinearLayoutManager(getContext()));
        adminRecycle.setAdapter(adapter);
        adminList();
        swipeReqAdmin.setOnRefreshListener(() -> adminList());

        return view;
    }


    public void adminList() {
        swipeReqAdmin.setRefreshing(true);
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<RequestAdminModel>> adminList = apiService.pendingRequest();
        adminList.enqueue(new Callback<List<RequestAdminModel>>() {
            @Override
            public void onResponse(Call<List<RequestAdminModel>> call, Response<List<RequestAdminModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    arrayAdmin.clear();
                    arrayAdmin.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    swipeReqAdmin.setRefreshing(false);
                } else {
                    swipeReqAdmin.setRefreshing(false);
                    Toast.makeText(getContext(), "Faild to Fetch List", Toast.LENGTH_SHORT).show();
                }
                if (response.body() == null || response.body().isEmpty()) {
                    swipeReqAdmin.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<RequestAdminModel>> call, Throwable throwable) {

            }
        });
    }
}