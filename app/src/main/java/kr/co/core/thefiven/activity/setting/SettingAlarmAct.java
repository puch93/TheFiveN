package kr.co.core.thefiven.activity.setting;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.databinding.ActivitySettingAlarmBinding;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.StatusBarUtil;

public class SettingAlarmAct extends BasicAct implements CompoundButton.OnCheckedChangeListener {
    ActivitySettingAlarmBinding binding;
    Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_alarm, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        /* set listener */
        binding.swLike.setOnCheckedChangeListener(this);
        binding.swLike.setTag(AppPreference.ALARM_LIKE);
        binding.swLike.setChecked(AppPreference.getAlarmPref(act, AppPreference.ALARM_LIKE));

        binding.swMatching.setOnCheckedChangeListener(this);
        binding.swMatching.setTag(AppPreference.ALARM_MATCHING);
        binding.swMatching.setChecked(AppPreference.getAlarmPref(act, AppPreference.ALARM_MATCHING));

        binding.swChatting.setOnCheckedChangeListener(this);
        binding.swChatting.setTag(AppPreference.ALARM_CHATTING);
        binding.swChatting.setChecked(AppPreference.getAlarmPref(act, AppPreference.ALARM_CHATTING));

        binding.swOther.setOnCheckedChangeListener(this);
        binding.swOther.setTag(AppPreference.ALARM_OTHER);
        binding.swOther.setChecked(AppPreference.getAlarmPref(act, AppPreference.ALARM_OTHER));

        binding.swLikeMessage.setOnCheckedChangeListener(this);
        binding.swLikeMessage.setTag(AppPreference.ALARM_LIKE_MESSAGE);
        binding.swLikeMessage.setChecked(AppPreference.getAlarmPref(act, AppPreference.ALARM_LIKE_MESSAGE));

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        AppPreference.setAlarmPref(act, (String) buttonView.getTag(), isChecked);
    }
}
