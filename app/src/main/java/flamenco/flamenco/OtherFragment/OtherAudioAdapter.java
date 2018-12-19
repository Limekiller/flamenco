package flamenco.flamenco.OtherFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class OtherAudioAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public OtherAudioAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AudiobooksFragment tab1 = new AudiobooksFragment();
                return tab1;
            case 1:
                PodcastsFragment tab2 = new PodcastsFragment();
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
