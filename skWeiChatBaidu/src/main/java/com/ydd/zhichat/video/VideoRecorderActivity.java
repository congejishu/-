package com.ydd.zhichat.video;

import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.widget.TextView;

import com.ydd.zhichat.R;
import com.ydd.zhichat.helper.CutoutHelper;
import com.ydd.zhichat.ui.base.BaseActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 聊天界面视频录制类
 * create by TAG
 * update time 2018-11-21 19:43:13
 */

public class VideoRecorderActivity extends BaseActivity {

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private List<String> listTitles = new ArrayList<>();

    private AlbumOrientationEventListener mAlbumOrientationEventListener;
    // 初始状态不旋转，
    private int mOrientation = 0;
    //2. 保存MyOnTouchListener接口的列表
    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>();

    public static void start(Context ctx, boolean videoOnly) {
        Intent intent = new Intent(ctx, VideoRecorderActivity.class);
        intent.putExtra("videoOnly", videoOnly);
        ctx.startActivity(intent);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        CutoutHelper.setWindowOut(getWindow());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_record);
        initView();
    }

    private void initView() {
        ViewPager vp_video = findViewById(R.id.vp_video);
        PictrueFragment pictrueFragment = new PictrueFragment();
        ViedioFragment viedioFragment = new ViedioFragment();
        boolean videoOnly = getIntent().getBooleanExtra("videoOnly", false);
        if (!videoOnly) {
            fragments.add(pictrueFragment);
            listTitles.add(getString(R.string.c_take_picture));
        }
        fragments.add(viedioFragment);
        listTitles.add("录像|视频");
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager(), fragments, listTitles);
        vp_video.setAdapter(adapter);
/*
        vp_video.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
            }

            @Override
            public void onPageSelected(int i) {
                if (i == 0) {
                    pictrueFragment.setClick(true);
                } else {
                    viedioFragment.setClick(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
*/

        if (!videoOnly) {
            CustomTabLayout mTabLayout = (CustomTabLayout) findViewById(R.id.tabLayout);
            mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.c_take_picture)), true);
            mTabLayout.addTab(mTabLayout.newTab().setText(getString(R.string.video)));
            mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    TextView textView = (TextView) LayoutInflater.from(mContext).inflate(R.layout.tab_custom, null);
                    textView.setText(tab.getText());
                    tab.setCustomView(textView);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.setCustomView(null);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });

            // 将TabLayout和ViewPager关联起来。
            mTabLayout.setupWithViewPager(vp_video);

        }
        CutoutHelper.initCutoutHolderTop(getWindow(), findViewById(R.id.vCutoutHolder));
        mAlbumOrientationEventListener = new AlbumOrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL);
        if (mAlbumOrientationEventListener.canDetectOrientation()) {
            mAlbumOrientationEventListener.enable();
        } else {
            Log.e("zx", "不能获取Orientation");
        }
    }

    //3.分发触摸事件给所有注册了MyOnTouchListener的接口
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            listener.onTouch(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    //4.提供给Fragment通过getActivity()方法来注册自己的触摸事件的方法
    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    //5.提供给Fragment通过getActivity()方法来注销自己的触摸事件的方法
    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }

    //1.触摸事件接口
    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }

    public class AlbumOrientationEventListener extends OrientationEventListener {
        public AlbumOrientationEventListener(Context context) {
            super(context);
        }

        public AlbumOrientationEventListener(Context context, int rate) {
            super(context, rate);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                return;
            }

            //保证只返回四个方向
            int newOrientation = ((orientation + 45) / 90 * 90) % 360;

            if (newOrientation != mOrientation) {
                mOrientation = newOrientation;
                Log.e("zx", "onOrientationChanged: " + mOrientation);
                //返回的mOrientation就是手机方向，为0°、90°、180°和270°中的一个
            }
        }
    }

    class MyPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mfragmentList;
        private List<String> listTitles;

        public MyPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> list) {
            super(fm);
            this.mfragmentList = fragmentList;
            this.listTitles = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mfragmentList.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return listTitles.get(position);
        }

        @Override
        public int getCount() {
            return mfragmentList.size();
        }
    }
}