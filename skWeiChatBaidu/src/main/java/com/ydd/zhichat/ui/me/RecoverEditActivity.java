package com.ydd.zhichat.ui.me;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.j256.ormlite.stmt.query.In;
import com.ydd.zhichat.AppConstant;
import com.ydd.zhichat.R;
import com.ydd.zhichat.ui.base.BaseActivity;
import com.ydd.zhichat.view.SkinTextView;

public class RecoverEditActivity extends BaseActivity {

    private int state = 0;//0新增加,1编辑
    private Integer Index = -1;
    EditText edit_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Index = getIntent().getIntExtra("Index", -1);
        state = getIntent().getIntExtra("state", 0);
        setContentView(R.layout.activity_recover_edit);
        initActionBar();
        initView();
    }

    private void initView() {
        edit_tv = findViewById(R.id.edit_tv);
        if (state == 1) {
            edit_tv.setText(RecoverActivity.recoverV.getList().get(Index).text);
        }
        findViewById(R.id.save_btn).setVisibility(View.GONE);
    }

    @SuppressLint("ResourceAsColor")
    private void initActionBar() {
        getSupportActionBar().hide();
        findViewById(R.id.tv_title_right).setVisibility(View.VISIBLE);
        ((SkinTextView)findViewById(R.id.tv_title_right)).setText("保存");
        ((SkinTextView)findViewById(R.id.tv_title_right)).setTextColor(getResources().getColor(R.color.blue));
        findViewById(R.id.tv_title_right).setOnClickListener((res)->{
            if (state == 1) {
                RecoverActivity.recoverV.getList().get(Index).text = edit_tv.getText().toString();
            } else {
                RecoverActivity.recoverVo recoverVo = new RecoverActivity.recoverVo();
                recoverVo.text = edit_tv.getText().toString();
                recoverVo.status = 0;
                RecoverActivity.recoverV.getList().add(recoverVo);
            }

            finishActivity(200);
            finish();
        });
        findViewById(R.id.iv_title_left).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView tvTitle = (TextView) findViewById(R.id.tv_title_center);
        tvTitle.setText(state == 0 ? "添加自动回复" : "编辑自动回复");
    }


}
