package com.ydd.zhichat.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ydd.zhichat.AppConfig;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.Reporter;
import com.ydd.zhichat.audio_x.VoiceManager;
import com.ydd.zhichat.audio_x.VoicePlayer;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.RoomMember;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.bean.collection.CollectionEvery;
import com.ydd.zhichat.bean.message.ChatMessage;
import com.ydd.zhichat.broadcast.OtherBroadcast;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.db.dao.ChatMessageDao;
import com.ydd.zhichat.db.dao.FriendDao;
import com.ydd.zhichat.db.dao.RoomMemberDao;
import com.ydd.zhichat.pay.TransferMoneyActivity;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.message.EventMoreSelected;
import com.ydd.zhichat.ui.message.InstantMessageActivity;
import com.ydd.zhichat.ui.message.MessageRemindActivity;
import com.ydd.zhichat.ui.message.multi.RoomReadListActivity;
import com.ydd.zhichat.util.AsyncUtils;
import com.ydd.zhichat.util.BitmapUtil;
import com.ydd.zhichat.util.Constants;
import com.ydd.zhichat.util.ContextUtil;
import com.ydd.zhichat.util.DES;
import com.ydd.zhichat.util.FileUtil;
import com.ydd.zhichat.util.HtmlUtils;
import com.ydd.zhichat.util.LogUtils;
import com.ydd.zhichat.util.Md5Util;
import com.ydd.zhichat.util.PreferenceUtils;
import com.ydd.zhichat.util.StringUtils;
import com.ydd.zhichat.util.TimeUtils;
import com.ydd.zhichat.util.ToastUtil;
import com.ydd.zhichat.view.chatHolder.AChatHolderInterface;
import com.ydd.zhichat.view.chatHolder.ChatHolderFactory;
import com.ydd.zhichat.view.chatHolder.ChatHolderFactory.ChatHolderType;
import com.ydd.zhichat.view.chatHolder.ChatHolderListener;
import com.ydd.zhichat.view.chatHolder.MessageEventClickFire;
import com.ydd.zhichat.view.chatHolder.TextViewHolder;
import com.ydd.zhichat.view.chatHolder.VoiceViewHolder;
import com.ydd.zhichat.xmpp.listener.ChatMessageListener;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.greenrobot.event.EventBus;
import okhttp3.Call;

import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_FILE;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_IMAGE;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_TEXT;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_VIDEO;
import static com.ydd.zhichat.bean.message.XmppMessage.TYPE_VOICE;

@SuppressWarnings("unused")
public class ChatContentView extends PullDownListView implements ChatBottomView.MoreSelectMenuListener {

    // 好友备注只有群组才有
    public Map<String, Bitmap> mCacheMap = new HashMap<>();
    Map<String, String> cache = new HashMap<>();
    private boolean isGroupChat;// 用于标记聊天界面是否为群聊界面
    private boolean isShowReadPerson;// 是否展示群已读人数
    private boolean isShowMoreSelect;// 用于标记是否显示多选框
    private boolean isScrollBottom;// 是否正在底部，如果是，来新消息时需要跳到底部，
    private int mGroupLevel = 3;// 我在当前群组的职位，用于控制群组消息能否撤回(default==3普通成员)
    private int mCurClickPos = -1;// 当前点击的position
    private String mRoomNickName; // 我在房间的昵称，只有群聊才有
    private String mToUserId;// 根据self.userId和mToUserId 唯一确定一张表
    private Friend mToUser;
    private String mRoomId;
    private User mLoginUser;// 当前登录的用户
    private Context mContext; // 界面上下文
    private ChatListType mCurChatType; // 标记当前处于什么界面
    private LayoutInflater mInflater;// 布局填充器
    private ChatBottomView mChatBottomView; // 聊天输入框控件
    private AutoVoiceModule aVoice; // 自动播放下一条未读语音消息的模块
    private ChatContentAdapter mChatContentAdapter;// 消息适配器
    private MessageEventListener mMessageEventListener; // 消息体点击监听
    private ChatTextClickPpWindow mChatPpWindow; // 长按选择框
    private ChatHeadClickPpWindow mChatHeadPpWindow; // 长按头像选择框
    // 当前适配器数据源
    private List<ChatMessage> mChatMessages;
    // 即将要删除的消息的packedId列表
    private Set<String> mDeletedChatMessageId = new HashSet<>();
    // 阅后即焚 正在倒计时缓存的时间列表
    private Map<String, CountDownTimer> mTextBurningMaps = new HashMap<>();
    // 所有阅后即焚的语音消息列表
    private Map<String, String> mFireVoiceMaps = new HashMap<>();
    // 好友备注只有群组才有
    private Map<String, String> mRemarksMap = new HashMap<>();
    // 群管理头像
    //private Map<String, Integer> memberMap = new HashMap<>();
    private Map<String, RoomMember> memberMap = new HashMap<>();
    // 是否第二次点击标志
    private boolean flag = true;
    // 第一次点击时间
    private long one = 0;
    // 第二次点击时间
    private long two = 0;
    // 算为双击点击时间间隔
    private int interval = 500;
    private boolean isShowEdit = false;//编辑

    public void setShowEdit(boolean isq) {
        isShowEdit = isq;
    }

    // 滚动到底部
    private Runnable mScrollTask = new Runnable() {
        @Override
        public void run() {
            if (mChatMessages == null) {
                return;
            }
            setSelection(mChatMessages.size());
        }
    };
    private Collection<OnScrollListener> onScrollListenerList = new ArrayList<>();
    private boolean secret;

    public ChatContentView(Context context) {
        this(context, null);
    }

