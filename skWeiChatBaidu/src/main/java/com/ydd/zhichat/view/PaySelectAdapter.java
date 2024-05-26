package com.ydd.zhichat.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ydd.zhichat.R;
import com.ydd.zhichat.bean.PaySelectBean;

import java.util.List;

public class PaySelectAdapter extends RecyclerView.Adapter<PaySelectAdapter.ViewHolder> {
    private List<PaySelectBean> notesList;
    private Context context;

    public void setNotesList(List<PaySelectBean> notesList) {
        this.notesList = notesList;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvDes;
        TextView tvOldPrice;
        TextView tvNewPrice;
        ImageView ivRecommend;

        Button button;
        TextView itemNumber;
        LinearLayout llItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvDes = itemView.findViewById(R.id.tv_des);
            tvOldPrice = itemView.findViewById(R.id.tv_old_price);
            tvNewPrice = itemView.findViewById(R.id.tv_new_price);
            ivRecommend=itemView.findViewById(R.id.iv_recommond);
            llItem=itemView.findViewById(R.id.ll_item);

        }
    }

    public PaySelectAdapter(List<PaySelectBean> notesList, Context context) {
        this.notesList = notesList;
        this.context = context;

    }
    private  OnPlayClick onPlayClick;
    public  interface  OnPlayClick{
        void  OnPlayClick(int voiceBean);
    }

    public void setOnPlayClick(OnPlayClick onPlayClick) {
        this.onPlayClick = onPlayClick;
    }

    @SuppressLint("RestrictedApi")
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pay_select_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PaySelectBean paySelectBean = notesList.get(position);
        holder.tvName.setText(paySelectBean.getName());
        holder.tvDes.setText(paySelectBean.getDes());
        holder.tvNewPrice.setText("特惠价: ￥"+paySelectBean.getNewPrice());
        if (!TextUtils.isEmpty(paySelectBean.getOldPrice())){
            holder.tvOldPrice.setText("￥"+paySelectBean.getOldPrice());
            holder.tvOldPrice.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//下划线
            holder.tvOldPrice.getPaint().setAntiAlias(true);//抗锯齿
        }
        if (paySelectBean.isRecommond()){
            holder.ivRecommend.setVisibility(View.VISIBLE);
        }else {
            holder.ivRecommend.setVisibility(View.GONE);
        }
        if (paySelectBean.isSelect()){
            holder.llItem.setBackgroundResource(R.drawable.bg_select);
        }else {
            holder.llItem.setBackgroundResource(R.drawable.bg_unselect);
        }

        holder.llItem.setOnClickListener(v -> {
            if (null!=onPlayClick){
                onPlayClick.OnPlayClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }



}
