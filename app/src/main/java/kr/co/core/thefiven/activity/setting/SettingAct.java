package kr.co.core.thefiven.activity.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.activity.BlockAct;
import kr.co.core.thefiven.activity.ExplanationAct;
import kr.co.core.thefiven.activity.LeaveAct;
import kr.co.core.thefiven.activity.LoginAct;
import kr.co.core.thefiven.activity.TermsAct;
import kr.co.core.thefiven.databinding.ActivitySettingBinding;
import kr.co.core.thefiven.dialog.ReviewDialog;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class SettingAct extends BasicAct implements View.OnClickListener {
    ActivitySettingBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        /* set click listener */
        binding.llAlarm.setOnClickListener(this);
        binding.llVisitor.setOnClickListener(this);
        binding.llOffline.setOnClickListener(this);
        binding.llBlock.setOnClickListener(this);

        binding.llKnowPeople.setOnClickListener(this);
        binding.llGuideSecurity.setOnClickListener(this);
        binding.llGuideCommunity.setOnClickListener(this);
        binding.llOverview.setOnClickListener(this);

        binding.llTermUse.setOnClickListener(this);
        binding.llTermPrivate.setOnClickListener(this);
        binding.llTermTrade.setOnClickListener(this);
        binding.llTermPay.setOnClickListener(this);

        binding.llLeave.setOnClickListener(this);

        binding.llReview.setOnClickListener(this);
        binding.llExplanationWatch.setOnClickListener(this);
        binding.llExplanationSecurity.setOnClickListener(this);

        binding.flBack.setOnClickListener(this);
        binding.flLogout.setOnClickListener(this);



        binding.llTermUse.setTag(StringUtil.TERMS_USE);
        binding.llTermPrivate.setTag(StringUtil.TERMS_PRIVATE);
        binding.llTermTrade.setTag(StringUtil.TERMS_TRADE);
        binding.llTermPay.setTag(StringUtil.TERMS_PAY);
    }

    private void checkOfflinestate() {
        ReqBasic server = new ReqBasic(act, NetUrls.OFFLINE_STATE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        final String loginYN = StringUtil.getStr(jo, "onoff_mode");
                        if(!StringUtil.isNull(loginYN) && loginYN.equalsIgnoreCase("Y")) {
                            startActivity(new Intent(act, SettingOfflineAct.class).putExtra("isChecked", true));
                        } else {
                            startActivity(new Intent(act, SettingOfflineAct.class).putExtra("isChecked", false));
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

        server.setTag("Offline State");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_alarm:
                startActivity(new Intent(act, SettingAlarmAct.class));
                break;
            case R.id.ll_visitor:
                startActivity(new Intent(act, SettingVisitorAct.class));
                break;
            case R.id.ll_offline:
                checkOfflinestate();
                break;
            case R.id.ll_block:
                startActivity(new Intent(act, BlockAct.class));
                break;

            case R.id.ll_know_people:
//                startActivity(new Intent(act, SettingKnowPeopleAct.class));
                startActivity(new Intent(act, SettingKnowPeopleAct2.class));
                break;
            case R.id.ll_guide_security:
                startActivity(new Intent(act, ExplanationAct.class).putExtra("type", StringUtil.EXPLANATION_GUIDE));
                break;
            case R.id.ll_guide_community:
                startActivity(new Intent(act, ExplanationAct.class).putExtra("type", StringUtil.EXPLANATION_COMMUNITY));
                break;
            case R.id.ll_overview:
                startActivity(new Intent(act, ExplanationAct.class).putExtra("type", StringUtil.EXPLANATION_INTRODUCE));
                break;
            case R.id.ll_term_use:
            case R.id.ll_term_private:
            case R.id.ll_term_trade:
            case R.id.ll_term_pay:
                startActivity(new Intent(act, TermsAct.class).putExtra("type", (String) v.getTag()));
                break;
            case R.id.ll_leave:
                startActivity(new Intent(act, LeaveAct.class));
                break;

            case R.id.ll_review:
                startActivity(new Intent(act, ReviewDialog.class));
                break;
            case R.id.ll_explanation_watch:
                startActivity(new Intent(act, ExplanationAct.class).putExtra("type", StringUtil.EXPLANATION_24HOUR));
                break;
            case R.id.ll_explanation_security:
                startActivity(new Intent(act, ExplanationAct.class).putExtra("type", StringUtil.EXPLANATION_SECURITY));
                break;

            case R.id.fl_back:
                finish();
                break;
            case R.id.fl_logout:
                logout();
                AppPreference.setProfilePrefBool(act, AppPreference.PREF_AUTO_LOGIN_STATE, false);
                startActivity(new Intent(act, LoginAct.class));
                finishAffinity();
                break;


        }
    }
    private void getExplanation(String type) {
        ReqBasic server = new ReqBasic(act, NetUrls.EXPLANATION_PAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
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

        server.setTag("Explanation" + type);
        server.addParams("type", type);
        server.execute(true, false);
    }


    private void logout() {
        ReqBasic server = new ReqBasic(act, NetUrls.LOGOUT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {

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

        server.setTag("Logout");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}
