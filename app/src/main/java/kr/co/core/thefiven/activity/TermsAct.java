package kr.co.core.thefiven.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityTermsBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class TermsAct extends BasicAct {
    ActivityTermsBinding binding;
    Activity act;

    private String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_terms, null);
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
            case StringUtil.TERMS_USE:
                binding.tvTitle.setText("이용약관");
                break;
            case StringUtil.TERMS_PRIVATE:
                binding.tvTitle.setText("개인정보 처리방침");
                break;
            case StringUtil.TERMS_PAY:
                binding.tvTitle.setText("자금결제법에 근거한 표시");
                break;
            case StringUtil.TERMS_TRADE:
                binding.tvTitle.setText("특정상거래법에 근거한 표시");
                break;
        }

        getTerms();
    }

    private void getTerms() {
        ReqBasic server = new ReqBasic(act, NetUrls.TERMS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        final JSONObject jo = new JSONObject(resultData.getResult());
                        StringUtil.logLargeString(jo.toString());

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.tvTemrs.setText(StringUtil.getStr(jo, type));
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

        server.setTag("Terms");
        server.execute(true, false);
    }
}
