package kr.co.core.thefiven.activity.gift;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatDialog;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.adapter.GiftBrandAdapter;
import kr.co.core.thefiven.data.GiftListData;
import kr.co.core.thefiven.databinding.ActivityGiftBrandBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class GiftBrandAct extends BasicAct {
    ActivityGiftBrandBinding binding;
    Activity act;
    HashMap<String, ArrayList<GiftListData>> brandMap = new HashMap<>();
    ArrayList<String> brandMap_Key = new ArrayList<>();
    GiftBrandAdapter adapter;
    AppCompatDialog progressDialog;

    private String yidx, nick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift_brand, null);
        act = this;

        yidx = getIntent().getStringExtra("yidx");
        nick = getIntent().getStringExtra("nick");
        if (StringUtil.isNull(yidx) || StringUtil.isNull(nick)) {
            Common.showToastNetwork(act);
            finish();
        } else {
            binding.flBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });

            /* set loading dialog */
            progressDialog = new AppCompatDialog(act);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.show();

            /* set RecyclerView */
            binding.rcvBrand.setLayoutManager(new LinearLayoutManager(act));
            binding.rcvBrand.setHasFixedSize(true);
            binding.rcvBrand.setItemViewCacheSize(20);
            adapter = new GiftBrandAdapter(act, brandMap_Key, brandMap, yidx, nick);
            binding.rcvBrand.setAdapter(adapter);

            getMyInfo();
            getGiftList();
        }
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.INFO_ME) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            final JSONObject job = jo.getJSONObject("value");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvPointBalance.setText(StringUtil.getStr(job, "u_point"));
                                }
                            });

                        } else {
                            Common.showToastNetwork(act);
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


    /**
     * code: 0000 (정상)
     * message: null (정상)
     */
    private void getGiftList() {
        ReqBasic server = new ReqBasic(act, NetUrls.GIFT_LIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        if (StringUtil.getStr(jo, "code").equalsIgnoreCase("0000")) {
                            JSONObject result = jo.getJSONObject("result");
                            JSONArray ja = result.getJSONArray("goodsList");

                            Log.i(StringUtil.TAG, "gift list size: " + ja.length());
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                Log.i(StringUtil.TAG, "gift list[" + i + "]: " + job);

                                //브랜드명
                                String goodsCode = StringUtil.getStr(job, "goodsCode");
                                //브랜드명
                                String brandName = StringUtil.getStr(job, "brandName");
                                //상품이름
                                String goodsName = StringUtil.getStr(job, "goodsName");
                                //상품가격
                                String goodsPrice = StringUtil.getStr(job, "realPrice");
                                int goodsPriceInt = Integer.parseInt(goodsPrice);
                                if (goodsPriceInt < 1000) {
                                    continue;
                                } else {
                                    goodsPrice = calPrice(goodsPriceInt);
                                }
                                //유효기간
                                String validPrdDay = StringUtil.getStr(job, "validPrdDay");
                                //상품설명
                                String content = StringUtil.getStr(job, "content");
                                //상품 이미지
                                String goodsImgS = StringUtil.getStr(job, "goodsImgS");


                                //브랜드별 정리
                                ArrayList<GiftListData> tmpList;
                                if (brandMap.containsKey(brandName)) {
                                    tmpList = brandMap.get(brandName);
                                } else {
                                    tmpList = new ArrayList<>();
                                }
                                tmpList.add(new GiftListData(goodsCode, brandName, goodsName, goodsPrice, validPrdDay, content, goodsImgS));
                                brandMap.put(brandName, tmpList);
                            }

                            TreeMap<String, ArrayList<GiftListData>> tm = new TreeMap<>(brandMap);

                            //키값 오름차순 정렬(기본)
                            //Iterator<String> iteratorKey = tm.descendingKeySet().iterator(); //키값 내림차순 정렬
                            for (String key : tm.keySet()) {
                                brandMap_Key.add(key);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(brandMap_Key, brandMap);
                                    if (progressDialog.isShowing()) {
                                        progressDialog.dismiss();
                                    }
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

        server.setTag("Gift List");
        server.addParams("start", "1");
        server.addParams("size", "10000");
        server.execute(true, false);
    }


    /**
     * 수수료 없음
     * 1P = 1000원으로 계산
     * 1200원 이하 -> 내림
     * 1200원 초과 -> 올림
     */
    private String calPrice(int price) {
        int a = price / 1200;

        if (price % 1200 != 0) {
            a += 1;
        }

        return Integer.toString(a) + "P";
    }
}
