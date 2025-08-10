package com.soumen.listongo.Fragment.ItemLi;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.soumen.listongo.R;

import java.util.List;

public class SuggestedProductAdapter extends RecyclerView.Adapter<SuggestedProductAdapter.ViewHolder> {
    private Context context;
    private List<ProductListModel> productList;
    private String apiUrl;

    public SuggestedProductAdapter(Context context, List<ProductListModel> productList, String apiUrl) {
        this.context = context;
        this.productList = productList;
        this.apiUrl = apiUrl;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggested_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductListModel product = productList.get(position);

        holder.title.setText(product.getTitle());
        holder.price.setText("₹ " + product.getPrice());

        String fullImageUrl = apiUrl + "/list-on-go/product/image/" + product.getId();

        Glide.with(context)
                .load(fullImageUrl)
                .centerCrop()
                .placeholder(R.drawable.puja)
                .error(R.drawable.fruit)
                .into(holder.image);
        holder.suggestItem.setOnClickListener(view -> {
            Intent viewActivity = new Intent(context, ItemViewActivity.class);
            viewActivity.putExtra("id", product.getId());
            viewActivity.putExtra("title", product.getTitle());
            viewActivity.putExtra("price", product.getPrice());
            viewActivity.putExtra("description", product.getDescription());
            viewActivity.putExtra("category",product.getCategory());

            // Pass image URL as string
            String fullImageUrl2 = apiUrl + "/list-on-go/product/image/" + product.getId();
            viewActivity.putExtra("image_url", fullImageUrl2);

            context.startActivity(viewActivity);
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title, price;
        LinearLayout suggestItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.suggestedImage);
            title = itemView.findViewById(R.id.suggestedTitle);
            price = itemView.findViewById(R.id.suggestedPrice);
            suggestItem=itemView.findViewById(R.id.suggestItem);
        }
    }
}
