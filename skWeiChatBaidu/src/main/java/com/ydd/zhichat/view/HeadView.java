package com.ydd.zhichat.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.makeramen.roundedimageview.RoundedImageView;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.RoomMember;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.ui.base.CoreManager;

import pl.droidsonroids.gif.GifImageView;

/**
 * 单人的头像，
 * 群组是另外的组合头像，
 */
public class HeadView extends RelativeLayout {

    private RoundedImageView ivHead;
    private ImageView ivFrame;
    private GifImageView ivLevel;
    private View layout;

    public HeadView(Context context) {
        super(context);
        init();
    }

    public HeadView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public HeadView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        layout = View.inflate(getContext(), R.layout.view_head, this);
        ivHead = layout.findViewById(R.id.ivHead);
        ivFrame = layout.findViewById(R.id.ivFrame);
        ivLevel = layout.findViewById(R.id.ivLevel);
    }

    public ImageView getHeadImage() {
        return ivHead;
    }

    public ImageView getLevelImage() {
        return ivLevel;
    }

    public void setGroupRole(Integer role) {
        if (role == null) {
            ivFrame.setVisibility(View.GONE);
            return;
        }
        switch (role) {
            case RoomMember.ROLE_OWNER:
                ivFrame.setImageResource(R.mipmap.frame_group_owner);
                ivFrame.setVisibility(View.VISIBLE);
                break;
            case RoomMember.ROLE_MANAGER:
                ivFrame.setImageResource(R.mipmap.frame_group_manager);
                ivFrame.setVisibility(View.VISIBLE);
                break;
            default:
                ivFrame.setVisibility(View.GONE);
        }
    }

    public void setRound(boolean round) {
        ivHead.setOval(round);
    }

    public void setVip(Integer vip) {
        if(!CoreManager.requireConfig(this.getContext()).isVipCenter) {
            ivLevel.setVisibility(GONE);
            return;
        }
        if(vip == 0) {
            ivLevel.setVisibility(GONE);
            return;
        }
        switch (vip) {
            case 1:
                ivLevel.setImageResource(R.mipmap.l16);
                AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(MyApplication.getContext()).vip1ImgUpdate, ivLevel, R.mipmap.l16);
                ivLevel.setVisibility(View.VISIBLE);
                break;
            case 2:
                ivLevel.setImageResource(R.mipmap.dwt_wh_136x136px);
                AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(MyApplication.getContext()).vip2ImgUpdate, ivLevel, R.mipmap.dwt_wh_136x136px);
                ivLevel.setVisibility(View.VISIBLE);
                break;
            case 3:
                ivLevel.setImageResource(R.mipmap.f5o_wh_136x136px);
                AvatarHelper.getInstance().displayUrl(CoreManager.requireConfig(MyApplication.getContext()).vip3ImgUpdate, ivLevel, R.mipmap.f5o_wh_136x136px);
                ivLevel.setVisibility(View.VISIBLE);
                break;
        }




    }

}
