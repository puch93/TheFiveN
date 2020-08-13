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
import kr.co.core.thefiven.databinding.FragmentJoin04Binding;
import kr.co.core.thefiven.dialog.ListProfileDlg;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join04Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin04Binding binding;
    private AppCompatActivity act;

    private static final int INFO_DIALOG = 100;

    private View selectedView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_04, container, false);
        act = (AppCompatActivity) getActivity();

        binding.flNext.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);
        binding.flSkip.setOnClickListener(this);

        binding.llBloodArea.setOnClickListener(this);
        binding.llEduArea.setOnClickListener(this);
        binding.llHolidayArea.setOnClickListener(this);
        binding.llFamilyArea.setOnClickListener(this);

        binding.llBloodArea.setTag(R.string.prof_tag01, StringUtil.PROF_BLOOD);
        binding.llBloodArea.setTag(R.string.prof_tag02, binding.tvBlood);
        binding.llEduArea.setTag(R.string.prof_tag01, StringUtil.PROF_EDU);
        binding.llEduArea.setTag(R.string.prof_tag02, binding.tvEdu);
        binding.llHolidayArea.setTag(R.string.prof_tag01, StringUtil.PROF_HOLIDAY);
        binding.llHolidayArea.setTag(R.string.prof_tag02, binding.tvHoliday);
        binding.llFamilyArea.setTag(R.string.prof_tag01, StringUtil.PROF_FAMILY);
        binding.llFamilyArea.setTag(R.string.prof_tag02, binding.tvFamily);

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
            case R.id.ll_blood_area:
            case R.id.ll_edu_area:
            case R.id.ll_holiday_area:
            case R.id.ll_family_area:
                selectedView = view;

                intent = new Intent(act, ListProfileDlg.class);
                intent.putExtra("type", (String) view.getTag(R.string.prof_tag01));
                intent.putExtra("data", ((TextView) view.getTag(R.string.prof_tag02)).getText().toString());
                startActivityForResult(intent, INFO_DIALOG);
                break;

            case R.id.fl_next:
                if (binding.tvBlood.length() == 0) {
                    Common.showToast(act, "혈액형을 선택해주세요");
                    return;
                } else if (binding.tvEdu.length() == 0) {
                    Common.showToast(act, "학력을 선택해주세요");
                    return;
                } else if (binding.tvHoliday.length() == 0) {
                    Common.showToast(act, "휴일을 선택해주세요");
                    return;
                } else if (binding.tvFamily.length() == 0) {
                    Common.showToast(act, "형제자매를 선택해주세요");
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
            JoinAct.joinData.setBlood("");
            JoinAct.joinData.setEdu("");
            JoinAct.joinData.setHoliday("");
            JoinAct.joinData.setFamily("");
        } else{
            if (binding.tvBlood.length() == 0) {
                JoinAct.joinData.setBlood("");
            } else {
                JoinAct.joinData.setBlood(binding.tvBlood.getText().toString());
            }

            if (binding.tvEdu.length() == 0) {
                JoinAct.joinData.setEdu("");
            } else {
                JoinAct.joinData.setEdu(binding.tvEdu.getText().toString());
            }

            if (binding.tvHoliday.length() == 0) {
                JoinAct.joinData.setHoliday("");
            } else {
                JoinAct.joinData.setHoliday(binding.tvHoliday.getText().toString());
            }

            if (binding.tvFamily.length() == 0) {
                JoinAct.joinData.setFamily("");
            } else {
                JoinAct.joinData.setFamily(binding.tvFamily.getText().toString());
            }
        }



        BasicFrag fragment = new Join05Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }
}