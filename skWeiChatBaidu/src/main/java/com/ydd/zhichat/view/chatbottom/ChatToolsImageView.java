package com.ydd.zhichat.view.chatbottom;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.ui.other.BasicInfoActivity;
import com.ydd.zhichat.view.ChatBottomView;
import com.ydd.zhichat.view.photopicker.Folder;
import com.ydd.zhichat.view.photopicker.FolderAdapter;
import com.ydd.zhichat.view.photopicker.Image;
import com.ydd.zhichat.view.photopicker.ImageCaptureManager;
import com.ydd.zhichat.view.photopicker.ImageConfig;
import com.ydd.zhichat.view.photopicker.ImageGridAdapter;
import com.ydd.zhichat.view.photopicker.PhotoPreviewActivity;
import com.ydd.zhichat.view.photopicker.intent.PhotoPreviewIntent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ezy.ui.view.BadgeButton;

public class ChatToolsImageView extends RelativeLayout {
    private Context mCxt;
    private ViewPager mViewPager;
    private RadioGroup mFaceRadioGroup;// 切换不同组表情的RadioGroup
    private ChatBottomView.ChatBottomListener listener;
    private ChatBottomView chatBottomView;
    private boolean mHasGif;
    // 最大照片数量
    private ImageCaptureManager captureManager;
    private int mDesireImageCount;
    private ImageConfig imageConfig; // 照片配置
    // 不同loader定义
    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;
    public static final String EXTRA_RESULT = "select_result";
    private boolean hasFolderGened = false;
    private boolean mIsShowCamera = false;
    public static final String EXTRA_RESULT_ORIGINAL = "select_result_Original";
    // 结果数据
    private ArrayList<String> resultList = new ArrayList<>();
    // 真正的如果数据，用于返回，
    private ArrayList<String> realResultList = new ArrayList<>();
    private ImageAdapter mImageAdapter;
    // 是否为原图
    private boolean isOriginal;
    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<>();
    private FolderAdapter mFolderAdapter;
    private RecyclerView mGridView;
    private ArrayList<Image> mSelectedImages = new ArrayList<>();
    private LinearLayoutManager layoutManager;

    public ChatToolsImageView(Context context) {
        super(context);
        init(context);
    }

