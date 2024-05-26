package com.ydd.zhichat.util;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.roamer.slidelistview.SlideBaseAdapter;
import com.ydd.zhichat.MyApplication;
import com.ydd.zhichat.R;
import com.ydd.zhichat.ui.me.RecoverActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListEditorAdapter extends SlideBaseAdapter {
    private LayoutInflater mInflater;
    private List<RecoverActivity.recoverVo> mData;// 存储的EditText值
    public Map<String, String> editorValue = new HashMap<String, String>();//
    public RecoverActivity.RecoverV recoverV = null;
    public ListEditorAdapter recoverAdapter;

    public ListEditorAdapter(Context context, RecoverActivity.RecoverV recoverV) {
        super(context);
        mData = recoverV.list;
        this.recoverV = recoverV;
        mInflater = LayoutInflater.from(context);
        init();
    }

    // 初始化
    private void init() {
        editorValue.clear();
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private Integer index = -1;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        // convertView为null的时候初始化convertView。

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_recover, null);
            holder.title = convertView.findViewById(R.id.recover_tite);
            holder.check = convertView.findViewById(R.id.check);
            holder.value = (EditText) convertView
                    .findViewById(R.id.psw_edit);
            holder.value.setText(mData.get(position).text);
            holder.value.setTag(position);
            holder.check.setOnClickListener((res)->{
                Log.d("aaaa","eeeee");
                recoverV.recoverIndex = position;
                recoverAdapter.notifyDataSetChanged();
            });
            holder.value.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        index = (Integer) v.getTag();
                    }
                    return false;
                }
            });
            class MyTextWatcher implements TextWatcher {
                public MyTextWatcher(ViewHolder holder) {
                    mHolder = holder;
                }

                private ViewHolder mHolder;

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s != null ) {
                        int position = (Integer) mHolder.value.getTag();
                        mData.get(position).text = s.toString();
                        int aaa = 0;
                    }
                }
            }
            holder.value.addTextChangedListener(new MyTextWatcher(holder));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            holder.value.setTag(position);
        }
        Object value = null;
        holder.title.setText("自定义回复"+(position+1)+":");

        if (recoverV.recoverIndex == position) {
            holder.check.setBackground(MyApplication.getContext().getDrawable(R.drawable.ic_pay_yes));
        }else{
            holder.check.setBackground(MyApplication.getContext().getDrawable(R.drawable.ic_pay_no));
        }

        holder.value.clearFocus();
        if (index != -1 && index == position) {
            holder.value.requestFocus();
        }
        return convertView;
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

    public final class ViewHolder {
        public TextView title;
        public ImageView check;
        public EditText value;// ListView中的输入
    }
}
