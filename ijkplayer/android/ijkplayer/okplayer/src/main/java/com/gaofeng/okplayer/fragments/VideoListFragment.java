/*
 *garrion.li
 */

package com.gaofeng.okplayer.fragments;

import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.gaofeng.okplayer.Controler.VideoPlayerControler;
import com.gaofeng.okplayer.R;
import com.gaofeng.okplayer.Source.OkMediaProvider;
import com.gaofeng.okplayer.Utils;
import com.gaofeng.okplayer.eventbus.FileExplorerEvents;
import com.yydcdut.sdlv.Menu;
import com.yydcdut.sdlv.MenuItem;
import com.yydcdut.sdlv.SlideAndDragListView;

import java.util.List;

import yalantis.com.sidemenu.interfaces.ScreenShotable;

public class VideoListFragment extends Fragment implements AdapterView.OnItemLongClickListener,
        AdapterView.OnItemClickListener, AbsListView.OnScrollListener,
        SlideAndDragListView.OnDragDropListener, SlideAndDragListView.OnSlideListener,
        SlideAndDragListView.OnMenuItemClickListener, SlideAndDragListView.OnItemDeleteListener,
        SlideAndDragListView.OnItemScrollBackListener,LoaderManager.LoaderCallbacks<Cursor>,ScreenShotable {
    private static final String TAG = VideoListFragment.class.getSimpleName();
    private View containerView;
    private Bitmap bitmap;

    private Menu mMenu;
//    private List<ApplicationInfo> mAppList;
    List<OkMediaProvider.Media> mediaList;
    private SlideAndDragListView mListView;
    private Toast mToast;
    private ApplicationInfo mDraggedEntity;

    private OkMediaProvider mMediaProvider;
    private Handler mHandler;

    public static VideoListFragment newInstance(){
        VideoListFragment f = new VideoListFragment();
        return f;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.file_video_layout);
    }

    ViewGroup viewGroup;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_video_list, container, false);
        mMediaProvider = OkMediaProvider.getInstance(getActivity());
        initData();
        initMenu();
        initUiAndListener();
        mToast = Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT);

        return viewGroup;
    }

    public void initData() {
         mediaList = mMediaProvider.getMedia();

//        mAppList = getActivity().getPackageManager().getInstalledApplications(0);
    }

    public void initMenu() {
        mMenu = new Menu(true);
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) * 2)
                .setBackground(Utils.getDrawable(getActivity(), R.drawable.btn_left0))
                .setText("One")
                .setTextColor(Color.GRAY)
                .setTextSize(14)
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width))
                .setBackground(Utils.getDrawable(getActivity(), R.drawable.btn_left1))
                .setText("Two")
                .setTextColor(Color.BLACK)
                .setTextSize((14))
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width) + 30)
                .setBackground(Utils.getDrawable(getActivity(), R.drawable.btn_right0))
                .setText("Three")
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setTextColor(Color.BLACK)
                .setTextSize(14)
                .build());
        mMenu.addItem(new MenuItem.Builder().setWidth((int) getResources().getDimension(R.dimen.slv_item_bg_btn_width_img))
                .setBackground(Utils.getDrawable(getActivity(), R.drawable.btn_right1))
                .setDirection(MenuItem.DIRECTION_RIGHT)
                .setIcon(getResources().getDrawable(R.mipmap.ic_launcher))
                .build());
    }

    public void initUiAndListener() {
        mListView = (SlideAndDragListView) viewGroup.findViewById(R.id.lv_edit);
        mListView.setMenu(mMenu);
        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(this);
        mListView.setOnDragDropListener(this);
        mListView.setOnItemClickListener(this);
        mListView.setOnSlideListener(this);
        mListView.setOnMenuItemClickListener(this);
        mListView.setOnItemDeleteListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setOnItemScrollBackListener(this);
    }

    private BaseAdapter mAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return mediaList.size();
        }

        @Override
        public Object getItem(int position) {
            return mediaList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return mediaList.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final CustomViewHolder cvh;
            if (convertView == null) {
                cvh = new CustomViewHolder();
                convertView = LayoutInflater.from(getActivity()).inflate(R.layout.item_custom_btn, null);
                cvh.imgLogo = (ImageView) convertView.findViewById(R.id.img_item_edit);
                cvh.txtName = (TextView) convertView.findViewById(R.id.txt_item_name);
                cvh.txtSize = (TextView) convertView.findViewById(R.id.txt_item_size);
                cvh.txtResolution = (TextView) convertView.findViewById(R.id.txt_item_resolution);
                cvh.btnClick = (CheckBox) convertView.findViewById(R.id.btn_item_click);
                cvh.btnClick.setOnCheckedChangeListener(mCheckChange);
                convertView.setTag(cvh);
            } else {
                cvh = (CustomViewHolder) convertView.getTag();
            }
            OkMediaProvider.Media item = (OkMediaProvider.Media ) this.getItem(position);

            int size = Integer.valueOf(item.get(MediaStore.Video.Media.SIZE));
            cvh.txtSize.setText(getResources().getString(R.string.txt_size) + size/(1024*1024) + "M");
            cvh.txtResolution.setText(getResources().getString(R.string.txt_resolution)
                    + item.get(MediaStore.Video.Media.RESOLUTION));
            cvh.txtName.setText(item.get(MediaStore.Video.Media.DISPLAY_NAME));

            Glide.with(getActivity()).
                    load(item.get(MediaStore.Video.Media.DATA)).
                    placeholder(R.mipmap.ic_ok).//加载中显示的图片
                    error(R.mipmap.ic_ok).//加载失败时显示的图片
                    crossFade(500).//淡入淡出,注意:如果设置了这个,则必须要去掉asBitmap
                    override(80, 80).//设置最终显示的图片像素为80*80,注意:这个是像素,而不是控件的宽高
                    centerCrop().//中心裁剪,缩放填充至整个ImageView
                    skipMemoryCache(true).//跳过内存缓存
                    diskCacheStrategy(DiskCacheStrategy.RESULT).//保存最终图片
                    thumbnail(0.1f).//10%的原图大小
                    into(cvh.imgLogo);

            cvh.btnClick.setChecked(false);
            cvh.btnClick.setTag(position);
            return convertView;
        }

        class CustomViewHolder {
            public ImageView imgLogo;
            public TextView txtName;
            public TextView txtSize;
            public TextView txtResolution;
            public CheckBox btnClick;
        }

        private CompoundButton.OnCheckedChangeListener mCheckChange = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Object o = compoundButton.getTag();
                String newPath = mediaList.get((Integer) o).get(MediaStore.Video.Media.DATA);
                String currentPlay = VideoPlayerControler.getInstance().getCurrentSource();
                if(currentPlay == null || !currentPlay.equals(newPath)){
                    if (TextUtils.isEmpty(newPath))
                        return;
                    FileExplorerEvents.getBus().post(new FileExplorerEvents.OnClickFile(newPath));
                    mAdapter.notifyDataSetChanged();
                    compoundButton.setChecked(true);
                    return;
                }

                if(b){//checked
                    VideoPlayerControler.getInstance().start();
                }else{
                    VideoPlayerControler.getInstance().pause();
                }
            }
        };
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void takeScreenShot() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Bitmap bitmap = Bitmap.createBitmap(containerView.getWidth(),
                        containerView.getHeight(), Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                containerView.draw(canvas);
                VideoListFragment.this.bitmap = bitmap;
            }
        };

        thread.start();

    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void onDragViewStart(int beginPosition) {
        //mDraggedEntity = mAppList.get(beginPosition);
        toast("onDragViewStart   beginPosition--->" + beginPosition);
    }

    @Override
    public void onDragDropViewMoved(int fromPosition, int toPosition) {
        //ApplicationInfo applicationInfo = mAppList.remove(fromPosition);
        //mAppList.add(toPosition, applicationInfo);
        toast("onDragDropViewMoved   fromPosition--->" + fromPosition + "  toPosition-->" + toPosition);
    }

    @Override
    public void onDragViewDown(int finalPosition) {
        //mAppList.set(finalPosition, mDraggedEntity);
        toast("onDragViewDown   finalPosition--->" + finalPosition);
    }

    @Override
    public void onSlideOpen(View view, View parentView, int position, int direction) {
        toast("onSlideOpen   position--->" + position + "  direction--->" + direction);
    }

    @Override
    public void onSlideClose(View view, View parentView, int position, int direction) {
        toast("onSlideClose   position--->" + position + "  direction--->" + direction);
    }

    @Override
    public int onMenuItemClick(View v, int itemPosition, int buttonPosition, int direction) {
        toast("onMenuItemClick   itemPosition--->" + itemPosition + "  buttonPosition-->" + buttonPosition + "  direction-->" + direction);
        switch (direction) {
            case MenuItem.DIRECTION_LEFT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_NOTHING;
                    case 1:
                        return Menu.ITEM_SCROLL_BACK;
                }
                break;
            case MenuItem.DIRECTION_RIGHT:
                switch (buttonPosition) {
                    case 0:
                        return Menu.ITEM_SCROLL_BACK;
                    case 1:
                        return Menu.ITEM_DELETE_FROM_BOTTOM_TO_TOP;
                }
        }
        return Menu.ITEM_NOTHING;
    }

    @Override
    public void onItemDeleteAnimationFinished(View view, int position) {
        mediaList.remove(position - mListView.getHeaderViewsCount());
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case AbsListView.OnScrollListener.SCROLL_STATE_FLING:
                break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        toast("onItemLongClick   position--->" + position);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        toast("onScrollBackAnimationFinished   position--->" + position);
    }

    @Override
    public void onScrollBackAnimationFinished(View view, int position) {
        Log.d("yuyidong", "onScrollBackAnimationFinished");
    }



    private void toast(String toast) {
        mToast.setText(toast);
        mToast.show();
    }

}
