package com.gaofeng.okplayer.Controler;

import android.util.Log;
import android.widget.TableLayout;

import com.gaofeng.okplayer.common.PlayInterface;
import com.gaofeng.okplayer.widget.media.AndroidMediaController;
import com.gaofeng.okplayer.widget.media.IjkVideoView;

/**
 * Created by ligaofeng on 2017/12/24.
 */

public class VideoPlayerControler implements PlayInterface{
    private static final String TAG = VideoPlayerControler.class.getSimpleName();
    private static VideoPlayerControler instance = new VideoPlayerControler();
    private IjkVideoView mVideoView;
    private AndroidMediaController mMediaController;
    private TableLayout mHudView;
    private String mCurrentSource;

    private VideoPlayerControler(){

    }

    public static VideoPlayerControler getInstance(){
        return instance;
    }


    @Override
    public void play(String path) throws InterruptedException{
        if(mVideoView == null || mMediaController == null){
            new InterruptedException("view not initalize!");
            Log.i(TAG, "controller must be initlized");
        }
        Log.i(TAG, "start play");
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);
        // prefer mVideoPath
        if (path != null)
            mVideoView.setVideoPath(path);
        else {
            Log.e(TAG, "Null Data Source\n");
            return;
        }
        mCurrentSource = path;
        start();
    }

    @Override
    public void start() {
        if(mVideoView != null){
            mVideoView.start();
        }
    }

    @Override
    public void pause() {
        if(mVideoView != null){
            mVideoView.pause();
        }
    }

    @Override
    public void stop() {
        if(mVideoView != null){
            mVideoView.stopPlayback();
        }
    }

    @Override
    public void release() {
        if(mVideoView != null){
            mVideoView.release(false);
        }
    }

    @Override
    public boolean isPlaying() {
        if(mVideoView != null){
            return mVideoView.isPlaying();
        }
        return false;
    }

    @Override
    public void setVideoView(IjkVideoView videoView, AndroidMediaController mediaController, TableLayout tableLayout) {
        mVideoView = videoView;
        mMediaController = mediaController;
        mHudView = tableLayout;
    }

    @Override
    public String getCurrentSource() {
        return mCurrentSource;
    }
}
