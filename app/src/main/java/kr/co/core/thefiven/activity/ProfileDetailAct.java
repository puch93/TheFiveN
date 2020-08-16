package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.gift.GiftBrandAct;
import kr.co.core.thefiven.adapter.ImagePagerAdapter;
import kr.co.core.thefiven.adapter.InterestProfileAdapter;
import kr.co.core.thefiven.data.InterestSubData;
import kr.co.core.thefiven.data.OtherProfileImageData;
import kr.co.core.thefiven.databinding.ActivityProfileDetailBinding;
import kr.co.core.thefiven.dialog.CgpmsConfirmDlg;
import kr.co.core.thefiven.dialog.CgpmsPointDlg;
import kr.co.core.thefiven.dialog.LikeMessageDlg;
import kr.co.core.thefiven.dialog.LikeMessageReplyDlg;
import kr.co.core.thefiven.dialog.WarningPopupDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class ProfileDetailAct extends BasicAct implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ActivityProfileDetailBinding binding;
    Activity act;

    ImagePagerAdapter imagePagerAdapter;
    ArrayList<OtherProfileImageData> imageList = new ArrayList<>();
    FragmentManager fragmentManager;

    private static final int WARNING_POINT = 1001;
    private static final int WARNING_BLOCK = 1002;

    private String yidx, nick, age, from, gender;
    private OtherProfileImageData image;
    private String cgpms_code = "";
    private String cgpms_point = "";
    private String cgpms_matching_text = "";
    private String cgpms_description = "";
    private boolean like_state = false;
    private boolean like_double_state = false;
    private boolean like_msg_state = false;

    private static final int LIKE_MESSAGE = 101;
    private static final int LIKE_MESSAGE_REPLY = 102;

    /* 관심사 */
    private String[] array;
    private TypedArray array_drawable;
    public static HashMap<String, InterestSubData> interest_map = new HashMap<>();

    private InterestProfileAdapter adapter;
    private ArrayList<InterestSubData> interest_list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile_detail, null);
        act = this;

        yidx = getIntent().getStringExtra("yidx");
        from = getIntent().getStringExtra("from");

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


        /* set click listener */
        binding.ivGift.setOnClickListener(this);
        binding.ivCgpmsKind.setOnClickListener(this);
        binding.ivCgpmsPoint.setOnClickListener(this);
        binding.flMore.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);
        binding.flReport.setOnClickListener(this);
        binding.ivLike.setOnClickListener(this);
        binding.ivLikeMsg.setOnClickListener(this);


        /* set view pager height */
        binding.imagePager.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.imagePager.getMeasuredWidth();
                Log.e(StringUtil.TAG, "getMeasuredWidth: " + height);
                height = binding.imagePager.getWidth();
                Log.e(StringUtil.TAG, "getWidth: " + height);
                if (height <= 0) {
                    height = getResources().getDimensionPixelSize(R.dimen.profile_detail_default_height);
                }
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) binding.imagePager.getLayoutParams();
                params.height = height;
                binding.imagePager.setLayoutParams(params);
            }
        });

        /* set view pager (image) */
        fragmentManager = getSupportFragmentManager();
        imagePagerAdapter = new ImagePagerAdapter(fragmentManager, imageList);
        binding.imagePager.setAdapter(imagePagerAdapter);

        // 상대정보 가져오기
        getOtherInfo();

        if (!StringUtil.isNull(from)) {
            checkLikeMessage();
        }
    }

    private void checkLikeMessage() {
        ReqBasic server = new ReqBasic(act, NetUrls.LIKE_CONFIRM_MESSAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            String message = StringUtil.getStr(jo, "contents");
                            startActivityForResult(new Intent(act, LikeMessageReplyDlg.class)
                                            .putExtra("message", message)
                                            .putExtra("nick", nick)
                                            .putExtra("age", age)
                                    , LIKE_MESSAGE_REPLY);
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
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

        server.setTag("Check Like Msg");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.execute(true, false);
    }

    private void getOtherInfo() {
        interest_list = new ArrayList<>();
        imageList = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.INFO_OTHER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        final JSONObject jo = new JSONObject(resultData.getResult());

                        StringUtil.logLargeString(jo.toString());

                        if (jo.getString("result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            final JSONObject job = jo.getJSONObject("value");

                            gender = StringUtil.getStr(job, "gender");
                            imagePagerAdapter.setGender(gender);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // 닉네임
                                    nick = StringUtil.getProfileStr(job, "nick");
                                    binding.tvNick.setText(nick);
                                    // 성별
                                    if (StringUtil.isNull(StringUtil.getStr(job, "gender"))) {
                                        Glide.with(act).load(R.drawable.icon_man).into(binding.ivGender);
                                    } else {
                                        Glide.with(act).load(StringUtil.getStr(job, "gender").equalsIgnoreCase("female") ? R.drawable.icon_woman : R.drawable.icon_man).into(binding.ivGender);
                                    }

                                    // 지역 나이
                                    age = StringUtil.calcAge(StringUtil.getProfileStr(job, "birth"));
                                    binding.tvLocationAge.setText(StringUtil.getProfileStr(job, "location") + "거주 " + age + "세");
                                    // 소개글
                                    binding.tvIntro.setText(StringUtil.getProfileStr(job, "intro"));
                                    // 직업
                                    binding.tvJob.setText(StringUtil.getProfileStr(job, "job"));
                                    // 연봉
                                    binding.tvSalary.setText(StringUtil.getProfileStr(job, "salary"));
                                    // 결혼이력
                                    binding.tvMarriage.setText(StringUtil.getProfileStr(job, "marriage"));
                                    // 성격
                                    binding.tvPersonality.setText(StringUtil.getProfileStr(job, "personality"));
                                    // 국적
                                    binding.tvNationality.setText(StringUtil.getProfileStr(job, "nationality"));


                                    // 혈액형
                                    if (StringUtil.isNull(StringUtil.getStr(job, "blood")))
                                        binding.tvBlood.setText(StringUtil.getProfileStr(job, "blood"));
                                    else
                                        binding.tvBlood.setText(StringUtil.getProfileStr(job, "blood") + "형");

                                    // 학력
                                    binding.tvEdu.setText(StringUtil.getProfileStr(job, "education"));
                                    // 휴일
                                    binding.tvHoliday.setText(StringUtil.getProfileStr(job, "holiday"));
                                    // 형제자매
                                    binding.tvFamily.setText(StringUtil.getProfileStr(job, "family"));
                                    // 키
                                    binding.tvHeight.setText(StringUtil.getProfileStr(job, "height"));
                                    // 체형
                                    binding.tvBody.setText(StringUtil.getProfileStr(job, "body"));
                                    // 음주
                                    binding.tvDrink.setText(StringUtil.getProfileStr(job, "drink"));
                                    // 흡연
                                    binding.tvSmoke.setText(StringUtil.getProfileStr(job, "smoke"));
                                }
                            });

                            // 관심사
                            String interest_tmp = StringUtil.getStr(job, "interest");
                            if (!StringUtil.isNull(interest_tmp)) {
                                String[] interests = interest_tmp.split(",");
                                for (String interest : interests) {
                                    interest_list.add(interest_map.get(interest));
                                }

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        adapter.setList(interest_list);
                                    }
                                });
                            }

                            //프로필 사진관련

                            JSONArray img_array = job.getJSONArray("piimg");
                            if (img_array.length() == 0) {
                                String profile_img = null;
                                String profile_img_ck = "NO";
                                imageList.add(new OtherProfileImageData(profile_img, profile_img_ck));
                            } else {
                                String profile_img = null;
                                String profile_img_ck = null;

                                String profile_img_tmp = null;
                                String profile_img_ck_tmp = null;

                                // null 이 아닌부분 넣기
                                for (int i = img_array.length() - 1; i >= 0; i--) {
                                    JSONObject img_object = img_array.getJSONObject(i);
                                    profile_img = StringUtil.getStr(img_object, "pi_img");
                                    profile_img_ck = StringUtil.getStr(img_object, "pi_img_chk");

                                    if(!StringUtil.isNull(profile_img)) {
                                        break;
                                    }
                                }


                                // 검수된 사진 우선표기
                                for (int i = img_array.length() - 1; i >= 0; i--) {
                                    JSONObject img_object = img_array.getJSONObject(i);
                                    profile_img_tmp = StringUtil.getStr(img_object, "pi_img");
                                    profile_img_ck_tmp = StringUtil.getStr(img_object, "pi_img_chk");

                                    if(!StringUtil.isNull(profile_img_tmp)) {
                                        if (profile_img_ck_tmp.equalsIgnoreCase("Y")) {
                                            profile_img = profile_img_tmp;
                                            profile_img_ck = profile_img_ck_tmp;
                                            break;
                                        }
                                    }
                                }

                                image = new OtherProfileImageData(profile_img, profile_img_ck);


                                imageList.add(image);

                                for (int i = img_array.length() - 1; i >= 0; i--) {
                                    JSONObject img_object = img_array.getJSONObject(i);
                                    profile_img_tmp = StringUtil.getStr(img_object, "pi_img");
                                    profile_img_ck_tmp = StringUtil.getStr(img_object, "pi_img_chk");

                                    if(!StringUtil.isNull(profile_img_tmp)) {
                                        if(!profile_img_tmp.equalsIgnoreCase(image.getProfile_img())) {
                                            imageList.add(new OtherProfileImageData(profile_img_tmp, profile_img_ck_tmp));
                                        }
                                    }
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i(StringUtil.TAG, "end: 22");
                                    imagePagerAdapter.setList(imageList);
                                    binding.pageIndicator.attachTo(binding.imagePager);
                                }
                            });


                            // CGPMS
                            cgpms_code = StringUtil.getStr(job, "cgpms");
                            cgpms_point = StringUtil.getStr(job, "matching_point");


                            // 심쿵, 심쿵x2, 심쿵메시지
                            like_state = StringUtil.getStr(job, "single_heartattack").equalsIgnoreCase("Y");
                            like_double_state = StringUtil.getStr(job, "double_heartattack").equalsIgnoreCase("Y");
                            like_msg_state = StringUtil.getStr(job, "message_heartattack").equalsIgnoreCase("Y");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (like_double_state) {
                                        Glide.with(act).load(R.drawable.btn_profile_double_heart_on).into(binding.ivLike);
                                    } else if (like_state) {
                                        Glide.with(act).load(R.drawable.btn_profile_double_heart_off).into(binding.ivLike);
                                    } else {
                                        Glide.with(act).load(R.drawable.btn_profile_heart_shake_off).into(binding.ivLike);
                                    }

                                    binding.ivLikeMsg.setSelected(like_msg_state);
                                }
                            });


                            //CGPMS 궁합내용
                            cgpms_matching_text = StringUtil.getStr(job, "cp_pointtext");

                            //CGPMS 코드내용
                            cgpms_description = StringUtil.getStr(job, "cgpms_description");
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

        server.setTag("Other Info");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.addParams("read", AppPreference.getAlarmPref(act, AppPreference.ALARM_VISIT) ? "Y" : "N");
        server.execute(true, false);
    }


    private void doLike(final String type) {
        ReqBasic server = new ReqBasic(act, type) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (type.equalsIgnoreCase(NetUrls.DO_LIKE)) {
                                        like_state = true;
                                        Glide.with(act).load(R.drawable.btn_double_heart_off).into(binding.ivLike);
                                        Common.showToastLike(act);
                                    } else {
                                        like_double_state = true;
                                        Glide.with(act).load(R.drawable.btn_profile_double_heart_on).into(binding.ivLike);
                                        Common.showToastLikeDouble(act);
                                    }
                                }
                            });
                        } else {
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

        if (type.equalsIgnoreCase(NetUrls.DO_LIKE)) {
            server.setTag("Like");
        } else {
            server.setTag("Like Double");
        }
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.execute(true, false);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.block:
                startActivityForResult(new Intent(act, WarningPopupDlg.class).putExtra("type", StringUtil.DLG_BLOCK).putExtra("nick", nick), WARNING_BLOCK);
                return true;

            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case WARNING_POINT:
                    startActivity(new Intent(act, PayPointAct.class));
                    break;

                case WARNING_BLOCK:
                    doBlock();
                    break;

                case LIKE_MESSAGE:
                    checkRoom(false, data.getStringExtra("contents"), "");
                    break;

                case LIKE_MESSAGE_REPLY:
                    checkRoom(true, data.getStringExtra("contents"), data.getStringExtra("state"));
                    break;
            }
        }
    }

    private void checkRoom(final boolean isReply, final String contents, final String state) {
        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_ROOM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            if (isReply) {
                                Common.showToast(act, "채팅방이 생성되어 있어, 답변을 보내실 수 없습니다");
                            } else {
                                Common.showToast(act, "채팅방이 생성되어 있어, 메시지를 보내실 수 없습니다");
                            }
                        } else {
                            if (isReply) {
                                sendLikeMessageReply(contents, state);
                            } else {
                                showAlertLikeMessage(contents);
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

        server.setTag("Check Room");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.execute(true, false);
    }

    private void showAlertLikeMessage(final String contents) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("심쿵 메시지 보내기");
        alertDialog.setMessage("2P 결제 후 보내실 수 있습니다. 결제하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendLikeMessage(contents);

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


    private void sendLikeMessage(String contents) {
        ReqBasic server = new ReqBasic(act, NetUrls.LIKE_SEND_MESSAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "전송 완료되었습니다");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    like_msg_state = true;
                                    binding.ivLikeMsg.setSelected(true);
                                }
                            });
                        } else {
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

        server.setTag("Like Msg Send");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.addParams("contents", contents);
        server.execute(true, false);
    }

    private void sendLikeMessageReply(String contents, String state) {
        ReqBasic server = new ReqBasic(act, NetUrls.LIKE_REPLY_MESSAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "전송 완료되었습니다");
                        } else {
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

        server.setTag("Like Msg Reply");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.addParams("state", state);
        server.addParams("contents", contents);
        server.execute(true, false);
    }

    private void doBlock() {
        ReqBasic server = new ReqBasic(act, NetUrls.BLOCK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            AppPreference.setMemberStateAll(act);
                            Common.showToast(act, "차단 되었습니다");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "comment"));
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

        server.setTag("Block");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.execute(true, false);
    }

    // CGPMS 1 Point 결제
    private void payCgpms(final String type) {
        ReqBasic server = new ReqBasic(act, NetUrls.CONFIRM_OTHER_CGPMS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            if (type.equalsIgnoreCase("type")) {
                                startActivity(new Intent(act, CgpmsConfirmDlg.class)
                                        .putExtra("nick", nick)
                                        .putExtra("code", cgpms_code)
                                        .putExtra("cgpms_description", cgpms_description));
                            } else {
                                startActivity(new Intent(act, CgpmsPointDlg.class)
                                        .putExtra("code", cgpms_code.substring(cgpms_code.length() - 2))
                                        .putExtra("point", cgpms_point)
                                        .putExtra("explanation", cgpms_matching_text));
                            }
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            startActivityForResult(new Intent(act, WarningPopupDlg.class).putExtra("type", StringUtil.DLG_POINT_NONE), WARNING_POINT);
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

        server.setTag("Pay Cgpms");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.addParams("value", type);
        server.execute(true, false);
    }

    // CGPMS 1 Point 결제 했는지 확인
    private void check_1Point_pay(final String type) {
        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_1POINT_PAY) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            if (type.equalsIgnoreCase("type")) {
                                startActivity(new Intent(act, CgpmsConfirmDlg.class)
                                        .putExtra("nick", nick)
                                        .putExtra("code", cgpms_code)
                                        .putExtra("cgpms_description", cgpms_description));
                            } else {
                                startActivity(new Intent(act, CgpmsPointDlg.class)
                                        .putExtra("code", cgpms_code.substring(cgpms_code.length() - 2))
                                        .putExtra("point", cgpms_point)
                                        .putExtra("explanation", cgpms_matching_text));
                            }
                        } else {
                            if (type.equalsIgnoreCase("type")) {
                                showAlertCgpms_code(type);
                            } else {
                                showAlertCgpms_score(type);
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

        server.setTag("1 Point Pay");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.addParams("value", type);
        server.execute(true, false);
    }

    private void showAlertCgpms_score(final String type) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("상대와 나의 궁합확인 ♥");
        alertDialog.setMessage("1P 결제 후 확인 가능합니다. 결제하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        payCgpms(type);
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

    private void showAlertCgpms_code(final String type) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("CGPMS 정보확인");
        alertDialog.setMessage("1P 결제 후 확인 가능합니다. 결제하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        payCgpms(type);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.fl_more:
                PopupMenu popup = new PopupMenu(this, view);
                popup.setOnMenuItemClickListener(this);
                popup.inflate(R.menu.menu_profile);
                popup.show();
                break;


            case R.id.iv_gift:
                startActivity(new Intent(act, GiftBrandAct.class).putExtra("yidx", yidx).putExtra("nick", nick));
                break;

            case R.id.iv_cgpms_kind:
                if(StringUtil.isNull(cgpms_code)) {
                    Common.showToast(act, "CGPMS 정보 미입력 회원입니다.");
                } else {
                    check_1Point_pay("type");
                }
                break;
            case R.id.iv_cgpms_point:
                if(StringUtil.isNull(cgpms_code)) {
                    Common.showToast(act, "CGPMS 정보 미입력 회원입니다.");
                } else {
                    check_1Point_pay("score");
                }
                break;


            case R.id.fl_report:
                startActivity(new Intent(act, ReportAct.class)
                        .putExtra("yidx", yidx)
                        .putExtra("image", image)
                        .putExtra("gender", gender)
                        .putExtra("nick", nick));
                break;

            case R.id.iv_like:
                if (like_double_state) {
                    Common.showToast(act, "이미 심쿵x2 하였습니다");
                } else if (like_state) {
                    doLike(NetUrls.DO_LIKE_DOUBLE);
                } else {
                    doLike(NetUrls.DO_LIKE);
                }
                break;

            case R.id.iv_like_msg:
                if (like_msg_state) {
                    Common.showToast(act, "이미 심쿵메시지를 보냈습니다");
                } else {
                    startActivityForResult(new Intent(act, LikeMessageDlg.class), LIKE_MESSAGE);
                }
                break;
        }
    }


}
