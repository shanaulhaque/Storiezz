package com.sh.storiezz.profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by shanaulhaque on 01/08/17.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {


    private ProfileActivity profileActivity;

    public SectionsPagerAdapter(FragmentManager fm,ProfileActivity profileActivity) {
        super(fm);
        this.profileActivity = profileActivity;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new FollowersFragment();
            case 1: return new FollowingFragment();
            case 2: return new StoriesFragment();
            default: return null;

        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    public CharSequence getPageTitle(int position){
        switch (position){
            case 0: return profileActivity.getCount(position)+" FOLLOWERS";
            case 1: return profileActivity.getCount(position)+" FOLLOWING";
            case 2: return profileActivity.getCount(position)+" STORIES";
            default: return null;

        }
    }






}
