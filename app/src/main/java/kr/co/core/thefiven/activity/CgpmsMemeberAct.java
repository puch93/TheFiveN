package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.adapter.MainBottomAdapter;
import kr.co.core.thefiven.data.MainBottomData;
import kr.co.core.thefiven.databinding.ActivityCgpmsMemeberBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AllOfDecoration;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class CgpmsMemeberAct extends BasicAct {
    ActivityCgpmsMemeberBinding binding;
    Activity act;

    LinearLayoutManager manager;
    MainBottomAdapter adapter;
    private ArrayList<MainBottomData> list = new ArrayList<>();

    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cgpms_memeber, null);
        act = this;

        StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        type = getIntent().getStringExtra("type");

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.tvTitle.setText(getIntent().getStringExtra("type"));

        setRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCgpmsMemberList();
    }

    private void getCgpmsMemberList() {
        list = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.CGPMS_MEMBER_LIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("list");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);

                                Log.i(StringUtil.TAG, "ja(" + i + ")-----");
                                StringUtil.logLargeString(ja.getJSONObject(i).toString());

                                String idx = StringUtil.getStr(job, "u_idx");
                                String nick = StringUtil.getProfileStr(job, "nick");
                                String age = StringUtil.calcAge(StringUtil.getProfileStr(job, "birth"));
                                String location = StringUtil.getProfileStr(job, "location");
                                String gender = StringUtil.getStr(job, "gender");

                                // 소개사진
                                //프로필 사진관련
                                JSONArray img_array = job.getJSONArray("profile_imgs");
                                String profile_img_count = String.valueOf(img_array.length());
                                String profile_img;
                                String pi_img_chk;

                                if (img_array.length() == 0) {
                                    profile_img = "";
                                    pi_img_chk = "NO";
                                } else {
                                    JSONObject img_object = img_array.getJSONObject(0);
                                    profile_img = StringUtil.getStr(img_object, "pi_img");
                                    pi_img_chk = StringUtil.getStr(img_object, "pi_img_chk");

                                    for (int j = 0; j < img_array.length(); j++) {
                                        JSONObject object = img_array.getJSONObject(j);
                                        String profile_img_tmp = StringUtil.getStr(object, "pi_img");
                                        String pi_img_chk_tmp = StringUtil.getStr(object, "pi_img_chk");

                                        if(pi_img_chk_tmp.equalsIgnoreCase("Y")) {
                                            profile_img = profile_img_tmp;
                                            pi_img_chk = pi_img_chk_tmp;
                                            break;
                                        }
                                    }
                                }

                                String cgpms_kind = StringUtil.getProfileStr(job, "cgpms");
                                if (!StringUtil.isNull(cgpms_kind) && !cgpms_kind.equalsIgnoreCase("미입력"))
                                    cgpms_kind = cgpms_kind.substring(cgpms_kind.length() - 2);
                                String cgpms_point = StringUtil.getProfileStr(job, "matching_point");

                                boolean like_state = StringUtil.getStr(job, "single_heartattack").equalsIgnoreCase("Y");
                                boolean like_double_state = StringUtil.getStr(job, "double_heartattack").equalsIgnoreCase("Y");

                                MainBottomData data = new MainBottomData(idx, nick, age, gender, location, profile_img, profile_img_count, cgpms_kind, cgpms_point, like_state, like_double_state, pi_img_chk);
                                Log.e(StringUtil.TAG, "data: " + data);
                                list.add(data);
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
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

        server.setTag("Cgpms List");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("type", type);
        server.execute(true, false);
    }

    private void setRecyclerView() {
        /* bottom */
        manager = new LinearLayoutManager(act);

        binding.rcvCgpms.setLayoutManager(manager);
        binding.rcvCgpms.setHasFixedSize(true);
        binding.rcvCgpms.setItemViewCacheSize(20);
        binding.rcvCgpms.addItemDecoration(new AllOfDecoration(act, "cgpms_member"));

        adapter = new MainBottomAdapter(act, list);
        binding.rcvCgpms.setAdapter(adapter);
    }
}
