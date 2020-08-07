package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class PushAct extends BasicAct {
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        act = this;

        if(StringUtil.isNull(getIntent().getStringExtra("type"))) {
            finishAffinity();
            startActivity(new Intent(this, SplashAct.class));
            finish();
        } else {
            Common.showToast(act, "새로운 기기에서 로그인 했습니다.");

            finishAffinity();
            finish();
        }
    }
}
