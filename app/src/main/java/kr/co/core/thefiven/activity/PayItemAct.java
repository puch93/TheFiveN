package kr.co.core.thefiven.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivityPayItemBinding;
import kr.co.core.thefiven.utility.Common;

public class PayItemAct extends BasicAct implements View.OnClickListener {
    ActivityPayItemBinding binding;
    Activity act;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_pay_item, null);
        act = this;

        /* set click listener */
        binding.llPayMembership.setOnClickListener(this);
        binding.llPayPoint.setOnClickListener(this);
        binding.llPayLike.setOnClickListener(this);
//        binding.llPayGifticon.setOnClickListener(this);

        binding.flBack.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;

            case R.id.ll_pay_membership:
                startActivity(new Intent(act, PayMemberAct.class));
                break;

            case R.id.ll_pay_point:
                startActivity(new Intent(act, PayPointAct.class));
                break;

            case R.id.ll_pay_like:
                startActivity(new Intent(act, PayLikeAct.class));
                break;

            case R.id.ll_pay_gifticon:
                Common.showToastDevelop(act);
                break;
        }
    }
}
