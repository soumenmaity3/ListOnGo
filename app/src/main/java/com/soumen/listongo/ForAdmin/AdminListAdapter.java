package com.soumen.listongo.ForAdmin;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminListAdapter extends RecyclerView.Adapter<AdminListAdapter.viewHolder> {
    ArrayList<AdminProductModel> arrayList;
    Context context;
    Long adminId;
    String image_url;

    public AdminListAdapter(ArrayList<AdminProductModel> arrayList, Context context, Long adminId,String image_url) {
        this.arrayList = arrayList;
        this.context = context;
        this.adminId = adminId;
        this.image_url=image_url;
    }

    @NonNull
    @Override
    public AdminListAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_panel_list, parent, false);
        return new AdminListAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminListAdapter.viewHolder holder, int position) {
        AdminProductModel model = arrayList.get(position);
        holder.txtTitle.setText(model.getTitle());
        holder.txtPrice.setText(String.valueOf(model.getPrice()));
        holder.txtDescription.setText(model.getDescription());
        String full_image=image_url+"/list-on-go/product/image/"+model.getId();
        Glide.with(context)
                .load(full_image)
                .placeholder(R.drawable.ic_upload)
                .error(R.drawable.puja)
                .into(holder.imgProduct);
        holder.btnActive.setOnClickListener(v -> {
            ApiService apiService = ApiClient.getInstance().create(ApiService.class);
            Call<ResponseBody> makeUser = apiService.makeUserProduct(model.getId(), adminId);
            makeUser.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Log.d("model.getId()", String.valueOf(model.getId()));
                    int position = holder.getAdapterPosition();
                    notifyItemRemoved(position);
                    arrayList.remove(position);
                    notifyDataSetChanged();

                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                    Toast.makeText(context, "Error" + throwable, Toast.LENGTH_SHORT).show();
                    Log.d("PutError", String.valueOf(throwable));
                }
            });
        });
        holder.adminPanelList.setOnClickListener(v -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            View setView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null);
            bottomSheetDialog.setContentView(setView);
            ImageView bottomImage = setView.findViewById(R.id.image_preview);
            TextView txtTitle = setView.findViewById(R.id.title_text);
            TextView txtDescription = setView.findViewById(R.id.description_text);
            TextView txtPrice = setView.findViewById(R.id.price_text);
            String load_image=image_url+"/list-on-go/product/image/"+model.getId();
            Glide.with(context)
                    .load(load_image)
                    .placeholder(R.drawable.ic_upload)
                    .error(R.drawable.puja)
                    .into(holder.imgProduct);
            txtTitle.setText(model.getTitle());
            txtDescription.setText(model.getDescription());
            txtPrice.setText(String.valueOf(model.getPrice()));
            bottomSheetDialog.show();
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtDescription, txtPrice;
        MaterialButton btnActive;
        ImageView imgProduct;
        RelativeLayout adminPanelList;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txtDescription = itemView.findViewById(R.id.description);
            txtPrice = itemView.findViewById(R.id.price);
            txtTitle = itemView.findViewById(R.id.ad_title);
            btnActive = itemView.findViewById(R.id.action_button);
            imgProduct = itemView.findViewById(R.id.image);
            adminPanelList = itemView.findViewById(R.id.adminPanelList);
        }
    }
}
