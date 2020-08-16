package kr.co.core.thefiven.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.setting.SettingAct;
import kr.co.core.thefiven.adapter.ChattingBottomAdapter;
import kr.co.core.thefiven.adapter.ChattingTopAdapter;
import kr.co.core.thefiven.data.ChattingBottomData;
import kr.co.core.thefiven.data.ChattingTopData;
import kr.co.core.thefiven.databinding.FragmentChattingBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AllOfDecoration;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class ChattingFrag extends MenuBasicFrag implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private FragmentChattingBinding binding;
    private AppCompatActivity act;

    private LinearLayoutManager topManager;
    private ChattingTopAdapter topAdapter;
    private ArrayList<ChattingTopData> topList = new ArrayList<>();


    private LinearLayoutManager bottomManager;
    private ChattingBottomAdapter bottomAdapter;
    private ArrayList<ChattingBottomData> bottomList = new ArrayList<>();

    private static final int PROFILE_DETAIL = 1003;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_chatting, container, false);
        act = (AppCompatActivity) getActivity();

        StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

        /* set click listener */
        binding.refreshLayout.setOnRefreshListener(this);
        binding.flBack.setOnClickListener(this);
        binding.flSetting.setOnClickListener(this);

        setRecyclerView();
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        getChattingList();
        getMatchingList();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PROFILE_DETAIL:
                    this.onRefresh();
                    break;
            }
        }
    }

    private boolean isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg);
    }

    public void getChattingList() {
        topList = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.LIST_CHATTING) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            StringUtil.logLargeString(jo.toString());

                            JSONObject jo_chat = jo.getJSONObject("data");
                            JSONArray ja = jo_chat.getJSONArray("chats");

                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                Log.i(StringUtil.TAG, "job(" + i + "): " + job);

                                String no_read_count = StringUtil.getStr(job, "nonereadmsgcnt");
                                String contents = StringUtil.getStr(job, "msg");
                                if (isImage(contents)) {
                                    contents = "이미지";
                                }
                                String date = StringUtil.getStr(job, "created_at");
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                date = Common.formatImeString(format.parse(date), act);
                                String room_idx = StringUtil.getStr(job, "room_idx");
                                String room_type = StringUtil.getStr(job, "room_type");
                                String paychatYN = StringUtil.getStr(job, "paychatYN");

                                if (room_type.equalsIgnoreCase("heartmatching")) {
                                    if (paychatYN.equalsIgnoreCase("N") && !contents.equalsIgnoreCase("방이 생성 되었습니다."))
                                        contents = "결제 후 확인가능합니다";
                                }


                                /* 회원정보 (상대) */
                                if (job.has("friend")) {
                                    JSONArray other_array = job.getJSONArray("friend");
                                    String other_string = other_array.toString();

                                    if (!other_string.equalsIgnoreCase("[\"\"]")) {
                                        JSONObject other = other_array.getJSONObject(0);
                                        String idx = StringUtil.getStr(other, "u_idx");
                                        String nick = StringUtil.getProfileStr(other, "nick");
                                        String gender = StringUtil.getProfileStr(other, "gender");
                                        String age = StringUtil.calcAge(StringUtil.getProfileStr(other, "birth"));
                                        String location = StringUtil.getProfileStr(other, "location");

                                        boolean login_state = StringUtil.getStr(other, "loginYN").equalsIgnoreCase("Y");

                                        //프로필 사진관련
                                        String profile_img = null;
                                        String profile_img_ck = null;
                                        if(StringUtil.isNull(StringUtil.getStr(other, "piimg"))) {
                                            profile_img_ck = "NO";
                                        } else {
                                            JSONArray img_array = other.getJSONArray("piimg");
                                            JSONObject img_object = img_array.getJSONObject(0);
                                            profile_img = StringUtil.getStr(img_object, "pi_img");
                                            profile_img_ck = StringUtil.getStr(img_object, "pi_img_chk");

                                            for (int j = 0; j < img_array.length(); j++) {
                                                JSONObject object = img_array.getJSONObject(j);
                                                String profile_img_tmp = StringUtil.getStr(object, "pi_img");
                                                String pi_img_chk_tmp = StringUtil.getStr(object, "pi_img_chk");

                                                if(pi_img_chk_tmp.equalsIgnoreCase("Y")) {
                                                    profile_img = profile_img_tmp;
                                                    profile_img_ck = pi_img_chk_tmp;
                                                    break;
                                                }
                                            }
                                        }

                                        topList.add(new ChattingTopData(idx, nick, age, gender, location, contents, date, login_state, profile_img, room_idx, profile_img_ck, no_read_count, room_type, paychatYN));
                                    }
                                }
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    topAdapter.setList(topList);
                                }
                            });
                        } else {
                            topList = new ArrayList<>();
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    topAdapter.setList(topList);
                                }
                            });
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Chatting List");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    private void getMatchingList() {
        bottomList = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.MATCHING) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);

                                Log.i(StringUtil.TAG, "data[" + i + "]: " + job);

                                String idx = StringUtil.getStr(job, "u_idx");
                                String nick = StringUtil.getProfileStr(job, "nick");
                                String gender = StringUtil.getStr(job, "gender");
                                String age = StringUtil.calcAge(StringUtil.getProfileStr(job, "birth"));
                                boolean login_state = StringUtil.getStr(job, "loginYN").equalsIgnoreCase("Y");

                                String lastchat = "";
                                if (!StringUtil.isNull(StringUtil.getStr(job, "lastchat"))) {
                                    JSONObject lastchat_job = job.getJSONObject("lastchat");
                                    lastchat = StringUtil.getStr(lastchat_job, "created_at");

                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    lastchat = Common.formatImeString(format.parse(lastchat), act);
                                } else {
                                    lastchat = "-";
                                }


                                //프로필 사진관련
                                JSONArray img_array = job.getJSONArray("profile_img");
                                String profile_img = null;
                                String pi_img_chk = null;

                                if (img_array.length() == 0) {
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

                                bottomList.add(new ChattingBottomData(idx, nick, age, gender, lastchat, login_state, profile_img, pi_img_chk));
                            }


                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bottomAdapter.setList(bottomList);
                                }
                            });
                        } else {
                            bottomList = new ArrayList<>();

//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Common.showToastNetwork(act);
                }

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (binding.refreshLayout.isRefreshing()) {
                            binding.refreshLayout.setRefreshing(false);
                        }
                    }
                });
            }
        };

        server.setTag("Matching");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

            if (AppPreference.getMemberState(act, AppPreference.LIST_CHAT_STATE)) {
                AppPreference.setMemberState(act, AppPreference.LIST_CHAT_STATE, false);
                this.onRefresh();
            }
        }
    }

    private void setRecyclerView() {
        /* top */
        topManager = new LinearLayoutManager(act);

        binding.rcvChattingTop.setLayoutManager(topManager);
        binding.rcvChattingTop.setHasFixedSize(true);
        binding.rcvChattingTop.setItemViewCacheSize(20);
        topAdapter = new ChattingTopAdapter(act, topList);
        binding.rcvChattingTop.setAdapter(topAdapter);

        /* bottom */
        bottomManager = new GridLayoutManager(act, 2);

        binding.rcvChattingBottom.setLayoutManager(bottomManager);
        binding.rcvChattingBottom.setHasFixedSize(true);
        binding.rcvChattingBottom.setItemViewCacheSize(20);
        binding.rcvChattingBottom.addItemDecoration(new AllOfDecoration(act, "chattingBottom"));

        bottomAdapter = new ChattingBottomAdapter(act, bottomList);
        bottomAdapter.setFrag(this);
        binding.rcvChattingBottom.setAdapter(bottomAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                onStateChangeListener.onStateChanged();
                break;

            case R.id.fl_setting:
                startActivity(new Intent(act, SettingAct.class));
                break;
        }
    }

    @Override
    public void onRefresh() {
        getChattingList();
        getMatchingList();
    }
}