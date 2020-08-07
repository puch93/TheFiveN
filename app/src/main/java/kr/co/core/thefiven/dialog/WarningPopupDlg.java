package kr.co.core.thefiven.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.utility.StringUtil;

public class WarningPopupDlg extends BasicDlg {
    Activity act;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        type = getIntent().getStringExtra("type");

        if (StringUtil.isNull(type)) {
            finish();
        }

        switch (type) {
            case StringUtil.DLG_BLOCK:
                setContentView(R.layout.dialog_warning_block);
                ((TextView)findViewById(R.id.tv_nick)).setText(getIntent().getStringExtra("nick"));
                break;

            case StringUtil.DLG_POINT_NONE:
                setContentView(R.layout.dialog_warning_point);
                break;
        }

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        (findViewById(R.id.tv_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        (findViewById(R.id.tv_ok)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
