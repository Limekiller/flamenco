package flamenco.flamenco.MainFragment;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import java.util.ArrayList;

import flamenco.flamenco.Folder;
import flamenco.flamenco.ListMusic;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;
import flamenco.flamenco.animations;

public class FoldersFragment extends Fragment {

    private ArrayList<Folder> folderList;
    private ArrayList<Song> songList;
    private ListView folderView;
    private Folder lastFolder;
    public ListMusic listMusic;
    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.foldersfragment, container, false);
        final View folderFocus = view.findViewById(R.id.folderFocus);

        folderView = view.findViewById(R.id.folder_list);
        listMusic = (ListMusic) getActivity();

        folderList = listMusic.folderList;
        listMusic.lastFolder = null;
        FoldersAdapter folderAdt = new FoldersAdapter(getActivity(), folderList, null);
        folderView.setAdapter(folderAdt);


        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        DisplayMetrics displayMetrics = getActivity().getResources().getDisplayMetrics();
                        float deviceHeight = displayMetrics.heightPixels;
                        if (e1.getY() - e2.getY() > 0) {

                            lastFolder = listMusic.lastFolder;
                            listMusic.lastFolder = lastFolder.getParentFolder();

                            if (lastFolder.getParentFolder() == null) {

                                ObjectAnimator animation = ObjectAnimator.ofFloat(folderFocus,
                                        "translationY", 0, -70);
                                animation.setDuration(300);
                                animation.setStartDelay(75);
                                animation.start();

                                animation = ObjectAnimator.ofFloat(view.findViewById(R.id.folder_list),
                                        "translationY", deviceHeight, 0);
                                animation.setDuration(225);
                                animation.setStartDelay(75);
                                animation.start();

                            } else {
                                FoldersAdapter foldersAdapter = new FoldersAdapter(view.getContext(), listMusic.lastFolder.getFolderList(), listMusic.lastFolder.getSongList());
                                ((ListView)view.findViewById(R.id.f_folder_list)).setAdapter(foldersAdapter);

                                ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.folderFocus),
                                        "translationY", deviceHeight, 0);
                                animation.setDuration(300);
                                animation.setStartDelay(75);
                                animation.start();
                            }
                        }

                        return false;
                    }
                });



        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gesture.onTouchEvent(event);
            }
        });

        return view;
    }

}