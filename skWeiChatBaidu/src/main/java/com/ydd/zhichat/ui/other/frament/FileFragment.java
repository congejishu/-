package com.ydd.zhichat.ui.other.frament;

import static com.ydd.zhichat.ui.tool.WebViewActivity.EXTRA_URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.message.ChatMessage;
import com.ydd.zhichat.bean.message.XmppMessage;
import com.ydd.zhichat.db.dao.FriendDao;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.ui.base.EasyFragment;
import com.ydd.zhichat.ui.message.ChatActivity;
import com.ydd.zhichat.ui.message.MucChatActivity;
import com.ydd.zhichat.ui.message.search.SearchDesignationContent;
import com.ydd.zhichat.ui.mucfile.DownManager;
import com.ydd.zhichat.ui.mucfile.MucFileDetails;
import com.ydd.zhichat.ui.mucfile.XfileUtils;
import com.ydd.zhichat.ui.mucfile.bean.MucFileBean;
import com.ydd.zhichat.ui.tool.WebViewActivity;
import com.ydd.zhichat.util.TimeUtils;
import com.ydd.zhichat.view.CircleImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ValidFragment")
public class FileFragment extends EasyFragment implements View.OnClickListener {

    private final List<ChatMessage> mChatMessage;
    private RecyclerView mRecyclerView;
    private DesignationContentAdapter mDesignationContentAdapter;
    protected Context mContext;
    private String mSearchObject;

    @SuppressLint("ValidFragment")
    public FileFragment(List<ChatMessage> mChatMessage,String SearchObject) {
        this.mChatMessage = mChatMessage;
        this.mSearchObject = SearchObject;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_item_viedio;
    }

    @Override
    protected void onActivityCreated(Bundle savedInstanceState, boolean createView) {
        if (createView) {
            initView();
            Update();
        }
    }

    private void initView() {
        mRecyclerView = findViewById(R.id.s_dest_content_rcy);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mDesignationContentAdapter = new DesignationContentAdapter(mChatMessage,this.getContext(),mSearchObject);
        mRecyclerView.setAdapter(mDesignationContentAdapter);
    }


    class DesignationContentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<ChatMessage> mChatMessageSource;
        protected Context mContext;
        private String mSearchObject;

        public DesignationContentAdapter(List<ChatMessage> chatMessages,Context mContext,String mSearchObject) {
            this.mChatMessageSource = chatMessages;
            if (mChatMessageSource == null) {
                mChatMessageSource = new ArrayList<>();
            }
            this.mContext = mContext;
            this.mSearchObject = mSearchObject;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new DesignationContentHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_designation, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            ChatMessage chatMessage = mChatMessageSource.get(i);
            Friend friend = FriendDao.getInstance().getFriend(CoreManager.requireSelf(mContext).getUserId(), chatMessage.getFromUserId());
            String name = chatMessage.getFromUserName();
            if (friend != null && !TextUtils.isEmpty(friend.getRemarkName())) {
                name = friend.getRemarkName();
            }

            AvatarHelper.getInstance().displayAvatar(name, chatMessage.getFromUserId(),
                    ((DesignationContentHolder) viewHolder).mAvatarIv, true);
            if (chatMessage.getType() == XmppMessage.TYPE_TRANSFER) {
                ((DesignationContentHolder) viewHolder).mNameTv.setText(getString(R.string.start_transfer, name));
            } else {
                ((DesignationContentHolder) viewHolder).mNameTv.setText(name);
            }
            ((DesignationContentHolder) viewHolder).mDateTv.setText(TimeUtils.getFriendlyTimeDesc(mContext,
                    chatMessage.getTimeSend()));
            if (chatMessage.getType() == XmppMessage.TYPE_FILE) {
                // 文件
                fillFileData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            } else if (chatMessage.getType() == XmppMessage.TYPE_LINK || chatMessage.getType() == XmppMessage.TYPE_SHARE_LINK) {
                // 链接
                fillLinkData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            } else if (chatMessage.getType() == XmppMessage.TYPE_RED || chatMessage.getType() == XmppMessage.TYPE_TRANSFER) {
                // 红包与转账
                fillRedTransferData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            }else if(chatMessage.getType() == XmppMessage.TYPE_IMAGE) {
                fillFileData(chatMessage, ((DesignationContentHolder) viewHolder).mAbstractLl, ((DesignationContentHolder) viewHolder).mAbstractLeftIv,
                        ((DesignationContentHolder) viewHolder).mAbstractTopTv, ((DesignationContentHolder) viewHolder).mAbstractBottomTv);
            }

        }

        @Override
        public int getItemCount() {
            return mChatMessageSource.size();
        }

