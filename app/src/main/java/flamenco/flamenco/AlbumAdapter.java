package flamenco.flamenco;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class AlbumAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Album> albums;
    private LayoutInflater songInf;

    public AlbumAdapter(Context context, ArrayList<Album> theAlbums) {

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
        LinearLayout albumLay = (LinearLayout) songInf.inflate
                (R.layout.album, parent, false);
        //get title and artist views
        TextView songView = (TextView)albumLay.findViewById(R.id.album_title);
        TextView artistView = (TextView)albumLay.findViewById(R.id.album_artist);
        ImageView artView = (ImageView)albumLay.findViewById(R.id.album_art);
        //get song using position
        Album currAlbum = albums.get(position);
        //get title and artist strings
        songView.setText(currAlbum.getTitle());
        artistView.setText(currAlbum.getArtist());

        Glide.with(context).load(currAlbum.getAlbumArt()).error(R.drawable.placeholder)
                .crossFade().dontAnimate().centerCrop().into(artView);

        //set position as tag
        albumLay.setTag(position);
        return albumLay;
    }
}
