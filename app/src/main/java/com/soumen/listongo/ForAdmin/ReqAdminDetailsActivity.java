package com.soumen.listongo.ForAdmin;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
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
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReqAdminDetailsActivity extends AppCompatActivity {
    Long id;
    String email, reason, adEmail;

    TextView userEmail, userId, userReason,emptyProduct;

    MaterialToolbar toolbar;

    MaterialButton denyBtn, approveBtn;

    RecyclerView recyclerAddByThis,approveUserList;

    ReqAdminDetailsApproveUserAdapter userAdapter;
    ArrayList<ReqAdminDetailsApproveUserModel>userLists=new ArrayList<>();

    ReqAdminDetailsProductAdapter adapter;
    ArrayList<ReqAdminDetailsProductModel>list=new ArrayList<>();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_req_admin_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        id = getIntent().getLongExtra("userId", 0);
        email = getIntent().getStringExtra("userEmail");
        reason = getIntent().getStringExtra("userReason");
        adEmail = getIntent().getStringExtra("adminEmail");

        userEmail = findViewById(R.id.emailText);
        userId = findViewById(R.id.reqAdminId);
        userReason = findViewById(R.id.reasonText);
        toolbar = findViewById(R.id.topAppBar);
        denyBtn = findViewById(R.id.denyButton);
        approveBtn = findViewById(R.id.approveButton);
        recyclerAddByThis = findViewById(R.id.addBYThis);
        emptyProduct=findViewById(R.id.emptyProduct);
        approveUserList=findViewById(R.id.approveUserList);

        toolbar.setSubtitle(id.toString());

        toolbar.setNavigationOnClickListener(v -> {
            finish();
        });



        userEmail.setText(email);
        userReason.setText(reason);
        userId.setText(String.valueOf(id));

        denyBtn.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog_deny_reason);
            dialog.show();

            TextInputEditText adminReason = dialog.findViewById(R.id.input_reason);
            MaterialButton btnSub = dialog.findViewById(R.id.btn_submit_reason);

            btnSub.setOnClickListener(vi -> {
                String reason = adminReason.getText().toString();
                if (reason == null || reason.isEmpty()) {
                    adminReason.setError("Reason required");
                    return;
                }

                ApiService apiService = ApiClient.getInstance().create(ApiService.class);
                Call<ResponseBody> deny = apiService.approveAdmin(false, adEmail, email, reason);

                deny.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            denyBtn.setEnabled(false);
                            approveBtn.setEnabled(false);
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        Toast.makeText(ReqAdminDetailsActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });


        approveBtn.setOnClickListener(view -> {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<ResponseBody> approve = apiService.approveAdmin(true, adEmail, email, reason);
            approve.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        denyBtn.setEnabled(false);
                        approveBtn.setEnabled(false);
                    Toast.makeText(ReqAdminDetailsActivity.this, "Approve Done", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Toast.makeText(ReqAdminDetailsActivity.this, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });


        adapter = new ReqAdminDetailsProductAdapter(list, id, this);
        recyclerAddByThis.setLayoutManager(new LinearLayoutManager(this));
        addByThis(id);
        recyclerAddByThis.setAdapter(adapter);

        userAdapter=new ReqAdminDetailsApproveUserAdapter(this,userLists);
        approveUserList.setLayoutManager(new LinearLayoutManager(this));
        approveUserListUpdate(email);
        approveUserList.setAdapter(userAdapter);

    }

    public void addByThis(Long userId){
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<ReqAdminDetailsProductModel>> addByThis = apiService.addByThis(userId);
        addByThis.enqueue(new Callback<List<ReqAdminDetailsProductModel>>() {
            @Override
            public void onResponse(Call<List<ReqAdminDetailsProductModel>> call, Response<List<ReqAdminDetailsProductModel>> response) {
                if (response.body() == null || response.body().isEmpty()) {
                    emptyProduct.setVisibility(VISIBLE);
                } else {
                    emptyProduct.setVisibility(GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                   list.clear();
                   list.addAll(response.body());
                   adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ReqAdminDetailsActivity.this, "Failed To Fetch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReqAdminDetailsProductModel>> call, Throwable throwable) {

            }
        });
    }

    public void approveUserListUpdate(String email){
        ApiService apiService = ApiClient.getInstance().create(ApiService.class);
        Call<List<ReqAdminDetailsApproveUserModel>> approveUser=apiService.approveByThisUser(email);
        approveUser.enqueue(new Callback<List<ReqAdminDetailsApproveUserModel>>() {
            @Override
            public void onResponse(Call<List<ReqAdminDetailsApproveUserModel>> call, Response<List<ReqAdminDetailsApproveUserModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    userLists.clear();
                    userLists.addAll(response.body());
                    userAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(ReqAdminDetailsActivity.this, "Failed To Fetch", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReqAdminDetailsApproveUserModel>> call, Throwable throwable) {

            }
        });

    }

}