package kr.co.core.thefiven.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.ActivitySearchBinding;
import kr.co.core.thefiven.dialog.AgePickerDlg;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class SearchAct extends BasicAct implements View.OnClickListener {
    ActivitySearchBinding binding;
    Activity act;

    public static final int AGE_PICKER = 1002;

    private String age = "";
    private String job = "";
    private String salary = "";
    private String marriage = "";
    private String personality = "";
    private String nationality = "";
    private String blood = "";
    private String location = "";
    private String education = "";
    private String holiday = "";
    private String family = "";
    private String height = "";
    private String body = "";
    private String drink = "";
    private String smoke = "";
    private String cgpms = "";

    private String intro = "";
    private String join3day = "";
    private String interest = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        setClickListener();
        setTags();

        Intent data = getIntent();
        age = data.getStringExtra(StringUtil.SEARCH_AGE).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_AGE);
        job = data.getStringExtra(StringUtil.SEARCH_JOB).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_JOB);
        salary = data.getStringExtra(StringUtil.SEARCH_SALARY).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_SALARY);
        marriage = data.getStringExtra(StringUtil.SEARCH_MARRIAGE).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_MARRIAGE);
        personality = data.getStringExtra(StringUtil.SEARCH_PERSONALITY).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_PERSONALITY);
        nationality = data.getStringExtra(StringUtil.SEARCH_NATIONALITY).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_NATIONALITY);
        blood = data.getStringExtra(StringUtil.SEARCH_BLOOD).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_BLOOD);
        location = data.getStringExtra(StringUtil.SEARCH_LOCATION).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_LOCATION);
        education = data.getStringExtra(StringUtil.SEARCH_EDU).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_EDU);
        holiday = data.getStringExtra(StringUtil.SEARCH_HOLIDAY).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_HOLIDAY);
        family = data.getStringExtra(StringUtil.SEARCH_FAMILY).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_FAMILY);
        height = data.getStringExtra(StringUtil.SEARCH_HEIGHT).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_HEIGHT);
        body = data.getStringExtra(StringUtil.SEARCH_BODY).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_BODY);
        drink = data.getStringExtra(StringUtil.SEARCH_DRINK).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_DRINK);
        smoke = data.getStringExtra(StringUtil.SEARCH_SMOKE).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_SMOKE);
        cgpms = data.getStringExtra(StringUtil.SEARCH_CGPMS).equalsIgnoreCase("none") ? "" : data.getStringExtra(StringUtil.SEARCH_CGPMS);

        binding.tvAge.setText(age);
        binding.tvJob.setText(job);
        binding.tvSalary.setText(salary);
        binding.tvMarriage.setText(marriage);
        binding.tvPersonality.setText(personality);
        binding.tvNationality.setText(nationality);
        binding.tvBlood.setText(blood);
        binding.tvLocation.setText(location);
        binding.tvEdu.setText(education);
        binding.tvHoliday.setText(holiday);
        binding.tvFamily.setText(family);
        binding.tvHeight.setText(height);
        binding.tvBody.setText(body);
        binding.tvDrink.setText(drink);
        binding.tvSmoke.setText(smoke);
        binding.tvCgpms.setText(cgpms);

        intro = data.getStringExtra(StringUtil.SEARCH_INTRO);
        join3day = data.getStringExtra(StringUtil.SEARCH_JOIN3DAYS);
        interest = data.getStringExtra(StringUtil.SEARCH_INTEREST);

        binding.llIntroIs.setSelected(!intro.equalsIgnoreCase("none"));
        binding.llJoin3days.setSelected(!join3day.equalsIgnoreCase("none"));
        binding.llInterestSame.setSelected(!interest.equalsIgnoreCase("none"));
    }

    private void setTags() {
        binding.llAgeArea.setTag(StringUtil.SEARCH_AGE);
        binding.llJobArea.setTag(StringUtil.SEARCH_JOB);
        binding.llSalaryArea.setTag(StringUtil.SEARCH_SALARY);
        binding.llMarriageArea.setTag(StringUtil.SEARCH_MARRIAGE);
        binding.llPersonalityArea.setTag(StringUtil.SEARCH_PERSONALITY);
        binding.llNationalityArea.setTag(StringUtil.SEARCH_NATIONALITY);
        binding.llBloodArea.setTag(StringUtil.SEARCH_BLOOD);
        binding.llLocationArea.setTag(StringUtil.SEARCH_LOCATION);
        binding.llEduArea.setTag(StringUtil.SEARCH_EDU);
        binding.llHolidayArea.setTag(StringUtil.SEARCH_HOLIDAY);
        binding.llFamilyArea.setTag(StringUtil.SEARCH_FAMILY);
        binding.llHeightArea.setTag(StringUtil.SEARCH_HEIGHT);
        binding.llBodyArea.setTag(StringUtil.SEARCH_BODY);
        binding.llDrinkArea.setTag(StringUtil.SEARCH_DRINK);
        binding.llSmokeArea.setTag(StringUtil.SEARCH_SMOKE);
        binding.llCgpmsArea.setTag(StringUtil.SEARCH_CGPMS);
    }

    private void setClickListener() {
        /* set click listener */
        binding.llAgeArea.setOnClickListener(this);
        binding.llJobArea.setOnClickListener(this);
        binding.llSalaryArea.setOnClickListener(this);
        binding.llMarriageArea.setOnClickListener(this);
        binding.llPersonalityArea.setOnClickListener(this);
        binding.llNationalityArea.setOnClickListener(this);
        binding.llBloodArea.setOnClickListener(this);
        binding.llLocationArea.setOnClickListener(this);
        binding.llEduArea.setOnClickListener(this);
        binding.llHolidayArea.setOnClickListener(this);
        binding.llFamilyArea.setOnClickListener(this);
        binding.llHeightArea.setOnClickListener(this);
        binding.llBodyArea.setOnClickListener(this);
        binding.llDrinkArea.setOnClickListener(this);
        binding.llSmokeArea.setOnClickListener(this);
        binding.llCgpmsArea.setOnClickListener(this);

        binding.llIntroIs.setOnClickListener(this);
        binding.llJoin3days.setOnClickListener(this);
        binding.llInterestSame.setOnClickListener(this);

        binding.flBack.setOnClickListener(this);
        binding.flSearch.setOnClickListener(this);
        binding.flReset.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == 1001) {
                String value = data.getStringExtra("value");

                if (value.equalsIgnoreCase("상관없음")) {
                    value = "";
                }

                switch (data.getStringExtra("type")) {
                    case StringUtil.SEARCH_JOB:
                        binding.tvJob.setText(value);
                        break;
                    case StringUtil.SEARCH_SALARY:
                        binding.tvSalary.setText(value);
                        break;
                    case StringUtil.SEARCH_MARRIAGE:
                        binding.tvMarriage.setText(value);
                        break;
                    case StringUtil.SEARCH_PERSONALITY:
                        binding.tvPersonality.setText(value);
                        break;
                    case StringUtil.SEARCH_NATIONALITY:
                        binding.tvNationality.setText(value);
                        break;
                    case StringUtil.SEARCH_BLOOD:
                        binding.tvBlood.setText(value);
                        break;
                    case StringUtil.SEARCH_LOCATION:
                        binding.tvLocation.setText(value);
                        break;
                    case StringUtil.SEARCH_EDU:
                        binding.tvEdu.setText(value);
                        break;
                    case StringUtil.SEARCH_HOLIDAY:
                        binding.tvHoliday.setText(value);
                        break;
                    case StringUtil.SEARCH_FAMILY:
                        binding.tvFamily.setText(value);
                        break;
                    case StringUtil.SEARCH_HEIGHT:
                        binding.tvHeight.setText(value);
                        break;
                    case StringUtil.SEARCH_BODY:
                        binding.tvBody.setText(value);
                        break;
                    case StringUtil.SEARCH_DRINK:
                        binding.tvDrink.setText(value);
                        break;
                    case StringUtil.SEARCH_SMOKE:
                        binding.tvSmoke.setText(value);
                        break;
                    case StringUtil.SEARCH_CGPMS:
                        binding.tvCgpms.setText(value);
                        break;
                }

            } else if (requestCode == AGE_PICKER) {
                Log.e(StringUtil.TAG, "big: " + data.getStringExtra("big"));
                Log.e(StringUtil.TAG, "small: " + data.getStringExtra("small"));

                String small = data.getStringExtra("small");
                String big = data.getStringExtra("big");
                binding.tvAge.setText(small + "," + big);
            }
        } else {
            if(requestCode == AGE_PICKER) {
                binding.tvAge.setText("");
            }
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.ll_age_area:
                intent = new Intent(act, AgePickerDlg.class);
                if (binding.tvAge.length() != 0) {
                    String[] ages = binding.tvAge.getText().toString().split(",");
                    intent.putExtra("small", Integer.parseInt(ages[0]));
                    intent.putExtra("big", Integer.parseInt(ages[1]));
                }
                startActivityForResult(intent, AGE_PICKER);
                break;

            case R.id.ll_job_area:
            case R.id.ll_salary_area:
            case R.id.ll_marriage_area:
            case R.id.ll_personality_area:
            case R.id.ll_nationality_area:
            case R.id.ll_blood_area:
            case R.id.ll_location_area:
            case R.id.ll_edu_area:
            case R.id.ll_holiday_area:
            case R.id.ll_family_area:
            case R.id.ll_height_area:
            case R.id.ll_body_area:
            case R.id.ll_drink_area:
            case R.id.ll_smoke_area:
            case R.id.ll_cgpms_area:
                startActivityForResult(new Intent(act, SearchSubAct.class).putExtra("type", (String) view.getTag()), 1001);
                break;

            case R.id.ll_intro_is:
            case R.id.ll_join_3days:
            case R.id.ll_interest_same:
                view.setSelected(!view.isSelected());
                break;

            case R.id.fl_back:

                finish();
                break;

            case R.id.fl_reset:
                binding.tvAge.setText("");
                binding.tvJob.setText("");
                binding.tvSalary.setText("");
                binding.tvMarriage.setText("");
                binding.tvPersonality.setText("");
                binding.tvNationality.setText("");
                binding.tvBlood.setText("");
                binding.tvLocation.setText("");
                binding.tvEdu.setText("");
                binding.tvHoliday.setText("");
                binding.tvFamily.setText("");
                binding.tvHeight.setText("");
                binding.tvBody.setText("");
                binding.tvDrink.setText("");
                binding.tvSmoke.setText("");
                binding.tvCgpms.setText("");

                binding.llIntroIs.setSelected(false);
                binding.llJoin3days.setSelected(false);
                binding.llInterestSame.setSelected(false);
                break;

            case R.id.fl_search:
                intent = new Intent();
                intent.putExtra(StringUtil.SEARCH_AGE, binding.tvAge.getText().toString());
                intent.putExtra(StringUtil.SEARCH_JOB, binding.tvJob.getText().toString());
                intent.putExtra(StringUtil.SEARCH_SALARY, binding.tvSalary.getText().toString());
                intent.putExtra(StringUtil.SEARCH_MARRIAGE, binding.tvMarriage.getText().toString());
                intent.putExtra(StringUtil.SEARCH_PERSONALITY, binding.tvPersonality.getText().toString());
                intent.putExtra(StringUtil.SEARCH_NATIONALITY, binding.tvNationality.getText().toString());
                intent.putExtra(StringUtil.SEARCH_BLOOD, binding.tvBlood.getText().toString().toLowerCase());
                intent.putExtra(StringUtil.SEARCH_LOCATION, binding.tvLocation.getText().toString());
                intent.putExtra(StringUtil.SEARCH_EDU, binding.tvEdu.getText().toString());
                intent.putExtra(StringUtil.SEARCH_HOLIDAY, binding.tvHoliday.getText().toString());
                intent.putExtra(StringUtil.SEARCH_FAMILY, binding.tvFamily.getText().toString());
                intent.putExtra(StringUtil.SEARCH_HEIGHT, binding.tvHeight.getText().toString());
                intent.putExtra(StringUtil.SEARCH_BODY, binding.tvBody.getText().toString());
                intent.putExtra(StringUtil.SEARCH_DRINK, binding.tvDrink.getText().toString());
                intent.putExtra(StringUtil.SEARCH_SMOKE, binding.tvSmoke.getText().toString());
                intent.putExtra(StringUtil.SEARCH_CGPMS, binding.tvCgpms.getText().toString().toLowerCase());

                intent.putExtra(StringUtil.SEARCH_INTRO, binding.llIntroIs.isSelected() ? "Y" : "none");
                intent.putExtra(StringUtil.SEARCH_JOIN3DAYS, binding.llJoin3days.isSelected() ? "Y" : "none");
                intent.putExtra(StringUtil.SEARCH_INTEREST, binding.llInterestSame.isSelected() ? "Y" : "none");

                Log.e(StringUtil.TAG, "onClick: " + intent.toString());
                setResult(RESULT_OK, intent);
                finish();
                break;
        }

    }
}
