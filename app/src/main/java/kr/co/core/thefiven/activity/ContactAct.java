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
import kr.co.core.thefiven.databinding.ActivityContactBinding;
import kr.co.core.thefiven.databinding.ActivityReportBinding;
import kr.co.core.thefiven.dialog.ListOtherDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class ContactAct extends BasicAct {
    ActivityContactBinding binding;
    Activity act;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_contact, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.flOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(binding.tvCategory.length() == 0) {
                   Common.showToast(act, "문의 카테고리를 선택해주세요");
               } else if(binding.etContents.length() == 0) {
                   Common.showToast(act, "문의내용을 입력해주세요");
               } else if(binding.etEmail.length() == 0) {
                   Common.showToast(act, "이메일을 입력해주세요");
               } else {
                   Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.etEmail.getText().toString());
                   if (matcher.find()) {
                       doContact();
                   } else {
                       Common.showToast(act, "이메일을 확인해주세요");
                   }
               }
            }
        });

        binding.llCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, ListOtherDlg.class);
                intent.putExtra("type", StringUtil.POPUP_CONTACT);
                intent.putExtra("data", binding.tvCategory.getText().toString());
                startActivityForResult(intent, 1001);
            }
        });
    }

    private void doContact() {
        ReqBasic server = new ReqBasic(act, NetUrls.CONTACT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "성공적으로 등록되었습니다");
                            finish();
                        } else {
                            Common.showToast(act, "등록에 실패했습니다");
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

        server.setTag("Contact");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("cate", binding.tvCategory.getText().toString());
        server.addParams("contents", binding.etContents.getText().toString());
        server.addParams("email", binding.etEmail.getText().toString());
        server.execute(true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {
            String value = data.getStringExtra("value");

            switch (data.getStringExtra("type")) {
                case "contact":
                    binding.tvCategory.setText(value);
                    break;
            }
        }
    }
}
