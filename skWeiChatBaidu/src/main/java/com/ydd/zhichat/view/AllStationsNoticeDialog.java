package com.ydd.zhichat.view;

import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.AllStationsNotice;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.ui.tool.SingleImagePreviewActivity;
import com.ydd.zhichat.util.Base64;
import com.ydd.zhichat.util.ScreenUtil;
import com.ydd.zhichat.video.PlayVideoActivity;

public class AllStationsNoticeDialog extends Dialog {
    private Context context;
    private AllStationsNotice allStationsNotice;

    public AllStationsNoticeDialog(Context context, AllStationsNotice allStationsNotice) {
        super(context, R.style.BottomDialog);
        this.context = context;
        this.allStationsNotice = allStationsNotice;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_stations_notice_dialog);
        setCanceledOnTouchOutside(true);
        initView();
    }

    private void initView() {
        WebView tvNotice = findViewById(R.id.tvNotice);
        //tvNotice.setMovementMethod(ScrollingMovementMethod.getInstance());

        ImageView ivNotice = findViewById(R.id.ivNotice);
        VideoView videoView = findViewById(R.id.ivNoticeVideo);
        ViewGroup.LayoutParams params = ivNotice.getLayoutParams();
        params.width = (int) (ScreenUtil.getScreenWidth(getContext()) * 0.8) - ScreenUtil.dip2px(context, 48);
        params.height = params.width / 2;
        ivNotice.setLayoutParams(params);

        String s = new String(Base64.decode(allStationsNotice.getContent()));
        tvNotice.loadData(s,"text/html","UTF-8");

        //tvNotice.setText(styledText);
        if (allStationsNotice.getType() == 1) {//加载图片
            ivNotice.setVisibility(View.VISIBLE);
            AvatarHelper.getInstance().displayUrl(allStationsNotice.getPicturn(), ivNotice);
        } else if (allStationsNotice.getType() == 2) {//加载视频
            /*
            ivNotice.setVisibility(View.VISIBLE);
            videoView.setVisibility(View.GONE);
            findViewById(R.id.ivStart).setVisibility(View.VISIBLE);
            AvatarHelper.getInstance().asyncDisplayOnlineVideoThumb(allStationsNotice.getPicturn(), ivNotice);
             */
            videoView.setVisibility(View.VISIBLE);
            ivNotice.setVisibility(View.VISIBLE);
            Uri mVideoUri = Uri.parse(allStationsNotice.getImageUrl());
            videoView.setVideoPath(mVideoUri.toString());
            findViewById(R.id.ivStart).setVisibility(View.VISIBLE);
            //加载视频
            ivNotice.setVisibility(View.GONE);
            findViewById(R.id.ivStart).setOnClickListener(v->{
                if (allStationsNotice.getType() == 1) {
                    SingleImagePreviewActivity.start(context, allStationsNotice.getPicturn());
                } else {
                    PlayVideoActivity.start(context, allStationsNotice.getImageUrl());
                }
            });
        } else if(allStationsNotice.getType() == 3) {

        }else{
            ivNotice.setVisibility(View.GONE);
            videoView.setVisibility(View.GONE);
        }
        findViewById(R.id.llClose).setOnClickListener(view -> dismiss());
        ivNotice.setOnClickListener(view -> {
            if (allStationsNotice.getType() == 1) {
                SingleImagePreviewActivity.start(context, allStationsNotice.getPicturn());
            } else {
                PlayVideoActivity.start(context, allStationsNotice.getImageUrl());
            }
        });
        findViewById(R.id.btnIKnow).setOnClickListener(view -> dismiss());

        Window o = getWindow();
        WindowManager.LayoutParams lp = o.getAttributes();
        lp.width = (int) (ScreenUtil.getScreenWidth(getContext()) * 0.8);
        lp.gravity = Gravity.CENTER;
        o.setAttributes(lp);
    }
}
