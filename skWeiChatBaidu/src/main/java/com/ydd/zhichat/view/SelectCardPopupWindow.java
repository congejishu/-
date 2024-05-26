package com.ydd.zhichat.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.Friend;
import com.ydd.zhichat.bean.User;
import com.ydd.zhichat.db.InternationalizationHelper;
import com.ydd.zhichat.db.dao.FriendDao;
import com.ydd.zhichat.helper.AvatarHelper;
import com.ydd.zhichat.ui.base.CoreManager;
import com.ydd.zhichat.util.AsyncUtils;
import com.ydd.zhichat.util.SkinUtils;
import com.ydd.zhichat.util.ViewHolder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 发送名片
 */
public class SelectCardPopupWindow extends PopupWindow {
    private View mMenuView;
    private EditText mEditText;

    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<Friend> oDatas = new ArrayList<>();
    private List<Friend> mCuttDatas = new ArrayList<>();

    private Map<String, Friend> mSelectPositions;

    private Context mContext;
    private SendCardS mSendCards;

    public SelectCardPopupWindow(FragmentActivity context, SendCardS SendCards) {
        super(context);
        this.mContext = context;
        this.mSendCards = SendCards;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.pop_send_card, null);
        mMenuView.findViewById(R.id.select_rl).setBackgroundColor(SkinUtils.getSkin(context).getAccentColor());

        mSelectPositions = new HashMap<>();

        //设置SelectRoomMemberPopupWindow的View
        this.setContentView(mMenuView);
        //设置SelectRoomMemberPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        //设置SelectRoomMemberPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        //设置SelectRoomMemberPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectRoomMemberPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Buttom_Popwindow);
        //实例化一个SelectRoomMemberPopupWindow颜色为白色
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.app_white));
        //设置SelectRoomMemberPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        initActionBar();
        initView();
    }

    private void initActionBar() {
        mMenuView.findViewById(R.id.title_iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        TextView tvTitle = (TextView) mMenuView.findViewById(R.id.tv_center_filter);
        tvTitle.setText(InternationalizationHelper.getString("SELECT_CONTANTS"));
        TextView tvTitleLeft = (TextView) mMenuView.findViewById(R.id.sure_btn);
        tvTitleLeft.setText(InternationalizationHelper.getString("JX_Confirm"));
        tvTitleLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                List<Friend> mFriendCardList = new ArrayList<>();
                for (String key : mSelectPositions.keySet()) {
                    Friend friend = mSelectPositions.get(key);
                    mFriendCardList.add(friend);
                }
                mSendCards.sendCardS(mFriendCardList);
            }
        });
    }

    private void initView() {
        mEditText = (EditText) mMenuView.findViewById(R.id.search_et);
        mEditText.setHint(InternationalizationHelper.getString("JX_Seach"));

        mListView = (ListView) mMenuView.findViewById(R.id.list_view);
        mAdapter = new ListViewAdapter();
        mListView.setAdapter(mAdapter);
        AsyncUtils.doAsync(this, contextAsyncContext -> {
            User self = CoreManager.requireSelf(mContext);
            oDatas = FriendDao.getInstance().getAllFriends(self.getUserId());
            Friend friend = new Friend();
            friend.setUserId(self.getUserId());
            friend.setNickName(self.getNickName());
            oDatas.add(friend); // 自己也可选

            mCuttDatas.addAll(oDatas);

            AsyncUtils.runOnUiThread(this, context -> {
                mAdapter.setData(mCuttDatas);
            });
        });

        mEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mCuttDatas.clear();
                String mContent = mEditText.getText().toString();
                if (TextUtils.isEmpty(mContent)) {
                    mCuttDatas = oDatas;
                } else {
                    for (int i = 0; i < oDatas.size(); i++) {
                        String name = !TextUtils.isEmpty(oDatas.get(i).getRemarkName()) ? oDatas.get(i).getRemarkName() : oDatas.get(i).getNickName();
                        if (name.contains(mContent)) {
                            // 符合搜索条件的好友
                            mCuttDatas.add(oDatas.get(i));
                        }
                    }
                }
                mAdapter.setData(mCuttDatas);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (hasSelected(position)) {
                    removeSelect(position);
                } else {
                    addSelect(position);
                }
            }
        });
    }

    private void addSelect(int position) {
        if (!hasSelected(position)) {
            Friend friend = mCuttDatas.get(position);
            mSelectPositions.put(friend.getUserId(), friend);
            mAdapter.notifyDataSetInvalidated();
        }
    }

    private boolean hasSelected(int position) {
        boolean b = false;
        Friend friend = mCuttDatas.get(position);
        if (mSelectPositions.containsKey(friend.getUserId())) {
            b = true;
        }
        return b;
    }

    private void removeSelect(int position) {
        Friend friend = mCuttDatas.get(position);
        mSelectPositions.put(friend.getUserId(), friend);
        if (mSelectPositions.containsKey(friend.getUserId())) {
            mSelectPositions.remove(friend.getUserId());
        }
        mAdapter.notifyDataSetInvalidated();
    }

    public interface SendCardS {
        void sendCardS(List<Friend> friends);
    }

    private class ListViewAdapter extends BaseAdapter {
        private List<Friend> mFriends;

        public ListViewAdapter() {
            mFriends = new ArrayList<>();
        }

        public void setData(List<Friend> mFriend) {
            mFriends = mFriend;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mFriends.size();
        }

        @Override
        public Object getItem(int position) {
            return mFriends.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.row_select_contacts, parent, false);
            }
            TextView mSortTv = ViewHolder.get(convertView, R.id.catagory_title);
            mSortTv.setVisibility(View.GONE);
            CheckBox checkBox = ViewHolder.get(convertView, R.id.check_box);
            HeadView avatarImg = ViewHolder.get(convertView, R.id.avatar_img);
            TextView userNameTv = ViewHolder.get(convertView, R.id.user_name_tv);

            Friend mFriend = mFriends.get(position);
            if (mFriend != null) {
                AvatarHelper.getInstance().displayAvatar(mFriend.getUserId(), avatarImg.getHeadImage(), true);
                userNameTv.setText(!TextUtils.isEmpty(mFriend.getRemarkName()) ? mFriend.getRemarkName() : mFriend.getNickName());

                checkBox.setChecked(false);
                if (mSelectPositions.containsKey(mFriend.getUserId())) {
                    checkBox.setChecked(true);
                }

                //VIP
                if (mFriend.getVip() != null && mFriend.getVip() > 0) {
                    switch (mFriend.getVip()) {
                        case 1: {
                            String myColorString = CoreManager.requireConfig(mContext).vip1TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    userNameTv.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        case 2: {
                            String myColorString = CoreManager.requireConfig(mContext).vip2TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    userNameTv.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                        case 3: {
                            String myColorString = CoreManager.requireConfig(mContext).vip3TextColo;
                            if (!myColorString.isEmpty()) {
                                Pattern colorPattern = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
                                Matcher m = colorPattern.matcher(myColorString);
                                boolean isColor = m.matches();
                                if (isColor) {
                                    userNameTv.setTextColor(Color.parseColor(myColorString));
                                }
                            }
                            break;
                        }
                    }

                }
            }
            return convertView;
        }
    }
}