package flamenco.flamenco.MainFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import flamenco.flamenco.MainFragment.AlbumsFragment;
import flamenco.flamenco.MainFragment.ArtistsFragment;
import flamenco.flamenco.MainFragment.SongsFragment;

public class MusicFragmentAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;
    private SongsFragment songsFragment;
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;

    public MusicFragmentAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {


        switch (position) {
            case 0:
                SongsFragment tab1 = new SongsFragment();
                return tab1;
            case 1:
                ArtistsFragment tab2 = new ArtistsFragment();
                return tab2;
            case 2:
                AlbumsFragment tab3 = new AlbumsFragment();
                return tab3;
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
