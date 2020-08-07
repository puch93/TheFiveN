package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityPayLikeBinding;
import kr.co.core.thefiven.databinding.ActivityPayMemberBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.BillingEntireManager;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.CustomApplication;
import kr.co.core.thefiven.utility.StringUtil;

public class PayMemberAct extends BasicAct implements View.OnClickListener {
    ActivityPayMemberBinding binding;
    public static Activity act;

    private View selectedView;
    BillingEntireManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_member, null);
        act = this;

        CustomApplication application = (CustomApplication) getApplication();
        manager = application.getManagerObject();

        /* set click listener */
        binding.flPay.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        binding.llMember01.setOnClickListener(this);
        binding.llMember02.setOnClickListener(this);
        binding.llMember03.setOnClickListener(this);
        binding.llMember04.setOnClickListener(this);

        /* 취소선 적용 */
        binding.tv01.setPaintFlags(binding.tv01.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.tv02.setPaintFlags(binding.tv02.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        binding.tv03.setPaintFlags(binding.tv03.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        /* set item tag */
        binding.llMember01.setTag(R.string.pay_subs_name, StringUtil.SUBS_01_NAME);
        binding.llMember01.setTag(R.string.pay_subs_code, StringUtil.SUBS_01_CODE);
        binding.llMember01.setTag(R.string.pay_subs_price, StringUtil.SUBS_01_PRICE);

        binding.llMember02.setTag(R.string.pay_subs_name, StringUtil.SUBS_02_NAME);
        binding.llMember02.setTag(R.string.pay_subs_code, StringUtil.SUBS_02_CODE);
        binding.llMember02.setTag(R.string.pay_subs_price, StringUtil.SUBS_02_PRICE);

        binding.llMember03.setTag(R.string.pay_subs_name, StringUtil.SUBS_03_NAME);
        binding.llMember03.setTag(R.string.pay_subs_code, StringUtil.SUBS_03_CODE);
        binding.llMember03.setTag(R.string.pay_subs_price, StringUtil.SUBS_03_PRICE);

        binding.llMember04.setTag(R.string.pay_subs_name, StringUtil.SUBS_04_NAME);
        binding.llMember04.setTag(R.string.pay_subs_code, StringUtil.SUBS_04_CODE);
        binding.llMember04.setTag(R.string.pay_subs_price, StringUtil.SUBS_04_PRICE);

        /* 첫 번째 아이템 선택 */
        binding.llMember01.performClick();

        checkPayMember();
    }

    private void checkPayMember() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_PAY_MEMBER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            /* 몇일간 방문했는지 세팅 */
                            //시간
                            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
                            Date date = null;
                            try {
                                date = dateFormat1.parse(StringUtil.getStr(jo, "matching_expiredate"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Calendar calendar_today = Calendar.getInstance();
                            Calendar calendar_from = Calendar.getInstance();
                            calendar_from.setTime(date);

                            final long today = calendar_today.getTimeInMillis() / 86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
                            final long from = calendar_from.getTimeInMillis() / 86400000;


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvPayMember.setText(String.valueOf(from-today));
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvPayMember.setText(String.valueOf(0));
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

        server.setTag("Check Pay Member");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    private void menuSelect(View selected) {
        binding.llMember01.setSelected(false);
        binding.llMember02.setSelected(false);
        binding.llMember03.setSelected(false);
        binding.llMember04.setSelected(false);

        selected.setSelected(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_member_01:
            case R.id.ll_member_02:
            case R.id.ll_member_03:
            case R.id.ll_member_04:
                selectedView = view;
                menuSelect(view);
                break;

            case R.id.fl_back:
                finish();
                break;

            case R.id.fl_pay:
                if (selectedView == null) {
                    Common.showToast(act, "결제할 아이템을 선택해주세요");
                } else {
//                    Common.showToast(act, "name: " + (String) selectedView.getTag(R.string.pay_subs_name) + "\n" +
//                            "code: " + (String) selectedView.getTag(R.string.pay_subs_code) + "\n" +
//                            "price: " + (String) selectedView.getTag(R.string.pay_subs_price)
//                    );

                    if (manager.getManager_state().equals("N")) {
                        Common.showToast(act, manager.getManager_state_message());
                    } else if (manager.getSubscription_state().equalsIgnoreCase("pending")) {
                        Common.showToast(act, "카드사 승인중인 결제가 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.");
                    } else {
                        manager.purchase((String) selectedView.getTag(R.string.pay_subs_code), false, act);
                    }
                }
                break;
        }
    }
}
