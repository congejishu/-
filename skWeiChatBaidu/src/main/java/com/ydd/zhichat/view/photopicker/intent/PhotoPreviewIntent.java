package com.ydd.zhichat.view.photopicker.intent;

import android.content.Context;
import android.content.Intent;

import com.ydd.zhichat.view.photopicker.Image;
import com.ydd.zhichat.view.photopicker.PhotoPreviewActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * 预览照片
 * Created by foamtrace on 2015/8/25.
 */
public class PhotoPreviewIntent extends Intent {

    public PhotoPreviewIntent(Context packageContext) {
        super(packageContext, PhotoPreviewActivity.class);
    }

    /**
     * 照片地址
     *
     * @param paths
     */
    public void setPhotoPaths(ArrayList<String> paths) {
        this.putStringArrayListExtra(PhotoPreviewActivity.EXTRA_PHOTOS, paths);
    }

    public void setPhotoImages(List<Image> images) {
        ArrayList<String> paths = new ArrayList<>();
        for(Image image:images) {
            paths.add(image.path);
        }
        this.putStringArrayListExtra(PhotoPreviewActivity.EXTRA_PHOTOS, paths);
    }

    public void setPhotoImage(Image images) {
        ArrayList<String> paths = new ArrayList<>();
        paths.add(images.path);
        this.putStringArrayListExtra(PhotoPreviewActivity.EXTRA_PHOTOS, paths);
    }

    /**
     * 当前照片的下标
     *
     * @param currentItem
     */
    public void setCurrentItem(int currentItem) {
        this.putExtra(PhotoPreviewActivity.EXTRA_CURRENT_ITEM, currentItem);
    }
}