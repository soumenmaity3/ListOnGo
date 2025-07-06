package com.soumen.listongo.ForAdmin.viewfragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewpagerAdminAdapter extends FragmentPagerAdapter {
    Long userId;
    public ViewpagerAdminAdapter(@NonNull FragmentManager fm,Long userId) {
        super(fm);
        this.userId=userId;
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
            default:
                fragment = new EditProductDetailsFragment(); // fallback
        }

        // Pass userId to the fragment
        Bundle bundle = new Bundle();
        bundle.putLong("UserId", userId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return "Pending List";
        }else if (position==1){
            return "Approve By You";
        }else {
            return "Edit Product";
        }
    }
}
