package kr.co.core.thefiven.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityFindPasswordBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class PasswordFindAct extends BasicAct {
    ActivityFindPasswordBinding binding;
    Activity act;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PHONE_NUMBER_REGEX = Pattern.compile("^01(?:0|1|[6-9])(?:\\d{3}|\\d{4})\\d{4}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_password, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.flOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Matcher matcher_email = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.etEmail.getText().toString());
                Matcher matcher_number = VALID_PHONE_NUMBER_REGEX.matcher(binding.etNumber.getText().toString());

                if(binding.etEmail.length() == 0) {
                    Common.showToast(act, "이메일을 입력해주세요");
                } else if(binding.etNumber.length() == 0) {
                    Common.showToast(act, "전화번호를 입력해주세요");
                } else if (!matcher_email.find()) {
                    Common.showToast(act, "이메일을 확인해주세요");
                } else if (!matcher_number.find()) {
                    Common.showToast(act, "전화번호를 확인해주세요");
                } else {
                    findPw();
                }
            }
        });
    }

    private void findPw() {
        ReqBasic server = new ReqBasic(act, NetUrls.FIND_PW) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            startActivity(new Intent(act, PasswordRegAct.class)
                                    .putExtra("email", binding.etEmail.getText().toString())
                                    .putExtra("phone", binding.etNumber.getText().toString())
                                    );
                            Common.showToast(act, "변경할 비밀번호를 입력해주세요");
                            finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "value"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Find Pw");
        server.addParams("id", binding.etEmail.getText().toString());
        server.addParams("phone", binding.etNumber.getText().toString());
        server.execute(true, false);
    }
}
