package com.ydd.zhichat.view.chatbottom;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ydd.im.audio.MessageEventVoice;
import com.ydd.im.audio.RecordManager;
import com.ydd.im.audio.VoicePlayer;
import com.ydd.im.audio.VoiceRecordActivity2;
import com.ydd.zhichat.R;
import com.ydd.zhichat.audio.IMRecordController;
import com.ydd.zhichat.audio.RecordListener;
import com.ydd.zhichat.audio.RecordStateListener;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.util.UiUtils;
import com.ydd.zhichat.view.ChatBottomView;
import com.ydd.zhichat.view.photopicker.Image;

import java.io.File;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/***
 * 录音页面
 */
public class ChatToolsTapeView  extends RelativeLayout implements View.OnClickListener {

    private Context mCxt;
    private RelativeLayout mStartRl;
    private List<String> mVoicePathList = new ArrayList<>();
    private String mVoiceFinalPath; // 最终合并的文件路径
    private VoicePlayer mVoicePlayer; // 音频播放器
    private int isRecording;
    private boolean isPlaying;
    private int mMaxRecordTime = 60;// 最大录制时长 default == 60
    private int mLastRecordTime;// 剩余录制时长

    private TextView tv1, tv2, tv3, tv4;

    private RelativeLayout mRecordTimeRl;
    private TextView mTimeTv;
    private ImageView mStartIv;

    private LinearLayout mOperatingLl;
    private TextView mLeftTv;
    private TextView mRightTv;
    private IMRecordController mRecordController;

    public ChatToolsTapeView(Context context) {
        super(context);
        init(context);
    }