    public ChatContentView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    // 输入法弹起时让界面跟着上去
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (oldh > h) {
            removeCallbacks(mScrollTask);
            //  int delay = getResources().getInteger(android.R.integer.config_shortAnimTime); // 200
            postDelayed(mScrollTask, 150);
        }
    }

    private void init(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);

        setCacheColorHint(0x00000000);
        mLoginUser = CoreManager.requireSelf(context);
        mRoomNickName = mLoginUser.getNickName();
        aVoice = new AutoVoiceModule();

        VoicePlayer.instance().addVoicePlayListener(new VoicePlayListener());

        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                for (OnScrollListener listener : onScrollListenerList) {
                    listener.onScrollStateChanged(view, scrollState);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isScrollBottom = firstVisibleItem + visibleItemCount >= totalItemCount;
                for (OnScrollListener listener : onScrollListenerList) {
                    listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
                }
            }
        });

        mChatContentAdapter = new ChatContentAdapter();
        setAdapter(mChatContentAdapter);
    }

    // 返回当前是否在底部
    public boolean shouldScrollToBottom() {
        return isScrollBottom;
    }

    // 点击空位置收起输入法软键盘
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mMessageEventListener != null) {
                mMessageEventListener.onEmptyTouch();
            }
        }
        return super.onTouchEvent(event);
    }

    // 设置对方的userid
    public void setToUserId(String toUserId) {
        mToUserId = toUserId;
    }

    public void setToUser(Friend friend) {
        mToUser = friend;
    }

    // 设置对方的userid
    public void setRoomId(String roomid) {
        mRoomId = roomid;
    }

    // 修改为多选
    public void setIsShowMoreSelect(boolean isShowMoreSelect) {
        this.isShowMoreSelect = isShowMoreSelect;
    }

    // 设置当前是否是群组，群组中显示的昵称
    public void setCurGroup(boolean group, String nickName) {
        isGroupChat = group;
        if (!TextUtils.isEmpty(nickName)) {
            mRoomNickName = nickName;
        }
        if (mRemarksMap.size() == 0) {
            AsyncUtils.doAsync(mContext, new AsyncUtils.Function<AsyncUtils.AsyncContext<Context>>() {
                @Override
                public void apply(AsyncUtils.AsyncContext<Context> contextAsyncContext) throws Exception {
                    List<Friend> friendList = FriendDao.getInstance().getAllFriends(mLoginUser.getUserId());
                    for (int i = 0; i < friendList.size(); i++) {
                        if (!TextUtils.isEmpty(friendList.get(i).getRemarkName())) {
                            mRemarksMap.put(friendList.get(i).getUserId(), friendList.get(i).getRemarkName());
                        }
                    }
                }
            });
        }
    }

    public boolean isGroupChat() {
        return isGroupChat;
    }

    // 设置当前用户在群组的的权限
    public void setRole(int role) {
        this.mGroupLevel = role;
    }

    public void setChatBottomView(ChatBottomView chatBottomView) {
        this.mChatBottomView = chatBottomView;
        if (mChatBottomView != null) {
            mChatBottomView.setMoreSelectMenuListener(this);
        }
    }

    public void setData(List<ChatMessage> chatMessages) {
        mChatMessages = chatMessages;
        if (mChatMessages == null) {
            mChatMessages = new ArrayList<>();
        }
        notifyDataSetChanged();
    }

    // 设置管理员头像控件
    public void setRoomMemberList(List<RoomMember> memberList) {
        memberMap.clear();
        for (RoomMember member : memberList) {
            memberMap.put(member.getUserId(), member);
        }
        notifyDataSetChanged();
    }

    // 使用动画删除某一条消息
    public void removeItemMessage(final String packedId) {
        mDeletedChatMessageId.add(packedId);
        notifyDataSetChanged();
    }

    // 界面更新
    public void notifyDataSetInvalidated(final int position) {
        notifyDataSetChanged();
        if (mChatMessages.size() > position) {
            this.post(() -> setSelection(position));
        }
    }

    /**
     * 用于在列表头部添加元素后更新列表使用，
     * 封装处理更新后跳到正确位置，
     */
    public void notifyDataSetAddedItemsToTop(final int count) {
        int oldPosition = getFirstVisiblePosition();
        View firstView = getChildAt(0);
        int oldTop = firstView == null ? 0 : firstView.getTop();
        notifyDataSetChanged();
        int position = count + oldPosition;
        if (mChatMessages.size() > position) {
            // 不知道之前为什么用post, 会导致明显跳到前面触发onScroll再闪下来，
            // 保留其他方法的post, 只改这里，
            setSelectionFromTop(position, oldTop);
        }
    }

    public void notifyDataSetInvalidated(boolean scrollToBottom) {
        notifyDataSetChanged();
        if (scrollToBottom && mChatMessages.size() > 0) {
            setSelection(mChatMessages.size());
        }
    }

    // 群组偶现第一次加载setSelection方法失效的问题 专门封装一个方法针对此问题
    // https://blog.csdn.net/santamail/article/details/38821763
    public void notifyDataSetInvalidatedForSetSelectionInvalid(boolean scrollToBottom) {
        notifyDataSetChanged();
        if (scrollToBottom && mChatMessages.size() > 0) {
            this.setAdapter(mChatContentAdapter);
            setSelection(mChatMessages.size());
        }
    }

    public void notifyDataSetChanged() {
        if (mChatContentAdapter != null) {
            mChatContentAdapter.notifyDataSetChanged();
        }
    }

    // 设置消息解监听
    public void setMessageEventListener(MessageEventListener listener) {
        mMessageEventListener = listener;
    }

    // 指定当前界面是在哪 直播 还是 课程
    public void setChatListType(ChatListType type) {
        mCurChatType = type;
    }

    // 一条消息的删除动画
    private void startRemoveAnim(View view, ChatMessage message, int position) {
        Animation anim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                view.setAlpha(1f - interpolatedTime);
            }
        };

        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mChatMessages.remove(message);
                mDeletedChatMessageId.remove(message);
                view.clearAnimation();
                notifyDataSetChanged();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        anim.setDuration(1000);
        view.startAnimation(anim);
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 消息业务具体实现 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 开始一个阅后即焚的计时器
    private void startCountDownTimer(long time, AChatHolderInterface holder, ChatMessage message) {
        if (time < 1000) {
            mTextBurningMaps.remove(message.getPacketId());
            EventBus.getDefault().post(new MessageEventClickFire("delete", message.getPacketId()));
            removeItemMessage(message.getPacketId());
            return;
        }

        TextViewHolder textViewHolder = (TextViewHolder) holder;
        CountDownTimer mNewCountDownTimer = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewHolder.tvFireTime.setText(String.valueOf(millisUntilFinished / 1000));
                message.setReadTime(millisUntilFinished);
                ChatMessageDao.getInstance().updateMessageReadTime(mLoginUser.getUserId(), message.getFromUserId(), message.getPacketId(), millisUntilFinished);
            }

            @Override
            public void onFinish() {
                mTextBurningMaps.remove(message.getPacketId());
                EventBus.getDefault().post(new MessageEventClickFire("delete", message.getPacketId()));

                removeItemMessage(message.getPacketId());
            }
        }.start();
        mTextBurningMaps.put(message.getPacketId(), mNewCountDownTimer);
    }

    // 点击了阅后即焚的文字消息
    private void clickFireText(AChatHolderInterface holder, ChatMessage message) {
        TextViewHolder textholder = (TextViewHolder) holder;

        textholder.mTvContent.setTextColor(getResources().getColor(R.color.black));
        String s = StringUtils.replaceSpecialChar(message.getContent());
        CharSequence charSequence = HtmlUtils.transform200SpanString(s, true);
        textholder.mTvContent.setText(charSequence);

        textholder.mTvContent.post(new Runnable() {
            @Override
            public void run() {
                final long time = textholder.mTvContent.getLineCount() * 10000;// 计算时间，一行10s
                textholder.tvFireTime.setText(String.valueOf(time / 1000));
                textholder.tvFireTime.setVisibility(VISIBLE);
                message.setReadTime(time);

                startCountDownTimer(time, textholder, message);
            }
        });
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓多选业务具体实现 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 判断 是否 一条消息也没有选择
    private boolean isNullSelectMore(List<ChatMessage> list) {
        if (list == null || list.size() == 0) {
            return true;
        }

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isMoreSelected) {
                return false;
            }
        }
        return true;
    }

    // 点击了 多选转发按钮
    @Override
    public void clickForwardMenu() {
        final Dialog mForwardDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = mInflater.inflate(R.layout.forward_dialog, null);
        mForwardDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        mForwardDialog.setCanceledOnTouchOutside(true);
        mForwardDialog.getWindow().setGravity(Gravity.BOTTOM);
        mForwardDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        mForwardDialog.show();
        mForwardDialog.findViewById(R.id.single_forward).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {// 逐条转发
                mForwardDialog.dismiss();
                if (isNullSelectMore(mChatMessages)) {
                    Toast.makeText(mContext, mContext.getString(R.string.name_connot_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                // 跳转至转发页面
                Intent intent = new Intent(mContext, InstantMessageActivity.class);
                intent.putExtra(Constants.IS_MORE_SELECTED_INSTANT, true);
                mContext.startActivity(intent);
            }
        });
        mForwardDialog.findViewById(R.id.sum_forward).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {// 合并转发
                mForwardDialog.dismiss();
                if (isNullSelectMore(mChatMessages)) {
                    Toast.makeText(mContext, mContext.getString(R.string.name_connot_null), Toast.LENGTH_SHORT).show();
                    return;
                }
                // 跳转至转发页面
                Intent intent = new Intent(mContext, InstantMessageActivity.class);
                intent.putExtra(Constants.IS_MORE_SELECTED_INSTANT, true);
                intent.putExtra(Constants.IS_SINGLE_OR_MERGE, true);
                mContext.startActivity(intent);
            }
        });
        mForwardDialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mForwardDialog.dismiss();
            }
        });
    }

    // 点击了多选收藏按钮
    @Override
    public void clickCollectionMenu() {
        if (isNullSelectMore(mChatMessages)) {
            Toast.makeText(mContext, mContext.getString(R.string.name_connot_null), Toast.LENGTH_SHORT).show();
            return;
        }
        SelectionFrame selectionFrame = new SelectionFrame(mContext);
        selectionFrame.setSomething(null, MyApplication.getInstance().getString(R.string.b9), MyApplication.getInstance().getString(R.string.b45), MyApplication.getInstance().getString(R.string.b53),
                new SelectionFrame.OnSelectionFrameClickListener() {
                    @Override
                    public void cancelClick() {

                    }

                    @Override
                    public void confirmClick() {
                        List<ChatMessage> temp = new ArrayList<>();
                        for (int i = 0; i < mChatMessages.size(); i++) {
                            if (mChatMessages.get(i).isMoreSelected
                                    && (mChatMessages.get(i).getType() == TYPE_TEXT
                                    || mChatMessages.get(i).getType() == TYPE_IMAGE
                                    || mChatMessages.get(i).getType() == TYPE_VOICE
                                    || mChatMessages.get(i).getType() == TYPE_VIDEO
                                    || mChatMessages.get(i).getType() == TYPE_FILE)) {
                                temp.add(mChatMessages.get(i));
                            }
                        }
                        moreSelectedCollection(temp);
                        // 发送EventBus，通知聊天页面解除多选状态
                        EventBus.getDefault().post(new EventMoreSelected("MoreSelectedCollection", false, isGroupChat()));
                    }
                });
        selectionFrame.show();
    }

    // 点击了多选删除按钮
    @Override
    public void clickDeleteMenu() {
        if (isNullSelectMore(mChatMessages)) {
            Toast.makeText(mContext, mContext.getString(R.string.name_connot_null), Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog mDeleteDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = mInflater.inflate(R.layout.delete_dialog, null);
        mDeleteDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        mDeleteDialog.setCanceledOnTouchOutside(true);
        mDeleteDialog.getWindow().setGravity(Gravity.BOTTOM);
        mDeleteDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        mDeleteDialog.show();
        mDeleteDialog.findViewById(R.id.delete_message).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteDialog.dismiss();
                EventBus.getDefault().post(new EventMoreSelected("MoreSelectedDelete", false, isGroupChat()));
            }
        });

        mDeleteDialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mDeleteDialog.dismiss();
            }
        });
    }

    // 点击了 更多按钮
    @Override
    public void clickEmailMenu() {
        if (isNullSelectMore(mChatMessages)) {
            Toast.makeText(mContext, mContext.getString(R.string.name_connot_null), Toast.LENGTH_SHORT).show();
            return;
        }
        final Dialog mEmailDialog = new Dialog(mContext, R.style.BottomDialog);
        View contentView = mInflater.inflate(R.layout.email_dialog, null);
        mEmailDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        mEmailDialog.setCanceledOnTouchOutside(true);
        mEmailDialog.getWindow().setGravity(Gravity.BOTTOM);
        mEmailDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        mEmailDialog.show();
        mEmailDialog.findViewById(R.id.save_message).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailDialog.dismiss();
                SelectionFrame selectionFrame = new SelectionFrame(mContext);
                selectionFrame.setSomething(null, getContext().getString(R.string.save_only_image), getContext().getString(R.string.cancel), getContext().getString(R.string.save),
                        new SelectionFrame.OnSelectionFrameClickListener() {
                            @Override
                            public void cancelClick() {

                            }

                            @Override
                            public void confirmClick() {
                                for (int i = 0; i < mChatMessages.size(); i++) {
                                    if (mChatMessages.get(i).isMoreSelected && mChatMessages.get(i).getType() == TYPE_IMAGE) {
                                        FileUtil.downImageToGallery(mContext, mChatMessages.get(i).getContent());
                                    }
                                }
                                EventBus.getDefault().post(new EventMoreSelected("MoreSelectedEmail", false, isGroupChat()));
                            }
                        });
                selectionFrame.show();
            }
        });

        mEmailDialog.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmailDialog.dismiss();
            }
        });
    }

    /**
     * 收藏和存表情共用一个接口，参数也基本相同，
     *
     * @param flag 是收藏就为true, 是存表情就为false,
     */
    private String collectionParam(List<ChatMessage> messageList, boolean flag) {
        JSONArray array = new JSONArray();
        for (ChatMessage message : messageList) {
            int type = 6;
            if (flag) {// 收藏
                if (message.getType() == TYPE_IMAGE) {
                    type = CollectionEvery.TYPE_IMAGE;
                } else if (message.getType() == TYPE_VIDEO) {
                    type = CollectionEvery.TYPE_VIDEO;
                } else if (message.getType() == TYPE_FILE) {
                    type = CollectionEvery.TYPE_FILE;
                } else if (message.getType() == TYPE_VOICE) {
                    type = CollectionEvery.TYPE_VOICE;
                } else if (message.getType() == TYPE_TEXT) {
                    type = CollectionEvery.TYPE_TEXT;
                }
            }
            JSONObject json = new JSONObject();
            json.put("type", String.valueOf(type));
            json.put("msg", message.getContent());
            if (flag) {
                // 收藏消息id
                json.put("msgId", message.getPacketId());
                if (isGroupChat()) {
                    // 群组收藏需要添加jid
                    json.put("roomJid", mToUserId);
                }
            } else {
                // 表情url
                json.put("url", message.getContent());
            }
            array.add(json);
        }
        return JSON.toJSONString(array);
    }

    public void collectionTypeMessage(ChatMessage chatMessage) {
        collectionEmotion(chatMessage, true);
    }

    /**
     * 添加为表情 && 收藏
     * 添加为表情Type 6.表情
     * 收藏Type    1.图片 2.视频 3.文件 4.语音 5.文本
     */
    public void collectionEmotion(ChatMessage message, final boolean flag) {
        if (TextUtils.isEmpty(message.getContent())) {
            return;
        }
        Map<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(getContext()).accessToken);
        params.put("emoji", collectionParam(Collections.singletonList(message), flag));

        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).Collection_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            Toast.makeText(mContext, InternationalizationHelper.getString("JX_CollectionSuccess"), Toast.LENGTH_SHORT).show();
                            if (!flag) { // 添加为表情
                                // 收藏成功后将对应的url存入内存中，以防下次再次收藏该链接
                                // PreferenceUtils.putInt(mContext, self.getUserId() + message.getContent(), 1);
                                // 发送广播更新收藏列表
                                MyApplication.getInstance().sendBroadcast(new Intent(OtherBroadcast.CollectionRefresh));
                            }
                        } else if (!TextUtils.isEmpty(result.getResultMsg())) {
                            ToastUtil.showToast(mContext, result.getResultMsg());
                        } else {
                            ToastUtil.showToast(mContext, R.string.tip_server_error);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                });
    }

    /**
     * 多选 收藏
     */
    public void moreSelectedCollection(List<ChatMessage> chatMessageList) {
        if (chatMessageList == null || chatMessageList.size() <= 0) {
            Toast.makeText(mContext, mContext.getString(R.string.name_connot_null), Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(getContext()).accessToken);
        params.put("emoji", collectionParam(chatMessageList, true));

        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).Collection_ADD)
                .params(params)
                .build()
                .execute(new BaseCallback<Void>(Void.class) {

                    @Override
                    public void onResponse(ObjectResult<Void> result) {
                        if (result.getResultCode() == 1) {
                            Toast.makeText(mContext, InternationalizationHelper.getString("JX_CollectionSuccess"), Toast.LENGTH_SHORT).show();
                        } else if (!TextUtils.isEmpty(result.getResultMsg())) {
                            ToastUtil.showToast(mContext, result.getResultMsg());
                        } else {
                            ToastUtil.showToast(mContext, R.string.tip_server_error);
                        }
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        ToastUtil.showNetError(mContext);
                    }
                });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (Map.Entry<String, Bitmap> entry : mCacheMap.entrySet()) {
            Bitmap bitmap = entry.getValue();
            bitmap.recycle();
            bitmap = null;
        }
        mCacheMap.clear();
        System.gc();
    }

    public void addOnScrollListener(OnScrollListener onScrollListener) {
        onScrollListenerList.add(onScrollListener);
    }

    public void setSecret(boolean secret) {
        this.secret = secret;
    }

    // ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓ 实体类 ↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    /**
     * 双击事件
     */
    public void twoTouch(View v, ChatMessage content) {
        // 判断是否第二次点击
        if (!flag) {
            // 取第二次时间时超时，重置标志之类
            if ((System.currentTimeMillis() - one) > interval) {
                flag = true;
                one = 0;
                two = 0;
            }
        }
        // 判断标志给 one two 赋值，初始 true 给 one 赋值
        if (flag) {
            Log.i("zx", "one 赋值");
            one = System.currentTimeMillis();
            flag = false;
            Log.e("zx", "twoTouch: ");
        } else {
            Log.i("zx", "two 赋值");
            two = System.currentTimeMillis();
            if ((two - one) > interval) {
                // 取第二次时间时超时
                flag = true;
                one = 0;
                two = 0;
            }
        }
        // 计算两次点击间隔
        long t_o = two - one;
        // 判断间隔是否在双击范围中
        if (t_o > 0 && t_o < interval) {
            Log.i("zx", "双击发生");
            // 双击发生则设置标志之类复位
            flag = true;
            one = 0;
            two = 0;
            MessageRemindActivity.start(getContext(), content, mToUserId);
        } else if (two > 0 && !flag) {
            Log.i("zx", "双击没有发生");
            // 双击没有发生设置标志之类复位
            flag = true;
            one = 0;
            two = 0;
        }
    }

    public enum ChatListType {
        // 单聊 直播 课程 设备
        SINGLE, LIVE, COURSE, DEVICE
    }

    // 适配器接口
    public interface MessageEventListener {
        // 点击空白处，让输入框归位
        void onEmptyTouch();

        void onTipMessageClick(ChatMessage message);

        /**
         * @param message 这是点击的消息，不是被回复的消息，
         */
        default void onReplayClick(ChatMessage message) {
        }

        void onMyAvatarClick();

        void onFriendAvatarClick(String friendUserId);

        void LongAvatarClick(ChatMessage chatMessage);

        void LongTranClick(ChatMessage chatMessage);

        void onNickNameClick(String friendUserId);

        void onMessageClick(ChatMessage chatMessage);

        void onMessageLongClick(ChatMessage chatMessage);

        void onSendAgain(ChatMessage chatMessage);

        void onMessageBack(ChatMessage chatMessage, int position);

        default void onMessageReplay(ChatMessage chatMessage) {

        }

        default void onEditMessage(ChatMessage chatMessage) {

        }

        void onCallListener(int type);
    }

    // 消息适配器
    public class ChatContentAdapter extends BaseAdapter implements ChatHolderListener {

        public int getCount() {
            if (mChatMessages != null) {
                return mChatMessages.size();
            }
            return 0;
        }

        @Override
        public ChatMessage getItem(int position) {
            return mChatMessages.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getViewTypeCount() {
            return ChatHolderFactory.viewholderCount();
        }

        @Override
        public int getItemViewType(int position) {
            ChatMessage message = getItem(position);
            int messageType = message.getType();

            // 正常情况的 mySend
            boolean mySend = message.isMySend() || mLoginUser.getUserId().equals(message.getFromUserId());

            // 兼容多点登陆
            if (message.getFromUserId().equals(message.getToUserId()) && !TextUtils.isEmpty(message.getFromId())) {
                if (message.getFromId().contains("android")) {
                    mySend = true;
                } else {
                    mySend = false;
                }
            }

            // 兼容直播间
            ChatHolderType holderType = ChatHolderFactory.getChatHolderType(mySend, message);
            if (mCurChatType == ChatListType.LIVE) {
                holderType = ChatHolderType.VIEW_SYSTEM_LIVE;
            }

            return holderType.ordinal();
        }

        public View createHolder(ChatHolderType holderType, View conver, ViewGroup parent) {
            AChatHolderInterface holder = ChatHolderFactory.getHolder(holderType);

            conver = mInflater.inflate(holder.getLayoutId(holder.isMysend), parent, false);
            holder.mContext = mContext;
            holder.mLoginUserId = mLoginUser.getUserId();
            holder.mLoginNickName = mRoomNickName;
            holder.mToUserId = mToUserId;
            holder.isGounp = isGroupChat();
            isShowReadPerson = PreferenceUtils.getBoolean(mContext, Constants.IS_SHOW_READ + mToUserId, false);
            holder.setShowPerson(isShowReadPerson);

            holder.findView(conver);
            holder.addChatHolderListener(this);
            conver.setTag(holder);

            return conver;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ChatMessage message = getItem(position);
            ChatHolderType holderType = ChatHolderFactory.getChatHolderType(getItemViewType(position));

            AChatHolderInterface holder;
            if (convertView == null) {
                convertView = createHolder(holderType, convertView, parent);
                holder = (AChatHolderInterface) convertView.getTag();
            } else {
                holder = (AChatHolderInterface) convertView.getTag();
                if (holder.mHolderType != holderType) {
                    convertView = createHolder(holderType, convertView, parent);
                    holder = (AChatHolderInterface) convertView.getTag();
                }
            }

            holder.chatMessages = mChatMessages;
            holder.selfGroupRole = mGroupLevel;
            holder.mHolderType = holderType;
            holder.position = position;
            holder.setMultiple(isShowMoreSelect);
            isShowReadPerson = PreferenceUtils.getBoolean(mContext, Constants.IS_SHOW_READ + mToUserId, false);
            holder.setShowPerson(isShowReadPerson);

            // 显示时间
            changeTimeVisible(holder, message);
            // 设置备注，显示与否是在基类实现的
            chagneNameRemark(holder, message);

            // 数据解密
            if (message.getIsEncrypt() == 1) { // == 1
                try {
                    String decryptKey = Md5Util.toMD5(AppConfig.apiKey + message.getTimeSend() + message.getPacketId());
                    String decryptContent = DES.decryptDES(message.getContent(), decryptKey);
                    // 为chatMessage重新设值
                    message.setContent(decryptContent);
                    message.setIsEncrypt(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (isGroupChat()) {
                //群组处理
                if (message.isMySend() && message.getType() != 10) {
                    holder.prepare(message, 0, secret, mLoginUser);
                } else {
                    RoomMember roomMember = memberMap.get(message.getFromUserId());
                    if (roomMember != null && message.getType() != 10) {
                        holder.prepare(message, memberMap.get(message.getFromUserId()).getRole(), secret, memberMap.get(message.getFromUserId()));
                    } else {
                        holder.prepare(message, 0, secret);
                    }
                }

            } else {
                //单聊处理
                if (message.isMySend() && message.getType() != 10) {
                    holder.prepare(message, 0, secret, mLoginUser);
                } else {
                    Friend friend = mToUser;
                    if (friend != null && message.getType() != 10) {
                        holder.prepare(message, 0, secret, friend);
                    } else {
                        //todo
                        holder.prepare(message, 0, secret);
                    }
                }
            }


            if (holder.mHolderType == ChatHolderType.VIEW_TO_VOICE) {
                // 阅后即焚语音的处理
                if (!isGroupChat() && message.getIsReadDel()) {
                    if (!TextUtils.isEmpty(message.getFilePath()) && !mFireVoiceMaps.containsKey(message.getFilePath())) {
                        mFireVoiceMaps.put(message.getFilePath(), message.getPacketId());
                    }
                }

                // 自动播放语音的处理
                if (!message.isSendRead()) {
                    aVoice.put((VoiceViewHolder) holder);
                }
            }

            if (holder.mHolderType == ChatHolderType.VIEW_TO_TEXT) {// 非群组、对方发送、阅后即焚类型、已发送已读 才显示倒计时
                // 阅后即焚消息显示倒计时控件
                TextViewHolder textViewHolder = (TextViewHolder) holder;
                textViewHolder.showFireTime(mTextBurningMaps.containsKey(message.getPacketId()));

                // 取出倒计时
                if (!isGroupChat() && message.getIsReadDel() && message.isSendRead()) {
                    textViewHolder.showFireTime(true);
                    long time = message.getReadTime();
                    if (mTextBurningMaps.containsKey(message.getPacketId())) {
                        CountDownTimer mCountDownTimer = mTextBurningMaps.get(message.getPacketId());
                        mCountDownTimer.cancel();// 取消上一个事件
                        mTextBurningMaps.remove(message.getPacketId());
                    }
                    startCountDownTimer(time, holder, message);
                }
            }

            // 删除视图的淡出效果，
            convertView.setAlpha(1f);
            if (mDeletedChatMessageId.contains(message.getPacketId())) {
                startRemoveAnim(convertView, message, position);
            }

            return convertView;
        }

        private void changeTimeVisible(AChatHolderInterface holder, ChatMessage message) {
            int position = holder.position;
            // 与上一条消息之间的间隔如果大于15分钟就显示本消息发送时间
            String timeStr = null;
            if (position >= 1) {
                long last = mChatMessages.get(position - 1).getTimeSend();
                if (message.getTimeSend() - last > 15 * 60) {// 小于15分钟，不显示
                    timeStr = TimeUtils.sk_time_long_to_chat_time_str(message.getTimeSend());
                }
            }
            holder.showTime(timeStr);
        }

        private void chagneNameRemark(AChatHolderInterface holder, ChatMessage message) {
            if (!isGroupChat || message.isMySend()) {
                return;
            }

            if (mGroupLevel == 1) {// 群主优先显示自己对群组成员的群内备注
                RoomMember member = RoomMemberDao.getInstance().getSingleRoomMember(mRoomId, message.getFromUserId());
                if (member != null
                        && !TextUtils.isEmpty(member.getCardName())
                        && !TextUtils.equals(member.getUserName(), member.getCardName())) {// 已有群内备注
                    String name = member.getCardName();
                    message.setFromUserName(name);
                    return;
                }
            }
            if (mRemarksMap.containsKey(message.getFromUserId())) {
                message.setFromUserName(mRemarksMap.get(message.getFromUserId()));
            }
        }

        public void clickRootItme(AChatHolderInterface holder, ChatMessage message) {
            mCurClickPos = holder.position;

            // 点击了一条阅后即焚的消息 非群组、阅后即焚类型、未发送已读 才可以点击
            if (!isGroupChat() && message.getIsReadDel() && !message.isSendRead()) {
                // 发送已读消息回执，对方收到后会删除该条阅后即焚消息
                if (holder.mHolderType == ChatHolderType.VIEW_TO_TEXT) {
                    // 通知chatactivity 等待播放完成后删除
                    EventBus.getDefault().post(new MessageEventClickFire("delay", message.getPacketId()));
                    // 阅后即焚的文字处理
                    clickFireText(holder, message);
                } else if (holder.mHolderType == ChatHolderType.VIEW_TO_VIDEO) {// 阅后即焚的视频处理
                    // 通知chatactivity 等待播放完成后删除
                    EventBus.getDefault().post(new MessageEventClickFire("delay", message.getPacketId()));
                } else if (holder.mHolderType == ChatHolderType.VIEW_TO_IMAGE) {// 阅后即焚的图片处理
                    // 通知chatactivity 等待播放完成后删除
                    EventBus.getDefault().post(new MessageEventClickFire("delay", message.getPacketId()));
                } else if (holder.mHolderType == ChatHolderType.VIEW_TO_VOICE) {// 阅后即焚的声音处理
                    // 通知chatactivity 等待播放完成后删除
                    EventBus.getDefault().post(new MessageEventClickFire("delay", message.getPacketId()));
                }
            }

            if (holder.mHolderType == ChatHolderType.VIEW_FROM_MEDIA_CALL || holder.mHolderType == ChatHolderType.VIEW_TO_MEDIA_CALL) {
                if (mMessageEventListener != null) {
                    mMessageEventListener.onCallListener(message.getType());
                }
                return;
            }

            holder.sendReadMessage(message);
        }

        @Override
        public void onItemClick(View v, AChatHolderInterface holder, ChatMessage message) {
            Log.e("xuan", "onItemClick: " + holder.position);
            if (isShowMoreSelect) {
                message.isMoreSelected = !message.isMoreSelected;
                holder.setBoxSelect(message.isMoreSelected);
                return;
            }
            if (message.getType() == TYPE_TEXT) {
                twoTouch(v, message);
            }
            switch (v.getId()) {
                case R.id.tv_read: // 点击了群已读人数
                    Intent intent = new Intent(mContext, RoomReadListActivity.class);
                    intent.putExtra("packetId", message.getPacketId());
                    intent.putExtra("roomId", mToUserId);
                    mContext.startActivity(intent);
                    break;
                case R.id.iv_failed: // 点击了发送失败的消息的感叹号
                    holder.mIvFailed.setVisibility(GONE);
                    holder.mSendingBar.setVisibility(VISIBLE);
                    message.setMessageState(ChatMessageListener.MESSAGE_SEND_ING);
                    mMessageEventListener.onSendAgain(message);
                    break;
                case R.id.chat_head_iv: // 点击了头像
                    if (message.isMySend()) {
                        mMessageEventListener.onFriendAvatarClick(mLoginUser.getUserId());
                    } else {
                        mMessageEventListener.onFriendAvatarClick(message.getFromUserId());
                    }
                    break;
                case R.id.chat_warp_view:
                    clickRootItme(holder, message);
                    break;
            }

            if (holder.mHolderType == ChatHolderType.VIEW_SYSTEM_TIP) {
                if (mMessageEventListener != null) {
                    mMessageEventListener.onTipMessageClick(message);
                }
                return;
            }
        }

        @Override
        public void onItemLongClick(View v, AChatHolderInterface holder, ChatMessage message) {
            Log.e("xuan", "onLongClick: " + holder.position);
            if (mCurChatType == ChatListType.LIVE) {
                return;
            }

            if (isShowMoreSelect) {
                return;
            }

            // 群组长按头像
            if (isGroupChat() && v.getId() == R.id.chat_head_iv) {

                /* mMessageEventListener.LongAvatarClick(message);
                 */


                if (message.getFromUserId().equals(CoreManager.getSelf(v.getContext()).getUserId())) {// @自己不处理
                    return;
                }
                mChatHeadPpWindow = new ChatHeadClickPpWindow(mContext, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mChatHeadPpWindow.dismiss();
                        if (v.getId() == R.id.item_chat_aita_tv) {
                            mMessageEventListener.LongAvatarClick(message);
                        } else if (v.getId() == R.id.item_chat_transfer_tv) {
                            mMessageEventListener.LongTranClick(message);
                        }
                    }
                },
                        message, mToUserId, mCurChatType == ChatListType.COURSE, isGroupChat(),
                        mCurChatType == ChatListType.DEVICE, mGroupLevel);
                int offsetY = v.getHeight() + holder.dp2px(45);
                mChatHeadPpWindow.showAsDropDown(v, 0, holder.mouseY - offsetY);
                //   mChatPpWindow.showAsDropDown(v);

            }
            else {

            /*Vibrator vib = (Vibrator) mContext.getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(40);// 只震动一秒，一次*/

                mChatPpWindow = new ChatTextClickPpWindow(mContext, new ClickListener(message, holder.position),
                        message, mToUserId, mCurChatType == ChatListType.COURSE, isGroupChat(),
                        mCurChatType == ChatListType.DEVICE, mGroupLevel);

                //int offsetY = v.getHeight() + holder.dp2px(45);
                //mChatPpWindow.showAsDropDown(v, 0, holder.mouseY - offsetY);
                mChatPpWindow.showAsDropDown(v);
                if(message.getType() == 1) {
                    if (isShowEdit) {
                        TextView edit = mChatPpWindow.getMenuView().findViewById(R.id.item_chat_edit);
                        edit.setVisibility(VISIBLE);
                    } else {
                        TextView edit = mChatPpWindow.getMenuView().findViewById(R.id.item_chat_edit);
                        edit.setVisibility(GONE);
                    }
                }else{
                    TextView edit = mChatPpWindow.getMenuView().findViewById(R.id.item_chat_edit);
                    edit.setVisibility(GONE);
                }


           /* int offSetX = holder.mouseX - mChatPpWindow.getWidth() / 2;
            // mChatBottomView的高度是可变的，选择Edit来计算
            int offSetY;
            if (mChatBottomView == null) {// 我的讲课无mChatBottomView
                offSetY = 0 - (v.getHeight() - holder.mouseY) - DisplayUtil.dip2px(mContext, 12);// 再向上偏移12dp
            } else {
                offSetY = 0 - (v.getHeight() - holder.mouseY) - mChatBottomView.getmChatEdit().getHeight()
                        - DisplayUtil.dip2px(mContext, 12);// 再向上偏移12dp
            }
            mChatPpWindow.showAsDropDown(v, offSetX, offSetY);*/
            }
            if (isGroupChat() && v.getId() != R.id.chat_head_iv) {
                Activity activity = ContextUtil.findActivity(mContext);
                if (activity != null) {
                    View view = activity.findViewById(android.R.id.content);
                    if (view instanceof FrameLayout) {
                        FrameLayout root = (FrameLayout) view;
                        View content = root.getChildAt(0);
                        new Thread(() -> {
                            try {
                                Bitmap bitmap = Bitmap.createBitmap(content.getWidth(), content.getHeight(), Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                content.draw(canvas);
                                Bitmap blur = BitmapUtil.blur(mContext, bitmap, 1f, 0.5f);
                                bitmap.recycle();
                                view.post(() -> {
                                    if (mChatPpWindow.isShowing()) {
                                        View blurView = new View(mContext);
                                        blurView.setBackground(new BitmapDrawable(mContext.getResources(), blur));
                                        root.addView(blurView, -1, -1);
                                        mChatPpWindow.setOnDismissListener(() -> {
                                            root.removeView(blurView);
                                        });
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }
                }
            }
        }

        @Override
        public void onChangeInputText(String text) {
            if (mChatBottomView != null) {
                mChatBottomView.getmChatEdit().setText(text);
            }
        }

        @Override
        public void onCompDownVoice(ChatMessage message) {
            if (!isGroupChat() && message.getType() == TYPE_VOICE && !message.isMySend()) {
                if (message.getIsReadDel() && !TextUtils.isEmpty(message.getFilePath()) && !mFireVoiceMaps.containsKey(message.getFilePath())) {
                    mFireVoiceMaps.put(message.getFilePath(), message.getPacketId());
                }
            }
        }

        @Override
        public Bitmap onLoadBitmap(String key, int width, int height) {
            if (mCacheMap.containsKey(key)) {
                Bitmap bitmap = mCacheMap.get(key);
                if (bitmap != null && !bitmap.isRecycled()) {
                    return bitmap;
                } else {
                    return loadBitmapImage(key, width, height);
                }
            } else {
                return loadBitmapImage(key, width, height);
            }
        }

        public Bitmap loadBitmapImage(String key, int width, int height) {
            Bitmap bitmap = null;
            try {
                bitmap = BitmapUtil.decodeBitmapFromFile(key, width, height);
            } catch (Exception e) {
                LogUtils.log(key);
                Reporter.post("图片加载失败，", e);
            }
            if (bitmap == null) {
                return null;
            }
            mCacheMap.put(key, bitmap);
            return bitmap;
        }

        @Override
        public void onReplayClick(View v, AChatHolderInterface holder, ChatMessage message) {
            Log.e("xuan", "onReplayClick: " + holder.position);
            if (isShowMoreSelect) {
                message.isMoreSelected = !message.isMoreSelected;
                holder.setBoxSelect(message.isMoreSelected);
                return;
            }

            ChatMessage replayMessage = new ChatMessage(message.getObjectId());
            int index = -1;
            for (int i = 0; i < mChatMessages.size(); i++) {
                ChatMessage m = mChatMessages.get(i);
                if (TextUtils.equals(m.getPacketId(), replayMessage.getPacketId())) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                // 内存里有被回复消息的话直接处理，
                smoothScrollToPosition(index);
            } else {
                // 内存没有就回调出去查询数据库，
                if (mMessageEventListener != null) {
                    mMessageEventListener.onReplayClick(message);
                }
            }
        }
    }

    // 语音消息播放监听
    public class VoicePlayListener implements VoiceManager.VoicePlayListener {
        @Override
        public void onFinishPlay(String path) {

            VoiceViewHolder holder = aVoice.next(mCurClickPos, mChatMessages);
            if (holder != null) {
                mCurClickPos = holder.position;
                ChatMessage message = mChatMessages.get(mCurClickPos);
                holder.sendReadMessage(message);
                VoicePlayer.instance().playVoice(holder.voiceView);

                if (message.getIsReadDel()) {
                    EventBus.getDefault().post(new MessageEventClickFire("delay", message.getPacketId()));
                }
            }

            if (mFireVoiceMaps.containsKey(path)) {
                EventBus.getDefault().post(new MessageEventClickFire("delete", mFireVoiceMaps.get(path)));
                mFireVoiceMaps.remove(path);
            }
        }

        @Override
        public void onStopPlay(String path) {
            aVoice.remove(mCurClickPos);
            if (mFireVoiceMaps.containsKey(path)) {
                EventBus.getDefault().post(new MessageEventClickFire("delete", mFireVoiceMaps.get(path)));
                mFireVoiceMaps.remove(path);
            }
        }

        @Override
        public void onErrorPlay() {
        }
    }

    // 自动播放语音消息 工具类
    public class AutoVoiceModule {
        HashMap<Integer, VoiceViewHolder> data = new HashMap<>();
        HashMap<VoiceViewHolder, Integer> last = new HashMap<>();

        // 放入消息
        public void put(VoiceViewHolder key) {
            if (last.containsKey(key)) {
                int index = last.get(key);
                data.remove(index);
                last.put(key, key.position);
                data.put(key.position, key);
            } else {
                last.put(key, key.position);
                data.put(key.position, key);
            }
        }

        // 取出消息
        public VoiceViewHolder next(int position, List<ChatMessage> list) {
            if (position + 1 >= list.size()) {
                return null;
            }

            for (int i = position + 1; i < list.size(); i++) {
                ChatMessage message = list.get(i);
                if (message.getType() == TYPE_VOICE && !message.isMySend() && !message.isSendRead()) {
                    if (data.containsKey(i)) {
                        return data.get(i);
                    }
                }
            }
            return null;
        }

        // 删除消息
        public void remove(int position) {
            if (data.containsKey(position)) {
                last.remove(data.get(position));
                data.remove(position);
            }
        }
    }

    // 长按弹窗的点击事件监听
    public class ClickListener implements OnClickListener {
        private ChatMessage message;
        private int position;

        public ClickListener(ChatMessage message, int position) {
            this.message = message;
            this.position = position;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public void onClick(View v) {
            mChatPpWindow.dismiss();
            switch (v.getId()) {
                case R.id.item_chat_copy_tv:
                    // 复制
                    if (message.getIsReadDel()) {
                        Toast.makeText(mContext, R.string.tip_cannot_copy_burn, Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String s = StringUtils.replaceSpecialChar(message.getContent());
                    CharSequence charSequence = HtmlUtils.transform200SpanString(s.replaceAll("\n", "\r\n"), true);
                    // 获得剪切板管理者,复制文本内容
                    ClipboardManager cmb = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(charSequence);
                    break;
                case R.id.item_chat_relay_tv:
                    // 转发消息
                    if (message.getIsReadDel()) {
                        // 为阅后即焚类型的消息，不可转发
                        // Toast.makeText(mContext, "阅后即焚类型的消息不可以转发哦", Toast.LENGTH_SHORT).show();
                        Toast.makeText(mContext, InternationalizationHelper.getString("CANNOT_FORWARDED"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent intent = new Intent(mContext, InstantMessageActivity.class);
                    intent.putExtra("fromUserId", mToUserId);
                    intent.putExtra("messageId", message.getPacketId());
                    mContext.startActivity(intent);
                    ((Activity) mContext).finish();
                    break;
                case R.id.item_chat_collection_tv:
                    // 添加为表情
                    if (message.getIsReadDel()) {
                        Toast.makeText(mContext, R.string.tip_cannot_save_burn_image, Toast.LENGTH_SHORT).show();

                        return;
                    }
                    collectionEmotion(message, false);
                    break;
                case R.id.collection_other:
                    // 收藏
                    if (message.getIsReadDel()) {
                        Toast.makeText(mContext, R.string.tip_cannot_collect_burn, Toast.LENGTH_SHORT).show();

                        return;
                    }
                    collectionEmotion(message, true);
                    break;
                case R.id.item_chat_back_tv:
                    // 撤回消息
                    mMessageEventListener.onMessageBack(message, position);
                    break;
                case R.id.item_chat_replay_tv:
                    // 回复消息
                    mMessageEventListener.onMessageReplay(message);
                    break;
                case R.id.item_chat_del_tv:
                    // 删除
                    if (mCurChatType == ChatListType.COURSE) {
                        if (mMessageEventListener != null) {
                            mMessageEventListener.onMessageClick(message);
                        }
                    } else {
                        // 发送广播去界面更新
                        Intent broadcast = new Intent(Constants.CHAT_MESSAGE_DELETE_ACTION);
                        broadcast.putExtra(Constants.CHAT_REMOVE_MESSAGE_POSITION, position);
                        mContext.sendBroadcast(broadcast);
                    }
                    break;
                case R.id.item_chat_more_select:
                    // 多选
                    Intent showIntent = new Intent(Constants.SHOW_MORE_SELECT_MENU);
                    showIntent.putExtra(Constants.CHAT_SHOW_MESSAGE_POSITION, position);
                    mContext.sendBroadcast(showIntent);
                    break;
                case R.id.item_chat_edit:
                    mMessageEventListener.onEditMessage(message);
                    break;
                default:
                    break;
            }
        }
    }
}
