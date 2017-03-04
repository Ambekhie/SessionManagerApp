package competition.sessionmanagerapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Shadwa Mobdy on 07-May-16.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private Fragment[] myFragmets;

    public MyFragmentPagerAdapter(FragmentManager fm, Fragment[] fragments) {
        super(fm);
        myFragmets = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return myFragmets[position];
    }

    @Override
    public int getCount() {
        return myFragmets.length;
    }

}
