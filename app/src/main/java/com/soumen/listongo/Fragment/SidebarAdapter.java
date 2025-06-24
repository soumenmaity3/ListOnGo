package com.soumen.listongo.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.soumen.listongo.R;

import java.util.List;

public class SidebarAdapter extends RecyclerView.Adapter<SidebarAdapter.ViewHolder> {

    private final List<SidebarItem> items;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onClick(int position);
    }

    public SidebarAdapter(List<SidebarItem> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        LinearLayout sidebarMenu;


        public ViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            sidebarMenu=itemView.findViewById(R.id.sidebarMenu);
        }

        public void bind(SidebarItem item, OnItemClickListener listener, int position) {
            icon.setImageResource(item.iconResId);
            title.setText(item.title);
            itemView.setOnClickListener(v -> listener.onClick(position));
            sidebarMenu.setOnLongClickListener(v->{
                Toast.makeText(itemView.getContext(), "Filter By "+item.title, Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sidebar_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(items.get(position), listener, position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
