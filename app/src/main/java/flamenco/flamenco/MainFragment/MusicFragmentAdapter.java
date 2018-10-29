package flamenco.flamenco.MainFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import flamenco.flamenco.MainFragment.AlbumsFragment;
import flamenco.flamenco.MainFragment.ArtistsFragment;
import flamenco.flamenco.MainFragment.SongsFragment;

public class MusicFragmentAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;

    public MusicFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                return new SongsFragment();
            case 1:
                return new ArtistsFragment();
            case 2:
                return new AlbumsFragment();
            case 3:
                return new FoldersFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
