package kr.co.core.thefiven.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.data.RegImageData;
import kr.co.core.thefiven.databinding.ActivityPayLikeBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class PayLikeAct extends BasicAct implements View.OnClickListener {
    ActivityPayLikeBinding binding;
    Activity act;

    private View selectedView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_like, null);
        act = this;

        getMyInfo();

        /* set click listener */
        binding.llLike01.setOnClickListener(this);
        binding.llLike02.setOnClickListener(this);
        binding.llLike03.setOnClickListener(this);
        binding.llLike04.setOnClickListener(this);
        binding.llLike05.setOnClickListener(this);

        binding.flPay.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        binding.llLike01.setTag(StringUtil.LIKE_ITEM_01);
        binding.llLike02.setTag(StringUtil.LIKE_ITEM_02);
        binding.llLike03.setTag(StringUtil.LIKE_ITEM_03);
        binding.llLike04.setTag(StringUtil.LIKE_ITEM_04);
        binding.llLike05.setTag(StringUtil.LIKE_ITEM_05);

        binding.llLike01.performClick();
    }


    private void menuSelect(View selected) {
        binding.llLike01.setSelected(false);
        binding.llLike02.setSelected(false);
        binding.llLike03.setSelected(false);
        binding.llLike04.setSelected(false);
        binding.llLike05.setSelected(false);

        selected.setSelected(true);
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

                            final String like = StringUtil.getStr(job, "heart");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (StringUtil.isNull(like)) {
                                        binding.tvLike.setText(AppPreference.getProfilePref(act, AppPreference.PREF_LIKE));
                                    } else {
                                        AppPreference.setProfilePref(act, AppPreference.PREF_LIKE, like);
                                        binding.tvLike.setText(like);
                                    }

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvLike.setText(AppPreference.getProfilePref(act, AppPreference.PREF_LIKE));
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

    private void showAlertDialog() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("심쿵 구매");
        alertDialog.setMessage("선택한 아이템을 구매하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        buyLike();
                        dialog.cancel();
                    }
                });
        // cancel
        alertDialog.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    private void buyLike() {
        ReqBasic server = new ReqBasic(act, NetUrls.PAY_LIKE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "구매가 완료되었습니다");
                            finish();
                        } else {
                            Common.showToast(act, "구매를 완료하지 못헀습니다");
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

        server.setTag("Buy Like");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("icode", (String) selectedView.getTag());
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_like_01:
            case R.id.ll_like_02:
            case R.id.ll_like_03:
            case R.id.ll_like_04:
            case R.id.ll_like_05:
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
                    showAlertDialog();
                }
                break;
        }
    }
}
