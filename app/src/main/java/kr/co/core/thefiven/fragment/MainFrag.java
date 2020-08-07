package kr.co.core.thefiven.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.myinfo.MyInfoAct;
import kr.co.core.thefiven.activity.SearchAct;
import kr.co.core.thefiven.adapter.MainBottomAdapter;
import kr.co.core.thefiven.adapter.MainTopAdapter;
import kr.co.core.thefiven.data.MainBottomData;
import kr.co.core.thefiven.data.MainTopData;
import kr.co.core.thefiven.databinding.FragmentMainBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.MainTopItemDecoration;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class MainFrag extends MenuBasicFrag implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private FragmentMainBinding binding;
    private AppCompatActivity act;

    private LinearLayoutManager topManager;
    private MainTopAdapter topAdapter;
    private ArrayList<MainTopData> topList = new ArrayList<>();

    private LinearLayoutManager bottomManager;
    private MainBottomAdapter bottomAdapter;
    private ArrayList<MainBottomData> bottomList = new ArrayList<>();

    private boolean isScroll = false;
    private int page = 1;

    private static final int SEARCH = 1001;
    private static final int PROFILE_DETAIL = 1002;


    private String age = "none";
    private String job = "none";
    private String salary = "none";
    private String marriage = "none";
    private String personality = "none";
    private String nationality = "none";
    private String blood = "none";
    private String location = "none";
    private String education = "none";
    private String holiday = "none";
    private String family = "none";
    private String height = "none";
    private String body = "none";
    private String drink = "none";
    private String smoke = "none";
    private String cgpms = "none";

    private String intro = "none";
    private String join3day = "none";
    private String interest = "none";

    private boolean isFirst = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false);
        act = (AppCompatActivity) getActivity();

        StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

        binding.refreshLayout.setOnRefreshListener(this);
        binding.flSearch.setOnClickListener(this);
        binding.flMypage.setOnClickListener(this);

        setRecyclerView();

        getRecommendList();
        getPopularList();

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onRefresh() {
        page = 1;
        bottomList = new ArrayList<>();
        bottomAdapter.setPayMember(AppPreference.getProfilePrefBool(act, AppPreference.PREF_PAY_MEMBER));

        getRecommendList();
        getPopularList();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

            if (AppPreference.getMemberState(act, AppPreference.LIST_MAIN_STATE)) {
                AppPreference.setMemberState(act, AppPreference.LIST_MAIN_STATE, false);
                this.onRefresh();
            }
        }
    }

    private void getPopularList() {
        topList = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.LIST_MAIN_POPULAR) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                Log.i(StringUtil.TAG, "ja(" + i + "): " + ja.getJSONObject(i));
                                JSONObject job = ja.getJSONObject(i);

                                String idx = StringUtil.getStr(job, "idx");
                                String location = StringUtil.getProfileStr(job, "location");
                                String age = StringUtil.getProfileStr(job, "birth");
                                String gender = StringUtil.getProfileStr(job, "gender");

                                //프로필 사진관련
                                JSONArray img_array = job.getJSONArray("profile_img");
                                String profile_img;
                                String pi_img_chk;

                                if (img_array.length() == 0) {
                                    profile_img = "";
                                    pi_img_chk = "NO";
                                } else {
                                    JSONObject img_object = img_array.getJSONObject(0);
                                    profile_img = StringUtil.getStr(img_object, "pi_img");
                                    pi_img_chk = StringUtil.getStr(img_object, "pi_img_chk");
                                }

                                MainTopData data = new MainTopData(idx, location, StringUtil.calcAge(age), gender, profile_img, pi_img_chk);
                                topList.add(data);
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    topAdapter.setList(topList);
                                }
                            });
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

        server.setTag("Popular List");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void getRecommendList() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_MAIN_RECOMMEND) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        StringUtil.logLargeString(jo.toString());

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            JSONArray ja = jo.getJSONArray("value");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);

                                Log.i(StringUtil.TAG, "ja(" + i + ")-----");
                                StringUtil.logLargeString(ja.getJSONObject(i).toString());

                                String idx = StringUtil.getStr(job, "u_idx");
                                String nick = StringUtil.getProfileStr(job, "nick");
                                String gender = StringUtil.getStr(job, "gender");
                                String age = StringUtil.calcAge(StringUtil.getProfileStr(job, "birth"));
                                String location = StringUtil.getProfileStr(job, "location");

                                String cgpms_kind = StringUtil.getProfileStr(job, "cgpms");
                                if (!StringUtil.isNull(cgpms_kind) && !cgpms_kind.equalsIgnoreCase("미입력"))
                                    cgpms_kind = cgpms_kind.substring(cgpms_kind.length() - 2);

                                String cgpms_point = StringUtil.getProfileStr(job, "cp_point");

                                boolean like_state = StringUtil.getStr(job, "single_heartattack").equalsIgnoreCase("Y");
                                boolean like_double_state = StringUtil.getStr(job, "double_heartattack").equalsIgnoreCase("Y");

                                //프로필 사진관련
                                JSONArray img_array = job.getJSONArray("profile_img");
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
                                }


                                MainBottomData data = new MainBottomData(idx, nick, age, gender, location, profile_img, profile_img_count, cgpms_kind, cgpms_point, like_state, like_double_state, pi_img_chk);
                                bottomList.add(data);
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    bottomAdapter.setList(bottomList);
                                    isScroll = false;
                                }
                            });
                        } else {
                            isScroll = true;

                            if (page == 1) {
                                Common.showToast(act, StringUtil.getStr(jo, "value"));

                                bottomList = new ArrayList<>();
                                act.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        bottomAdapter.setList(bottomList);
                                        isScroll = true;
                                    }
                                });
                            }
                        }
                    } catch (JSONException e) {
                        isScroll = false;
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    isScroll = false;
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

        server.setTag("Recommend List");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("pagenum", String.valueOf(page));

        server.addParams("age", age);
        server.addParams("job", job);
        server.addParams("salary", salary);
        server.addParams("marriage", marriage);
        server.addParams("personality", personality);
        server.addParams("nationality", nationality);
        server.addParams("blood", blood);
        server.addParams("location", location);
        server.addParams("education", education);
        server.addParams("holiday", holiday);
        server.addParams("family", family);
        server.addParams("height", height);
        server.addParams("body", body);
        server.addParams("drink", drink);
        server.addParams("smoke", smoke);
        server.addParams("cgpms", cgpms);

        server.addParams("intro", intro);
        server.addParams("images", "none");
        server.addParams("join3day", join3day);
        server.addParams("interest", interest);
        server.execute(true, false);
    }

    private void setRecyclerView() {
        /* top */
        topManager = new LinearLayoutManager(act);
        topManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        binding.rcvTop.setLayoutManager(topManager);
        binding.rcvTop.setHasFixedSize(true);
        binding.rcvTop.setItemViewCacheSize(20);
        topAdapter = new MainTopAdapter(act, topList);
        binding.rcvTop.setAdapter(topAdapter);
        binding.rcvTop.addItemDecoration(new MainTopItemDecoration(act));

        /* bottom */
        bottomManager = new LinearLayoutManager(act);

        binding.rcvBottom.setLayoutManager(bottomManager);
        binding.rcvBottom.setHasFixedSize(true);
        binding.rcvBottom.setItemViewCacheSize(20);

        bottomAdapter = new MainBottomAdapter(act, bottomList);
        binding.rcvBottom.setAdapter(bottomAdapter);
        bottomAdapter.setMainFrag(this);


        binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) binding.scrollView.getChildAt(binding.scrollView.getChildCount() - 1);

                int diff = (view.getBottom() - (binding.scrollView.getHeight() + binding.scrollView
                        .getScrollY()));

                if (diff == 0) {
                    if (!isScroll) {
                        Log.e(StringUtil.TAG, "onScrollChange");
                        ++page;
                        getRecommendList();
                    }
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH) {
                age = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_AGE)) ? "none" : data.getStringExtra(StringUtil.SEARCH_AGE);
                job = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_JOB)) ? "none" : data.getStringExtra(StringUtil.SEARCH_JOB);
                salary = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_SALARY)) ? "none" : data.getStringExtra(StringUtil.SEARCH_SALARY);
                marriage = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_MARRIAGE)) ? "none" : data.getStringExtra(StringUtil.SEARCH_MARRIAGE);
                personality = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_PERSONALITY)) ? "none" : data.getStringExtra(StringUtil.SEARCH_PERSONALITY);
                nationality = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_NATIONALITY)) ? "none" : data.getStringExtra(StringUtil.SEARCH_NATIONALITY);
                blood = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_BLOOD)) ? "none" : data.getStringExtra(StringUtil.SEARCH_BLOOD);
                location = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_LOCATION)) ? "none" : data.getStringExtra(StringUtil.SEARCH_LOCATION);
                education = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_EDU)) ? "none" : data.getStringExtra(StringUtil.SEARCH_EDU);
                holiday = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_HOLIDAY)) ? "none" : data.getStringExtra(StringUtil.SEARCH_HOLIDAY);
                family = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_FAMILY)) ? "none" : data.getStringExtra(StringUtil.SEARCH_FAMILY);
                height = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_HEIGHT)) ? "none" : data.getStringExtra(StringUtil.SEARCH_HEIGHT);
                body = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_BODY)) ? "none" : data.getStringExtra(StringUtil.SEARCH_BODY);
                drink = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_DRINK)) ? "none" : data.getStringExtra(StringUtil.SEARCH_DRINK);
                smoke = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_SMOKE)) ? "none" : data.getStringExtra(StringUtil.SEARCH_SMOKE);
                cgpms = StringUtil.isNull(data.getStringExtra(StringUtil.SEARCH_CGPMS)) ? "none" : data.getStringExtra(StringUtil.SEARCH_CGPMS);

                intro = data.getStringExtra(StringUtil.SEARCH_INTRO);
                join3day = data.getStringExtra(StringUtil.SEARCH_JOIN3DAYS);
                interest = data.getStringExtra(StringUtil.SEARCH_INTEREST);

                page = 1;
                bottomList = new ArrayList<>();
                getRecommendList();
            } else if (requestCode == PROFILE_DETAIL) {
                this.onRefresh();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_search:
                Intent intent = new Intent(act, SearchAct.class);
                intent.putExtra(StringUtil.SEARCH_AGE, age);
                intent.putExtra(StringUtil.SEARCH_JOB, job);
                intent.putExtra(StringUtil.SEARCH_SALARY, salary);
                intent.putExtra(StringUtil.SEARCH_MARRIAGE, marriage);
                intent.putExtra(StringUtil.SEARCH_PERSONALITY, personality);
                intent.putExtra(StringUtil.SEARCH_NATIONALITY, nationality);
                intent.putExtra(StringUtil.SEARCH_BLOOD, blood);
                intent.putExtra(StringUtil.SEARCH_LOCATION, location);
                intent.putExtra(StringUtil.SEARCH_EDU, education);
                intent.putExtra(StringUtil.SEARCH_HOLIDAY, holiday);
                intent.putExtra(StringUtil.SEARCH_FAMILY, family);
                intent.putExtra(StringUtil.SEARCH_HEIGHT, height);
                intent.putExtra(StringUtil.SEARCH_BODY, body);
                intent.putExtra(StringUtil.SEARCH_DRINK, drink);
                intent.putExtra(StringUtil.SEARCH_SMOKE, smoke);
                intent.putExtra(StringUtil.SEARCH_CGPMS, cgpms);

                intent.putExtra(StringUtil.SEARCH_INTRO, intro);
                intent.putExtra(StringUtil.SEARCH_JOIN3DAYS, join3day);
                intent.putExtra(StringUtil.SEARCH_INTEREST, interest);
                startActivityForResult(intent, SEARCH);
                break;

            case R.id.fl_mypage:
                startActivity(new Intent(act, MyInfoAct.class));
                break;
        }
    }
}