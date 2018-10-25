package flamenco.flamenco.MainFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import flamenco.flamenco.ListsFragment.ListsFragment;

public class MainFragmentAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public MainFragmentAdapter (FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MusicFragment tab1 = new MusicFragment();
                return tab1;
            case 1:
                ListsFragment tab2 = new ListsFragment();
                return tab2;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
