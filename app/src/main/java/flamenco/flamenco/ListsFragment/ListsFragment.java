package flamenco.flamenco.ListsFragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.MusicActivity;
import flamenco.flamenco.MainFragment.SongAdapter;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class ListsFragment extends Fragment {

    public MusicActivity musicActivity;
    private ArrayList<Song> playList;
    private String mString;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.listsfragment, container, false);
        musicActivity = (MusicActivity) getActivity();
        playList = musicActivity.playList;

        final FloatingActionButton addNew = view.findViewById(R.id.addNew);
        final FloatingActionButton floatingActionButton = view.findViewById(R.id.floatingActionButton);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Playlists"));
        tabLayout.addTab(tabLayout.newTab().setText("Queue"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        floatingActionButton.hide();

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        final ListsFragmentAdapter adapter = new ListsFragmentAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        addNew.show();
                        floatingActionButton.hide();
                        break;

                    case 1:
                        // Refresh the queue when viewing the queue page
                        refreshQueue();
                        addNew.hide();
                        floatingActionButton.show();
                        break;

                    default:
                        addNew.show();
                        floatingActionButton.hide();
                        break;
                }

                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });



        final SongAdapter playAdt = new SongAdapter(getActivity(), playList, "playlists");
        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = (Fragment)viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
                final ListView playListView = fragment.getView().findViewById(R.id.playlist_list);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Title");

                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mString = input.getText().toString();
                        Song newPlaylist = new Song(0, mString, null, 0, null, null);
                        newPlaylist.setAlbumSongList(new ArrayList<Song>());
                        playList.add(newPlaylist);
                        playListView.setAdapter(playAdt);
                    }
                });

                builder.show();
            }
        });


        return view;
    }

    public void refreshQueue(){
        for (final Fragment child: getChildFragmentManager().getFragments()) {
            if (child instanceof  QueueFragment) {
                ((QueueFragment) child).refreshQueue();
            }
        }
    }
}