package kr.co.core.thefiven.activity.setting;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.databinding.ActivitySettingVisitorBinding;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.StatusBarUtil;

public class SettingVisitorAct extends BasicAct {
    ActivitySettingVisitorBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting_visitor, null);

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.swVisit.setTag(AppPreference.ALARM_VISIT);
        binding.swVisit.setChecked(AppPreference.getAlarmPref(getApplicationContext(), AppPreference.ALARM_VISIT));

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        binding.swVisit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                AppPreference.setAlarmPref(getApplicationContext(), (String) buttonView.getTag(), isChecked);
            }
        });
    }
}
