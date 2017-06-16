package com.example.alip.evcma;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Alip on 20/12/2016.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if(position == 0){
            return new Fragment1();
        /*} else if (position == 2){
            return new Fragment3();
        } else if (position == 3){
            return new Fragment4();*/
        } else {
            return new Fragment2();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
