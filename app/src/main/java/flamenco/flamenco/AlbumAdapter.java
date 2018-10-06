package flamenco.flamenco;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import flamenco.flamenco.ListMusic;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AlbumAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Song> albums;
    private LayoutInflater songInf;
    private String area;
    public ListMusic listMusic;

    public AlbumAdapter(Context context, ArrayList<Song> theAlbums, String Area) {

        area=Area;
        this.context = context;
        albums=theAlbums;
        songInf=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return albums.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //map to song layout
        final LinearLayout albumLay;

        if (area.equals("albums")) {
            albumLay = (LinearLayout) songInf.inflate
                    (R.layout.album, parent, false);
        } else {
            albumLay = (LinearLayout) songInf.inflate
                    (R.layout.artist, parent, false);
        }

        //get title and artist views
        TextView songView = (TextView)albumLay.findViewById(R.id.album_title);
        TextView artistView = (TextView)albumLay.findViewById(R.id.album_artist);
        ImageView artView = (ImageView)albumLay.findViewById(R.id.album_art);
        LayoutInflater inflater = LayoutInflater.from(context);
        //get song using position
        Song currAlbum = albums.get(position);
        //get title and artist strings
        artistView.setText(currAlbum.getArtist());

        if (area.equals("albums")) {
            Glide.with(context).load(currAlbum.getAlbumSongList().get(0).getAlbumArt()).error(R.drawable.placeholder)
                    .crossFade().dontAnimate().centerCrop().into(artView);
            songView.setText(currAlbum.getTitle());

        } else {
            Glide.with(context).load(currAlbum.getAlbumArt()).error(R.drawable.placeholder)
                    .crossFade().dontAnimate().centerCrop().into(artView);
        }
        //set position as tag
        albumLay.setTag(position);
        return albumLay;
    }
}