    public ChatToolsTapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        init(context);
    }

    public ChatToolsTapeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        init(context);
    }

    private void init(Context context) {
        mCxt = context;
        LayoutInflater.from(mCxt).inflate(R.layout.chat_tape_view, this);

        mRecordTimeRl = findViewById(R.id.record_time_rl);
        mTimeTv = findViewById(R.id.record_time_tv);

        mStartRl = findViewById(R.id.start_rl);
        mStartIv = findViewById(R.id.start_iv);

        mOperatingLl = findViewById(R.id.operating_ll);
        mLeftTv = findViewById(R.id.left_tv);
        mRightTv = findViewById(R.id.right_tv);


        mStartIv.setOnClickListener(this);
        mLeftTv.setOnClickListener(this);
        mRightTv.setOnClickListener(this);

        mRecordController = new IMRecordController(mCxt);

        mRecordController.setRecordListener(new RecordListener() {
            @Override
            public void onRecordSuccess(String filePath, int timeLen) {
                // 录音成功，返回录音文件的路径
                changeSomething(0);
                if (timeLen < 1) {
                    Toast.makeText(mCxt, InternationalizationHelper.getString("JXChatVC_TimeLess"), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (listener != null) {
                    listener.sendVoice(filePath, timeLen);
                }
            }

            @Override
            public void onRecordChane(int seconds) {
                mLastRecordTime = mMaxRecordTime - seconds;
                if (mLastRecordTime <= 0) {

                } else {
                    mTimeTv.setText(String.valueOf(mLastRecordTime));
                }
            }


            @Override
            public void onRecordStart() {
                listener.stopVoicePlay();//停止播放聊天记录里的语音
                // 录音开始
                //mRecordBtn.setText(R.string.motalk_voice_chat_tip_2);
                //mRecordBtn.setBackgroundResource(R.drawable.im_voice_button_pressed2);
                changeSomething(1);
            }

            @Override
            public void onRecordCancel() {
                // 录音取消
                mLastRecordTime = 0;
                changeSomething(0);
            }
        });

        mStartIv.setOnTouchListener(mRecordController);

    }



    private void initAttrs(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }
    }


    public void setBottomListener(ChatBottomView.ChatBottomListener listener) {
        this.listener = listener;
    }

    private ChatBottomView.ChatBottomListener listener;
    public void init(ChatBottomView.ChatBottomListener listener) {
        setBottomListener(listener);
    }


    private void changeSomething(int flag) {
        isRecording = flag;
        if (flag == 1) { //录音中
            mStartIv.setImageResource(R.mipmap.tounded1_normal);
            mRecordTimeRl.setVisibility(View.VISIBLE);
            mOperatingLl.setVisibility(View.GONE);
        } else if(flag == 2) {
            mStartIv.setImageResource(R.mipmap.triangle1_normal);
            mOperatingLl.setVisibility(View.GONE);
            mMaxRecordTime = mLastRecordTime;
        }else{
            mStartIv.setImageResource(R.mipmap.tape_normal);
            mOperatingLl.setVisibility(View.GONE);
        }
    }

    private void playVoice() {
        isPlaying = true;
        linkVoice();
        mStartRl.setVisibility(View.INVISIBLE);
        mLeftTv.setText(mCxt.getString(R.string.stop));

        mVoicePlayer = null;
        mVoicePlayer = new VoicePlayer();
        mVoicePlayer.play(mVoiceFinalPath);
        mVoicePlayer.setOnFinishPlayListener(new VoicePlayer.OnFinishPlayListener() {
            @Override
            public void onFinishPlay() {
                isPlaying = false;
                mStartRl.setVisibility(View.VISIBLE);
                mLeftTv.setText(mCxt.getString(R.string.test_listen_voice));
            }
        });
    }

    private void finishRecord() {
        linkVoice();
        File file = new File(mVoiceFinalPath);
        if (file.exists()) {
            EventBus.getDefault().post(new MessageEventVoice(mVoiceFinalPath, (60 - mLastRecordTime) * 1000));
            //finish();
            destroy();
        }
    }

    private void destroy() {
        if (isRecording == 1) {
            //mRecordManager.cancel();
        }
        if (isPlaying) {
            mVoicePlayer.stop();
        }
        //finish();
    }

    /**
     * 连接各个录音片段
     */
    public void linkVoice() {
        if (mVoicePathList.size() == 0) {
            return;
        } else if (mVoicePathList.size() == 1) {
            mVoiceFinalPath = mVoicePathList.get(0);
            return;
        }

        int end = mVoicePathList.get(0).lastIndexOf("/");
        String path = mVoicePathList.get(0).substring(0, end);
        mVoiceFinalPath = path + "/" + CoreManager.getSelf(mCxt).getUserId() + System.currentTimeMillis() + "_voice.amr";

        if (uniteAMRFile(mVoicePathList, mVoiceFinalPath)) {// 合并成功
            // 删除每个语音片段文件
            for (int i = 0; i < mVoicePathList.size(); i++) {
                File file = new File(mVoicePathList.get(i));
                if (file.exists()) {
                    file.delete();
                }
            }
            // 清空list
            mVoicePathList.clear();
            // 将生成的文件路径放入list内
            mVoicePathList.add(mVoiceFinalPath);
        } else {
            Toast.makeText(mCxt, R.string.merger_failed, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 需求:将两个amr格式音频文件合并为1个
     * 注意:amr格式的头文件为6个字节的长度
     *
     * @param partsPaths     各部分路径
     * @param unitedFilePath 合并后路径
     */
    public boolean uniteAMRFile(List<String> partsPaths, String unitedFilePath) {
        try {
            File unitedFile = new File(unitedFilePath);
            FileOutputStream fileOutputStream = new FileOutputStream(unitedFile);
            RandomAccessFile randomAccessFile = null;
            for (int i = 0; i < partsPaths.size(); i++) {
                randomAccessFile = new RandomAccessFile(partsPaths.get(i), "r");
                if (i != 0) {
                    randomAccessFile.seek(6);
                }
                byte[] buffer = new byte[1024 * 8];
                int len = 0;
                while ((len = randomAccessFile.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                }
            }
            randomAccessFile.close();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {

        }
        return false;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_iv:
                // 防止连续点击，
                if (!UiUtils.isNormalClick(v)) {
                    return;
                }
                break;
            case R.id.left_tv:
                if (isPlaying) {
                    mVoicePlayer.stop();
                } else {
                    playVoice();
                }
                break;
            case R.id.right_tv:
                finishRecord();
                break;
        }
    }

    public int isRecording(){
        return isRecording;
    }
}
