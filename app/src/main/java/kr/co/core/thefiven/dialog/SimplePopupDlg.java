package kr.co.core.thefiven.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.utility.StringUtil;

public class SimplePopupDlg extends BasicDlg {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String type = getIntent().getStringExtra("type");

        if (StringUtil.isNull(type)) {
            finish();
        }

        switch (type) {
            case StringUtil.DLG_POINT:
                setContentView(R.layout.dialog_popup_profile_point);
                break;

            case StringUtil.DLG_BIRTH:
                setContentView(R.layout.dialog_popup_birth);
                break;

            case StringUtil.DLG_INTEREST:
                setContentView(R.layout.dialog_popup_interest);
                break;

            case StringUtil.DLG_INTRO:
                setContentView(R.layout.dialog_popup_intro);
                break;

            case StringUtil.DLG_CHECK:
                setContentView(R.layout.dialog_popup_check);
                break;

            case StringUtil.DLG_JOIN_COMPLETE:
                setContentView(R.layout.dialog_popup_join_complete);

                ((TextView) findViewById(R.id.tv_cgpms_code)).setText(getIntent().getStringExtra("code"));
                break;

            case StringUtil.DLG_PASSWORD:
                setContentView(R.layout.dialog_popup_password);
                break;
        }


        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        (findViewById(R.id.tv_confirm)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }
}
