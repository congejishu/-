package com.ydd.zhichat.ui.me.uid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.UUidShop;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.ui.base.CoreManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.srain.cube.views.GridViewWithHeaderAndFooter;
import okhttp3.Call;

public class UidShopActivity extends BaseActivity implements View.OnClickListener {

    private UidGridViewAdapter mAdapter;
    private GridViewWithHeaderAndFooter mGridView;
    private List<UUidShop> mCurrentMembers = new ArrayList<>();
    private EditText tvEditText;

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 200) {
            GetList();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_me_uid_shop);
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mGridView = findViewById(R.id.grid_view);
        tvEditText = findViewById(R.id.tv_edit_text);
        tvEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String mContent = tvEditText.getText().toString();
                if(!mContent.isEmpty()) {
                    search(mContent);
                }else{
                    InitData();
                }
            }
        });

        mAdapter = new UidGridViewAdapter();
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //弹出一个对话框...
                UUidShop data = mCurrentMembers.get(position);
                UidInfoActivity.start(UidShopActivity.this,data);
            }
        });

        //mGridView.setOnRefreshListener();

        GetList();

    }

    private void InitData(){
        GetList();
    }

    public class UidGridViewHolder {
        TextView imageView;
        TextView memberName;

        UidGridViewHolder(View itemView) {
            imageView = itemView.findViewById(R.id.shop_uid_price);
            memberName = itemView.findViewById(R.id.shop_uid_text);
        }
    }


    /***
     * 获取靓号
     */
    public void GetList(){
        Map<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(MyApplication.getInstance()).accessToken);

        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).UID_SHOP_LIST)
                .params(params)
                .build()
                .execute(new ListCallback<UUidShop>(UUidShop.class) {

                    @Override
                    public void onResponse(ArrayResult<UUidShop> result) {
                        mCurrentMembers.clear();
                        mCurrentMembers.addAll(result.getData());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        int aaa = 0;
                    }
                });
    }

    /***
     * 搜索靓号
     * @param text
     */
    public void search(String text) {
        Map<String, String> params = new HashMap<>();
        params.put("access_token", CoreManager.requireSelfStatus(MyApplication.getInstance()).accessToken);
        params.put("search", text);

        HttpUtils.get().url(CoreManager.requireConfig(MyApplication.getInstance()).UID_SHOP_SEARCH)
                .params(params)
                .build()
                .execute(new ListCallback<UUidShop>(UUidShop.class) {

                    @Override
                    public void onResponse(ArrayResult<UUidShop> result) {
                        mCurrentMembers.clear();
                        mCurrentMembers.addAll(result.getData());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Call call, Exception e) {

                    }
                });
    }

    public class UidGridViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mCurrentMembers.size();
        }

        @Override
        public Object getItem(int position) {
            return mCurrentMembers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_uid_shop, parent, false);
                UidGridViewHolder vh = new UidGridViewHolder(convertView);
                convertView.setTag(vh);
            }

            UidGridViewHolder vh = (UidGridViewHolder) convertView.getTag();
            TextView imageView = vh.imageView;
            TextView memberName = vh.memberName;

            memberName.setText(mCurrentMembers.get(position).getUid());
            imageView.setText(mCurrentMembers.get(position).getPrice()+"");


            return convertView;
        }
    }



}
