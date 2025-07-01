package com.soumen.listongo.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.google.android.material.button.MaterialButton;
import com.soumen.listongo.MainActivity;
import com.soumen.listongo.OptionActivity;
import com.soumen.listongo.R;
import com.soumen.listongo.SettingsUtil;


public class ProfileFragment extends Fragment {
    public ProfileFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);
        TextView name=view.findViewById(R.id.text_username);
        TextView userid=view.findViewById(R.id.text_user_id);
        TextView email=view.findViewById(R.id.text_email);
        MaterialButton logoutButton=view.findViewById(R.id.button_logout);

        Long id=getArguments().getLong("UserId");
        String userName=getArguments().getString("UserName");
        String userEmail=getArguments().getString("email");

        email.setText(userEmail);
        name.setText(userName);
        userid.setText(id.toString());

        logoutButton.setOnClickListener(v->{
            Intent intent = new Intent(getContext(), OptionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            requireActivity().finish();
        });
        return view;
    }
}