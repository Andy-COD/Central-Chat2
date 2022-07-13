package com.example.centralchat;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.centralchat.homefragments.ChatFragment;
import com.example.centralchat.homefragments.NewsFragment;
import com.example.centralchat.homefragments.ProfileFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final Bundle bundle;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, Bundle bundle) {
        super(fragmentActivity);
        this.bundle = bundle;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {


        switch(position) {
            case 2:
                return new ProfileFragment();
            case 0:
                return new NewsFragment();
            case 1:
            default:
                Fragment chatFragment = new ChatFragment();
                chatFragment.setArguments(bundle);
                return chatFragment;
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
