package flamenco.flamenco.ListsFragment;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Objects;

import flamenco.flamenco.ListMusic;
import flamenco.flamenco.MainFragment.SongAdapter;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class PlaylistsFragment extends Fragment{

    private String mString;
    private ArrayList<Song> playList;
    public ListMusic listMusic;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view =  inflater.inflate(R.layout.playlistsfragment, container, false);
        final ListView playListView = view.findViewById(R.id.playlist_list);

        listMusic = (ListMusic) getActivity();
        assert listMusic != null;
        playList = listMusic.playList;

        final SongAdapter playAdt = new SongAdapter(getActivity(), playList, "playlists");
        playListView.setAdapter(playAdt);
        registerForContextMenu(playListView);
        registerForContextMenu(view.findViewById(R.id.specPlaylist));


        final GestureDetector gesture = new GestureDetector(getActivity(),
                new GestureDetector.SimpleOnGestureListener() {

                    @Override
                    public boolean onDown(MotionEvent e) {
                        view.findViewById(R.id.imageView).animate().scaleY(3f).setDuration(200);
                        return true;
                    }

                    @Override
                    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                           float velocityY) {

                        if (e1.getY() - e2.getY() > 0) {
                            DisplayMetrics displayMetrics = Objects.requireNonNull(getContext()).getResources().getDisplayMetrics();
                            float height = displayMetrics.heightPixels;

                            ObjectAnimator animation = ObjectAnimator.ofFloat(view.findViewById(R.id.playlistFocus),
                                    "translationY", 0, -70);
                            animation.setDuration(225);
                            animation.start();

                            animation = ObjectAnimator.ofFloat(view.findViewById(R.id.playlist_init),
                                    "translationY", height, 0);
                            animation.setDuration(300);
                            animation.start();

                            ((FloatingActionButton)view.getRootView().findViewById(R.id.floatingActionButton2)).hide();
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    ((FloatingActionButton)view.getRootView().findViewById(R.id.addNew)).show();
                                }
                            }, 400);

                            playListView.setAdapter(playAdt);
                            listMusic.savePlaylistList();

                        }

                        return false;
                    }
                });

        view.findViewById(R.id.imageView).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.findViewById(R.id.imageView).animate().scaleY(1f).setDuration(200);
                }
                return gesture.onTouchEvent(event);
            }
        });

        return  view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {

        if (v == v.getRootView().findViewById(R.id.specPlaylist)) {
            getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
        } else {
            getActivity().getMenuInflater().inflate(R.menu.menu_secondary, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int listPos = info.position;
        if(item.getItemId()==R.id.Delete){
            playList.remove(listPos);
            final ListView playListView = this.getView().findViewById(R.id.playlist_list);
            final SongAdapter playAdt = new SongAdapter(getActivity(), playList, "playlists");
            playListView.setAdapter(playAdt);
            listMusic.playList = playList;
            listMusic.savePlaylistList();

        } else if (item.getItemId()==R.id.Remove) {
            playList.get(listMusic.lastPlaylistIndex).getAlbumSongList().remove(listPos);
            final ListView playListView = this.getView().findViewById(R.id.specPlaylist);
            final SongAdapter playAdt = new SongAdapter(getActivity(), playList.get(listMusic.lastPlaylistIndex).getAlbumSongList(), "song");
            playListView.setAdapter(playAdt);

        } else if (item.getItemId()==R.id.Edit) {
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
                    playList.get(listPos).setTitle(mString);
                    final ListView playListView = getActivity().findViewById(R.id.specPlaylist);
                    final SongAdapter playAdt = new SongAdapter(getActivity(), playList.get(listMusic.lastPlaylistIndex).getAlbumSongList(), "song");
                    playListView.setAdapter(playAdt);

                }
            });

            builder.show();

        } else {
            return false;
        }

        listMusic.playList = playList;
        listMusic.savePlaylistList();
        return true;
    }

}