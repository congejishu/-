package com.ydd.zhichat.ui.me;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.roamer.slidelistview.SlideBaseAdapter;
import com.roamer.slidelistview.SlideListView;
import com.xuan.xuanhttplibrary.okhttp.HttpUtils;
import com.xuan.xuanhttplibrary.okhttp.callback.BaseCallback;
import com.xuan.xuanhttplibrary.okhttp.callback.ListCallback;
import com.xuan.xuanhttplibrary.okhttp.result.ArrayResult;
import com.xuan.xuanhttplibrary.okhttp.result.ObjectResult;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.collection.CollectionEvery;
import com.ydd.zhichat.bean.message.CourseBean;
import com.ydd.zhichat.course.CourseDateilsActivity;
import com.ydd.zhichat.course.LocalCourseActivity;
import com.ydd.zhichat.helper.DialogHelper;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.ui.other.BasicInfoActivity;
import com.ydd.zhichat.util.CommonAdapter;
import com.ydd.zhichat.util.CommonViewHolder;
import com.ydd.zhichat.util.ListEditorAdapter;
import com.ydd.zhichat.util.LocaleHelper;
import com.ydd.zhichat.util.ToastUtil;
import com.ydd.zhichat.util.ViewHolder;
import com.ydd.zhichat.view.ClearEditText;
import com.ydd.zhichat.view.PullToRefreshSlideListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;

public class RecoverActivity extends BaseActivity {

