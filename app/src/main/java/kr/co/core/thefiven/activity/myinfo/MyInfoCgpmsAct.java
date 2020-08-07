package kr.co.core.thefiven.activity.myinfo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.databinding.ActivityMyInfoCgpmsBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class MyInfoCgpmsAct extends BasicAct {
    ActivityMyInfoCgpmsBinding binding;
    Activity act;

    String cgpms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_info_cgpms, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* cgpms 정보설정 */
        cgpms = getIntent().getStringExtra("cgpms");
        binding.tvCgpmsKind01.setText(cgpms);

        String cgpms_title = cgpms.substring(cgpms.length()-2);
        switch (cgpms_title) {
            case "CP":
                Glide.with(act).load(R.drawable.pic_menu_2_cp).into(binding.ivCgpms);
                binding.tvCgpmsKind02.setText("CHILD PERSON");
                break;
            case "GP":
                Glide.with(act).load(R.drawable.pic_menu_2_gp).into(binding.ivCgpms);
                binding.tvCgpmsKind02.setText("GROWN UP PERSON");
                break;
            case "PP":
                Glide.with(act).load(R.drawable.pic_menu_2_pp).into(binding.ivCgpms);
                binding.tvCgpmsKind02.setText("PRIVATE PERSON");
                break;
            case "MP":
                Glide.with(act).load(R.drawable.pic_menu_2_mp).into(binding.ivCgpms);
                binding.tvCgpmsKind02.setText("MASOCHIST PERSON");
                break;
            case "SP":
                Glide.with(act).load(R.drawable.pic_menu_2_sp).into(binding.ivCgpms);
                binding.tvCgpmsKind02.setText("SADIST PERSON");
                break;
        }

        getCgpmsInfo();
    }

    private void getCgpmsInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.CGPMS_EXPLANATION) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        final JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvCgpmsContents.setText(StringUtil.getStr(jo, "description"));
                                }
                            });
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

        server.setTag("Cgpms Info");
        server.addParams("gender", AppPreference.getProfilePref(act, AppPreference.PREF_GENDER));
        server.addParams("cgpms", cgpms);
        server.execute(true, false);
    }
}
