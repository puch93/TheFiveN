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
import kr.co.core.thefiven.databinding.FragmentJoin03Binding;
import kr.co.core.thefiven.dialog.ListProfileDlg;
import kr.co.core.thefiven.dialog.ListProfileMultiDlg;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join03Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin03Binding binding;
    private AppCompatActivity act;

    private static final int INFO_DIALOG = 100;

    private View selectedView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_03, container, false);
        act = (AppCompatActivity) getActivity();

        binding.flNext.setOnClickListener(this);
        binding.flSkip.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);
        binding.areaAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        binding.llJobArea.setOnClickListener(this);
        binding.llSalaryArea.setOnClickListener(this);
        binding.llMarriageArea.setOnClickListener(this);
        binding.llPersonalityArea.setOnClickListener(this);
        binding.llNationalityArea.setOnClickListener(this);

        binding.llJobArea.setTag(R.string.prof_tag01, StringUtil.PROF_JOB);
        binding.llJobArea.setTag(R.string.prof_tag02, binding.tvJob);
        binding.llSalaryArea.setTag(R.string.prof_tag01, StringUtil.PROF_SALARY);
        binding.llSalaryArea.setTag(R.string.prof_tag02, binding.tvSalary);
        binding.llMarriageArea.setTag(R.string.prof_tag01, StringUtil.PROF_MARRIAGE);
        binding.llMarriageArea.setTag(R.string.prof_tag02, binding.tvMarriage);
        binding.llPersonalityArea.setTag(R.string.prof_tag01, StringUtil.PROF_PERSONALITY);
        binding.llPersonalityArea.setTag(R.string.prof_tag02, binding.tvPersonality);
        binding.llNationalityArea.setTag(R.string.prof_tag01, StringUtil.PROF_NATIONALITY);
        binding.llNationalityArea.setTag(R.string.prof_tag02, binding.tvNationality);

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
            case R.id.ll_job_area:
            case R.id.ll_salary_area:
            case R.id.ll_marriage_area:
            case R.id.ll_nationality_area:
                selectedView = view;

                intent = new Intent(act, ListProfileDlg.class);
                intent.putExtra("type", (String) view.getTag(R.string.prof_tag01));
                intent.putExtra("data", ((TextView) view.getTag(R.string.prof_tag02)).getText().toString());
                startActivityForResult(intent, INFO_DIALOG);
                break;

            case R.id.ll_personality_area:
                selectedView = view;

                intent = new Intent(act, ListProfileMultiDlg.class);
                intent.putExtra("type", (String) view.getTag(R.string.prof_tag01));
                intent.putExtra("data", ((TextView) view.getTag(R.string.prof_tag02)).getText().toString());
                startActivityForResult(intent, INFO_DIALOG);
                break;

            case R.id.fl_next:
                if (binding.tvJob.length() == 0) {
                    Common.showToast(act, "직업을 선택해주세요");
                    return;
                } else if (binding.tvSalary.length() == 0) {
                    Common.showToast(act, "연봉을 선택해주세요");
                    return;
                } else if (binding.tvMarriage.length() == 0) {
                    Common.showToast(act, "결혼여부를 선택해주세요");
                    return;
                } else if (binding.tvPersonality.length() == 0) {
                    Common.showToast(act, "성격을 선택해주세요");
                    return;
                } else if (binding.tvNationality.length() == 0) {
                    Common.showToast(act, "국적을 선택해주세요");
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
            JoinAct.joinData.setJob("");
            JoinAct.joinData.setSalary("");
            JoinAct.joinData.setMarriage("");
            JoinAct.joinData.setPersonality("");
            JoinAct.joinData.setNationality("");

            BasicFrag fragment = new Join04Frag();
            ((JoinAct) act).replaceFragment(fragment);
        } else {
            JoinAct.joinData.setJob(binding.tvJob.getText().toString());
            JoinAct.joinData.setSalary(binding.tvSalary.getText().toString());
            JoinAct.joinData.setMarriage(binding.tvMarriage.getText().toString());
            JoinAct.joinData.setPersonality(binding.tvPersonality.getText().toString());
            JoinAct.joinData.setNationality(binding.tvNationality.getText().toString());

            BasicFrag fragment = new Join04Frag();
            ((JoinAct) act).replaceFragment(fragment);
        }
    }
}