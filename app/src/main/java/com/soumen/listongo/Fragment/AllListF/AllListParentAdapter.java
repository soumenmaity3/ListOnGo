package com.soumen.listongo.Fragment.AllListF;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.recyclerview.widget.RecyclerView;

import com.soumen.listongo.R;

import java.util.List;

public class AllListParentAdapter extends RecyclerView.Adapter<AllListParentAdapter.ViewHolder> {

    private List<AllListParentModel> items;

    public AllListParentAdapter(List<AllListParentModel> items) {
        this.items = items;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, price, quantity,priceText;
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.textTitle);
            price = itemView.findViewById(R.id.textTotalPrice);
            quantity = itemView.findViewById(R.id.textQuantity);
            priceText=itemView.findViewById(R.id.priceText);
        }
    }

    @Override
    public AllListParentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_list_parent_recycler_design, parent, false); // Reuse cart_item layout
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllListParentAdapter.ViewHolder holder, int position) {
        AllListParentModel item = items.get(position);
        holder.title.setText(item.getTitle());
        holder.price.setText("Total ₹" + (item.getPrice()*item.getQuantity()));
        holder.quantity.setText("Qty: " + item.getQuantity());
        holder.priceText.setText("Price: ₹"+item.getPrice()+"/P");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

