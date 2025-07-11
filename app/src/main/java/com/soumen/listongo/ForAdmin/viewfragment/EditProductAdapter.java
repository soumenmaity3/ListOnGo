package com.soumen.listongo.ForAdmin.viewfragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.soumen.listongo.ApiClient;
import com.soumen.listongo.ApiService;
import com.soumen.listongo.ForAdmin.AdminProductModel;
import com.soumen.listongo.R;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProductAdapter extends RecyclerView.Adapter<EditProductAdapter.viewHolder> {
    Context context;
    ArrayList<AdminProductModel> arrayList;
    String image_url;
//    Long proId;

    public EditProductAdapter(Context context, ArrayList<AdminProductModel> arrayList, String image_url) {
        this.context = context;
        this.arrayList = arrayList;
        this.image_url = image_url;
//        this.proId = proId;
    }

    @NonNull
    @Override
    public EditProductAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.admin_panel_list, parent, false);
        return new EditProductAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditProductAdapter.viewHolder holder, @SuppressLint("RecyclerView") int position) {
        AdminProductModel model = arrayList.get(position);
        String title = model.getTitle();
        String description = model.getDescription();
        String price = String.valueOf(model.getPrice());

        holder.txtTitle.setText(title);
        holder.txtPrice.setText(price);
        holder.txtDescription.setText(description);
        String full_image = image_url + "/list-on-go/product/image/" + model.getId();
        Glide.with(context)
                .load(full_image)
                .placeholder(R.drawable.ic_upload)
                .error(R.drawable.puja)
                .into(holder.imgProduct);
        holder.btnEdit.setText("Edit");
        holder.btnEdit.setOnClickListener(v -> {
            Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.edit_product_dialog);
            TextInputEditText edtTitle = dialog.findViewById(R.id.etTitle);
            TextInputEditText edtPrice = dialog.findViewById(R.id.etPrice);
            TextInputEditText edtDescription = dialog.findViewById(R.id.etDescription);
            TextInputEditText edtNickName = dialog.findViewById(R.id.etNickname);
            MaterialButton btnEditDone = dialog.findViewById(R.id.btnSubmit);
            edtTitle.setText(title);
            edtTitle.setEnabled(false);
            edtPrice.setText(price);
            edtNickName.setHint("Add nick name like potato-alu.");
            edtDescription.setText(description);
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("What?")
                    .setMessage("Delete Or Update")
                    .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialog.show();
                            btnEditDone.setOnClickListener(vi -> {
                                double edPrice = Double.parseDouble(edtPrice.getText().toString());
                                String edDescription = edtDescription.getText().toString();
                                String edNickName = edtNickName.getText().toString();
                                ApiService service = ApiClient.getInstance().create(ApiService.class);
                                Call<ResponseBody> update = service.update(edPrice, edDescription, model.getId(), edNickName);
                                update.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            Toast.makeText(context, "Product updated", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Update failed", Toast.LENGTH_SHORT).show();
                                        }

                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                                        dialog.dismiss();
                                        Toast.makeText(context, "Network error: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            });
                        }
                    })
                    .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ApiService apiService=ApiClient.getInstance().create(ApiService.class);
                            Call<ResponseBody> deleteProduct=apiService.deleteProduct(model.getId());
                            deleteProduct.enqueue(new Callback<ResponseBody>() {
                                @Override
                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                    if (response.isSuccessful()){
                                    Toast.makeText(context, "Deleted Done", Toast.LENGTH_SHORT).show();
                                    arrayList.remove(position);
                                    notifyItemRemoved(position);
                                    }
                                    else
                                        Toast.makeText(context, "Don't find this product", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<ResponseBody> call, Throwable throwable) {

                                }
                            });
                        }
                    })
                    .show();


        });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtPrice, txtDescription;
        MaterialButton btnEdit;
        ImageView imgProduct;

        public viewHolder(@NonNull View itemView) {
            super(itemView);

            txtPrice = itemView.findViewById(R.id.price);
            txtDescription = itemView.findViewById(R.id.description);
            txtTitle = itemView.findViewById(R.id.ad_title);
            btnEdit = itemView.findViewById(R.id.action_button);
            imgProduct = itemView.findViewById(R.id.image);
        }
    }
}
