package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityLoginBinding;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class LoginAct extends BasicAct implements View.OnClickListener {
    ActivityLoginBinding binding;
    public static Activity act;

    public static final int SIMPLE_DIALOG = 1001;
    public static final int JOIN_RESULT = 1002;

    private String joinType = "normal";
    private String facebook_token = "";

    CallbackManager facebookCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login, null);
        act = this;

        FacebookSdk.fullyInitialize();
        LoginManager.getInstance().logOut();

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        /* set click listener */
        binding.tvNormalJoin.setOnClickListener(this);
        binding.flFacebookLogin.setOnClickListener(this);
        binding.flNormalLogin.setOnClickListener(this);
        binding.flFacebookLogin.setOnClickListener(this);
        binding.tvFind.setOnClickListener(this);

        setFaceBookCallBack();
    }

    private void setFaceBookCallBack() {
        /* set facebook callback */
        facebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // 로그인에 성공하면 LoginResult 매개변수에 새로운 AccessToken 과 최근에 부여되거나 거부된 권한이 포함됩니다.
                final GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                if (response.getError() != null) {
                                    Log.e(StringUtil.TAG, "onCompleted error message: " + response.getError().getErrorMessage());
                                } else {
                                    Log.i(StringUtil.TAG, "onCompleted success data: " + object.toString());
                                    try {
                                        joinType = "facebook";
                                        facebook_token = object.getString("id");
                                        Log.i(StringUtil.TAG, "facebook result token: " + facebook_token);
                                        doLogin(facebook_token, facebook_token, false);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e(StringUtil.TAG, "facebook onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(StringUtil.TAG, "facebook onError: " + error.getMessage());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SIMPLE_DIALOG) {
                if (joinType.equalsIgnoreCase("normal")) {
                    startActivityForResult(new Intent(act, CellPhoneAuthAct.class), JOIN_RESULT);
                } else {
                    //실제
                    startActivityForResult(new Intent(act, CellPhoneAuthAct.class).putExtra("id", facebook_token), JOIN_RESULT);
                    //테스트
//                    startActivityForResult(new Intent(act, JoinAct.class).putExtra("id", facebook_token), JOIN_RESULT);

                    joinType = "normal";
                }
            } else if (requestCode == JOIN_RESULT) {
                finish();
            }
        }
    }


    private void doLogin(final String id, final String pw, final boolean isNormal) {
        ReqBasic server = new ReqBasic(act, NetUrls.LOGIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "정상적으로 로그인되었습니다");

                            JSONObject job = jo.getJSONObject("value");
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "idx"));

                            AppPreference.setProfilePref(act, AppPreference.PREF_ID, id);
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, pw);
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, StringUtil.getStr(job, "gender"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, StringUtil.getStr(job, "phone"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, StringUtil.getStr(job, "p_image1"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_CGPMS, StringUtil.getStr(job, "cgpms"));
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_AUTO_LOGIN_STATE, true);

                            checkPayMember();
                        } else {
                            if (isNormal) {
                                Common.showToast(act, StringUtil.getStr(jo, "value"));
                            } else {
                                startActivityForResult(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_POINT), SIMPLE_DIALOG);
                            }
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

        server.setTag("Login");
        server.addParams("id", id);
        server.addParams("pw", pw);
        server.addParams("os", "ANDROID");
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_normal_join:
                startActivityForResult(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_POINT), SIMPLE_DIALOG);
//                startActivity(new Intent(act, JoinAct.class));
                break;

            case R.id.fl_normal_login:
                if (binding.etId.length() == 0) {
                    Common.showToast(act, "아이디를 확인해주세요");
                } else if (binding.etPw.length() == 0) {
                    Common.showToast(act, "비밀번호를 확인해주세요");
                } else {
                    doLogin(binding.etId.getText().toString(), binding.etPw.getText().toString(), true);
                }
                break;

            case R.id.fl_facebook_login:
                LoginManager.getInstance().logInWithReadPermissions(act, Arrays.asList("public_profile"));
                break;

            case R.id.tv_find:
                startActivity(new Intent(act, PasswordFindAct.class));
                break;

        }
    }
}
