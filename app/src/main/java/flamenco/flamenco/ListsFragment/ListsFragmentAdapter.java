package flamenco.flamenco.ListsFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ListsFragmentAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;


    public ListsFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                PlaylistsFragment tab1 = new PlaylistsFragment();
                return tab1;
            case 1:
                QueueFragment tab2 = new QueueFragment();
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
