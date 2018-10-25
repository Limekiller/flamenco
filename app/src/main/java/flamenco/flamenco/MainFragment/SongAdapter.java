package flamenco.flamenco.MainFragment;

import android.content.ContentUris;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import flamenco.flamenco.R;
import flamenco.flamenco.Song;

public class SongAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private String area;

    public SongAdapter(Context context, ArrayList<Song> theSongs, String Area) {

        this.context = context;
        songs=theSongs;
        songInf=LayoutInflater.from(context);
        area=Area;

    }

    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        LinearLayout songLay = (LinearLayout) songInf.inflate
                (R.layout.song, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        TextView yearView = songLay.findViewById(R.id.song_year);
        ImageView artView = (ImageView)songLay.findViewById(R.id.song_art);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());

        if (area.equals("song")) {
            artistView.setText(currSong.getArtist());
            yearView.setText(currSong.getYear());
            Glide.with(context).load(currSong.getAlbumArt()).error(R.drawable.placeholder)
                    .crossFade().centerCrop().into(artView);
        } else {
            artistView.setVisibility(View.GONE);
            yearView.setVisibility(View.GONE);
            artView.setVisibility(View.GONE);
        }

        //set position as tag
        songLay.setTag(position);
        return songLay;
    }
}
