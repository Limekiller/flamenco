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
import android.widget.Filter;
import android.widget.Filterable;
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

public class SongAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private ArrayList<Song> fullSongList;
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    private String area;

    public SongAdapter(Context context, ArrayList<Song> theSongs, String Area) {

        this.context = context;
        songs=theSongs;
        fullSongList=theSongs;
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
        TextView songView;
        TextView artistView;
        TextView yearView;
        ImageView artView;
        LinearLayout songLay;

        if (area.equals("playlists")) {
            songLay = (LinearLayout) songInf.inflate
                    (R.layout.playlist, parent, false);

            //get title and artist views
            songView = (TextView)songLay.findViewById(R.id.playlist_title);
            artView = (ImageView)songLay.findViewById(R.id.playlist_art);
            artistView = null;
            yearView = null;
        } else {
            songLay = (LinearLayout) songInf.inflate
                    (R.layout.song, parent, false);

            //get title and artist views
            songView = (TextView)songLay.findViewById(R.id.song_title);
            artistView = (TextView)songLay.findViewById(R.id.song_artist);
            yearView = songLay.findViewById(R.id.song_year);
            artView = (ImageView)songLay.findViewById(R.id.song_art);
        }

        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());

        if (area.equals("song")) {
            artistView.setText(currSong.getArtist());
            yearView.setText(currSong.getYear());
            Glide.with(context).load(currSong.getAlbumArt()).error(R.drawable.placeholder)
                    .crossFade().centerCrop().into(artView);
        } else if (!area.equals("playlists")){
            artistView.setVisibility(View.GONE);
            yearView.setVisibility(View.GONE);
            artView.setVisibility(View.GONE);
        }

        //set position as tag

        songLay.setTag(position);
        return songLay;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // We implement here the filter logic
                if (constraint == null || constraint.length() == 0) {
                    // No filter implemented we return all the list
                    results.values = fullSongList;
                    results.count = fullSongList.size();
                }
                else {
                    // We perform filtering operation
                    List<Song> nSongList = new ArrayList<>();

                    for (Song p : fullSongList) {
                        if (p.getTitle().toUpperCase()
                                .startsWith(constraint.toString().toUpperCase()))
                            nSongList.add(p);
                    }

                    results.values = nSongList;
                    results.count = nSongList.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                // Now we have to inform the adapter about the new list filtered
                if (results.count == 0)
                    notifyDataSetInvalidated();
                else {
                    songs = (ArrayList<Song>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
    }
}
