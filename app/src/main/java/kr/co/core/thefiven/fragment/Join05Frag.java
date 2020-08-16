package kr.co.core.thefiven.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.JoinAct;
import kr.co.core.thefiven.databinding.FragmentJoin05Binding;
import kr.co.core.thefiven.dialog.ListProfileDlg;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join05Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin05Binding binding;
    private AppCompatActivity act;

    private static final int INFO_DIALOG = 100;

    private View selectedView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_05, container, false);
        act = (AppCompatActivity) getActivity();

        binding.flNext.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);
        binding.flSkip.setOnClickListener(this);
        binding.areaAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        binding.llHeightArea.setOnClickListener(this);
        binding.llBodyArea.setOnClickListener(this);
        binding.llDrinkArea.setOnClickListener(this);
        binding.llSmokeArea.setOnClickListener(this);

        binding.llHeightArea.setTag(R.string.prof_tag01, StringUtil.PROF_HEIGHT);
        binding.llHeightArea.setTag(R.string.prof_tag02, binding.tvHeight);
        binding.llBodyArea.setTag(R.string.prof_tag01, StringUtil.PROF_BODY);
        binding.llBodyArea.setTag(R.string.prof_tag02, binding.tvBody);
        binding.llDrinkArea.setTag(R.string.prof_tag01, StringUtil.PROF_DRINK);
        binding.llDrinkArea.setTag(R.string.prof_tag02, binding.tvDrink);
        binding.llSmokeArea.setTag(R.string.prof_tag01, StringUtil.PROF_SMOKE);
        binding.llSmokeArea.setTag(R.string.prof_tag02, binding.tvSmoke);

        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String value = data.getStringExtra("value");
            String type = data.getStringExtra("type");

            if (!StringUtil.isNull(value)) {
                if (value.equalsIgnoreCase(StringUtil.PROF_NO_DATA)) {
                    ((TextView) selectedView.getTag(R.string.prof_tag02)).setText(null);
                } else {
                    ((TextView) selectedView.getTag(R.string.prof_tag02)).setText(value);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.ll_height_area:
            case R.id.ll_body_area:
            case R.id.ll_drink_area:
            case R.id.ll_smoke_area:
                selectedView = view;

                intent = new Intent(act, ListProfileDlg.class);
                intent.putExtra("type", (String) view.getTag(R.string.prof_tag01));
                intent.putExtra("data", ((TextView) view.getTag(R.string.prof_tag02)).getText().toString());
                startActivityForResult(intent, INFO_DIALOG);
                break;

            case R.id.fl_next:
                if(binding.tvHeight.length() == 0) {
                    Common.showToast(act, "키를 선택해주세요");
                    return;
                } else if(binding.tvBody.length() == 0) {
                    Common.showToast(act, "체형을 선택해주세요");
                    return;
                } else if(binding.tvDrink.length() == 0) {
                    Common.showToast(act, "음주량을 선택해주세요");
                    return;
                } else if(binding.tvSmoke.length() == 0) {
                    Common.showToast(act, "흡연량을 선택해주세요");
                    return;
                }
                nextProcess(false);
                break;

            case R.id.fl_skip:
                nextProcess(true);
                break;

            case R.id.fl_back:
                act.onBackPressed();
                break;
        }
    }

    private void nextProcess(boolean isPass) {
        if(isPass) {
            JoinAct.joinData.setHeight("");
            JoinAct.joinData.setBody("");
            JoinAct.joinData.setDrink("");
            JoinAct.joinData.setSmoke("");
        } else {
            if(binding.tvHeight.length() == 0) {
                JoinAct.joinData.setHeight("none");
            } else {
                JoinAct.joinData.setHeight(binding.tvHeight.getText().toString());
            }

            if(binding.tvBody.length() == 0) {
                JoinAct.joinData.setBody("none");
            } else {
                JoinAct.joinData.setBody(binding.tvBody.getText().toString());
            }

            if(binding.tvDrink.length() == 0) {
                JoinAct.joinData.setDrink("none");
            } else {
                JoinAct.joinData.setDrink(binding.tvDrink.getText().toString());
            }

            if(binding.tvSmoke.length() == 0) {
                JoinAct.joinData.setSmoke("none");
            } else {
                JoinAct.joinData.setSmoke(binding.tvSmoke.getText().toString());
            }
        }

        BasicFrag fragment = new Join06Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }
}