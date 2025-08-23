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

public class ReqAdminDetailsApproveUserAdapter extends RecyclerView.Adapter<ReqAdminDetailsApproveUserAdapter.viewHolder> {

    Context context;
    ArrayList<ReqAdminDetailsApproveUserModel> userList;

    public ReqAdminDetailsApproveUserAdapter(Context context, ArrayList<ReqAdminDetailsApproveUserModel> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ReqAdminDetailsApproveUserAdapter.viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.req_admin_details_approve_user_design,parent,false);
        return new ReqAdminDetailsApproveUserAdapter.viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReqAdminDetailsApproveUserAdapter.viewHolder holder, int position) {

        ReqAdminDetailsApproveUserModel model=userList.get(position);
        holder.txtId.setText(String.valueOf(model.getId()));
        holder.txtEmail.setText(model.getEmail());
        if(model.isAdmin()){
            holder.txtStatus.setText("Admin");
        }else {
            holder.txtStatus.setText("User");
        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        TextView txtId,txtEmail,txtStatus;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            txtId=itemView.findViewById(R.id.userId);
            txtEmail=itemView.findViewById(R.id.userEmail);
            txtStatus=itemView.findViewById(R.id.userCurrentStatus);
        }
    }
}
