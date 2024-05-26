package com.ydd.zhichat.view;

import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_EXIT_VOICE;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_FILE;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_IMAGE;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_IS_CONNECT_VOICE;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_RED;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_TEXT;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_TRANSFER;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_VIDEO;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_VOICE;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.message.ChatMessage;
import com.ydd.zhichat.bean.message.XmppMessage;
import com.ydd.zhichat.course.ChatRecordHelper;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.util.Constants;
import com.ydd.zhichat.util.PreferenceUtils;
import com.ydd.zhichat.util.TimeUtils;

public class ChatTextClickPpWindow001 extends PopupWindow {
    private View mMenuView;
    private TextView tvCopy;
    private TextView tvRelay;
    private TextView tvCollection;// 存表情
    private TextView tvCollectionOther; // 收藏其他类型的消息
    private TextView tvBack;
    private TextView tvReplay;
    private TextView tvDel;
    private TextView tvMoreSelected;
    // 开始 & 停止录制
    private TextView tvRecord;
    private TextView tvEdit;
    private TextView tvTransfer;//item_chat_transfer

    private boolean isGroup;
    private boolean isDevice;
    private int mRole;
    private Friend mFriend;
    Context mContext;

    public ChatTextClickPpWindow001(Context context, View.OnClickListener listener,
                                    final String toUserId, final Friend friend) {
        super(context);
        mContext = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        mMenuView = inflater.inflate(R.layout.item_chat_long_click_001, null);
        // mMenuView = inflater.inflate(R.layout.item_chat_long_click_list_style, null);

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mMenuView.measure(w, h);
        setWidth(mMenuView.getMeasuredWidth());
        setHeight(mMenuView.getMeasuredHeight());


        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
//        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
//        mWidth = (int) (manager.getDefaultDisplay().getWidth() * 0.9);
//        this.setWidth(mWidth);
//        //	 this.setWidth(ViewPiexlUtil.dp2px(context,200));
//        //设置SelectPicPopupWindow弹出窗体的高
//        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Buttom_Popwindow);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
        /*mMenuView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int bottom = mMenuView.findViewById(R.id.pop_layout).getBottom();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    } else if (y > bottom) {
                        dismiss();
                    }
                }
                return true;
            }
        });*/

        //hideButton(type, course);

        tvCopy = (TextView) mMenuView.findViewById(R.id.item_chat_copy_tv);//阅后即焚
        tvRelay = (TextView) mMenuView.findViewById(R.id.item_chat_relay_tv);//消息过期自动销毁
        tvCollection = (TextView) mMenuView.findViewById(R.id.item_chat_collection_tv);//清空聊天记录
        tvReplay = (TextView) mMenuView.findViewById(R.id.item_chat_replay_tv);//置顶聊天
        tvMoreSelected = (TextView) mMenuView.findViewById(R.id.item_chat_more_select);//清空双方聊天记录
        tvEdit = (TextView) mMenuView.findViewById(R.id.item_chat_edit);//消息免打扰
        tvCollectionOther = (TextView) mMenuView.findViewById(R.id.collection_other);//消息免打扰


        tvCopy.setOnClickListener(listener);
        tvRelay.setOnClickListener(listener);
        tvCollection.setOnClickListener(listener);
        tvReplay.setOnClickListener(listener);
        tvMoreSelected.setOnClickListener(listener);
        tvEdit.setOnClickListener(listener);
        tvCollectionOther.setOnClickListener(listener);
        mFriend = friend;

        int isReadDela = PreferenceUtils.getInt(mMenuView.getContext(), Constants.MESSAGE_READ_FIRE_2 + friend.getUserId() + CoreManager.requireSelf(context).getUserId(), 0);
        tvEdit.setText(mFriend.getOfflineNoPushMsg() == 0 ? "消息免打扰(开启)":"消息免打扰(关闭)");
        //int isReadDelc = PreferenceUtils.getInt(mMenuView.getContext(), Constants.MESSAGE_READ_FIRE_1 + friend.getUserId() + CoreManager.requireSelf(context).getUserId(), 0);
        tvReplay.setText(mFriend.getTopTime() == 0 ? "置顶聊天(开启)" :"置顶聊天(关闭)");
        int isReadDel = PreferenceUtils.getInt(mMenuView.getContext(), Constants.MESSAGE_READ_FIRE + friend.getUserId() + CoreManager.requireSelf(context).getUserId(), 0);
        tvCopy.setText(isReadDel == 1 ? "阅后即焚(开启)":"阅后即焚(关闭)");
        tvRelay.setText("消息过期自动销毁("+conversion(friend.getChatRecordTimeOut())+")");
    }

    private String conversion(double outTime) {
        String outTimeStr;
        if (outTime == -1 || outTime == 0) {
            outTimeStr = mContext.getString(R.string.permanent);
        } else if (outTime == -2) {
            outTimeStr = mContext.getString(R.string.no_sync);
        } else if (outTime == 0.04) {
            outTimeStr = mContext.getString(R.string.one_hour);
        } else if (outTime == 1) {
            outTimeStr = mContext.getString(R.string.one_day);
        } else if (outTime == 7) {
            outTimeStr = mContext.getString(R.string.one_week);
        } else if (outTime == 30) {
            outTimeStr = mContext.getString(R.string.one_month);
        } else if (outTime == 90) {
            outTimeStr = mContext.getString(R.string.one_season);
        } else {
            outTimeStr = mContext.getString(R.string.one_year);
        }
        return outTimeStr;
    }


    /*
    判断当前消息已发送的时间是否超过五分钟
     */
    private boolean judgeTime(long timeSend) {
        return timeSend + 300 < TimeUtils.sk_time_current_time();
    }

    public View getMenuView(){
        return mMenuView;
    }
}
