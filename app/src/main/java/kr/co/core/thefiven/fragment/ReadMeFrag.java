package kr.co.core.thefiven.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.setting.SettingAct;
import kr.co.core.thefiven.adapter.ReadMeAdapter;
import kr.co.core.thefiven.data.ReadMeData;
import kr.co.core.thefiven.databinding.FragmentReadMeBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class ReadMeFrag extends MenuBasicFrag implements View.OnClickListener, PopupMenu.OnMenuItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private FragmentReadMeBinding binding;
    private Activity act;
    private static final int LIKE_MESSAGE = 101;

    private ArrayList<ReadMeData> list = new ArrayList<>();
    private LinearLayoutManager manager;
    private ReadMeAdapter adapter;

    private boolean isScroll = false;
    private int page = 1;

    private static final int PROFILE_DETAIL = 1003;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_read_me, container, false);
        act = (Activity) getActivity();

        StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

        binding.refreshLayout.setOnRefreshListener(this);
        binding.flBack.setOnClickListener(this);
        binding.flSetting.setOnClickListener(this);

        setRecyclerView();

        getReadMeList();

        return binding.getRoot();
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
                                if(list.get(i).getIdx().equalsIgnoreCase(yidx)) {
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


    private void getReadMeList() {
        isScroll = true;
        ReqBasic server = new ReqBasic(act, NetUrls.LIST_ME_READED) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());
                        StringUtil.logLargeString(jo.toString());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            final JSONObject jo_data = jo.getJSONObject("value");

                            /* 몇일간 방문했는지 세팅 */
                            //시간
                            SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
                            Date date = null;
                            try {
                                date = dateFormat1.parse(StringUtil.getStr(jo_data, "lastdata_date"));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            Calendar calendar_today = Calendar.getInstance();
                            Calendar calendar_from = Calendar.getInstance();
                            calendar_from.setTime(date);

                            final long today = calendar_today.getTimeInMillis() / 86400000; //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
                            final long from = calendar_from.getTimeInMillis() / 86400000;

                            //텍스트
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvDate.setText(String.valueOf(today - from));
                                    binding.tvMember.setText(StringUtil.getStr(jo_data, "readedcnt") + "명");
                                }
                            });


                            //회원 데이터
                            JSONArray ja = jo_data.getJSONArray("data");
                            for (int i = 0; i < ja.length(); i++) {
                                JSONObject job = ja.getJSONObject(i);
                                String idx = StringUtil.getStr(job, "idx");
                                String intro = StringUtil.getProfileStr(job, "intro");
                                String gender = StringUtil.getStr(job, "gender");

                                boolean login_state = StringUtil.getStr(job, "loginYN").equalsIgnoreCase("Y");
                                boolean like_state = StringUtil.getStr(job, "heartattack_single").equalsIgnoreCase("Y");
                                boolean like_double_state = StringUtil.getStr(job, "heartattack_double").equalsIgnoreCase("Y");
                                boolean like_msg_state = StringUtil.getStr(job, "heartattack_message").equalsIgnoreCase("Y");

                                //프로필 사진관련

                                String profile_img = null;
                                String pi_img_chk = null;

                                if(StringUtil.isNull(StringUtil.getStr(job, "piimg"))) {
                                    pi_img_chk = "NO";
                                } else {
                                    JSONArray img_array = job.getJSONArray("piimg");
                                    JSONObject img_object = img_array.getJSONObject(0);
                                    profile_img = StringUtil.getStr(img_object, "pi_img");
                                    pi_img_chk = StringUtil.getStr(img_object, "pi_img_chk");
                                }

                                list.add(new ReadMeData(idx, profile_img, intro, gender, login_state, like_state, like_double_state, like_msg_state, pi_img_chk));
                            }

                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(list);
                                }
                            });
                        } else {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvDate.setText("0");
                                    binding.tvMember.setText("0" + "명");
                                    binding.tvMember.setText("0");
                                }
                            });
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
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

        server.setTag("Read Me");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("pagenum", String.valueOf(page));
        server.execute(true, false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            StatusBarUtil.setStatusBarColor(act, StatusBarUtil.StatusBarColorType.DEFAULT_STATUS_BAR);

            if (AppPreference.getMemberState(act, AppPreference.LIST_READ_ME_STATE)) {
                AppPreference.setMemberState(act, AppPreference.LIST_READ_ME_STATE, false);
                this.onRefresh();
            }
        }
    }

    private void setRecyclerView() {
        manager = new LinearLayoutManager(act);
        binding.rcvRead.setLayoutManager(manager);
        binding.rcvRead.setHasFixedSize(true);
        binding.rcvRead.setItemViewCacheSize(20);

        adapter = new ReadMeAdapter(act, this, list);
        adapter.setFrag(this);
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
                        getReadMeList();
                    }
                }
            }
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.block:
                Common.showToastDevelop(act);
                return true;

            default:
                return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PROFILE_DETAIL:
                    this.onRefresh();
                    break;

                case LIKE_MESSAGE:
                    checkRoom(data.getStringExtra("yidx"), data.getStringExtra("contents"));
                    break;
            }
        }
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
        list = new ArrayList<>();
        page = 1;
        getReadMeList();
    }
}