package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityCellPhoneAuthBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class CellPhoneAuthAct extends BasicAct implements View.OnClickListener {
    ActivityCellPhoneAuthBinding binding;
    Activity act;

    String phone = null;

    public static final int JOIN_RESULT = 1001;

    private String facebook_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cell_phone_auth, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        facebook_token = getIntent().getStringExtra("id");

        /* set click listener */
        binding.flBack.setOnClickListener(this);
        binding.tvRequestCheck.setOnClickListener(this);
        binding.tvAuthCheck.setOnClickListener(this);
    }

    private void doCheckCellNum() {
        ReqBasic server = new ReqBasic(act, NetUrls.AUTH_PHONE_REQUEST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if(StringUtil.getStr(jo, "result").equalsIgnoreCase("N")) {
                            Common.showToast(act, StringUtil.getStr(jo, "value"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    // 인증번호 요청성공
                    phone = binding.etCellNum.getText().toString();
                    Common.showToast(act, "인증번호가 요청되었습니다");
                    binding.etCellNum.setEnabled(false);
                }
            }
        };

        server.setTag("Request Phone Auth");
        server.addParams("phone", binding.etCellNum.getText().toString());
        server.execute(true, false);
    }

    private void doCheckAuthNum() {
        ReqBasic server = new ReqBasic(act, NetUrls.AUTH_PHONE_CHECK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS) || StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "인증되었습니다.");

                            if(StringUtil.isNull(facebook_token)) {
                                startActivityForResult(new Intent(act, JoinAct.class).putExtra("phone", phone), JOIN_RESULT);
                            } else {
                                startActivityForResult(new Intent(act, JoinAct.class).putExtra("phone", phone).putExtra("id", facebook_token), JOIN_RESULT);
                            }
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

        server.setTag("Check Phone Auth");
        server.addParams("phone", phone);
        server.addParams("auth_num", binding.etAuthNum.getText().toString());
        server.execute(true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            setResult(RESULT_OK);
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_request_check:
                if (binding.etCellNum.length() == 0) {
                    Common.showToast(act, "휴대폰번호를 입력해주세요");
                    return;
                } else if (!Common.checkCellnum(binding.etCellNum.getText().toString())) {
                    Common.showToast(act, "휴대폰번호를 확인해주세요");
                    return;
                }

                doCheckCellNum();
                break;

            case R.id.tv_auth_check:
                if (StringUtil.isNull(phone)) {
                    Common.showToast(act, "인증번호를 요청해주세요");
                    return;
                } else if (binding.etAuthNum.length() != 6) {
                    Common.showToast(act, "인증번호를 확인해주세요");
                    return;
                }
                doCheckAuthNum();
                break;

            case R.id.fl_back:
                finish();
                break;
        }
    }
}
