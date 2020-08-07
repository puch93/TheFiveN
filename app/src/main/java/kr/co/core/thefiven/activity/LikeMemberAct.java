package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.adapter.ReadMeAdapter;
import kr.co.core.thefiven.data.ReadMeData;
import kr.co.core.thefiven.databinding.ActivityLikeMemberBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class LikeMemberAct extends BasicAct {
    ActivityLikeMemberBinding binding;
    private Activity act;
    private static final int LIKE_MESSAGE = 101;

    private ArrayList<ReadMeData> list = new ArrayList<>();
    private LinearLayoutManager manager;
    private ReadMeAdapter adapter;

    private boolean isScroll = false;
    private int page = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_like_member, null);
        act = this;

        StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        setRecyclerView();

    }

    @Override
    protected void onResume() {
        super.onResume();

        getLikeMemberList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LIKE_MESSAGE:
                    checkRoom(data.getStringExtra("yidx"), data.getStringExtra("contents"));
                    break;
            }
        }
    }

    private void getLikeMemberList() {
        list = new ArrayList<>();

        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_LIKE_MEMBER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("datalist");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String idx = StringUtil.getStr(job, "u_idx");
                                String intro = StringUtil.getProfileStr(job, "intro");
                                String gender = StringUtil.getStr(job, "gender");

                                boolean login_state = StringUtil.getStr(job, "loginYN").equalsIgnoreCase("Y");
                                boolean like_state = StringUtil.getStr(job, "heart_attack_single").equalsIgnoreCase("Y");
                                boolean like_double_state = StringUtil.getStr(job, "heart_attack_double").equalsIgnoreCase("Y");
                                boolean like_msg_state = StringUtil.getStr(job, "heart_attack_message").equalsIgnoreCase("Y");

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
                                }

                                ReadMeData data = new ReadMeData(idx, profile_img, intro, gender, login_state, like_state, like_double_state, like_msg_state, pi_img_chk);

//                                if (!list.contains(data)) {
//                                    list.add(data);
//                                }

                                if (list.size() == 0) {
                                    Log.i(StringUtil.TAG, "list.size() == 0");
                                    list.add(data);
                                } else {
                                    for (int j = 0; j < list.size(); j++) {
                                        if (!list.get(j).getIdx().equalsIgnoreCase(data.getIdx())) {
                                            Log.i(StringUtil.TAG, "list position == " + j);
                                            if (j == list.size() - 1) {
                                                Log.i(StringUtil.TAG, "list position == " + j);
                                                list.add(data);
                                            }
                                        } else {
                                            break;
                                        }
                                    }
                                }
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

        server.setTag("Like Member");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("pagenum", String.valueOf(page));
        server.execute(true, false);
    }

    private void checkRoom(final String yidx, final String contents) {
        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_ROOM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "채팅방이 생성되어 있어, 메시지를 보내실 수 없습니다");
                        } else {
                            showAlertLikeMessage(yidx, contents);
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

    private void sendLikeMessage(final String yidx, String contents) {
        ReqBasic server = new ReqBasic(act, NetUrls.LIKE_SEND_MESSAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "전송 완료되었습니다");
                            for (int i = 0; i < list.size(); i++) {
                                if (list.get(i).getIdx().equalsIgnoreCase(yidx)) {
                                    list.get(i).setLike_message(true);
                                    act.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            adapter.setList(list);
                                        }
                                    });
                                    break;
                                }
                            }
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

    private void showAlertLikeMessage(final String yidx, final String contents) {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("심쿵 메시지 보내기");
        alertDialog.setMessage("2P 결제 후 보내실 수 있습니다. 결제하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        sendLikeMessage(yidx, contents);

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

    private void setRecyclerView() {
        manager = new LinearLayoutManager(act);
        binding.rcvRead.setLayoutManager(manager);
        binding.rcvRead.setHasFixedSize(true);
        binding.rcvRead.setItemViewCacheSize(20);

        adapter = new ReadMeAdapter(act, null, list);
        binding.rcvRead.setAdapter(adapter);
        binding.rcvRead.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int totalCount = manager.getItemCount();
                int lastItemPosition = manager.findLastCompletelyVisibleItemPosition();
                if (!isScroll) {
                    if (lastItemPosition == totalCount - 1) {
                        ++page;
                        getLikeMemberList();
                    }
                }
            }
        });
    }
}
