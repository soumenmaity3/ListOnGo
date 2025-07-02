package com.soumen.listongo.Fragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static java.lang.System.load;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.soumen.listongo.ForCart.AppDatabase;
import com.soumen.listongo.ForCart.AppDatabaseClient;
import com.soumen.listongo.ForCart.CartModel;
import com.soumen.listongo.R;

import java.util.List;

public class ItemListAdapter extends RecyclerView.Adapter<ItemListAdapter.ProductViewHolder> {

    private List<ProductListModel> productList;
    static String api_url;
    static Context context;

    public ItemListAdapter(Context context, List<ProductListModel> productList, String api_url) {
        this.productList = productList;
        this.api_url = api_url;
        this.context = context;
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textPrice,
                textDescription;
        MaterialButton btnAdd;
        LinearLayout itemList;
        ImageView itemImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.itemTitle);
            textPrice = itemView.findViewById(R.id.itemPrice);
            btnAdd = itemView.findViewById(R.id.addButton);
            itemImage = itemView.findViewById(R.id.itemImage);
            textDescription = itemView.findViewById(R.id.itemDescription);
            itemList = itemView.findViewById(R.id.itemList);
        }

        public void bind(ProductListModel ProductListModel) {
            textTitle.setText(ProductListModel.getTitle());
            textPrice.setText(String.valueOf(ProductListModel.getPrice()));
            textDescription.setText(ProductListModel.getDescription());
            String fullImageUrl = api_url + "/list-on-go/product/image/" + ProductListModel.getId();

            Glide.with(itemView.getContext())
                    .load(fullImageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.puja)
                    .error(R.drawable.fruit)
                    .into(itemImage);

            itemList.setOnClickListener(view -> {
                BottomSheetDialogModel product = new BottomSheetDialogModel();
                product.imageResId = product.getImageResId();
                product.title = ProductListModel.getTitle();
                product.description = ProductListModel.getDescription();
                product.price = String.valueOf(ProductListModel.getPrice());
                showBottomSheet(context, product,ProductListModel.getId());
            });

            btnAdd.setOnClickListener(v -> {
                ProductListModel product = ProductListModel;
                AppDatabase db = AppDatabaseClient.getInstance(context);
                CartModel cartModel = new CartModel(product.getId(), product.getTitle(), product.getPrice(), product.getCategory(), product.getImageUrl(), product.getQuantity());
                new Thread(() -> {
                    CartModel existing = db.cartDao().getItemById(product.getId());

                    if (existing != null) {
                        existing.setQuantity(existing.getQuantity() + 1);
                        db.cartDao().updateItem(existing);
                    } else {
                        cartModel.setQuantity(1);
                        db.cartDao().insert(cartModel);
                        ((Activity) context).runOnUiThread(()->{
                            Toast.makeText(context, "Add to cart", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();

            });
        }
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_recycle, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        holder.bind(productList.get(position));
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private static void showBottomSheet(Context context, BottomSheetDialogModel product,Long id) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null);

        ImageView imageView = view.findViewById(R.id.image_preview);
        TextView titleText = view.findViewById(R.id.title_text);
        TextView descText = view.findViewById(R.id.description_text);
        TextView priceText = view.findViewById(R.id.price_text);

        String fullImageUrl = api_url + "/list-on-go/product/image/" +id;
        Glide.with(context)
                .load(fullImageUrl)
                .centerCrop()
                .placeholder(R.drawable.puja)
                .error(R.drawable.fruit)
                .into(imageView);

        titleText.setText(product.title);
        descText.setText(product.description);
        priceText.setText(product.price);

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}

