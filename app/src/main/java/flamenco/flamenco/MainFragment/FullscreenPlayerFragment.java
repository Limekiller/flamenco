package flamenco.flamenco.MainFragment;

import android.support.v4.app.Fragment;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.sip.SipSession;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import flamenco.flamenco.animations;


/**
 * Created by cwgehman on 12/8/18.
 */

public class FullscreenPlayerFragment extends Fragment {

    private ArrayList<Song> playList;
    public ListMusic listMusic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fullscreen_player, container, false);


        listMusic = (ListMusic) getActivity();
        playList = listMusic.playList;
        return view;
    }
}
