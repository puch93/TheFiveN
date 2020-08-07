package kr.co.core.thefiven.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityPayPointBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.BillingEntireManager;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.CustomApplication;
import kr.co.core.thefiven.utility.StringUtil;

public class PayPointAct extends BasicAct implements View.OnClickListener {
    ActivityPayPointBinding binding;
    public static Activity act;

    private View selectedView;
    BillingEntireManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_point, null);
        act = this;

        CustomApplication application = (CustomApplication) getApplication();
        manager = application.getManagerObject();

        getMyInfo();

        /* set click listener */
        binding.flPay.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        binding.llPoint01.setOnClickListener(this);
        binding.llPoint02.setOnClickListener(this);
        binding.llPoint03.setOnClickListener(this);
        binding.llPoint04.setOnClickListener(this);
        binding.llPoint05.setOnClickListener(this);
        binding.llPoint06.setOnClickListener(this);

        /* set item tag */
        binding.llPoint01.setTag(R.string.pay_item_name, StringUtil.POINT_01_NAME);
        binding.llPoint01.setTag(R.string.pay_item_code, StringUtil.POINT_01_CODE);
        binding.llPoint01.setTag(R.string.pay_item_price, StringUtil.POINT_01_PRICE);

        binding.llPoint02.setTag(R.string.pay_item_name, StringUtil.POINT_02_NAME);
        binding.llPoint02.setTag(R.string.pay_item_code, StringUtil.POINT_02_CODE);
        binding.llPoint02.setTag(R.string.pay_item_price, StringUtil.POINT_02_PRICE);

        binding.llPoint03.setTag(R.string.pay_item_name, StringUtil.POINT_03_NAME);
        binding.llPoint03.setTag(R.string.pay_item_code, StringUtil.POINT_03_CODE);
        binding.llPoint03.setTag(R.string.pay_item_price, StringUtil.POINT_03_PRICE);

        binding.llPoint04.setTag(R.string.pay_item_name, StringUtil.POINT_04_NAME);
        binding.llPoint04.setTag(R.string.pay_item_code, StringUtil.POINT_04_CODE);
        binding.llPoint04.setTag(R.string.pay_item_price, StringUtil.POINT_04_PRICE);

        binding.llPoint05.setTag(R.string.pay_item_name, StringUtil.POINT_05_NAME);
        binding.llPoint05.setTag(R.string.pay_item_code, StringUtil.POINT_05_CODE);
        binding.llPoint05.setTag(R.string.pay_item_price, StringUtil.POINT_05_PRICE);

        binding.llPoint06.setTag(R.string.pay_item_name, StringUtil.POINT_06_NAME);
        binding.llPoint06.setTag(R.string.pay_item_code, StringUtil.POINT_06_CODE);
        binding.llPoint06.setTag(R.string.pay_item_price, StringUtil.POINT_06_PRICE);

        /* 첫 번째 아이템 선택 */
        binding.llPoint01.performClick();
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_ME) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONObject job = jo.getJSONObject("value");
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, StringUtil.getStr(job, "p_image1"));

                            final String point = StringUtil.getStr(job, "u_point");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (StringUtil.isNull(point)) {
                                        binding.tvPoint.setText(AppPreference.getProfilePref(act, AppPreference.PREF_POINT));
                                    } else {
                                        AppPreference.setProfilePref(act, AppPreference.PREF_POINT, point);
                                        binding.tvPoint.setText(point);
                                    }

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvPoint.setText(AppPreference.getProfilePref(act, AppPreference.PREF_POINT));
                                }
                            });
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

        server.setTag("My Profile");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void menuSelect(View selected) {
        binding.llPoint01.setSelected(false);
        binding.llPoint02.setSelected(false);
        binding.llPoint03.setSelected(false);
        binding.llPoint04.setSelected(false);
        binding.llPoint05.setSelected(false);
        binding.llPoint06.setSelected(false);

        selected.setSelected(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_point_01:
            case R.id.ll_point_02:
            case R.id.ll_point_03:
            case R.id.ll_point_04:
            case R.id.ll_point_05:
            case R.id.ll_point_06:
                selectedView = view;
                menuSelect(view);
                break;

            case R.id.fl_pay:
                if(selectedView == null) {
                    Common.showToast(act, "결제할 아이템을 선택해주세요");
                } else {
//                    Common.showToast(act, "name: " + (String) selectedView.getTag(R.string.pay_item_name) + "\n" +
//                            "code: " + (String) selectedView.getTag(R.string.pay_item_code) + "\n" +
//                            "price: " + (String) selectedView.getTag(R.string.pay_item_price)
//                    );

                    if (manager.getManager_state().equals("N")) {
                        Common.showToast(act, manager.getManager_state_message());
                    } else if (manager.getInapp_state().equalsIgnoreCase("pending")) {
                        Common.showToast(act, "카드사 승인중인 결제가 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
                    } else {
                        manager.purchase((String) selectedView.getTag(R.string.pay_item_code), true, act);
                    }
                }
                break;

            case R.id.fl_back:
                finish();
                break;
        }
    }
}
