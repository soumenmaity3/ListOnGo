package com.soumen.listongo.ForCart;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.viewHolder> {
    private Context context;
    private List<CartModel> arrCart;
    String image_url;

    public CartItemAdapter(Context context, List<CartModel> arrCart, String image_url) {
        this.context = context;
        this.arrCart = arrCart;
        this.image_url = image_url;
    }

    @NonNull
    @Override
    public CartItemAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item_view, parent, false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemAdapter.viewHolder holder, int position) {
        CartModel model = arrCart.get(position);
        holder.textTitle.setText(model.getTitle());
        double mTotalPrice = model.getPrice() * model.getQuantity();
        holder.price.setText(String.format("₹%.2f", mTotalPrice));
        holder.txtQuantity.setText(String.valueOf(model.getQuantity()));
        String fullUrl = image_url + "/list-on-go/product/image/" + model.getId();
        Glide.with(context)
                .load(fullUrl)
                .placeholder(R.drawable.ic_upload)
                .error(R.drawable.puja)
                .into(holder.productIma);
        holder.btnPlus.setOnClickListener(v -> {
            AppDatabase db = AppDatabaseClient.getInstance(context);
            new Thread(() -> {
                db.cartDao().increaseQuantityByOne(model.getId());
                int updatedQuantity = db.cartDao().getQuantityById(model.getId());
                model.setQuantity(updatedQuantity);

                ((Activity) context).runOnUiThread(() -> {
                    holder.txtQuantity.setText(String.valueOf(updatedQuantity));
                    double totalPrice = model.getPrice() * updatedQuantity;
                    holder.price.setText("₹" + totalPrice);
                });
            }).start();
        });


        holder.btnMinus.setOnClickListener(v -> {
            AppDatabase db = AppDatabaseClient.getInstance(context);
            new Thread(() -> {
                db.cartDao().decreaseQuantityByOne(model.getId());
                int updatedQuantity = db.cartDao().getQuantityById(model.getId());

                if (updatedQuantity <= 0) {
                    db.cartDao().deleteById(model.getId());
                }

                ((Activity) context).runOnUiThread(() -> {
                    int currentPosition = holder.getAdapterPosition();
                    if (updatedQuantity > 0) {
                        model.setQuantity(updatedQuantity);
                        holder.txtQuantity.setText(String.valueOf(updatedQuantity));
                        double totalPrice = model.getPrice() * updatedQuantity;
                        holder.price.setText("₹" + totalPrice);
                    } else {
                        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();

                        if (currentPosition >= 0 && currentPosition < arrCart.size()) {
                            arrCart.remove(currentPosition);
                            notifyItemRemoved(currentPosition);
                            notifyItemRangeChanged(currentPosition, arrCart.size());
                        }
                    }
                });
            }).start();
        });


    }

    @Override
    public int getItemCount() {
        return arrCart.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        ImageView productIma;
        TextView textTitle, txtQuantity, price;
        ImageButton btnPlus, btnMinus;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            productIma = itemView.findViewById(R.id.img_product);
            textTitle = itemView.findViewById(R.id.text_product_name);
            price = itemView.findViewById(R.id.text_price);
            txtQuantity = itemView.findViewById(R.id.text_quantity);
            btnMinus = itemView.findViewById(R.id.btn_decrease);
            btnPlus = itemView.findViewById(R.id.btn_increase);
        }
    }
}
