package com.soumen.listongo.Fragment.AllListF;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.soumen.listongo.ForCart.ForAllListModel;
import com.soumen.listongo.R;

import java.util.ArrayList;
import java.util.List;

public class AllListFragmentAdapter extends RecyclerView.Adapter<AllListFragmentAdapter.ViewHolder> {

    private List<AllListFragmentModel> groupedLists;
    private Context context;

    public AllListFragmentAdapter(Context context, List<AllListFragmentModel> groupedLists) {
        this.context = context;
        this.groupedLists = groupedLists;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateText,listName,listTotal;
        RecyclerView childRecycler;
        LinearLayout itemCartGroup;

        public ViewHolder(View itemView) {
            super(itemView);
            listName=itemView.findViewById(R.id.listName);
            dateText = itemView.findViewById(R.id.date_text);
            childRecycler = itemView.findViewById(R.id.child_recycler);
            itemCartGroup=itemView.findViewById(R.id.itemCartGroup);
            listTotal=itemView.findViewById(R.id.listPrice);
        }
    }

    @Override
    public AllListFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.all_list_item_cart_group, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AllListFragmentAdapter.ViewHolder holder, int position) {
        AllListFragmentModel model = groupedLists.get(position);

        holder.dateText.setText(model.getDateTime());
        holder.listName.setText(model.getListName());
        List<AllListParentModel> childItems = new ArrayList<>();
        double total=0;
        for (ForAllListModel item : model.getItems()) {
            childItems.add(new AllListParentModel(
                    Math.toIntExact(item.getId()),
                    item.getTitle(),
                    item.getPrice(),
                    item.getDateAndTime(),
                    item.getQuantity(),
                    item.getList_name()
            ));
            total+=(item.getPrice()*item.getQuantity());
        }
        holder.listTotal.setText("Total: ₹"+String.valueOf(total));

        AllListParentAdapter childAdapter = new AllListParentAdapter(childItems);
        holder.childRecycler.setLayoutManager(new LinearLayoutManager(context));
        holder.childRecycler.setAdapter(childAdapter);

        // Optional: Set click listener to go to details activity/fragment
        holder.itemCartGroup.setOnClickListener(v -> {
            // pass model.getItems() or group ID to a detail view
            Toast.makeText(context, "item", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return groupedLists.size();
    }
}

