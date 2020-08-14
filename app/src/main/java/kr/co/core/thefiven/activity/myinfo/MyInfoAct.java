package kr.co.core.thefiven.activity.myinfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.activity.ContactAct;
import kr.co.core.thefiven.activity.ExplanationAct;
import kr.co.core.thefiven.activity.LikeMemberAct;
import kr.co.core.thefiven.activity.NoticeAct;
import kr.co.core.thefiven.activity.PayItemAct;
import kr.co.core.thefiven.activity.PayLikeAct;
import kr.co.core.thefiven.activity.PayPointAct;
import kr.co.core.thefiven.activity.TermsAct;
import kr.co.core.thefiven.activity.gift.GiftStorageAct;
import kr.co.core.thefiven.activity.setting.SettingAct;
import kr.co.core.thefiven.databinding.ActivityMyInfoBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class MyInfoAct extends BasicAct implements View.OnClickListener {
    ActivityMyInfoBinding binding;
    Activity act;

    private String cgpms_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_info, null);
        act = this;

        setLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMyInfo();
        getStorage();
    }

    private void getStorage() {
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
                                    if(ja.length() > 0) {
                                        binding.tvGiftCount.setVisibility(View.VISIBLE);
                                        binding.tvGiftCount.setText(String.valueOf(ja.length()));
                                    }
                                }
                            });
                        } else {
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        server.setTag("Gift Storage");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    /**
     * 검수완료: Y / 검수중: N
     * */
    private void checkImageState(ImageView imageView, String dataUrl, String state) {
        if(StringUtil.isNull(dataUrl)) {
            if(!StringUtil.isNull(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER))) {
                if (AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(imageView);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(imageView);
                }
            } else {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(imageView);
            }
        } else {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(imageView);
        }


        if (state.equalsIgnoreCase("Y")) {
            binding.ivProfileImgCk.setVisibility(View.GONE);
        } else if (state.equalsIgnoreCase("N")) {
            binding.ivProfileImgCk.setVisibility(View.VISIBLE);
            Glide.with(act).load(R.drawable.icon_confirm).into(binding.ivProfileImgCk);
        } else if (state.equalsIgnoreCase("fail")) {
            binding.ivProfileImgCk.setVisibility(View.VISIBLE);
            Glide.with(act).load(R.drawable.icon_fail).into(binding.ivProfileImgCk);
        } else {
            binding.ivProfileImgCk.setVisibility(View.GONE);
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

                            //프로필 사진관련
                            String profile_img = null;
                            String profile_img_ck = null;

                            if(StringUtil.isNull(StringUtil.getStr(job, "piimg"))) {
                                profile_img_ck = "NO";
                            } else {
                                JSONArray img_array = job.getJSONArray("piimg");
                                JSONObject img_object = img_array.getJSONObject(img_array.length() - 1);
                                profile_img = StringUtil.getStr(img_object, "pi_img");
                                profile_img_ck = StringUtil.getStr(img_object, "pi_img_chk");
                            }

                            checkImageState(binding.ivProfileImg, profile_img, profile_img_ck);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 닉네임
                                    binding.tvNick.setText(StringUtil.getProfileStr(job, "nick"));

                                    // 성별
                                    if(StringUtil.isNull(StringUtil.getStr(job, "gender"))) {
                                        Glide.with(act).load(R.drawable.icon_man).into(binding.ivGender);
                                    } else {
                                        Glide.with(act).load(StringUtil.getStr(job, "gender").equalsIgnoreCase("female") ? R.drawable.icon_woman : R.drawable.icon_man).into(binding.ivGender);
                                    }

                                    // 지역 나이
                                    binding.tvInfo.setText(StringUtil.getProfileStr(job, "location") + "거주 " + StringUtil.calcAge(StringUtil.getProfileStr(job, "birth")) + "세");

                                    // CGPMS 코드
                                    cgpms_code = StringUtil.getProfileStr(job, "cgpms");
                                    binding.tvCgpmsCode.setText(cgpms_code);

                                    // 잔여 포인트
//                                    binding.tvPointBalance.setText(StringUtil.getStr(job, "point"));
                                    binding.tvPointBalance.setText(StringUtil.getStr(job, "u_point"));

                                    // 잔여 심쿵
                                    binding.tvLikeBalance.setText(StringUtil.getStr(job, "heart"));
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

        server.setTag("My Profile");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    private void setLayout() {
        binding.flAddPhoto.setOnClickListener(this);
        binding.flEditProfile.setOnClickListener(this);

        binding.llNotice.setOnClickListener(this);
        binding.llPay.setOnClickListener(this);
        binding.llLike.setOnClickListener(this);
        binding.llSetting.setOnClickListener(this);
        binding.llHelp.setOnClickListener(this);
        binding.llQna.setOnClickListener(this);
        binding.llFive.setOnClickListener(this);
        binding.llCoupon.setOnClickListener(this);
        binding.llPrivate.setOnClickListener(this);

        binding.flSetting.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        binding.llCgpmsArea.setOnClickListener(this);
        binding.llPayLike.setOnClickListener(this);
        binding.llPayPoint.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_add_photo:
                startActivity(new Intent(act, MyInfoImageAct.class));
                break;

            case R.id.fl_edit_profile:
                startActivity(new Intent(act, MyInfoEditAct.class));
                break;

            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_pay_point:
                startActivity(new Intent(act, PayPointAct.class));
                break;

            case R.id.ll_pay_like:
                startActivity(new Intent(act, PayLikeAct.class));
                break;

            case R.id.ll_cgpms_area:
                if(cgpms_code.equalsIgnoreCase("미입력")) {
                    Common.showToast(act, "CGPMS 정보가 없습니다.");
                } else {
                    startActivity(new Intent(act, MyInfoCgpmsAct.class).putExtra("cgpms", cgpms_code));
                }
                break;

            case R.id.ll_help:
                startActivity(new Intent(act, ExplanationAct.class).putExtra("type", StringUtil.EXPLANATION_HELP));
                break;
            case R.id.ll_five:
                startActivity(new Intent(act, ExplanationAct.class).putExtra("type", StringUtil.EXPLANATION_FIVE));
                break;


            /* bottom menu */
            case R.id.ll_notice:
                startActivity(new Intent(act, NoticeAct.class));
                break;
            case R.id.ll_like:
                startActivity(new Intent(act, LikeMemberAct.class));
                break;
            case R.id.ll_coupon:
                startActivity(new Intent(act, GiftStorageAct.class));
                break;
            case R.id.fl_setting:
            case R.id.ll_setting:
                startActivity(new Intent(act, SettingAct.class));
                break;

            case R.id.ll_qna:
                startActivity(new Intent(act, ContactAct.class));
                break;

            case R.id.ll_pay:
                startActivity(new Intent(act, PayItemAct.class));
                break;

            case R.id.ll_private:
                startActivity(new Intent(act, TermsAct.class).putExtra("type", StringUtil.TERMS_PRIVATE));
                break;
        }
    }
}
