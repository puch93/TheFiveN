package kr.co.core.thefiven.dialog;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ContactAct;
import kr.co.core.thefiven.utility.Common;

public class ReviewDialog extends BasicDlg {
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        setContentView(R.layout.dialog_review);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        (findViewById(R.id.tv_qna)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(act, ContactAct.class));
                finish();
            }
        });

        (findViewById(R.id.tv_review)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=kr.co.core.thefiven"));
                startActivity(intent);
                finish();
            }
        });
    }
}
