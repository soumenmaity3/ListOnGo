package com.soumen.listongo.Fragment.AllListF;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForCart.ForAllListModel;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllListFragment extends Fragment {

    private RecyclerView parentRecycler;
    private AllListFragmentAdapter parentAdapter;
    private List<AllListFragmentModel> data = new ArrayList<>();
    SwipeRefreshLayout allListSwipe;

    public AllListFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_list, container, false);
        parentRecycler = view.findViewById(R.id.AllListRecycler);
        allListSwipe=view.findViewById(R.id.allListSwipe);
        parentRecycler.setLayoutManager(new LinearLayoutManager(getContext()));

        Long userId=getArguments().getLong("UserId");

        fetchData(userId);
        allListSwipe.setOnRefreshListener(()->fetchData(userId));

        return view;
    }

    private void fetchData(Long userId) {
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);

        Call<List<AllListFragmentModel>> call = apiService.getGroupedList(userId);

        call.enqueue(new Callback<List<AllListFragmentModel>>() {
            @Override
            public void onResponse(Call<List<AllListFragmentModel>> call, Response<List<AllListFragmentModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AllListFragmentModel> data = response.body();

                    data.sort((a, b) -> b.getDateTime().compareTo(a.getDateTime()));

                    parentAdapter = new AllListFragmentAdapter(getContext(), data);
                    parentRecycler.setAdapter(parentAdapter);
                    allListSwipe.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<List<AllListFragmentModel>> call, Throwable t) {
                Log.e("AllListFragment", "API call failed: " + t.getMessage());
            }
        });
    }



}
