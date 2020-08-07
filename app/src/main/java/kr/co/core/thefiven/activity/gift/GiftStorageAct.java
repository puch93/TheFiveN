package kr.co.core.thefiven.activity.gift;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.adapter.GiftStorageAdapter;
import kr.co.core.thefiven.data.GiftStorageData;
import kr.co.core.thefiven.databinding.ActivityGiftStorageBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AllOfDecoration;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class GiftStorageAct extends BasicAct {
    ActivityGiftStorageBinding binding;
    Activity act;
    GiftStorageAdapter adapter;
    ArrayList<GiftStorageData> list = new ArrayList<>();

    AppCompatDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift_storage, null);
        act = this;

        /* set loading dialog */
        progressDialog = new AppCompatDialog(act);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressDialog.setContentView(R.layout.dialog_loading);
        progressDialog.show();

        /* set RecyclerView */
        binding.rcvStorage.setLayoutManager(new LinearLayoutManager(act));
        binding.rcvStorage.setHasFixedSize(true);
        binding.rcvStorage.setItemViewCacheSize(20);
        adapter = new GiftStorageAdapter(act, list);
        binding.rcvStorage.setAdapter(adapter);
        binding.rcvStorage.addItemDecoration(new AllOfDecoration(this, "top10dp"));

        getStorage();

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void getStorage() {
        list = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.GIFT_STORAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        StringUtil.logLargeString(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            final JSONArray ja = jo.getJSONArray("giftshow");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvCount.setText(String.valueOf(ja.length()));
                                }
                            });


                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String p_name = StringUtil.getStr(job, "goodsName");
                                String couponImgUrl = StringUtil.getStr(job, "couponImgUrl");
                                String goodsImgS = StringUtil.getStr(job, "goodsImgS");
                                String tr_id = StringUtil.getStr(job, "tr_id");

                                if (i == ja.length() - 1) {
                                    getStorageDetail(new GiftStorageData(p_name, "", goodsImgS, couponImgUrl, tr_id), true);
                                } else {
                                    getStorageDetail(new GiftStorageData(p_name, "", goodsImgS, couponImgUrl, tr_id), false);
                                }
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
                                    adapter.setList(list);
                                }
                            });
                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
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

        server.setTag("Gift Storage");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    private void getStorageDetail(final GiftStorageData data, final boolean isLast) {
        ReqBasic server = new ReqBasic(act, NetUrls.GIFT_COUPON_DETAIL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        StringUtil.logLargeString(jo.toString());

                        if (StringUtil.getStr(jo, "code").equalsIgnoreCase("0000")) {
                            JSONArray ja = jo.getJSONArray("result").getJSONObject(0).getJSONArray("couponInfoList");
                            GiftStorageData dataTMP = data;

                            JSONObject job = ja.getJSONObject(0);
                            String validPrdEndDt = StringUtil.getStr(job, "validPrdEndDt");
                            String resultDate = validPrdEndDt.substring(0, 4) + "년 " + validPrdEndDt.substring(4,6) + "월 " + validPrdEndDt.substring(6,8) + "일";
                            dataTMP.setP_limit_date(resultDate);

                            list.add(dataTMP);
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }

                if(isLast) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            adapter.setList(list);
                        }
                    });
                }
            }
        };

        server.setTag("Gift Storage Detail");
        server.addParams("tr_id", data.getTr_id());
        server.execute(true, false);
    }
}
