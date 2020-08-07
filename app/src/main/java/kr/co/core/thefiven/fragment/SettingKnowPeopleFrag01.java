package kr.co.core.thefiven.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.TermsAct;
import kr.co.core.thefiven.activity.setting.SettingKnowPeopleAct2;
import kr.co.core.thefiven.databinding.FragmentSettingKnowPeople01Binding;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class SettingKnowPeopleFrag01 extends BasicFrag {
    private FragmentSettingKnowPeople01Binding binding;
    private AppCompatActivity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_know_people_01, container, false);
        act = (AppCompatActivity) getActivity();

        binding.flNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(binding.cbConfirm.isChecked()) {
                    ((SettingKnowPeopleAct2) act).replaceFragment(new SettingKnowPeopleFrag02());
                } else {
                    Common.showToast(act, "위의 내용을 확인해주신 후, 확인표시에 체크 부탁드립니다");
                }
            }
        });

        binding.flPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, TermsAct.class).putExtra("type", StringUtil.TERMS_PRIVATE));
            }
        });

        return binding.getRoot();
    }

}