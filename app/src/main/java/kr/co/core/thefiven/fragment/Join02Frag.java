package kr.co.core.thefiven.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import java.util.Calendar;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.JoinAct;
import kr.co.core.thefiven.databinding.FragmentJoin02Binding;
import kr.co.core.thefiven.dialog.DatePickerDialog;
import kr.co.core.thefiven.dialog.ListProfileDlg;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join02Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin02Binding binding;
    private AppCompatActivity act;

    private static final int INFO_DIALOG = 100;
    private static final int INFO_DIALOG2 = 102;
    private static final int DATE_PICKER = 101;

    private int year = 1970;
    private int month = 1;
    private int day = 1;

    private String gender = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.i(StringUtil.TAG, "onCreateView: ");
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_02, container, false);
        act = (AppCompatActivity) getActivity();

        startActivity(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_BIRTH));

        binding.flNext.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);
        binding.llBirthArea.setOnClickListener(this);
        binding.llGenderArea.setOnClickListener(this);
        binding.llLocationArea.setOnClickListener(this);

        // 음력체크
        binding.rgSolarLunar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_lunar) {
                    binding.rgSolarLunar.setTag("lunar");
                } else {
                    binding.rgSolarLunar.setTag("solar");
                }
            }
        });
        binding.rbSolar.setChecked(true);
        binding.rgSolarLunar.setTag("solar");

        // 태어난 시 체크
        binding.cbBornTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbBornTime.setTag("Y");
                } else {
                    binding.cbBornTime.setTag("N");
                }
            }
        });
        binding.cbBornTime.setTag("N");

        // 쌍둥이체크
        binding.cbTwin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    binding.cbTwin.setTag("Y");
                } else {
                    binding.cbTwin.setTag("N");
                }
            }
        });
        binding.cbTwin.setTag("N");

        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(StringUtil.TAG, "onResume: ");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            String value = data.getStringExtra("value");

            if (requestCode == INFO_DIALOG) {
                if (value.equalsIgnoreCase(StringUtil.PROF_NO_DATA)) {
                    binding.tvGender.setText(null);
                    gender = "";
                } else {
                    binding.tvGender.setText(value);
                    if (value.equalsIgnoreCase("남성")) {
                        gender = "male";
                    } else {
                        gender = "female";
                    }
                }
            } else if (requestCode == DATE_PICKER) {
                year = data.getIntExtra("year", 0);
                month = data.getIntExtra("month", 0);
                day = data.getIntExtra("day", 0);

                String year_s = String.valueOf(year);
                String month_s = String.valueOf(month);
                String day_s = String.valueOf(day);

                if (month_s.length() == 1)
                    month_s = "0" + month_s;

                if (day_s.length() == 1)
                    day_s = "0" + day_s;

                binding.tvBirth.setText(year_s + "년 " + month_s + "월 " + day_s + "일");
            } else {
                if (!StringUtil.isNull(value)) {
                    if (value.equalsIgnoreCase(StringUtil.PROF_NO_DATA)) {
                        binding.tvLocation.setText(null);
                    } else {
                        binding.tvLocation.setText(value);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;

        switch (view.getId()) {
            case R.id.ll_birth_area:
                intent = new Intent(act, DatePickerDialog.class);
                intent.putExtra("year", year);
                intent.putExtra("month", month);
                intent.putExtra("day", day);
                startActivityForResult(intent, DATE_PICKER);
                break;

            case R.id.ll_gender_area:
                intent = new Intent(act, ListProfileDlg.class);
                intent.putExtra("type", "gender");
                intent.putExtra("data", binding.tvGender.getText().toString());
                startActivityForResult(intent, INFO_DIALOG);
                break;

            case R.id.ll_location_area:
                intent = new Intent(act, ListProfileDlg.class);
                intent.putExtra("type", "location");
                intent.putExtra("data", binding.tvLocation.getText().toString());
                startActivityForResult(intent, INFO_DIALOG2);
                break;

            case R.id.fl_next:
                if (binding.tvGender.length() == 0) {
                    Common.showToast(act, "성별을 선택해주세요");
                    return;
                } else if (binding.tvBirth.length() == 0) {
                    Common.showToast(act, "생년월일을 선택해주세요");
                    return;
                } else if (binding.tvLocation.length() == 0) {
                    Common.showToast(act, "지역을 선택해주세요");
                    return;
                }

                nextProcess();
                break;

            case R.id.fl_back:
                act.onBackPressed();
                break;
        }
    }

    private void nextProcess() {
        String year_s = "";
        String month_s = "";
        String day_s = "";

        if (((String) binding.cbBornTime.getTag()).equalsIgnoreCase("Y")) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);

            year_s = String.valueOf(calendar.get(Calendar.YEAR));
            month_s = String.valueOf(calendar.get(Calendar.MONTH));
            day_s = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            year_s = String.valueOf(year);
            month_s = String.valueOf(month);
            day_s = String.valueOf(day);
        }

        if (month_s.length() == 1)
            month_s = "0" + month_s;

        if (day_s.length() == 1)
            day_s = "0" + day_s;

        JoinAct.joinData.setGender(gender);
        JoinAct.joinData.setBirth(year_s + month_s + day_s);
        JoinAct.joinData.setBirth_type((String) binding.rgSolarLunar.getTag());
        JoinAct.joinData.setBirth_twin((String) binding.cbTwin.getTag());
        JoinAct.joinData.setLocation(binding.tvLocation.getText().toString());

        BasicFrag fragment = new Join03Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }
}