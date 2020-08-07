package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityExplanationBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class ExplanationAct extends BasicAct {
    ActivityExplanationBinding binding;
    Activity act;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_explanation, null);
        act = this;

        StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        type = getIntent().getStringExtra("type");

        switch (type) {
            case StringUtil.EXPLANATION_HELP:
                binding.tvTitle.setText("도움말");
                break;
            case StringUtil.EXPLANATION_FIVE:
                binding.tvTitle.setText("THE FIVE 란?");
                break;
            case StringUtil.EXPLANATION_24HOUR:
                binding.tvTitle.setText("24시간 감시체제");
                break;
            case StringUtil.EXPLANATION_SECURITY:
                binding.tvTitle.setText("안심의 보안체제");
                break;
            case StringUtil.EXPLANATION_GUIDE:
                binding.tvTitle.setText("안심/안전가이드");
                break;
            case StringUtil.EXPLANATION_COMMUNITY:
                binding.tvTitle.setText("커뮤니티 가이드라인에 대하여");
                break;
            case StringUtil.EXPLANATION_INTRODUCE:
                binding.tvTitle.setText("회사개요");
                break;
        }

        getExplanation();
    }

    private void getExplanation() {
        ReqBasic server = new ReqBasic(act, NetUrls.EXPLANATION_PAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        final JSONObject jo = new JSONObject(resultData.getResult());
                        StringUtil.logLargeString(jo.toString());

                        final JSONObject value = jo.getJSONObject("value");

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvExplanation.setText(StringUtil.getStr(value, "ci_text"));
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Explanation" + type);
        server.addParams("type", type);
        server.execute(true, false);
    }
}
