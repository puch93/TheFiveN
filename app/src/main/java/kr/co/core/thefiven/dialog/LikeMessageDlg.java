package kr.co.core.thefiven.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.DialogLikeMessageBinding;
import kr.co.core.thefiven.utility.Common;

public class LikeMessageDlg extends BasicDlg {
    DialogLikeMessageBinding binding;
    Activity act;

    private String type, yidx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_like_message, null);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        yidx = getIntent().getStringExtra("yidx");

        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.etContents.length() == 0) {
                    Common.showToast(act, "메시지를 입력해주세요");
                } else {
                    setResult(RESULT_OK, new Intent().putExtra("contents", binding.etContents.getText().toString()).putExtra("yidx", yidx));
                    finish();
                }
            }
        });
    }
}
