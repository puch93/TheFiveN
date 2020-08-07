package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.adapter.BlockAdapter;
import kr.co.core.thefiven.data.BlockData;
import kr.co.core.thefiven.databinding.ActivityBlockBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class BlockAct extends BasicAct {
    ActivityBlockBinding binding;
    Activity act;

    BlockAdapter adapter;
    LinearLayoutManager manager;
    ArrayList<BlockData> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_block, null);
        act = this;

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        manager = new LinearLayoutManager(act);

        binding.rcvBlock.setLayoutManager(manager);
        binding.rcvBlock.setHasFixedSize(true);
        binding.rcvBlock.setItemViewCacheSize(20);
        adapter = new BlockAdapter(act, list);
        binding.rcvBlock.setAdapter(adapter);

        getBlockList();
    }

    private void getBlockList() {
        ReqBasic server = new ReqBasic(act, NetUrls.BLOCK_LIST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("value");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String idx = StringUtil.getStr(job, "bu_idx");
                                String nick = StringUtil.getProfileStr(job, "nick");
                                String gender = StringUtil.getStr(job, "gender");
                                String age = StringUtil.calcAge(StringUtil.getProfileStr(job, "birth"));
                                String location = StringUtil.getProfileStr(job, "location");

                                //프로필 사진관련
                                JSONArray img_array = job.getJSONArray("profile_img");
                                String profile_img = null;
                                String pi_img_chk = null;
                                if(img_array.length() == 0) {
                                    pi_img_chk = "NO";
                                } else {
                                    JSONObject img_object = img_array.getJSONObject(0);
                                    profile_img = StringUtil.getStr(img_object, "pi_img");
                                    pi_img_chk = StringUtil.getStr(img_object, "pi_img_chk");
                                }


                                list.add(new BlockData(idx, nick, age, gender, location, profile_img, pi_img_chk));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
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

        server.setTag("Block List");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

}
