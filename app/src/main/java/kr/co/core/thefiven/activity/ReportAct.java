package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONException;
import org.json.JSONObject;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.data.OtherProfileImageData;
import kr.co.core.thefiven.databinding.ActivityReportBinding;
import kr.co.core.thefiven.dialog.ListOtherDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class ReportAct extends BasicAct {
    ActivityReportBinding binding;
    Activity act;

    private String yidx, nick, gender;
    private OtherProfileImageData image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_report, null);
        act = this;

        /* set other data */
        yidx = getIntent().getStringExtra("yidx");
        image = (OtherProfileImageData) getIntent().getSerializableExtra("image");
        nick = getIntent().getStringExtra("nick");
        gender = getIntent().getStringExtra("gender");

        if (image.getProfile_img_ck().equalsIgnoreCase("Y")) {
            Glide.with(act)
                    .load(image.getProfile_img())
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(binding.ivProfileImg);
        } else if(image.getProfile_img_ck().equalsIgnoreCase("fail") || image.getProfile_img_ck().equalsIgnoreCase("N")){
            Glide.with(act)
                    .load(image.getProfile_img())
                    .centerCrop()
                    .transform(new BlurTransformation(10, 3), new CircleCrop())
                    .into(binding.ivProfileImg);
        } else {
            if(!StringUtil.isNull(gender)) {
                if (AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(binding.ivProfileImg);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(binding.ivProfileImg);
                }
            } else {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(binding.ivProfileImg);
            }
        }

        binding.tvNick.setText(nick);


        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.flReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.tvViolation01.length() == 0 || binding.tvViolation02.length() == 0) {
                    Common.showToast(act, "위반항목을 선택해주세요");
                } else if (binding.etContents.length() == 0) {
                    Common.showToast(act, "구체적인 이유를 입력해주세요");
                } else {
                    doReport();
                }
            }
        });

        binding.llViolate01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, ListOtherDlg.class);
                intent.putExtra("type", StringUtil.POPUP_REPORT01);
                intent.putExtra("data", binding.tvViolation01.getText().toString());
                startActivityForResult(intent, 1001);
            }
        });

        binding.llViolate02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, ListOtherDlg.class);
                intent.putExtra("type", StringUtil.POPUP_REPORT02);
                intent.putExtra("data", binding.tvViolation02.getText().toString());
                startActivityForResult(intent, 1001);
            }
        });
    }

    private void doReport() {
        ReqBasic server = new ReqBasic(act, NetUrls.REPORT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "신고가 등록되었습니다");
                            finish();
                        } else {

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

        server.setTag("Report");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.addParams("cate1", binding.tvViolation01.getText().toString());
        server.addParams("cate2", binding.tvViolation02.getText().toString());
        server.addParams("contents", binding.etContents.getText().toString());
        server.execute(true, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String value = data.getStringExtra("value");

            switch (data.getStringExtra("type")) {
                case "violation01":
                    binding.tvViolation01.setText(value);
                    break;
                case "violation02":
                    binding.tvViolation02.setText(value);
                    break;
            }
        }
    }
}
