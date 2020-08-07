package kr.co.core.thefiven.activity.gift;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Random;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.data.GiftListData;
import kr.co.core.thefiven.databinding.ActivityGiftProductDetailBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class GiftProductDetailAct extends BasicAct {
    ActivityGiftProductDetailBinding binding;
    Activity act;
    GiftListData data;
    String yidx, nick;
    String tr_id;

    String rmIdBuyCntFlagCd;
    String discountRate;
    String goldPrice;
    String mdCode;
    String vipDiscountRate;
    String discountPrice;
    String mmsGoodsImg;
    String limitDay;
    String content;
    String goodsImgB;
    String goodsTypeNm;
    String categoryName1;
    String vipPrice;
    String goodsName;
    String mmsReserveFlag;
    String goodsStateCd;
    String brandCode;
    String goldDiscountRate;
    String goodsNo;
    String platinumPrice;
    String brandName;
    String salePrice;
    String brandIconImg;
    String goodsDescImgWeb;
    String rmCntFlag;
    String goodsTypeCd;
    String platinumDiscountRate;
    String categorySeq1;
    String goodsCode;
    String goodsTypeDtlNm;
    String goodsImgS;
    String affiliate;
    String saleDateFlag;
    String realPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift_product_detail, null);
        act = this;

        yidx = getIntent().getStringExtra("yidx");
        nick = getIntent().getStringExtra("nick");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String dateTime = dateFormat.format(calendar.getTime());
        tr_id = "theFive_" + dateTime + "_" + new Random().nextInt(100000000);
        Log.i(StringUtil.TAG, "tr_id: " + tr_id);


        data = (GiftListData) getIntent().getSerializableExtra("data");

        Glide.with(act).load(data.getGoodsImgS()).into(binding.ivProduct);

        binding.tvBrandAndDate.setText(data.getBrandName() + " / 유효기한: " + data.getValidPrdDay() + "까지");
        binding.tvName.setText(data.getGoodsName());
        binding.tvTitle.setText(data.getGoodsName());
        binding.tvPoint.setText(data.getGoodsPrice());
        binding.tvContent.setText(data.getContent());

        binding.flBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyInfo();
            }
        });

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getItemDetail();
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
                            if (Integer.parseInt(StringUtil.getStr(job, "u_point")) < Integer.parseInt(data.getGoodsPrice().replace("P", ""))) {
                                Common.showToast(act, "포인트가 부족합니다");
                            } else {
                                showAlertBuy();
                            }
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

    private void showAlertBuy() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("기프티콘 구매");
        alertDialog.setMessage(nick + "님에게 해당 상품을 선물하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        buyGifticon();
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

    private void getItemDetail() {
        ReqBasic server = new ReqBasic(act, NetUrls.GIFT_ITEM_DETAIL) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "code").equalsIgnoreCase("0000") && StringUtil.isNull(StringUtil.getStr(jo, "message"))) {
                            JSONObject result = jo.getJSONObject("result");
                            JSONObject job = result.getJSONObject("goodsDetail");

                            rmIdBuyCntFlagCd = StringUtil.getStr(job, "rmIdBuyCntFlagCd");
                            discountRate = StringUtil.getStr(job, "discountRate");
                            goldPrice = StringUtil.getStr(job, "goldPrice");
                            mdCode = StringUtil.getStr(job, "mdCode");
                            vipDiscountRate = StringUtil.getStr(job, "vipDiscountRate");
                            discountPrice = StringUtil.getStr(job, "discountPrice");
                            mmsGoodsImg = StringUtil.getStr(job, "mmsGoodsImg");
                            limitDay = StringUtil.getStr(job, "limitDay");
                            content = StringUtil.getStr(job, "content");
                            goodsImgB = StringUtil.getStr(job, "goodsImgB");
                            goodsTypeNm = StringUtil.getStr(job, "goodsTypeNm");
                            categoryName1 = StringUtil.getStr(job, "categoryName1");
                            vipPrice = StringUtil.getStr(job, "vipPrice");
                            goodsName = StringUtil.getStr(job, "goodsName");
                            mmsReserveFlag = StringUtil.getStr(job, "mmsReserveFlag");
                            goodsStateCd = StringUtil.getStr(job, "goodsStateCd");
                            brandCode = StringUtil.getStr(job, "brandCode");
                            goldDiscountRate = StringUtil.getStr(job, "goldDiscountRate");
                            goodsNo = StringUtil.getStr(job, "goodsNo");
                            platinumPrice = StringUtil.getStr(job, "platinumPrice");
                            brandName = StringUtil.getStr(job, "brandName");
                            salePrice = StringUtil.getStr(job, "salePrice");
                            brandIconImg = StringUtil.getStr(job, "brandIconImg");
                            goodsDescImgWeb = StringUtil.getStr(job, "goodsDescImgWeb");
                            rmCntFlag = StringUtil.getStr(job, "rmCntFlag");
                            goodsTypeCd = StringUtil.getStr(job, "goodsTypeCd");
                            platinumDiscountRate = StringUtil.getStr(job, "platinumDiscountRate");
                            categorySeq1 = StringUtil.getStr(job, "categorySeq1");
                            goodsCode = StringUtil.getStr(job, "goodsCode");
                            goodsTypeDtlNm = StringUtil.getStr(job, "goodsTypeDtlNm");
                            goodsImgS = StringUtil.getStr(job, "goodsImgS");
                            affiliate = StringUtil.getStr(job, "affiliate");
                            saleDateFlag = StringUtil.getStr(job, "saleDateFlag");
                            realPrice = StringUtil.getStr(job, "realPrice");
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

        server.setTag("Goods Code");
        server.addParams("goods_code", data.getGoodsCode());
        server.execute(true, false);
    }

    /**
     * (Y:핀번호 수신, N:MMS, I:바코드이미지수신)
     */
    private void buyGifticon() {
        ReqBasic server = new ReqBasic(act, NetUrls.GIFT_COUPON_REQUEST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "code").equalsIgnoreCase("0000") && StringUtil.isNull(StringUtil.getStr(jo, "message"))) {
                            JSONObject result = jo.getJSONObject("result").getJSONObject("result");
                            String orderNum = StringUtil.getStr(result, "orderNo");
                            String pinNo = StringUtil.getStr(result, "pinNo");
                            String couponImgUrl = StringUtil.getStr(result, "couponImgUrl");

                            buyCoupon(couponImgUrl);
                        } else {
                            switch (StringUtil.getStr(jo, "code")) {
                                case "ERR0215":
                                    Common.showToast(act, "이미 구매한 상품입니다");
                                    finish();
                                    break;

                            }
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

        server.setTag("Gift Coupon Request");
        server.addParams("goods_code", goodsCode);
        server.addParams("tr_id", tr_id);
        server.addParams("order_no", "test");
        server.addParams("mms_msg", "test");
        server.addParams("mms_title", "test");
        server.addParams("callback_no", "01000000000");
        server.addParams("phone_no", "01000000000");
        server.addParams("template_id", "test");
        server.addParams("banner_id", "test");
        server.addParams("gubun", "I");
        server.execute(true, false);
    }

    private void buyCoupon(String couponImgUrl) {
        ReqBasic server = new ReqBasic(act, NetUrls.GIFT_BUY_COUPON) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "성공적으로 선물하였습니다");
                            finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
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

        server.setTag("Buy Item");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.addParams("real_point", data.getGoodsPrice().replace("P", ""));
        server.addParams("tr_id", tr_id);
        server.addParams("couponImgUrl", couponImgUrl);

        server.addParams("goodsNo", goodsNo);
        server.addParams("goodsCode", goodsCode);
        server.addParams("goodsName", goodsName);
        server.addParams("brandCode", brandCode);
        server.addParams("brandName", brandName);
        server.addParams("content", content);
        server.addParams("contentAddDesc", ""); // 없음
        server.addParams("goodsTypeCd", goodsTypeCd);
        server.addParams("goodstypeNm", goodsTypeNm);
        server.addParams("goodsImgS", goodsImgS);
        server.addParams("goodsImgB", goodsImgB);
        server.addParams("goodsDescImgWeb", goodsDescImgWeb);
        server.addParams("brandIconImg", brandIconImg);
        server.addParams("mmsGoodsImg", mmsGoodsImg);
        server.addParams("realPrice", realPrice);
        server.addParams("salePrice", salePrice);
        server.addParams("categorySeq1", categorySeq1);
        server.addParams("categoryName1", categoryName1);
        server.addParams("rmIdBuyCntFlagCd", rmIdBuyCntFlagCd);
        server.addParams("discountRate", discountRate);
        server.addParams("discountPrice", discountPrice);
        server.addParams("goodsStateCd", goodsStateCd);
        server.addParams("rmCntFlag", rmCntFlag);
        server.addParams("goodsTypeDtlNm", goodsTypeDtlNm);
        server.addParams("saleDateFlagCd", ""); // 없음
        server.addParams("saleDateFlag", saleDateFlag);
        server.addParams("mmsReserveFlag", mmsReserveFlag);
        server.addParams("limitday", limitDay);
        server.addParams("affilate", affiliate);
        server.execute(true, false);
    }

}
