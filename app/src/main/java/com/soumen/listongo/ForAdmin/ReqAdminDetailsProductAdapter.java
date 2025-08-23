package com.soumen.listongo.ForAdmin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soumen.listongo.R;

import java.util.ArrayList;

public class ReqAdminDetailsProductAdapter extends RecyclerView.Adapter<ReqAdminDetailsProductAdapter.viewHolder> {

    ArrayList<ReqAdminDetailsProductModel> list;
    Long userId;
    Context context;

    public ReqAdminDetailsProductAdapter(ArrayList<ReqAdminDetailsProductModel> list, Long userId, Context context) {
        this.list = list;
        this.userId = userId;
        this.context = context;
    }

    @NonNull
    @Override
    public ReqAdminDetailsProductAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.req_admin_details_product_layout,parent,false);
        return new ReqAdminDetailsProductAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReqAdminDetailsProductAdapter.viewHolder holder, int position) {
        holder.txtId.setText(String.valueOf(list.get(position).getId()));
        holder.txtTitle.setText(list.get(position).getTitle());
        holder.txtPrice.setText(String.valueOf(list.get(position).getPrice()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle,txtPrice,txtId;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txtId=itemView.findViewById(R.id.productId);
            txtTitle=itemView.findViewById(R.id.productName);
            txtPrice=itemView.findViewById(R.id.productPrice2);
        }
    }
}