    public ChatToolsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init(context);
    }

    public ChatToolsImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        init(context);
    }

    private static int dip_To_px(Context context, int dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }


    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ChatFaceView);// TypedArray是一个数组容器
        mHasGif = a.getBoolean(R.styleable.ChatFaceView_hasGif, true);
        a.recycle();
    }

    public void init(ChatBottomView.ChatBottomListener listener, ChatBottomView activity) {
        chatBottomView = activity;
        setBottomListener(listener);
        // 首次加载所有图片
        //getSupportLoaderManager().initLoader(LOADER_ALL, null, mLoaderCallback);
        listener.loadImage();
        mGridView.postDelayed(() -> {
            List<Image> images = listener.getImage();
            Log.d("图片加载", "init: 加载图片数量:" + images.size());
            mImageAdapter.setData(images);
            mImageAdapter.notifyDataSetChanged();
            //layoutManager.scrollToPositionWithOffset(images.size(),0);
            mGridView.scrollToPosition(images.size());
        }, 1000);
    }

    public static void MoveToPosition(LinearLayoutManager manager, int n) {
        manager.scrollToPositionWithOffset(n, 0);
        //manager.setStackFromEnd(true);
    }

    public void setBottomListener(ChatBottomView.ChatBottomListener listener) {
        this.listener = listener;
    }

    private void init(Context context) {
        mCxt = context;
        LayoutInflater.from(mCxt).inflate(R.layout.chat_image_view, this);
        //加载默认相册
        findViewById(R.id.image_edit).setOnClickListener(v -> {

        });
        findViewById(R.id.image_canvas).setOnClickListener(v -> {
            listener.clickPhoto();
        });

        findViewById(R.id.send_btn).setOnClickListener(v -> {
            CheckBox adc = findViewById(R.id.default_image);
            listener.sendImage(mSelectedImages, adc.isChecked());
            mSelectedImages.clear();
            mGridView.getAdapter().notifyDataSetChanged();
        });


        mGridView = findViewById(R.id.recyclerView);
        mImageAdapter = new ImageAdapter();
        mGridView.setAdapter(mImageAdapter);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setReverseLayout(true);
        mGridView.setLayoutManager(layoutManager);

        //mGridView.setNumColumns(3);//只显示一行
    }

    /**
     * 获取GridView Item宽度
     *
     * @return
     */
    private int getItemImageWidth() {
        int cols = getNumColnums();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int columnSpace = getResources().getDimensionPixelOffset(R.dimen.space_size);
        return (screenWidth - columnSpace * (cols - 1)) / cols;
    }

    /**
     * 根据屏幕宽度与密度计算GridView显示的列数， 最少为三列
     *
     * @return
     */
    private int getNumColnums() {
        int cols = getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().densityDpi;
        return cols < 1 ? 1 : cols;
    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolde> {

        private boolean showSelectIndicator = true;
        private Map<String, String> changedFileMap = new HashMap<>();
        private int mItemSize;
        List<Image> data = new ArrayList<>();
        private boolean isImage = false;

        public void setData(List<Image> data) {
            mSelectedImages.clear();
            this.data = data;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ImageAdapter.ViewHolde onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_select_image_new, viewGroup, false);
            //(R.layout.item_image_layout, getContext(), false);
            return new ImageAdapter.ViewHolde(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageAdapter.ViewHolde viewHolder, int i) {
            if (viewHolder != null) {
                viewHolder.bindData(data.get(i));
            }
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class ViewHolde extends RecyclerView.ViewHolder {
            ImageView image;
            TextView indicator;
            View mask;
            TextView checkmark_text;

            public ViewHolde(View view) {
                super(view);
                image = (ImageView) view.findViewById(R.id.image);
                indicator = view.findViewById(R.id.checkmark);
                mask = view.findViewById(R.id.mask);
                checkmark_text = view.findViewById(R.id.checkmark_text);
                view.setTag(this);
            }

            void bindData(final Image data) {
                if (data == null)
                    return;
                // 处理单选和多选状态
                if (showSelectIndicator) {
                    indicator.setVisibility(View.VISIBLE);
                    indicator.setOnClickListener(v -> {
                        if (mSelectedImages.contains(data)) {
                            mSelectedImages.remove(data);
                        } else {
                            mSelectedImages.add(data);
                        }
                        notifyDataSetChanged();
                    });

                    int index = 0;
                    for (int i = 0; i < mSelectedImages.size(); i++) {
                        Image dataa = mSelectedImages.get(i);
                        if (dataa.equals(data)) {
                            index = i + 1;
                        }
                    }

                    chatBottomView.showIndex(mSelectedImages.size());

                    image.setOnClickListener(v -> {
                        PhotoPreviewIntent intent = new PhotoPreviewIntent(mCxt);
                        intent.setCurrentItem(0);
                        if (mSelectedImages.size() <= 0) {
                            intent.setPhotoImage(data);
                        } else {
                            intent.setPhotoImages(mSelectedImages);
                        }

                        MyApplication.getInstance().startActivity(intent);//.startActivityForResult(intent, PhotoPreviewActivity.REQUEST_PREVIEW);
                    });

                    if (mSelectedImages.contains(data)) {
                        // 设置选中状态
                        indicator.setBackground(getResources().getDrawable(R.drawable.sel_check));
                        //.setImageResource(R.drawable.sel_check_wx2);
                        indicator.setText(index + "");
                        //mask.setVisibility(View.VISIBLE);
                        mask.setBackgroundColor(Color.parseColor("#88000000"));
                    } else {
                        // 未选择
                        indicator.setBackground(getResources().getDrawable(R.drawable.sel_nor));//.setImageResource(R.drawable.sel_check_wx2);
                        indicator.setText("");
                        mask.setBackgroundColor(Color.parseColor("#00000000"));
                        //mask.setVisibility(View.GONE);
                    }
                } else {
                    indicator.setVisibility(View.GONE);
                }

                File imageFile;
                if (changedFileMap.containsKey(data.path)) {
                    imageFile = new File(changedFileMap.get(data.path));
                } else {
                    imageFile = new File(data.path);
                }

                checkmark_text.setText(String.format("%s",imageFile.getName()));

                // 显示图片
                Glide.with(mCxt)
                        .load(imageFile)
                        .placeholder(R.mipmap.default_error)
                        .error(R.mipmap.default_error)
                        .centerCrop()
                        .fitCenter()
                        .into(image);

            }
        }
    }


}
