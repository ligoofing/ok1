package com.gaofeng.okplayer.common;

import android.widget.TableLayout;

import com.gaofeng.okplayer.widget.media.AndroidMediaController;
import com.gaofeng.okplayer.widget.media.IjkVideoView;

/**
 * Created by ligaofeng on 2017/12/23.
 */

public interface PlayInterface {
    void play(String path) throws InterruptedException;
    void start();
    void pause();
    void stop();
    void release();
    boolean isPlaying();
    void setVideoView(IjkVideoView videoView, AndroidMediaController mediaController, TableLayout tableLayout);
    String getCurrentSource();
}