    private PullToRefreshSlideListView mPullToRefreshListView;
    private RecoverAdapter recoverAdapter;
    public static RecoverV recoverV = new RecoverV();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocaleHelper.setLocale(this, LocaleHelper.getLanguage(this));
        setContentView(R.layout.activity_recover_list);
        initActionBar();
        initView();
        LoadData();
    }

    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText("自动回复");
    }

    private List<Map<String, Object>> mCheckItemList = new ArrayList<Map<String, Object>>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initView() {

        recoverAdapter = new RecoverAdapter(this);

        mPullToRefreshListView = (PullToRefreshSlideListView) findViewById(R.id.pull_refresh_list);
        View emptyView = LayoutInflater.from(mContext).inflate(R.layout.layout_list_empty_view, null);
        mPullToRefreshListView.setEmptyView(emptyView);
        mPullToRefreshListView.getRefreshableView().setAdapter(recoverAdapter);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mPullToRefreshListView.setShowIndicator(false);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<SlideListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<SlideListView> refreshView) {
                //loadData();
            }
        });

        mPullToRefreshListView.getRefreshableView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = (int) id;
                recoverVo reVo = recoverV.list.get(position);
                if (reVo.System == 2) {
                    //增加
                    StartEdit(position, false);
                } else {
                    recoverV.recoverIndex = position;
                    reVo.status = 1;
                    if(recoverV.restoreStatue == 3) {
                        ToastUtil.showToast(mContext,"已开启,系统将自动为您回复好友的消息!");
                    }

                    if(reVo.System == 1) {
                        recoverV.restoreStatue = 3;
                        reVo.text="";
                        ToastUtil.showToast(mContext,"您已关闭自动回复功能!");
                    }else{
                        recoverV.restoreStatue = 1;
                    }
                }
                SaveData(true);
                recoverAdapter.notifyDataSetChanged();
                mPullToRefreshListView.onRefreshComplete();
                setListViewHeightBasedOnChildren(mPullToRefreshListView.getRefreshableView());
            }

        });


        mPullToRefreshListView.setAdapter(this.recoverAdapter);


    }

    private void StartEdit(int postion, boolean isEdit) {
        Intent intent = new Intent(mContext, RecoverEditActivity.class);
        intent.putExtra("Index", postion);
        intent.putExtra("state", isEdit ? 1 : 0);
        startActivityForResult(intent, 200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("onActivityResult:", resultCode + "");
        if (requestCode == 200) {
            SaveData(false);
        }
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();

        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;

        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        ViewGroup.LayoutParams params5 = mPullToRefreshListView.getLayoutParams();

        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+dp2px(this.mContext,50);

        params5.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1))+dp2px(this.mContext,0);

        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10); // 可删除
        //((ViewGroup.MarginLayoutParams) params5).setMargins(10, 10, 10, 10); // 可删除

        listView.setLayoutParams(params);
        mPullToRefreshListView.setLayoutParams(params5);
    }
    //像素转换dp到px
    private int dp2px(Context context,float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }

    public class RecoverAdapter extends SlideBaseAdapter {

        public RecoverAdapter(Context context) {
            super(context);
        }

        @Override
        public int getFrontViewId(int position) {
            return R.layout.item_recover;
        }

        @Override
        public int getLeftBackViewId(int position) {
            return 0;
        }

        @Override
        public int getRightBackViewId(int position) {
            return R.layout.row_item_delete;
        }

        @Override
        public int getCount() {
            return recoverV.getList().size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("ResourceAsColor")
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = createConvertView(position);
            }

            TextView delete_tv = ViewHolder.get(convertView, R.id.delete_tv);
            TextView psw_edit = ViewHolder.get(convertView, R.id.psw_edit);
            ImageView check = ViewHolder.get(convertView, R.id.check);
            psw_edit.setText(recoverV.list.get(position).text);
            check.setVisibility(View.VISIBLE);
            if (recoverV.recoverIndex == position) {
                check.setBackground(MyApplication.getContext().getDrawable(R.drawable.ic_pay_yes));
            } else {
                check.setBackground(MyApplication.getContext().getDrawable(R.drawable.ic_pay_no));
            }

            psw_edit.setTextColor(getResources().getColor(R.color.black_2));
            recoverVo vo = recoverV.list.get(position);
            TextView edit_tv = ViewHolder.get(convertView, R.id.edit_tv);
            if (recoverV.list.get(position).System == 1) {
                delete_tv.setVisibility(View.GONE);
                edit_tv.setVisibility(View.GONE);
                psw_edit.setText("无");
                //recoverV.restoreStatue = 3;
                //recoverV.restoreStatue = 0;
                psw_edit.setTextColor(getResources().getColor(R.color.black_2));
            } else if (recoverV.list.get(position).System == 2) {
                delete_tv.setVisibility(View.GONE);
                edit_tv.setVisibility(View.GONE);
                check.setVisibility(View.GONE);
                psw_edit.setTextColor(getResources().getColor(R.color.blue));
                psw_edit.setText("添加自动回复");
            } else {
                psw_edit.setTextColor(getResources().getColor(R.color.black_2));
                delete_tv.setVisibility(View.VISIBLE);
                delete_tv.setOnClickListener((res) -> {
                    delete(position);
                });
                edit_tv.setText("编辑");
                edit_tv.setVisibility(View.VISIBLE);
                edit_tv.setOnClickListener((res) -> {
                    StartEdit(position, true);
                });
            }

            return convertView;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SaveData(true);
    }

    Runnable RefreComplete = new Runnable() {
        @Override
        public void run() {
            mPullToRefreshListView.onRefreshComplete();
            setListViewHeightBasedOnChildren(mPullToRefreshListView.getRefreshableView());
        }
    };

    private void updateData() {

        List<recoverVo> list = new ArrayList<>();
        for (recoverVo resr : recoverV.getList()) {
            if (resr.System == 0) {
                list.add(resr);
            }
        }


        recoverV.getList().clear();
        recoverV.getList().addAll(list);

        recoverVo vo = new recoverVo();
        vo.text = "无";
        vo.status = 0;
        vo.System = 1;

        recoverVo vo5 = new recoverVo();
        vo5.text = "添加自动回复";
        vo5.status = 0;
        vo5.System = 2;
        recoverV.getList().add(vo);
        recoverV.getList().add(vo5);

    }

    private List<recoverVo> getData() {
        List<recoverVo> list = new ArrayList<>();
        for (recoverVo resr : recoverV.getList()) {
            if (resr.System == 0) {
                list.add(resr);
            }
        }

        return list;
    }

    private void LoadData() {
        DialogHelper.showDefaulteMessageProgressDialog(this);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);

        HttpUtils.get().url(coreManager.getConfig().GET_RECOVER)
                .params(params)
                .build()
                .execute(new BaseCallback<RecoverV>(RecoverV.class) {

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onResponse(ObjectResult<RecoverV> result) {
                        DialogHelper.dismissProgressDialog();
                        if (result.getResultCode() == 1) {
                            recoverV.recoverIndex = result.getData().recoverIndex;
                            recoverV.restoreStatue = result.getData().restoreStatue;
                            recoverV.getList().clear();
                            recoverV.getList().addAll(result.getData().list);
                            recoverVo vo = new recoverVo();
                            vo.text = "无";
                            vo.status = 0;
                            vo.System = 1;

                            recoverVo vo5 = new recoverVo();
                            vo5.text = "添加自动回复";
                            vo5.status = 0;
                            vo5.System = 2;
                            recoverV.getList().add(vo);
                            recoverV.getList().add(vo5);
                            recoverAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.showToast(mContext, result.getResultMsg());
                        }
                        mPullToRefreshListView.postDelayed(RefreComplete, 200);
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        DialogHelper.dismissProgressDialog();
                        ToastUtil.showErrorNet(mContext);
                        mPullToRefreshListView.postDelayed(RefreComplete, 200);
                    }
                });
    }

    private void SaveData(boolean isEnd) {
        //DialogHelper.showDefaulteMessageProgressDialog(this);
        Map<String, String> params = new HashMap<>();
        params.put("access_token", coreManager.getSelfStatus().accessToken);
        params.put("resList", JSON.toJSONString(getData()));
        params.put("recoverIndex", recoverV.recoverIndex.toString());
        params.put("restoreStatue", recoverV.restoreStatue.toString());

        HttpUtils.get().url(coreManager.getConfig().SET_RECOVER)
                .params(params)
                .build()
                .execute(new BaseCallback<RecoverV>(RecoverV.class) {

                    @Override
                    public void onResponse(ObjectResult<RecoverV> result) {
                        //DialogHelper.dismissProgressDialog();
                        int aaa = 0;
                        if(!isEnd) {
                            updateData();
                            recoverAdapter.notifyDataSetChanged();
                            mPullToRefreshListView.onRefreshComplete();
                            setListViewHeightBasedOnChildren(mPullToRefreshListView.getRefreshableView());
                        }
                        //ToastUtil.showToast(mContext, "保存成功");
                    }

                    @Override
                    public void onError(Call call, Exception e) {
                        //DialogHelper.dismissProgressDialog();
                        //ToastUtil.showErrorNet(mContext);
                    }
                });
    }

    public static class recoverVo {
        public String text = "";
        public Integer status = 0;
        public Integer System = 0;

        public recoverVo() {

        }

        public recoverVo(String text, Integer status) {
            this.text = text;
            this.status = status;
        }
    }

    public static class RecoverV implements java.io.Serializable {
        public List<recoverVo> list = new ArrayList<>();
        public Integer recoverIndex = 0;
        public Integer restoreStatue = 0;
        public Integer restoreStatue01 = 0;

        public Integer getRestoreStatue01() {
            return restoreStatue01;
        }

        public void setRestoreStatue01(Integer restoreStatue01) {
            this.restoreStatue01 = restoreStatue01;
        }

        public void setRestoreStatue(Integer restoreStatue) {
            this.restoreStatue = restoreStatue;
        }

        public Integer getRestoreStatue() {
            return restoreStatue;
        }

        public Integer getRecoverIndex() {
            return recoverIndex;
        }

        public List<recoverVo> getList() {
            return list;
        }

        public void setList(List<recoverVo> list) {
            this.list = list;
        }

        public void setRecoverIndex(Integer recoverIndex) {
            this.recoverIndex = recoverIndex;
        }

        public RecoverV() {

        }
    }

    private boolean delete(final int position) {
        if (recoverV.getList().remove(position) != null) {
            SaveData(false);
        }

        return true;
    }

}
