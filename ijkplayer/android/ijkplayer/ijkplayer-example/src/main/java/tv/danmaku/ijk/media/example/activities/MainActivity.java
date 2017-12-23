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

package tv.danmaku.ijk.media.example.activities;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import tv.danmaku.ijk.media.example.fragments.RecentMediaListFragment;
import tv.danmaku.ijk.media.example.fragments.SettingsFragment;
import yalantis.com.sidemenu.interfaces.Resourceble;
import yalantis.com.sidemenu.interfaces.ScreenShotable;
import yalantis.com.sidemenu.model.SlideMenuItem;
import yalantis.com.sidemenu.util.ViewAnimator;

import com.squareup.otto.Subscribe;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.application.AppActivity;
import tv.danmaku.ijk.media.example.application.Settings;
import tv.danmaku.ijk.media.example.eventbus.FileExplorerEvents;
import tv.danmaku.ijk.media.example.fragments.FileListFragment;
import tv.danmaku.ijk.media.example.fragments.PlayFragment;


public class MainActivity extends AppActivity implements ViewAnimator.ViewAnimatorListener {
    private Settings mSettings;
    private static boolean mIsFullScreen = false;
    private static boolean mIsPlay = false;

    private static String ROOT_PATH = "/sdcard/";

    public static final String CLOSE = "Close";
    public static final String BUILDING = "Building";
    public static final String BOOK = "Book";
    public static final String PAINT = "Paint";
    public static final String CASE = "Case";
    public static final String SHOP = "Shop";
    public static final String PARTY = "Party";
    public static final String MOVIE = "Movie";

    private List<SlideMenuItem> list = new ArrayList<>();

    DrawerLayout mDrawerLayout;
    LinearLayout mLeftLayout;
    private FileListFragment mFileListFragment;
    private ViewAnimator viewAnimator;
    private ActionBarDrawerToggle drawerToggle;

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
            case BUILDING:

                SettingsFragment settingsFragment = SettingsFragment.newInstance();
                ft.replace(R.id.body,settingsFragment).commit();
                return settingsFragment;
            case BOOK:
                RecentMediaListFragment recentMediaFragment = RecentMediaListFragment.newInstance();
                ft.replace(R.id.body,recentMediaFragment).commit();
                return recentMediaFragment;
            case PAINT:
            default:
                ft.replace(R.id.body,mFileListFragment).commit();
                return mFileListFragment;
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
        viewAnimator = new ViewAnimator<>(this, list, mFileListFragment, mDrawerLayout, this);
    }

    private void createMenuList() {
        SlideMenuItem menuItem0 = new SlideMenuItem(this.CLOSE, R.drawable.icn_close);
        list.add(menuItem0);
        SlideMenuItem menuItem = new SlideMenuItem(this.BUILDING, R.drawable.settings);
        list.add(menuItem);
        SlideMenuItem menuItem2 = new SlideMenuItem(this.BOOK, R.drawable.history);
        list.add(menuItem2);
        SlideMenuItem menuItem3 = new SlideMenuItem(this.PAINT, R.drawable.list);
        list.add(menuItem3);

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
        doOpenDirectory(ROOT_PATH, false);
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

            PlayFragment playFragment = PlayFragment.newInstance(f.getPath(), null);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.mini_view, playFragment);
            transaction.commit();
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

}
