package com.gaofeng.okplayer.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.gaofeng.okplayer.R;
import com.gaofeng.okplayer.common.PlayInterface;
import com.gaofeng.okplayer.widget.media.AndroidMediaController;
import com.gaofeng.okplayer.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * A simple {@link Fragment} subclass.
 *
 * Use the {@link PlayFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PlayFragment extends Fragment implements PlayInterface{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "PlayFrament";
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mVideoPath;
    private String mParam2;

    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private TextView mToastTextView;
    private TableLayout mHudView;
    private DrawerLayout mDrawerLayout;
    private ViewGroup mRightDrawer;

    public PlayFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param path Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PlayFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PlayFragment newInstance(String path, String param2) {
        PlayFragment fragment = new PlayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, path);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVideoPath = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_play, container, false);

        initView(view);
        return view;
    }

    private void initView(ViewGroup v){
        mMediaController = new AndroidMediaController(getContext(), false);

        mToastTextView = (TextView) v.findViewById(R.id.toast_text_view);
        mHudView = (TableLayout) v.findViewById(R.id.hud_view);
        mDrawerLayout = (DrawerLayout) v.findViewById(R.id.drawer_layout_video);
        mRightDrawer = (ViewGroup) v.findViewById(R.id.right_drawer);

        mDrawerLayout.setScrimColor(Color.TRANSPARENT);

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = (IjkVideoView) v.findViewById(R.id.video_view);

        play(mVideoPath);
//        mVideoView.setMediaController(mMediaController);
//        mVideoView.setHudView(mHudView);
//        // prefer mVideoPath
//        if (mVideoPath != null)
//            mVideoView.setVideoPath(mVideoPath);
//        else {
//            Log.e(TAG, "Null Data Source\n");
//            return;
//        }
//        mVideoView.start();
    }


    @Override
    public void play(String path) {
        if(mVideoView == null || mMediaController == null)return;

        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (path != null)
            mVideoView.setVideoPath(path);
        else {
            Log.e(TAG, "Null Data Source\n");
            return;
        }
        mVideoView.start();
    }
}
