/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gaofeng.okplayer.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gaofeng.okplayer.R;
import com.gaofeng.okplayer.application.AppActivity;
import com.gaofeng.okplayer.application.Settings;
import com.gaofeng.okplayer.eventbus.FileExplorerEvents;
import com.gaofeng.okplayer.fragments.FileListFragment;
import com.gaofeng.okplayer.fragments.PlayFragment;
import com.gaofeng.okplayer.fragments.RecentMediaListFragment;
import com.gaofeng.okplayer.fragments.SettingsFragment;
import com.gaofeng.okplayer.fragments.VideoListFragment;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

import static com.gaofeng.okplayer.fragments.PlayFragment.mVideoPlayerControler;


public class MainActivity extends AppActivity implements ViewAnimator.ViewAnimatorListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Settings mSettings;
    private static boolean mIsFullScreen = false;
    private static boolean mIsPlay = false;

    private static String ROOT_PATH = "/sdcard/";

    public static final String CLOSE = "Close";
    public static final String SETTINGS = "Settings";
    public static final String HISTORY = "Hostory";
    public static final String MOVIE = "Movie";
    public static final String LIST = "List";
    public static final String CASE = "Case";
    public static final String SHOP = "Shop";
    public static final String PARTY = "Party";


    private List<SlideMenuItem> list = new ArrayList<>();

    DrawerLayout mDrawerLayout;
    LinearLayout mLeftLayout;
    private FileListFragment mFileListFragment;
    private VideoListFragment mVideoListFragment;
    private SettingsFragment mSettingsFragment;
    private ViewAnimator viewAnimator;
    private ActionBarDrawerToggle drawerToggle;

    PlayFragment mPlayFragment;

    @Override
    public void disableHomeButton() {

    }

    @Override
    public void enableHomeButton() {

    }

    @Override
    public ScreenShotable onSwitch(Resourceble slideMenuItem, ScreenShotable screenShotable, int position) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (slideMenuItem.getName()) {
            case CLOSE:
                return screenShotable;
            case SETTINGS:
                if(mSettingsFragment == null){
                    mSettingsFragment = SettingsFragment.newInstance();
                }
                ft.replace(R.id.body,mSettingsFragment).commit();
                return mSettingsFragment;
            case HISTORY:
                RecentMediaListFragment recentMediaFragment = RecentMediaListFragment.newInstance();
                ft.replace(R.id.body,recentMediaFragment).commit();
                return recentMediaFragment;
            case MOVIE:
                if(mVideoListFragment == null){
                    mVideoListFragment = VideoListFragment.newInstance();
                }
                ft.replace(R.id.body, mVideoListFragment).commit();
                return mVideoListFragment;
            case LIST:
                if(mFileListFragment == null){
                    mFileListFragment = FileListFragment.newInstance(ROOT_PATH);
                }
                ft.replace(R.id.body,mFileListFragment).commit();
                return mFileListFragment;
            default:
                if(mVideoListFragment == null){
                    mVideoListFragment = VideoListFragment.newInstance();
                }
                ft.replace(R.id.body, mVideoListFragment).commit();
                return mVideoListFragment;
        }
    }


    @Override
    public void addViewToContainer(View view) {
        mLeftLayout.addView(view);
    }

    private ScreenShotable replaceFragment(ScreenShotable screenShotable, int topPosition) {
        /*View view = findViewById(R.id.body);
        int finalRadius = Math.max(view.getWidth(), view.getHeight());
        SupportAnimator animator = ViewAnimationUtils.createCircularReveal(view, 0, topPosition, 0, finalRadius);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(ViewAnimator.CIRCULAR_REVEAL_ANIMATION_DURATION);
        findViewById(R.id.content_overlay).setBackgroundDrawable(new BitmapDrawable(getResources(),
                screenShotable.getBitmap()));
        animator.start();*/

        return screenShotable;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_main);
        mLeftLayout = (LinearLayout) findViewById(R.id.left_drawer);

        startRun();
        setActionBar();
        createMenuList();
        viewAnimator = new ViewAnimator<>(this, list, mVideoListFragment, mDrawerLayout, this);
    }

    private void createMenuList() {
        SlideMenuItem closeItem = new SlideMenuItem(this.CLOSE, R.drawable.icn_close);
        list.add(closeItem);
        SlideMenuItem menuItem = new SlideMenuItem(this.SETTINGS, R.drawable.settings);
        list.add(menuItem);
//        SlideMenuItem menuItem2 = new SlideMenuItem(this.HISTORY, R.drawable.history);
//        list.add(menuItem2);
        SlideMenuItem filesItem = new SlideMenuItem(this.LIST, R.drawable.list);
        list.add(filesItem);
        SlideMenuItem movieItem = new SlideMenuItem(this.MOVIE, R.drawable.icn_4);
        list.add(movieItem);

        /*SlideMenuItem menuItem4 = new SlideMenuItem(this.CASE, R.drawable.icn_4);
        list.add(menuItem4);
        SlideMenuItem menuItem5 = new SlideMenuItem(this.SHOP, R.drawable.icn_5);
        list.add(menuItem5);
        SlideMenuItem menuItem6 = new SlideMenuItem(this.PARTY, R.drawable.icn_6);
        list.add(menuItem6);
        SlideMenuItem menuItem7 = new SlideMenuItem(this.MOVIE, R.drawable.icn_7);
        list.add(menuItem7);*/
    }

    private void setActionBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                mLeftLayout.removeAllViews();
                mLeftLayout.invalidate();
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (slideOffset > 0.6 && mLeftLayout.getChildCount() == 0)
                    viewAnimator.showMenuContent();
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        mDrawerLayout.setDrawerListener(drawerToggle);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onResume() {
        super.onResume();

        FileExplorerEvents.getBus().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        FileExplorerEvents.getBus().unregister(this);
    }

    private void startRun() {
        if (mSettings == null) {
            mSettings = new Settings(this);
        }

        /*String lastDirectory = mSettings.getLastDirectory();
        if (!TextUtils.isEmpty(lastDirectory) && new File(lastDirectory).isDirectory())
            doOpenDirectory(lastDirectory, false);
        else*/
        //doOpenDirectory(ROOT_PATH, false);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(mVideoListFragment == null){
            mVideoListFragment = VideoListFragment.newInstance();
        }
        ft.replace(R.id.body, mVideoListFragment).commit();
    }

    private void doOpenDirectory(String path, boolean addToBackStack) {
        mFileListFragment = FileListFragment.newInstance(path);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.body, mFileListFragment);
        if (addToBackStack)
            transaction.addToBackStack(null);
        transaction.commit();
    }

    @Subscribe
    public void onClickFile(FileExplorerEvents.OnClickFile event) {
        File f = event.mFile;
        try {
            f = f.getAbsoluteFile();
            f = f.getCanonicalFile();
            if (TextUtils.isEmpty(f.toString()))
                f = new File("/");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (f.isDirectory()) {
            String path = f.toString();
            mSettings.setLastDirectory(path);
            doOpenDirectory(path, true);
        } else if (f.exists()) {
            if(mPlayFragment == null){
                mPlayFragment = PlayFragment.newInstance(f.getPath(), null);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.mini_view, mPlayFragment);
                transaction.commit();
            }else{
                mPlayFragment.play(f.getPath());
            }

            mIsPlay = true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Toast.makeText(this, "竖屏", Toast.LENGTH_SHORT).show();

            onOrientationPortrait();

        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            Toast.makeText(this, "横屏", Toast.LENGTH_SHORT).show();
            if (!mIsPlay) onOrientationPortrait();//没有播放时保持竖屏
            else
                onOrientationLandscape();

        }

    }

    /**
     * full screen
     */
    private void onOrientationLandscape() {

        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        getSupportActionBar().hide();
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);

        findViewById(R.id.body).setVisibility(View.GONE);
    }

    /**
     * no full screen
     */
    private void onOrientationPortrait() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        getSupportActionBar().show();
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN;
        //获得当前窗体对象
        Window window = this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        getWindow().getDecorView().setSystemUiVisibility(View.INVISIBLE);
        findViewById(R.id.body).setVisibility(View.VISIBLE);
    }

    public void onFullScreenClick(View view) {
        if (!mIsFullScreen) {
            onOrientationLandscape();
            mIsFullScreen = true;
        } else {
            onOrientationPortrait();
            mIsFullScreen = false;
        }

    }

    long firstBackTime = 0;
    @Override
    public void onBackPressed() {
        if(firstBackTime == 0){
            firstBackTime = System.currentTimeMillis();
            Toast.makeText(this, getResources().getString(R.string.txt_back), Toast.LENGTH_SHORT).show();
            return;
        }
        long time = System.currentTimeMillis() - firstBackTime;
        if(time < 3000){
            firstBackTime = 0;
            finish();
        }else{
            firstBackTime = System.currentTimeMillis();
            Toast.makeText(this, getResources().getString(R.string.txt_back), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        if(mVideoPlayerControler != null){
            mVideoPlayerControler.stop();
            mVideoPlayerControler.release();
        }
        super.onDestroy();
    }
}
