package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityMainBinding;
import kr.co.core.thefiven.fragment.CgpmsFrag;
import kr.co.core.thefiven.fragment.ChattingFrag;
import kr.co.core.thefiven.fragment.LikeFrag;
import kr.co.core.thefiven.fragment.MainFrag;
import kr.co.core.thefiven.fragment.MenuBasicFrag;
import kr.co.core.thefiven.fragment.ReadMeFrag;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.BackPressCloseHandler;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class MainAct extends BasicAct implements View.OnClickListener, MenuBasicFrag.OnStateChangeListener {
    ActivityMainBinding binding;
    public static Activity act;

    public static final String FRAG_MAIN = "main";
    public static final String FRAG_CGPMS = "cgpms";
    public static final String FRAG_LIKE = "like";
    public static final String FRAG_CHAT = "chat";
    public static final String FRAG_READ = "read";
    String currentFrag = FRAG_MAIN;

    private BackPressCloseHandler backPressCloseHandler;

    MainFrag mainFrag;
    CgpmsFrag cgpmsFrag;
    LikeFrag likeFrag;
    ChattingFrag chattingFrag;
    ReadMeFrag readMeFrag;

    private int currentPos = 0;

    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main, null);
        act = this;

        backPressCloseHandler = new BackPressCloseHandler(this);

        fragmentManager = getSupportFragmentManager();

        setLayout();

        checkChattingCount();

//        getReleaseHashKey();
//        getHashKey();
    }

    //SHA1: 7E:F9:D3:79:BD:61:2B:F4:BC:F8:30:56:F6:D4:96:A8:A0:2D:8B:3A
    private void getReleaseHashKey() {
        byte[] sha1 = {
                0x7E, (byte) 0xF9, (byte) 0xD3, 0x79, (byte) 0xBD, 0x61, 0x2B, (byte) 0xF4, (byte) 0xBC, (byte) 0xF8, 0x30, 0x56, (byte) 0xF6, (byte) 0xD4, (byte) 0x96, (byte) 0xA8, (byte) 0xA0, 0x2D, (byte) 0x8B, 0x3A
        };
        Log.e(StringUtil.TAG, "getReleaseHashKey: " + Base64.encodeToString(sha1, Base64.NO_WRAP));
    }

    private void getHashKey() {
        try {
            // 패키지이름을 입력해줍니다.
            PackageInfo info = getPackageManager().getPackageInfo("kr.co.core.thefiven", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(StringUtil.TAG, "key_hash=" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }


    public void checkChattingCount() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHATTING_ALL_COUNT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        final JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvMessageCount.setVisibility(View.VISIBLE);
                                    String count = StringUtil.getStr(jo, "noreadcnt");
                                    if (count.equalsIgnoreCase("0")) {
                                        binding.tvMessageCount.setVisibility(View.INVISIBLE);
                                    } else {
                                        binding.tvMessageCount.setText(StringUtil.getStr(jo, "noreadcnt"));
                                    }
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.tvMessageCount.setVisibility(View.INVISIBLE);
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
            }
        };

        server.setTag("Chatting All Count");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void setLayout() {
        /* set click listener */
        binding.mainArea.setOnClickListener(this);
        binding.interestArea.setOnClickListener(this);
        binding.likeArea.setOnClickListener(this);
        binding.chatArea.setOnClickListener(this);
        binding.readArea.setOnClickListener(this);

        binding.mainArea.performClick();
    }



    private void replaceFragment(MenuBasicFrag frag, String tag) {
        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.ll_replace_area, frag, tag);
        transaction.commit();
    }

