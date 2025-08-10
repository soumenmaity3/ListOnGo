package com.soumen.listongo.ForAdmin.viewfragment;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.Fragment.ItemLi.SidebarAdapter;
import com.soumen.listongo.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestAdminAdapter extends RecyclerView.Adapter<RequestAdminAdapter.viewHolder> {
    Context context;
    String adEmail;
    ArrayList<RequestAdminModel> adminModels;


    public RequestAdminAdapter(Context context, String adEmail, ArrayList<RequestAdminModel> adminModels) {
        this.context = context;
        this.adEmail = adEmail;
        this.adminModels = adminModels;
    }

    @NonNull
    @Override
    public RequestAdminAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.design_request_admin_list, parent, false);
        return new RequestAdminAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestAdminAdapter.viewHolder holder, int position) {
        RequestAdminModel model = adminModels.get(position);

        holder.userID.setText("ID: " + model.getId());
        holder.userEmail.setText("Email: " + model.getEmail());
        holder.userReason.setText("Reason: " + model.getAdminReason());

        holder.approveBtn.setOnClickListener(v -> {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<ResponseBody> approve = apiService.approveAdmin(true, adEmail, model.getEmail(), model.getAdminReason());
            approve.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        adminModels.remove(position);
                        notifyItemRemoved(position);
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Toast.makeText(context, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        holder.denyBtn.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
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
                // ✅ Corrected: use typed reason instead of model.getAdminReason()
                Call<ResponseBody> deny = apiService.approveAdmin(false, adEmail, model.getEmail(), reason);

                deny.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        int position = holder.getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            adminModels.remove(position);
                            notifyItemRemoved(position);
                        }
                        dialog.dismiss();
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                        Toast.makeText(context, "Error: " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        });
    }


    @Override
    public int getItemCount() {
        return adminModels.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView userID, userEmail, userReason;
        MaterialButton denyBtn, approveBtn;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            userID = itemView.findViewById(R.id.user_id);
            userEmail = itemView.findViewById(R.id.user_email);
            userReason = itemView.findViewById(R.id.user_reason);
            denyBtn = itemView.findViewById(R.id.btn_deny);
            approveBtn = itemView.findViewById(R.id.btn_approve);

        }
    }
}
