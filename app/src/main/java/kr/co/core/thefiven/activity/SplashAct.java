package kr.co.core.thefiven.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivitySplashBinding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class SplashAct extends BasicAct {
    ActivitySplashBinding binding;
    Activity act;

    private String device_version;

    Timer timer = new Timer();
    private boolean isReady = true;

    private static final int PERMISSION = 1000;
    private static final int NETWORK = 1001;

    private String fcm_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash, null);
        act = this;

        Glide.with(act)
                .load(R.raw.splash)
                .into(binding.ivSplash);

        // get device version
        try {
            device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getFcmToken();
        StringUtil.getDeviceId(act);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVersion();
            }
        }, 1500);
    }

    private void getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(StringUtil.TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcm_token = task.getResult().getToken();
                        Log.i(StringUtil.TAG, "fcm_token: " + fcm_token);
                    }
                });
    }

    private void startProgram() {
        if (!checkPermission()) {
            startActivityForResult(new Intent(act, PermissionAct.class), PERMISSION);
        } else {
            checkSetting();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(StringUtil.TAG, "resultCode: " + resultCode);

        if (resultCode != RESULT_OK && resultCode != RESULT_CANCELED)
            return;

        switch (requestCode) {
            case PERMISSION:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                } else {
                    startProgram();
                }
                break;

            case NETWORK:
                checkSetting();
                break;
        }
    }

    private void checkSetting() {
        checkNetwork(new Runnable() {
            @Override
            public void run() {
                checkTimer();
            }
        });
    }

    //로딩중 텍스트 애니메이션
    public void checkTimer() {
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (
                                isReady && !StringUtil.isNull(fcm_token)
                        ) {
                            isReady = false;

                            AppPreference.setProfilePref(act, AppPreference.PREF_FCM, fcm_token);
                            if (AppPreference.getProfilePrefBool(act, AppPreference.PREF_AUTO_LOGIN_STATE)) {
                                doAutoLogin();
                            } else {
                                startActivity(new Intent(act, LoginAct.class));
                                finish();
                            }

                            timer.cancel();
                        }
                    }
                }, 0);
            }
        };
        timer.schedule(adTask, 0, 1000);
    }

    //데이터 또는 WIFI 켜져 있는지 확인 / 안켜져있으면 데이터 설정창으로
    private void checkNetwork(final Runnable afterCheckAction) {
        ConnectivityManager manager = (ConnectivityManager) act.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (manager != null) {
            networkInfo = manager.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            if (afterCheckAction != null) {
                afterCheckAction.run();
            }
        } else {
            showNetworkAlert();
        }
    }

    //네트워크 연결 다이얼로그
    public void showNetworkAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("네트워크 사용유무");
        alertDialog.setMessage("인터넷이 연결되어 있지 않습니다. \n설정창으로 가시겠습니까?");

        // OK 를 누르게 되면 설정창으로 이동합니다.
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        Intent in = new Intent(Settings.ACTION_DATA_USAGE_SETTINGS);
                        in.addCategory(Intent.CATEGORY_DEFAULT);
                        startActivityForResult(in, NETWORK);
                    }
                });
        // Cancle 하면 종료 합니다.
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
        alertDialog.show();
    }

    private void doAutoLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.LOGIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONObject job = jo.getJSONObject("value");
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "idx"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, StringUtil.getStr(job, "gender"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, StringUtil.getStr(job, "phone"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, StringUtil.getStr(job, "p_image1"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_CGPMS, StringUtil.getStr(job, "cgpms"));

                            checkPayMember();
                        } else {
                            Common.showToast(act, "자동로그인에 실패했습니다");
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_AUTO_LOGIN_STATE, false);
                            startActivity(new Intent(act, LoginAct.class));
                            finish();
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

        server.setTag("Auto Login");
        server.addParams("id", AppPreference.getProfilePref(act, AppPreference.PREF_ID));
        server.addParams("pw", AppPreference.getProfilePref(act, AppPreference.PREF_PW));
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.execute(true, false);
    }

    private void checkVersion() {
        ReqBasic server = new ReqBasic(this, NetUrls.TERMS) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                final String res = resultData.getResult();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!StringUtil.isNull(res)) {
                                JSONObject jo = new JSONObject(res);

                                String[] version = jo.getString("app_version").split("\\.");
                                String[] version_me = device_version.split("\\.");


                                for (int i = 0; i < 3; i++) {
                                    int tmp1 = Integer.parseInt(version[i]);
                                    int tmp2 = Integer.parseInt(version_me[i]);

                                    if(tmp2 < tmp1) {
                                        android.app.AlertDialog.Builder alertDialogBuilder =
                                                new android.app.AlertDialog.Builder(new ContextThemeWrapper(act, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert));
                                        alertDialogBuilder.setTitle("업데이트");
                                        alertDialogBuilder.setMessage("새로운 버전이 있습니다.")
                                                .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=kr.co.core.thefiven"));
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                        android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                        alertDialog.setCanceledOnTouchOutside(false);
                                        alertDialog.show();

                                        return;
                                    }
                                }

                                startProgram();
                            } else {
                                startProgram();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        server.setTag("Version Check");
        server.execute(true, false);
    }

    private void checkPayMember() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_PAY_MEMBER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_PAY_MEMBER, true);
                        } else {
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_PAY_MEMBER, false);
                        }

                        startActivity(new Intent(act, MainAct.class));
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Check Pay Member");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }
}
