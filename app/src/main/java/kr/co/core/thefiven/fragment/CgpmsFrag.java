package kr.co.core.thefiven.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.CgpmsMemeberAct;
import kr.co.core.thefiven.databinding.FragmentCgpmsBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class CgpmsFrag extends MenuBasicFrag implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private FragmentCgpmsBinding binding;
    private AppCompatActivity act;


    private String gender ="";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_cgpms, container, false);
        act = (AppCompatActivity) getActivity();

        gender = AppPreference.getProfilePref(act, AppPreference.PREF_GENDER);

        StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

        binding.refreshLayout.setOnRefreshListener(this);
        binding.flBack.setOnClickListener(this);
        binding.llAreaCp.setOnClickListener(this);
        binding.llAreaGp.setOnClickListener(this);
        binding.llAreaPp.setOnClickListener(this);
        binding.llAreaMp.setOnClickListener(this);
        binding.llAreaSp.setOnClickListener(this);

        binding.llAreaCp.setTag("CP");
        binding.llAreaGp.setTag("GP");
        binding.llAreaPp.setTag("PP");
        binding.llAreaMp.setTag("MP");
        binding.llAreaSp.setTag("SP");

        getCgpmsCount();

        return binding.getRoot();
    }


    private void getCgpmsCount() {
        ReqBasic server = new ReqBasic(act, NetUrls.CGPMS_MEMBER_COUNT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONArray ja = new JSONArray(resultData.getResult().toString());
                        for (int i = 0; i < ja.length(); i++) {
                            JSONObject job = ja.getJSONObject(i);
                            final String type = StringUtil.getStr(job, "type");
                            final int count = Integer.parseInt(StringUtil.getStr(job, "count"));

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (type) {
                                        case "CP":
                                            binding.tvCountCp.setText(String.valueOf(count));
                                            break;
                                        case "GP":
                                            binding.tvCountGp.setText(String.valueOf(count));
                                            break;
                                        case "PP":
                                            binding.tvCountPp.setText(String.valueOf(count));
                                            break;
                                        case "MP":
                                            binding.tvCountMp.setText(String.valueOf(count));
                                            break;
                                        case "SP":
                                            binding.tvCountSp.setText(String.valueOf(count));
                                            break;
                                    }
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

        server.setTag("Cgpms Count");
        server.execute(true, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                onStateChangeListener.onStateChanged();
                break;

            case R.id.ll_area_cp:
            case R.id.ll_area_gp:
            case R.id.ll_area_pp:
            case R.id.ll_area_mp:
            case R.id.ll_area_sp:
                startActivity(new Intent(act, CgpmsMemeberAct.class).putExtra("type", (String) view.getTag()));
                break;
        }
    }

    @Override
    public void onRefresh() {
        getCgpmsCount();
    }
}