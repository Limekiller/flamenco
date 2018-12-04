package flamenco.flamenco.MainFragment;

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

import flamenco.flamenco.Folder;
import flamenco.flamenco.ListMusic;
import flamenco.flamenco.R;
import flamenco.flamenco.Song;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;

public class FoldersAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Folder> folders;
    private ArrayList<Song> songs;
    private LayoutInflater songInf;
    public ListMusic listMusic;

    public FoldersAdapter(Context context, ArrayList<Folder> theFolders, ArrayList<Song> theSongs) {

        this.context = context;
        folders=theFolders;
        songs=theSongs;
        songInf=LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        int songsSize;
        if (songs == null) {
            songsSize = 0;
        } else {
            songsSize = songs.size();
        }
        return folders.size()+songsSize;
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

        // First list all folders
        if (position < folders.size()) {
            //map to song layout
            final LinearLayout folderLay;
            folderLay = (LinearLayout) songInf.inflate(R.layout.folder, parent, false);

            //get title and artist views
            TextView pathView = folderLay.findViewById(R.id.folder_path);
            ImageView artView = folderLay.findViewById(R.id.album_art);
            LayoutInflater inflater = LayoutInflater.from(context);
            //get song using position
            Folder currFolder = folders.get(position);
            //get title and artist strings
            pathView.setText(currFolder.getPath());

//            try {
//                Glide.with(context).load(currFolder.getSongList().get(0).getAlbumArt())
//                        .error(R.drawable.placeholder).crossFade().dontAnimate().centerCrop().into(artView);
//            } catch (IndexOutOfBoundsException e) {
//                Glide.with(context).load(R.drawable.placeholder)
//                        .crossFade().dontAnimate().centerCrop().into(artView);
//            }
            Glide.with(context).load(R.drawable.baseline_folder_black_18dp)
                         .crossFade().dontAnimate().centerCrop().into(artView);

            //set position as tag
            folderLay.setTag(position);
            return folderLay;
        } else {

            LinearLayout songLay = (LinearLayout) songInf.inflate
                    (R.layout.song, parent, false);

            //get title and artist views
            TextView songView = (TextView)songLay.findViewById(R.id.song_title);
            TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
            TextView yearView = songLay.findViewById(R.id.song_year);
            ImageView artView = (ImageView)songLay.findViewById(R.id.song_art);


            Song currSong = songs.get(position-folders.size());
            songView.setText(currSong.getTitle());

            artistView.setText(currSong.getArtist());
            yearView.setText(currSong.getYear());
            Glide.with(context).load(currSong.getAlbumArt()).error(R.drawable.placeholder)
                    .crossFade().centerCrop().into(artView);

            songLay.setTag(position-folders.size());
            return songLay;
        }
    }
}
