package kr.co.core.thefiven.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.adapter.SearchSubAdapter;
import kr.co.core.thefiven.databinding.ActivitySearchSubBinding;
import kr.co.core.thefiven.utility.StatusBarUtil;
import kr.co.core.thefiven.utility.StringUtil;

public class SearchSubAct extends BasicAct implements View.OnClickListener {
    ActivitySearchSubBinding binding;
    Activity act;

    private String type;
    private ArrayList<String> array;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_sub, null);
        act = this;

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        binding.flBack.setOnClickListener(this);

        type = getIntent().getStringExtra("type");

        switch (type) {
            case StringUtil.SEARCH_AGE:
                binding.tvTitle.setText("나이");
                array = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_age)));
                break;
            case StringUtil.SEARCH_JOB:
                binding.tvTitle.setText("직업");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_job)));
                break;
            case StringUtil.SEARCH_SALARY:
                binding.tvTitle.setText("연수입");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_salary)));
                break;
            case StringUtil.SEARCH_MARRIAGE:
                binding.tvTitle.setText("결혼이력");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_marriage)));
                break;
            case StringUtil.SEARCH_PERSONALITY:
                binding.tvTitle.setText("성격");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_personality)));
                break;
            case StringUtil.SEARCH_NATIONALITY:
                binding.tvTitle.setText("국적");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_nationality)));
                break;
            case StringUtil.SEARCH_BLOOD:
                binding.tvTitle.setText("혈액형");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_blood)));
                break;
            case StringUtil.SEARCH_LOCATION:
                binding.tvTitle.setText("지역");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_location)));
                array.remove(0);
                break;
            case StringUtil.SEARCH_EDU:
                binding.tvTitle.setText("학력");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_edu)));
                array.remove(0);
                break;
            case StringUtil.SEARCH_HOLIDAY:
                binding.tvTitle.setText("휴일");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_holiday)));
                array.remove(0);
                break;
            case StringUtil.SEARCH_FAMILY:
                binding.tvTitle.setText("형제자매");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_family)));
                array.remove(0);
                break;
            case StringUtil.SEARCH_HEIGHT:
                binding.tvTitle.setText("키");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_height)));
                break;
            case StringUtil.SEARCH_BODY:
                binding.tvTitle.setText("체형");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_body)));
                break;
            case StringUtil.SEARCH_DRINK:
                binding.tvTitle.setText("음주");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_drink)));
                break;
            case StringUtil.SEARCH_SMOKE:
                binding.tvTitle.setText("흡연");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_smoke)));
                break;
            case StringUtil.SEARCH_CGPMS:
                binding.tvTitle.setText("기본성향");
                array =  new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.array_cgpms)));
                break;
        }

        if(array == null) {
            finish();
        }

        array.add(0, "상관없음");

        binding.rcvSearchSub.setLayoutManager(new LinearLayoutManager(act));
        binding.rcvSearchSub.setHasFixedSize(true);
        binding.rcvSearchSub.setItemViewCacheSize(20);
        binding.rcvSearchSub.setAdapter(new SearchSubAdapter(act, array, type));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
        }
    }
}
