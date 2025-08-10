package com.soumen.listongo.ForAdmin.viewfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewpagerAdminAdapter extends FragmentPagerAdapter {
    Long userId;
    String email;
    public ViewpagerAdminAdapter(@NonNull FragmentManager fm,Long userId,String email) {
        super(fm);
        this.userId=userId;
        this.email=email;
    }


    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new PendingFragment();
                break;
            case 1:
                fragment = new ApproveByYouFragment();
                break;
            case 2:
                fragment = new EditProductDetailsFragment();
                break;
            default:
                fragment=new ReqAdminFragment();
        }

        // Pass userId to the fragment
        Bundle bundle = new Bundle();
        bundle.putLong("UserId", userId);
        bundle.putString("email",email);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Pending List";
        }else if (position==1){
            return "Approve By You";
        }else if(position==2) {
            return "Edit Product";
        }else {
            return "Admin List";
        }
    }
}
