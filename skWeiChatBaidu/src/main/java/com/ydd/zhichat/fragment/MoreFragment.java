package com.ydd.zhichat.fragment;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.ConfigBean;
import com.ydd.zhichat.ui.MainActivity;
import com.ydd.zhichat.ui.base.EasyFragment;
import com.ydd.zhichat.view.SkinImageView;
import com.ydd.zhichat.view.SkinTextView;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

public class MoreFragment extends EasyFragment {
    SkinImageView ivTitleLeft;

    SkinTextView tvTitleLeft;

    SkinTextView tvTitleCenter;

    ProgressBar pbTitleCenter;

    SkinImageView ivTitleRight;

    SkinImageView ivTitleRightRight;

    SkinTextView tvTitleRight;

    SkinImageView tvTitleRightloadnew;

    String urlstr;
    WebView webview;
    ConfigBean configBean;

    Uri imageUri;

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_more;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        configBean = coreManager.readConfigBean();
        initView();
        initUI();
    }

    private void initView() {
        ivTitleLeft = findViewById(R.id.iv_title_left);
        tvTitleLeft = findViewById(R.id.tv_title_left);
        tvTitleCenter = findViewById(R.id.tv_title_center);
        pbTitleCenter = findViewById(R.id.pb_title_center);
        ivTitleRight = findViewById(R.id.iv_title_right);
        ivTitleRightRight = findViewById(R.id.iv_title_right_right);
        tvTitleRight = findViewById(R.id.tv_title_right);

        tvTitleRightloadnew = findViewById(R.id.add_img_right_right);
        ivTitleRightRight.setOnClickListener((view)->{
            toWebSite();
        });

        webview= findViewById(R.id.webview);
        ivTitleLeft.setVisibility(View.VISIBLE);

        tvTitleRightloadnew.setOnClickListener((view)->{
            webview.reload();
        });

        ivTitleLeft.setOnClickListener((view)->{
            webview.goBack();
        });
    }

    @SuppressLint("JavascriptInterface")
    protected void initUI() {
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webview.getSettings().setBlockNetworkImage(false);

        webview.setWebViewClient(new WebViewClient() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();

                if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                    return false;
                }
                return shouldOverrideUrlLoading(view, request.getUrl().toString());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //网页加载完成后 请求数据
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }
        });
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress == 100) {

                } else {

                }
            }
            /**
             * 8(Android 2.2) <= API <= 10(Android 2.3)回调此方法
             */
            private void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg) {
                // Log.e("WangJ", "运行方法 openFileChooser-1");
                // (2)该方法回调时说明版本API < 21，此时将结果赋值给 mUploadCallbackBelow，使之 != null
                mUploadCallbackBelow = uploadMsg;
                takePhoto();
            }

            /**
             * 11(Android 3.0) <= API <= 15(Android 4.0.3)回调此方法
             */
            public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg, String acceptType) {
                //Log.e("WangJ", "运行方法 openFileChooser-2 (acceptType: " + acceptType + ")");
                // 这里我们就不区分input的参数了，直接用拍照
                openFileChooser(uploadMsg);
            }

            /**
             * 16(Android 4.1.2) <= API <= 20(Android 4.4W.2)回调此方法
             */
            public void openFileChooser(android.webkit.ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                //  Log.e("WangJ", "运行方法 openFileChooser-3 (acceptType: " + acceptType + "; capture: " + capture + ")");
                // 这里我们就不区分input的参数了，直接用拍照
                openFileChooser(uploadMsg);
            }

            /**
             * API >= 21(Android 5.0.1)回调此方法
             */
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, FileChooserParams fileChooserParams) {
                //  Log.e("WangJ", "运行方法 onShowFileChooser");
                // (1)该方法回调时说明版本API >= 21，此时将结果赋值给 mUploadCallbackAboveL，使之 != null
                mUploadCallbackAboveL = valueCallback;
                takePhoto();
                return true;
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                tvTitleCenter.setText(title);
            }
        });

        webview.addJavascriptInterface(this, "android");

        toWebSite();
    }

    /**
     * 调用相机
     */
    private void takePhoto() {
        // 指定拍照存储位置的方式调起相机
       /* */ String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = Uri.fromFile(new File(filePath + fileName));

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        Intent Photo = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(Photo, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

        startActivityForResult(chooserIntent, 99);


    }

    private android.webkit.ValueCallback<Uri[]> mUploadCallbackAboveL;
    private android.webkit.ValueCallback<Uri>   mUploadCallbackBelow;

    private void updatePhotos() {
        // 该广播即使多发（即选取照片成功时也发送）也没有关系，只是唤醒系统刷新媒体文件
//        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        intent.setData(imageUri);
//        startActivityForResult(intent,1);
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), 11);
    }

    private void sendBroadcast(Intent photo) {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 11);
        startActivityForResult(intent, 11);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (RESULT_OK == resultCode) {
            if (data != null) {
                // 这里是针对从文件中选图片的处理
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    for (Uri uri : results) {
                        Log.e("WangJ", "系统返回URI：" + uri.toString());
                    }
                    mUploadCallbackAboveL.onReceiveValue(results);
                } else {
                    mUploadCallbackAboveL.onReceiveValue(null);
                }
            } else {
                Log.e("WangJ", "自定义结果：" + imageUri.toString());
                mUploadCallbackAboveL.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            mUploadCallbackAboveL.onReceiveValue(null);
        }
        mUploadCallbackAboveL = null;

    }



    /**
     * 打开网页
     */
    private void toWebSite() {
        if (MainActivity.findItems!=null&&MainActivity.findItems.size()!=0){
            webview.loadUrl(MainActivity.findItems.get(0).getUrl());
        }

    }

}
