package kr.co.core.thefiven.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.databinding.ActivitySettingKnowPeople2Binding;
import kr.co.core.thefiven.fragment.BasicFrag;
import kr.co.core.thefiven.fragment.SettingKnowPeopleFrag01;
import kr.co.core.thefiven.utility.StatusBarUtil;

public class SettingKnowPeopleAct2 extends BasicAct {
    ActivitySettingKnowPeople2Binding binding;
    public static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_know_people2, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        replaceFragment(new SettingKnowPeopleFrag01());
    }

    public void replaceFragment(BasicFrag frag) {
        /* replace fragment */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(!(frag instanceof SettingKnowPeopleFrag01)) {
            transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        transaction.replace(R.id.ll_replace_area, frag);
        transaction.commit();
    }
}


