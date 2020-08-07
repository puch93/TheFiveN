package kr.co.core.thefiven.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.JoinAct;
import kr.co.core.thefiven.databinding.FragmentJoin01Binding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class Join01Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin01Binding binding;
    private AppCompatActivity act;

    private static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    private static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{7,13}.$", Pattern.CASE_INSENSITIVE);

    private static final int INFO_DIALOG = 101;

    private String email = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_01, container, false);
        act = (AppCompatActivity) getActivity();

        binding.flNext.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);
        binding.tvRequestCheck.setOnClickListener(this);
        binding.tvAuthCheck.setOnClickListener(this);

        setTextWatcher();
        return binding.getRoot();
    }

    private void inspect_nick() {
        ReqBasic server = new ReqBasic(act, NetUrls.INSPECT_NICK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            nextProcess();
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

        server.setTag("Inspect Nick");
        server.addParams("nick", binding.etNick.getText().toString());
        server.execute(true, false);
    }

    private void doCheckEmail() {
        ReqBasic server = new ReqBasic(act, NetUrls.AUTH_EMAIL_REQUEST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                email = binding.etEmail.getText().toString();
                Common.showToast(act, "인증번호가 요청되었습니다");
                binding.etEmail.setEnabled(false);
            }
        };

        server.setTag("Request Email Auth");
        server.addParams("email", binding.etEmail.getText().toString());
        server.execute(true, false);
    }

    private void doCheckAuthNum() {
        ReqBasic server = new ReqBasic(act, NetUrls.AUTH_EMAIL_CHECK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS) || StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "인증되었습니다.");
                            binding.etAuthNum.setEnabled(false);
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "value"));
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

        server.setTag("Check Email Auth");
        server.addParams("email", email);
        server.addParams("auth_num", binding.etAuthNum.getText().toString());
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                act.onBackPressed();
                break;

            case R.id.fl_next:
                if (binding.etAuthNum.isEnabled()) {
                    Common.showToast(act, "이메일 인증을 진행해주세요");
                    return;
                } else if (!binding.ivPwSelect.isSelected()) {
                    Common.showToast(act, "비밀번호를 확인해주세요");
                    return;
                } else if (!binding.ivPwConfirmSelect.isSelected()) {
                    Common.showToast(act, "비밀번호를 확인해주세요");
                    return;
                } else if (binding.etNick.length() == 0 || binding.etNick.length() > 6) {
                    Common.showToast(act, "닉네임을 확인해주세요");
                    return;
                } else
                    inspect_nick();
                break;


            case R.id.tv_request_check:
                String text = binding.etEmail.getText().toString();
                if (!StringUtil.isNull(text)) {
                    Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(text);
                    if (matcher.find()) {
                        inspect_id();
                    } else {
                        Common.showToast(act, "이메일을 확인해주세요");
                    }
                } else {
                    Common.showToast(act, "이메일을 입력해주세요");
                }
                break;

            case R.id.tv_auth_check:
                if (StringUtil.isNull(email)) {
                    Common.showToast(act, "이메일을 입력해주세요");
                    return;
                } else if (binding.etAuthNum.length() != 6) {
                    Common.showToast(act, "인증번호를 확인해주세요");
                    return;
                }

                doCheckAuthNum();
                break;
        }
    }

    private void inspect_id() {
        ReqBasic server = new ReqBasic(act, NetUrls.INSPECT_ID) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            doCheckEmail();
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

        server.setTag("Inspect Id");
        server.addParams("id", binding.etEmail.getText().toString());
        server.execute(true, false);
    }

    private void nextProcess() {
        JoinAct.joinData.setNick(binding.etNick.getText().toString());
        JoinAct.joinData.setId(email);
        JoinAct.joinData.setPw(binding.etPwConfirm.getText().toString());

        BasicFrag fragment = new Join02Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }

    private void setTextWatcher() {
        binding.etPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!StringUtil.isNull(text)) {
                    Matcher matcher = VALID_PASSWORD_REGEX.matcher(text);
                    if (matcher.find()) {
                        binding.ivPwSelect.setSelected(true);
                        if(binding.etPw.getText().toString().equalsIgnoreCase(binding.etPwConfirm.getText().toString())) {
                            binding.ivPwConfirmSelect.setSelected(true);
                        } else {
                            binding.ivPwConfirmSelect.setSelected(false);
                        }
                    } else {
                        binding.ivPwSelect.setSelected(false);
                        binding.ivPwConfirmSelect.setSelected(false);
                    }
                } else {
                    binding.ivPwSelect.setSelected(false);
                    binding.ivPwConfirmSelect.setSelected(false);
                }
            }
        });

        binding.etPwConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (!StringUtil.isNull(text) && binding.etPw.length() != 0) {
                    if (binding.etPw.getText().toString().equalsIgnoreCase(binding.etPwConfirm.getText().toString()) && binding.ivPwSelect.isSelected()) {
                        binding.ivPwConfirmSelect.setSelected(true);
                    } else {
                        binding.ivPwConfirmSelect.setSelected(false);
                    }
                } else {
                    binding.ivPwConfirmSelect.setSelected(false);
                }
            }
        });
    }
}