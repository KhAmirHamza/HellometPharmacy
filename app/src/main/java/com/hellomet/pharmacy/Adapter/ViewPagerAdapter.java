package com.hellomet.pharmacy.Adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    Context context;
    List<String> titleList;
    List<Fragment> fragmentList;

    public ViewPagerAdapter(@NonNull FragmentManager fm, Context context, List<String> titleList, List<Fragment> fragmentList) {
        super(fm);
        this.context = context;
        this.titleList = titleList;
        this.fragmentList = fragmentList;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        //Toast.makeText(context, "Size: "+fragmentList.size(), Toast.LENGTH_SHORT).show();
        return fragmentList.size();

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titleList.get(position);
    }
}
