package kr.co.core.thefiven.activity;

import android.os.Bundle;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.data.UserData;
import kr.co.core.thefiven.databinding.ActivityJoinBinding;
import kr.co.core.thefiven.fragment.BasicFrag;
import kr.co.core.thefiven.fragment.Join01Frag;
import kr.co.core.thefiven.fragment.Join02Frag;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class JoinAct extends BasicAct {
    ActivityJoinBinding binding;

    public static UserData joinData;
    public static FragmentManager fragmentManager;


    private String facebook_token = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_join, null);
        joinData = new UserData();

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        fragmentManager = getSupportFragmentManager();

        facebook_token = getIntent().getStringExtra("id");
        BasicFrag fragment;
        if(!StringUtil.isNull(facebook_token)) {
            joinData.setJoinType("facebook");
            joinData.setId(facebook_token);
            joinData.setPw(facebook_token);

            fragment = new Join02Frag();
        } else {
            joinData.setJoinType("normal");

            fragment = new Join01Frag();
        }

        joinData.setPhone(getIntent().getStringExtra("phone"));

//        Bundle bundle = new Bundle(1);
//        bundle.putString("phone", getIntent().getStringExtra("phone"));
//        fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        Log.i(StringUtil.TAG, "getBackStackEntryCount: " + fragmentManager.getBackStackEntryCount());
        if (fragmentManager.getBackStackEntryCount() > 1 ){
            fragmentManager.popBackStack();
        } else {
            finish();
        }
//        super.onBackPressed();
    }

    public void replaceFragment(BasicFrag frag) {
        /* replace fragment */
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if(!(frag instanceof Join01Frag)) {
            transaction.setCustomAnimations(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left, R.anim.anim_slide_in_left, R.anim.anim_slide_out_right);
        }
        transaction.add(R.id.ll_replace_area, frag);
        transaction.addToBackStack(null);

        System.out.println("check point : " + frag);
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult(RESULT_CANCELED);
    }
}
