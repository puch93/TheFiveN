package kr.co.core.thefiven.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.DialogLikeMessageBinding;
import kr.co.core.thefiven.databinding.DialogLikeMessageReplyBinding;
import kr.co.core.thefiven.databinding.DialogLikeMessageReplyBindingImpl;
import kr.co.core.thefiven.utility.Common;

public class LikeMessageReplyDlg extends BasicDlg {
    DialogLikeMessageReplyBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_like_message_reply, null);
        binding.tvMessage.setText(getIntent().getStringExtra("message"));
        binding.tvNick.setText(getIntent().getStringExtra("nick"));
        binding.tvAge.setText(getIntent().getStringExtra("age"));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog();
            }
        });

        binding.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.etContents.length() == 0) {
                    Common.showToast(act, "메시지를 입력해주세요");
                } else {
                    setResult(RESULT_OK, new Intent().putExtra("contents", binding.etContents.getText().toString()).putExtra("state", "yes"));
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        showAlertDialog();
    }

    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("심쿵 메시지");
        alertDialog.setMessage("거절시 해당 메시지는 더이상 확인할 수 없습니다. 거절하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_OK, new Intent().putExtra("contents", binding.etContents.getText().toString()).putExtra("state", "no"));
                        finish();

                        dialog.cancel();
                    }
                });
        // cancel
        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}
