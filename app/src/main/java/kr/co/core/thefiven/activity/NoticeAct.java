package kr.co.core.thefiven.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.adapter.NoticeAdapter;
import kr.co.core.thefiven.data.NoticeData;
import kr.co.core.thefiven.databinding.ActivityNoticeBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class NoticeAct extends BasicAct {
    ActivityNoticeBinding binding;
    Activity act;

    LinearLayoutManager manager;
    ArrayList<NoticeData> list = new ArrayList<>();
    NoticeAdapter adapter;

    boolean isScroll = false;

    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_notice, null);
        act = this;

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* set recycler view */
        manager = new LinearLayoutManager(act);
        binding.rcvNotice.setLayoutManager(manager);
        binding.rcvNotice.setHasFixedSize(true);
        binding.rcvNotice.setItemViewCacheSize(20);

        adapter = new NoticeAdapter(act, list);
        binding.rcvNotice.setAdapter(adapter);

        binding.rcvNotice.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = manager.getItemCount();
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();

                if (totalCount - 1 == lastItemPosition) {
                    Log.e(StringUtil.TAG, "onScrolled: ");
                    if (!isScroll) {
                        ++page;
                        getNotice();
                    }
                }
            }
        });


        getNotice();
    }

    private void getNotice() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.NOTICE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("value");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String idx = StringUtil.getStr(job, "idx");
                                String title = StringUtil.getStr(job, "title");
                                String detail = StringUtil.getStr(job, "content");
                                String date = StringUtil.converTime(StringUtil.getStr(job, "created_at"), "yyyy-MM-dd");
                                list.add(new NoticeData(idx, title, date, detail));
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
                        } else {
                            isScroll = true;
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
            }
        };

        server.setTag("Get Notice");
        server.addParams("pg", String.valueOf(page));
        server.addParams("btype", "notice");
        server.execute(true, false);
    }

    private ArrayList<NoticeData> setTestData() {
        for (int i = 0; i < 10; i++) {
            list.add(new NoticeData(String.valueOf(i), i + " 안녕하세요. 더 파이브입니다.", "2020-03-02", "안녕하세요. 더 파이브입니다.\n더파이브를 이용해주셔서 감사합니다.\n\n더 나은 서비스로 찾아뵙겠습니다."));
        }

        return list;
    }

}
