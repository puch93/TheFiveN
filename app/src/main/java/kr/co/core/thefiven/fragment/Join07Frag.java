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
import kr.co.core.thefiven.activity.JoinAct;
import kr.co.core.thefiven.databinding.FragmentJoin07Binding;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class Join07Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin07Binding binding;
    private AppCompatActivity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_07, container, false);
        act = (AppCompatActivity) getActivity();

        startActivity(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_INTRO));

        binding.flNext.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        return binding.getRoot();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_next:
                if(binding.etIntro.length() == 0) {
                    Common.showToast(act, "자기소개를 작성해주세요");
                    return;
                }

                nextProcess(false);
                break;

            case R.id.fl_back:
                act.onBackPressed();
                break;
        }
    }

    private void nextProcess(boolean isPass) {
        if(isPass) {
            JoinAct.joinData.setIntro("");
        } else {
            if(binding.etIntro.length() == 0) {
                JoinAct.joinData.setIntro("");
            } else {
                JoinAct.joinData.setIntro(binding.etIntro.getText().toString());
            }
        }

        BasicFrag fragment = new Join08Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }
}