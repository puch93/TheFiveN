package kr.co.core.thefiven.activity.myinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.adapter.InterestProfileAdapter;
import kr.co.core.thefiven.data.InterestSubData;
import kr.co.core.thefiven.databinding.ActivityMyInfoEditBinding;
import kr.co.core.thefiven.dialog.ListProfileDlg;
import kr.co.core.thefiven.dialog.ListProfileMultiDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class MyInfoEditAct extends BasicAct implements View.OnClickListener {
    ActivityMyInfoEditBinding binding;
    Activity act;
    ActionBar actionBar;

    private static final int INFO_DIALOG = 100;
    private static final int INTEREST = 101;
    private static final int IMAGE = 103;

    private View selectedView;

    /* 관심사 */
    private String[] array;
    private TypedArray array_drawable;
    public static HashMap<String, InterestSubData> interest_map = new HashMap<>();

    private InterestProfileAdapter adapter;
    private ArrayList<InterestSubData> interest_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_info_edit, null);
        act = this;

        Log.e(StringUtil.TAG, "test2: ");
        /* set interest data list and set recycler view*/
        array = getResources().getStringArray(R.array.array_interest);
        array_drawable = getResources().obtainTypedArray(R.array.array_interest_int);
        for (int i = 0; i < array.length; i++) {
            interest_map.put(array[i], new InterestSubData(array[i], array_drawable.getResourceId(i, 0), false));
        }
        array_drawable.recycle();

        LinearLayoutManager manager = new LinearLayoutManager(act);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        binding.rcvInterest.setLayoutManager(manager);
        binding.rcvInterest.setHasFixedSize(true);
        binding.rcvInterest.setItemViewCacheSize(20);
        adapter = new InterestProfileAdapter(act, interest_list);
        binding.rcvInterest.setAdapter(adapter);

        setLayout();

        getMyInfo();
    }


    private void getMyInfo() {
        interest_list = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.INFO_ME) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            final JSONObject job = jo.getJSONObject("value");

                            //프로필 사진관련
                            final String profile_img;
                            if(!StringUtil.isNull(StringUtil.getStr(job, "piimg"))) {
                                JSONArray img_array = job.getJSONArray("piimg");
                                JSONObject img_object = img_array.getJSONObject(img_array.length()-1);
                                profile_img = StringUtil.getStr(img_object, "pi_img");
                            } else {
                                profile_img = "";
                            }


                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 프로필 사진
                                    Glide.with(act).load(profile_img).centerCrop().transform(new CircleCrop()).into(binding.ivProfileImg);

                                    // 닉네임
                                    binding.tvNick.setText(StringUtil.getStr(job, "nick"));

                                    // 소개글
                                    binding.etIntro.setText(StringUtil.getStr(job, "intro"));


                                    // 직업
                                    binding.tvJob.setText(StringUtil.getStr(job, "job"));
                                    //연봉
                                    binding.tvSalary.setText(StringUtil.getStr(job, "salary"));
                                    //결혼이력
                                    binding.tvMarriage.setText(StringUtil.getStr(job, "marriage"));
                                    //성격
                                    binding.tvPersonality.setText(StringUtil.getStr(job, "personality"));
                                    //국적
                                    binding.tvNationality.setText(StringUtil.getStr(job, "nationality"));
                                    //혈액형
                                    binding.tvBlood.setText(StringUtil.getStr(job, "blood"));
                                    //거주지
                                    binding.tvLocation.setText(StringUtil.getStr(job, "location"));
                                    //학력
                                    binding.tvEdu.setText(StringUtil.getStr(job, "education"));
                                    //휴일
                                    binding.tvHoliday.setText(StringUtil.getStr(job, "holiday"));
                                    //형제자매
                                    binding.tvFamily.setText(StringUtil.getStr(job, "family"));
                                    //키
                                    binding.tvHeight.setText(StringUtil.getStr(job, "height"));
                                    //체형
                                    binding.tvBody.setText(StringUtil.getStr(job, "body"));
                                    //음주
                                    binding.tvDrink.setText(StringUtil.getStr(job, "drink"));
                                    //흡연
                                    binding.tvSmoke.setText(StringUtil.getStr(job, "smoke"));
                                }
                            });

                            // 관심사
                            String interest_tmp = StringUtil.getStr(job, "interest");
                            String[] interests = interest_tmp.split(",");
                            for (String interest : interests) {
                                InterestSubData data = interest_map.get(interest);
                                if (data != null) {
                                    data.setSelected(true);
                                    interest_list.add(data);
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(interest_list);
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

    private void setMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.EDIT_INFO) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "정상적으로 수정되었습니다");
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

        server.setTag("Set MyInfo");
        //필수
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        //선택
        server.addParams("job", StringUtil.isNull(binding.tvJob.getText().toString()) ? "none" : binding.tvJob.getText().toString());
        server.addParams("salary", StringUtil.isNull(binding.tvSalary.getText().toString()) ? "none" : binding.tvSalary.getText().toString());
        server.addParams("marriage", StringUtil.isNull(binding.tvMarriage.getText().toString()) ? "none" : binding.tvMarriage.getText().toString());
        server.addParams("personality", StringUtil.isNull(binding.tvPersonality.getText().toString()) ? "none" : binding.tvPersonality.getText().toString());
        server.addParams("nationality", StringUtil.isNull(binding.tvNationality.getText().toString()) ? "none" : binding.tvNationality.getText().toString());
        server.addParams("blood", StringUtil.isNull(binding.tvBlood.getText().toString()) ? "none" : binding.tvBlood.getText().toString());
        server.addParams("location", StringUtil.isNull(binding.tvLocation.getText().toString()) ? "none" : binding.tvLocation.getText().toString());
        server.addParams("education", StringUtil.isNull(binding.tvEdu.getText().toString()) ? "none" : binding.tvEdu.getText().toString());
        server.addParams("holiday", StringUtil.isNull(binding.tvHoliday.getText().toString()) ? "none" : binding.tvHoliday.getText().toString());
        server.addParams("family", StringUtil.isNull(binding.tvFamily.getText().toString()) ? "none" : binding.tvFamily.getText().toString());
        server.addParams("height", StringUtil.isNull(binding.tvHeight.getText().toString()) ? "none" : binding.tvHeight.getText().toString());
        server.addParams("body", StringUtil.isNull(binding.tvBody.getText().toString()) ? "none" : binding.tvBody.getText().toString());
        server.addParams("drink", StringUtil.isNull(binding.tvDrink.getText().toString()) ? "none" : binding.tvDrink.getText().toString());
        server.addParams("smoke", StringUtil.isNull(binding.tvSmoke.getText().toString()) ? "none" : binding.tvSmoke.getText().toString());
        server.addParams("intro", StringUtil.isNull(binding.etIntro.getText().toString()) ? "none" : binding.etIntro.getText().toString());
        server.execute(true, false);
    }


    private void setLayout() {
        /* set click listener */
        binding.flBack.setOnClickListener(this);
        binding.tvSave.setOnClickListener(this);

        binding.tvChangeInterest.setOnClickListener(this);
        binding.flChangeProfileImg.setOnClickListener(this);

        binding.tvJob.setOnClickListener(this);
        binding.tvSalary.setOnClickListener(this);
        binding.tvMarriage.setOnClickListener(this);
        binding.tvPersonality.setOnClickListener(this);
        binding.tvNationality.setOnClickListener(this);
        binding.tvBlood.setOnClickListener(this);
        binding.tvLocation.setOnClickListener(this);
        binding.tvEdu.setOnClickListener(this);
        binding.tvHoliday.setOnClickListener(this);
        binding.tvFamily.setOnClickListener(this);
        binding.tvHeight.setOnClickListener(this);
        binding.tvBody.setOnClickListener(this);
        binding.tvDrink.setOnClickListener(this);
        binding.tvSmoke.setOnClickListener(this);


        binding.tvJob.setTag(StringUtil.PROF_JOB);
        binding.tvSalary.setTag(StringUtil.PROF_SALARY);
        binding.tvMarriage.setTag(StringUtil.PROF_MARRIAGE);
        binding.tvPersonality.setTag(StringUtil.PROF_PERSONALITY);
        binding.tvNationality.setTag(StringUtil.PROF_NATIONALITY);
        binding.tvBlood.setTag(StringUtil.PROF_BLOOD);
        binding.tvLocation.setTag(StringUtil.PROF_LOCATION);
        binding.tvEdu.setTag(StringUtil.PROF_EDU);
        binding.tvHoliday.setTag(StringUtil.PROF_HOLIDAY);
        binding.tvFamily.setTag(StringUtil.PROF_FAMILY);
        binding.tvHeight.setTag(StringUtil.PROF_HEIGHT);
        binding.tvBody.setTag(StringUtil.PROF_BODY);
        binding.tvDrink.setTag(StringUtil.PROF_DRINK);
        binding.tvSmoke.setTag(StringUtil.PROF_SMOKE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == INFO_DIALOG) {
                String value = data.getStringExtra("value");

                if (!StringUtil.isNull(value)) {
                    if (value.equalsIgnoreCase(StringUtil.PROF_NO_DATA)) {
                        ((TextView) selectedView).setText(null);
                    } else {
                        ((TextView) selectedView).setText(value);
                    }
                }
            } else if(requestCode == INTEREST) {
                interest_list = (ArrayList<InterestSubData>) data.getSerializableExtra("list");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(interest_list);
                    }
                });
            } else if(requestCode == IMAGE) {
                getMyInfo();
            }
        }
    }


    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.tv_job:
            case R.id.tv_salary:
            case R.id.tv_marriage:
            case R.id.tv_nationality:
            case R.id.tv_blood:
            case R.id.tv_location:
            case R.id.tv_edu:
            case R.id.tv_holiday:
            case R.id.tv_family:
            case R.id.tv_height:
            case R.id.tv_body:
            case R.id.tv_drink:
            case R.id.tv_smoke:
                selectedView = view;

                intent = new Intent(act, ListProfileDlg.class);
                intent.putExtra("type", (String) view.getTag());
                intent.putExtra("data", ((TextView) view).getText().toString());
                startActivityForResult(intent, INFO_DIALOG);
                break;

            case R.id.tv_personality:
                selectedView = view;
                intent = new Intent(act, ListProfileMultiDlg.class);
                intent.putExtra("type", (String) view.getTag());
                intent.putExtra("data", ((TextView) view).getText().toString());
                startActivityForResult(intent, INFO_DIALOG);
                break;


            case R.id.fl_back:
                finish();
                break;

            case R.id.tv_save:
                setMyInfo();

//                if(binding.tvJob.length() == 0) {
//                    Common.showToast(act, "직업을 선택해주세요");
//                } else if(binding.tvSalary.length() == 0) {
//                    Common.showToast(act, "연봉을 선택해주세요");
//                } else if(binding.tvMarriage.length() == 0) {
//                    Common.showToast(act, "결혼이력을 선택해주세요");
//                } else if(binding.tvPersonality.length() == 0) {
//                    Common.showToast(act, "성격을 선택해주세요");
//                } else if(binding.tvNationality.length() == 0) {
//                    Common.showToast(act, "국적을 선택해주세요");
//                } else {
//                    setMyInfo();
//                }
                break;

            case R.id.tv_change_interest:
                startActivityForResult(new Intent(act, MyInfoInterestAct.class).putExtra("list", interest_list), INTEREST);
                break;

            case R.id.fl_change_profile_img:
                startActivityForResult(new Intent(act, MyInfoImageAct.class), IMAGE);
                break;
        }
    }
}