        private void fillFileData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {
            String filePath = TextUtils.isEmpty(chatMessage.getFilePath()) ? chatMessage.getContent() : chatMessage.getFilePath();
            int index = filePath.lastIndexOf(".");
            String type = filePath.substring(index + 1).toLowerCase();
            int start = filePath.lastIndexOf("/");
            String fileName = filePath.substring(start + 1).toLowerCase();

            if (type.equals("png") || type.equals("jpg")) {
                Glide.with(mContext)
                        .load(filePath)
                        .error(R.drawable.image_download_fail_icon)
                        .into(iv);
            } else {
                AvatarHelper.getInstance().fillFileView(type, iv);
            }
            topTv.setText(fileName);
            bottomTv.setText(XfileUtils.fromatSize(chatMessage.getFileSize()));

            ll.setOnClickListener(v -> {
                MucFileBean mucFileBean = new MucFileBean();
                mucFileBean.setName(fileName);
                mucFileBean.setNickname(fileName);
                mucFileBean.setUrl(chatMessage.getContent());
                mucFileBean.setSize(chatMessage.getFileSize());
                mucFileBean.setState(DownManager.STATE_UNDOWNLOAD);
                mucFileBean.setType(XfileUtils.getFileType(type));
                Intent intent = new Intent(mContext, MucFileDetails.class);
                intent.putExtra("data", mucFileBean);
                startActivity(intent);
            });
        }

        private void fillLinkData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {
            if (chatMessage.getType() == XmppMessage.TYPE_LINK) {
                // 普通链接
                try {
                    JSONObject json = new JSONObject(chatMessage.getContent());
                    String linkTitle = json.getString("title");
                    String linkImage = json.getString("img");
                    String linkAddress = json.getString("url");

                    Glide.with(mContext)
                            .load(linkImage)
                            .error(R.drawable.browser)
                            .into(iv);
                    topTv.setText(linkTitle);

                    ll.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(EXTRA_URL, linkAddress);
                        mContext.startActivity(intent);
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                // SK ShareSDK 分享进来的链接
                try {
                    JSONObject json = new JSONObject(chatMessage.getObjectId());
                    String appName = json.getString("appName");
                    String appIcon = json.getString("appIcon");
                    String title = json.getString("title");
                    String subTitle = json.getString("subTitle");
                    String imageUrl = json.getString("imageUrl");
                    String linkShareAddress = json.getString("url");
                    String linkShareDownAppAddress = json.getString("downloadUrl");

                    if (TextUtils.isEmpty(appIcon) && TextUtils.isEmpty(imageUrl)) {
                        iv.setImageResource(R.drawable.browser);
                    } else if (TextUtils.isEmpty(imageUrl)) {
                        AvatarHelper.getInstance().displayUrl(appIcon, iv);
                    } else {
                        AvatarHelper.getInstance().displayUrl(imageUrl, iv);
                    }
                    topTv.setText(title);
                    bottomTv.setText(subTitle);

                    ll.setOnClickListener(v -> {
                        Intent intent = new Intent(mContext, WebViewActivity.class);
                        intent.putExtra(WebViewActivity.EXTRA_URL, linkShareAddress);
                        intent.putExtra(WebViewActivity.EXTRA_DOWNLOAD_URL, linkShareDownAppAddress);
                        mContext.startActivity(intent);
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void fillRedTransferData(ChatMessage chatMessage, LinearLayout ll, ImageView iv, TextView topTv, TextView bottomTv) {
            if (chatMessage.getType() == XmppMessage.TYPE_RED) {
                // 红包
                iv.setImageResource(R.drawable.ic_chat_hongbao);
                topTv.setText(chatMessage.getContent());
            } else {
                // 转账
                iv.setImageResource(R.drawable.ic_tip_transfer_money);
                topTv.setText("￥ " + chatMessage.getContent());
                bottomTv.setText(chatMessage.getFilePath());
            }

            ll.setOnClickListener(v -> {
                Friend friend = FriendDao.getInstance().getFriend(CoreManager.requireSelf(mContext).getUserId(), mSearchObject);
                if (friend != null) {
                    Intent intent = new Intent();
                    if (friend.getRoomFlag() == 0) { // 个人
                        intent.setClass(mContext, ChatActivity.class);
                        intent.putExtra(ChatActivity.FRIEND, friend);
                    } else {
                        intent.setClass(mContext, MucChatActivity.class);
                        intent.putExtra(AppConstant.EXTRA_USER_ID, friend.getUserId());
                        intent.putExtra(AppConstant.EXTRA_NICK_NAME, friend.getNickName());
                    }

                    intent.putExtra("isserch", true);
                    intent.putExtra("jilu_id", chatMessage.getDoubleTimeSend());
                    startActivity(intent);
                }
            });
        }
    }

    class DesignationContentHolder extends RecyclerView.ViewHolder {

        private CircleImageView mAvatarIv;
        private TextView mNameTv, mDateTv;

        private LinearLayout mAbstractLl;
        private ImageView mAbstractLeftIv;
        private TextView mAbstractTopTv, mAbstractBottomTv;

        public DesignationContentHolder(@NonNull View itemView) {
            super(itemView);
            mAvatarIv = itemView.findViewById(R.id.avatar_iv);
            mNameTv = itemView.findViewById(R.id.name_tv);
            mDateTv = itemView.findViewById(R.id.date_tv);
            mAbstractLl = itemView.findViewById(R.id.abstract_ll);
            mAbstractLeftIv = itemView.findViewById(R.id.abstract_left_iv);
            mAbstractTopTv = itemView.findViewById(R.id.abstract_top_tv);
            mAbstractBottomTv = itemView.findViewById(R.id.abstract_bottom_tv);
        }
    }

}