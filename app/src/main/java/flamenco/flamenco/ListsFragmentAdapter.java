package flamenco.flamenco;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

public class ListsFragmentAdapter extends FragmentStatePagerAdapter{

    int mNumOfTabs;
    private SongsFragment songsFragment;
    private ArtistsFragment artistsFragment;
    private AlbumsFragment albumsFragment;

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
