package flamenco.flamenco.MainFragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.Folder;
import flamenco.flamenco.MusicActivity;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class MusicFragment extends Fragment {

    private ArrayList<Song> albumList;
    private GridView songView;
    public MusicActivity musicActivity;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_list_music, container, false);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.TabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Songs"));
        tabLayout.addTab(tabLayout.newTab().setText("Artists"));
        tabLayout.addTab(tabLayout.newTab().setText("Albums"));
        tabLayout.addTab(tabLayout.newTab().setText("Folders"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
        final float deviceHeight = displayMetrics.heightPixels;
        musicActivity = (MusicActivity) getActivity();

        final ViewPager viewPager = (ViewPager) view.findViewById(R.id.pager);
        final MusicFragmentAdapter adapter = new MusicFragmentAdapter
                (getChildFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

                if (tab.getText().equals("Folders") && musicActivity.lastFolder != null) {
                    Folder lastFolder = musicActivity.lastFolder;
                    musicActivity.lastFolder = lastFolder.getParentFolder();

                    if (lastFolder.getParentFolder() == null) {

                        ObjectAnimator animation = ObjectAnimator.ofFloat(musicActivity.findViewById(R.id.folderFocus),
                                "translationY", 0, -70);
                        animation.setDuration(300);
                        animation.setStartDelay(75);
                        animation.start();


                        animation = ObjectAnimator.ofFloat(view.findViewById(R.id.folder_list),
                                "translationY", deviceHeight, 0);
                        animation.setDuration(225);
                        animation.setStartDelay(75);
                        animation.setInterpolator(new DecelerateInterpolator(3));
                        animation.start();

                    } else {
                        FoldersAdapter foldersAdapter = new FoldersAdapter(view.getContext(), musicActivity.lastFolder.getFolderList(), musicActivity.lastFolder.getSongList());
                        ((ListView) view.findViewById(R.id.f_folder_list)).setAdapter(foldersAdapter);

                        ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.folderFocus),
                                "translationY", deviceHeight, 0);
                        animation.setDuration(300);
                        animation.setStartDelay(75);
                        animation.start();
                    }
                }
                if (tab.getText().equals("Albums") && view.findViewById(R.id.album_list).getY() != 0) {
                    ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.album_list),
                            "translationY", deviceHeight, 0);
                    animation.setInterpolator(new DecelerateInterpolator(3));
                    animation.setDuration(300);
                    animation.start();

                    animation = ObjectAnimator.ofFloat(view.findViewById(R.id.albumFocus),
                            "translationY", 0, -70);
                    animation.setDuration(300);
                    animation.start();
                }
                if (tab.getText().equals("Artists") && view.findViewById(R.id.artist_list).getY() != 0) {

                    view.findViewById(R.id.album_list).setAlpha(0.99f);
                    ObjectAnimator animation2 = ObjectAnimator.ofFloat(view.findViewById(R.id.artist_list),
                            "translationY", deviceHeight, 0);
                    animation2.setDuration(300);
                    animation2.setInterpolator(new DecelerateInterpolator(3));
                    animation2.start();
                    view.findViewById(R.id.artist_list).setAlpha(1f);
                }
            }
        });


        return view;
    }
}