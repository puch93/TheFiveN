package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityFindPasswordBinding;
import kr.co.core.thefiven.databinding.ActivityRegPasswordBinding;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class PasswordRegAct extends BasicAct {
    ActivityRegPasswordBinding binding;
    Activity act;

    private static final int SIMPLE_DIALOG = 101;
    private static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{7,13}.$", Pattern.CASE_INSENSITIVE);

    String email, phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_reg_password, null);
        act = this;

        email = getIntent().getStringExtra("email");
        phone = getIntent().getStringExtra("phone");

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
                Matcher matcher_pw = VALID_PASSWORD_REGEX.matcher(binding.etPassword.getText().toString());
                Matcher matcher_pw_confirm = VALID_PASSWORD_REGEX.matcher(binding.etPasswordConfirm.getText().toString());

                if(binding.etPassword.length() == 0 || binding.etPasswordConfirm.length() == 0 ||
                                !matcher_pw.find() || !matcher_pw_confirm.find()) {
                    Common.showToast(act, "비밀번호를 확인해주세요");
                } else if (!binding.etPassword.getText().toString().equalsIgnoreCase(binding.etPasswordConfirm.getText().toString())) {
                    Common.showToast(act, "비밀번호를 일치하지 않습니다");
                } else {
                    registerPw();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            finish();
        }
    }

    private void registerPw() {
        ReqBasic server = new ReqBasic(act, NetUrls.FIND_PW_REG) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            startActivityForResult(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_PASSWORD), SIMPLE_DIALOG);
                        } else {
                            Common.showToast(act, "비밀번호 변경에 실패했습니다");
                            finish();
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

        server.setTag("Reg Pw");
        server.addParams("id", email);
        server.addParams("phone", phone);
        server.addParams("pw", binding.etPassword.getText().toString());
        server.execute(true, false);
    }
}
