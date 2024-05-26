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
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.message.ChatMessage;
import com.ydd.zhichat.bean.message.XmppMessage;
import com.ydd.zhichat.course.ChatRecordHelper;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.util.TimeUtils;

/***
 * 头像长按
 */
public class ChatHeadClickPpWindow extends PopupWindow {
    private View mMenuView;
    private int mWidth, mHeight;
    private boolean isGroup;
    private boolean isDevice;
    private int mRole;
    public ChatHeadClickPpWindow(Context context, View.OnClickListener listener,
                                 final ChatMessage type, final String toUserId, boolean course,
                                 boolean group, boolean device, int role) {
        super(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        mMenuView = inflater.inflate(R.layout.item_chat_head_long_click, null);
        // mMenuView = inflater.inflate(R.layout.item_chat_long_click_list_style, null);

        this.isGroup = group;
        this.isDevice = device;
        this.mRole = role;

        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        mMenuView.measure(w, h);
        // 获取PopWindow宽和高
        mHeight = mMenuView.getMeasuredHeight();
        mWidth = mMenuView.getMeasuredWidth();

        mMenuView.findViewById(R.id.item_chat_aita_tv).setOnClickListener(listener);
        mMenuView.findViewById(R.id.item_chat_transfer_tv).setOnClickListener(listener);



        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mWidth = (int) (manager.getDefaultDisplay().getWidth() * 0.9);
        this.setWidth(mWidth);
        //	 this.setWidth(ViewPiexlUtil.dp2px(context,200));
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Buttom_Popwindow);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
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

        hideButton(type, course);
        // 设置按钮监听
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    /*
    根据消息类型隐藏部分操作
     */
    private void hideButton(ChatMessage message, boolean course) {
        int type = message.getType();

        mMenuView.findViewById(R.id.item_chat_text_ll).setBackgroundResource(R.drawable.bg_chat_text_long);
    }

    /*
    判断当前消息已发送的时间是否超过五分钟
     */
    private boolean judgeTime(long timeSend) {
        return timeSend + 300 < TimeUtils.sk_time_current_time();
    }

}
