package kr.co.core.thefiven.activity.gift;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.adapter.GiftProductAdapter;
import kr.co.core.thefiven.data.GiftListData;
import kr.co.core.thefiven.databinding.ActivityGiftProductBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AllOfDecoration;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class GiftProductAct extends BasicAct {
    ActivityGiftProductBinding binding;
    Activity act;

    ArrayList<GiftListData> list =  new ArrayList<>();
    GiftProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift_product, null);
        act = this;

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getMyInfo();

        list = (ArrayList<GiftListData>)getIntent().getSerializableExtra("productList");

        /* set RecyclerView */
        binding.rcvProduct.setLayoutManager(new GridLayoutManager(act, 2));
        binding.rcvProduct.setHasFixedSize(true);
        binding.rcvProduct.setItemViewCacheSize(20);
        adapter = new GiftProductAdapter(act, list, getIntent().getStringExtra("yidx"), getIntent().getStringExtra("nick"));
        binding.rcvProduct.setAdapter(adapter);
        binding.rcvProduct.addItemDecoration(new AllOfDecoration(act, "gift_product"));
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
}
