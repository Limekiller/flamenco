package flamenco.flamenco.ListsFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.sip.SipSession;
import android.provider.SyncStateContract;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import javax.security.auth.callback.Callback;

import flamenco.flamenco.ListMusic;
import flamenco.flamenco.MainFragment.FoldersAdapter;
import flamenco.flamenco.MainFragment.SongAdapter;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class PlaylistsFragment extends Fragment{

    private String mString;
    private ArrayList<Song> playList;
    public ListMusic listMusic;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.playlistsfragment, container, false);
        Button addNew = view.findViewById(R.id.addNew);
        final ListView playListView = view.findViewById(R.id.playlist);

        listMusic = (ListMusic) getActivity();
        playList = listMusic.playList;

        final SongAdapter playAdt = new SongAdapter(getActivity(), playList, "song");
        playListView.setAdapter(playAdt);

        addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        playList.add(new Song(0, mString, null, 0, null, null));
                        playAdt.notifyDataSetChanged();
                    }
                });

                builder.show();
            }
        });

        return  view;
    }

}