//    private void replaceFragment(String tag) {
//        switch (tag) {
//            case MainAct.FRAG_MAIN:
//                if (mainFrag == null) {
//                    mainFrag = new MainFrag();
//                    fragmentManager.beginTransaction().add(R.id.ll_replace_area, mainFrag, tag).commit();
//                } else {
//                    currentFrag = tag;
//                    fragmentManager.beginTransaction().show(mainFrag).commit();
//                }
//
//                if (cgpmsFrag != null) fragmentManager.beginTransaction().hide(cgpmsFrag).commit();
//                if (likeFrag != null) fragmentManager.beginTransaction().hide(likeFrag).commit();
//                if (chattingFrag != null)
//                    fragmentManager.beginTransaction().hide(chattingFrag).commit();
//                if (readMeFrag != null)
//                    fragmentManager.beginTransaction().hide(readMeFrag).commit();
//                break;
//
//
//            case MainAct.FRAG_CGPMS:
//                if (cgpmsFrag == null) {
//                    cgpmsFrag = new CgpmsFrag();
//                    fragmentManager.beginTransaction().add(R.id.ll_replace_area, cgpmsFrag, tag).commit();
//                } else {
//                    currentFrag = tag;
//                    fragmentManager.beginTransaction().show(cgpmsFrag).commit();
//                }
//
//                if (mainFrag != null) fragmentManager.beginTransaction().hide(mainFrag).commit();
//                if (likeFrag != null) fragmentManager.beginTransaction().hide(likeFrag).commit();
//                if (chattingFrag != null)
//                    fragmentManager.beginTransaction().hide(chattingFrag).commit();
//                if (readMeFrag != null)
//                    fragmentManager.beginTransaction().hide(readMeFrag).commit();
//                break;
//
//
//            case MainAct.FRAG_LIKE:
//                if (likeFrag == null) {
//                    likeFrag = new LikeFrag();
//                    fragmentManager.beginTransaction().add(R.id.ll_replace_area, likeFrag, tag).commit();
//                } else {
//                    currentFrag = tag;
//                    fragmentManager.beginTransaction().show(likeFrag).commit();
//                }
//
//                if (mainFrag != null) fragmentManager.beginTransaction().hide(mainFrag).commit();
//                if (cgpmsFrag != null) fragmentManager.beginTransaction().hide(cgpmsFrag).commit();
//                if (chattingFrag != null)
//                    fragmentManager.beginTransaction().hide(chattingFrag).commit();
//                if (readMeFrag != null)
//                    fragmentManager.beginTransaction().hide(readMeFrag).commit();
//                break;
//
//
//            case MainAct.FRAG_CHAT:
//                if (chattingFrag == null) {
//                    chattingFrag = new ChattingFrag();
//                    fragmentManager.beginTransaction().add(R.id.ll_replace_area, chattingFrag, tag).commit();
//                } else {
//                    currentFrag = tag;
//                    fragmentManager.beginTransaction().show(chattingFrag).commit();
//                }
//
//                if (mainFrag != null) fragmentManager.beginTransaction().hide(mainFrag).commit();
//                if (cgpmsFrag != null) fragmentManager.beginTransaction().hide(cgpmsFrag).commit();
//                if (likeFrag != null) fragmentManager.beginTransaction().hide(likeFrag).commit();
//                if (readMeFrag != null)
//                    fragmentManager.beginTransaction().hide(readMeFrag).commit();
//                break;
//
//
//            case MainAct.FRAG_READ:
//                if (readMeFrag == null) {
//                    readMeFrag = new ReadMeFrag();
//                    fragmentManager.beginTransaction().add(R.id.ll_replace_area, readMeFrag, tag).commit();
//                } else {
//                    currentFrag = tag;
//                    fragmentManager.beginTransaction().show(readMeFrag).commit();
//                }
//
//                if (mainFrag != null) fragmentManager.beginTransaction().hide(mainFrag).commit();
//                if (cgpmsFrag != null) fragmentManager.beginTransaction().hide(cgpmsFrag).commit();
//                if (likeFrag != null) fragmentManager.beginTransaction().hide(likeFrag).commit();
//                if (chattingFrag != null)
//                    fragmentManager.beginTransaction().hide(chattingFrag).commit();
//                break;
//        }
//    }

    public void refreshAboutChatting() {
        //채팅 프래그먼트 업데이트
        if (currentPos == 3 && chattingFrag != null) {
            chattingFrag.getChattingList();
        }

        //채팅 카운트 업데이트
        Log.i(StringUtil.TAG, "refreshAboutChatting: ");
        checkChattingCount();
    }

    private void menuSelect(View view) {
        binding.mainArea.setSelected(false);
        binding.interestArea.setSelected(false);
        binding.likeArea.setSelected(false);
        binding.chatArea.setSelected(false);
        binding.readArea.setSelected(false);

        view.setSelected(true);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_area:
                menuSelect(view);
                currentPos = 0;
                mainFrag = new MainFrag();
                replaceFragment(mainFrag, MainAct.FRAG_MAIN);
                break;

            case R.id.interest_area:
                menuSelect(view);
                currentPos = 1;
                cgpmsFrag = new CgpmsFrag();
                replaceFragment(cgpmsFrag, MainAct.FRAG_CGPMS);
                break;

            case R.id.like_area:
                menuSelect(view);
                currentPos = 2;
                likeFrag = new LikeFrag();
                replaceFragment(likeFrag, MainAct.FRAG_LIKE);
                break;

            case R.id.chat_area:
                menuSelect(view);
                currentPos = 3;
                chattingFrag = new ChattingFrag();
                replaceFragment(chattingFrag, MainAct.FRAG_CHAT);
                break;

            case R.id.read_area:
                menuSelect(view);
                currentPos = 4;
                readMeFrag = new ReadMeFrag();
                replaceFragment(readMeFrag, MainAct.FRAG_READ);
                break;
        }
    }

    @Override
    public void onStateChanged() {
        menuSelect(binding.mainArea);
        currentPos = 0;
        mainFrag = new MainFrag();
        replaceFragment(mainFrag, MainAct.FRAG_MAIN);
    }

    @Override
    public void onGoChattingFrag() {
        menuSelect(binding.chatArea);
        currentPos = 3;
        chattingFrag = new ChattingFrag();
        replaceFragment(chattingFrag, MainAct.FRAG_CHAT);
    }
}