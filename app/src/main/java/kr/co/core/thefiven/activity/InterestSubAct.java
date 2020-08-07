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
import kr.co.core.thefiven.activity.myinfo.MyInfoInterestAct;
import kr.co.core.thefiven.adapter.InterestSubAdapter;
import kr.co.core.thefiven.data.InterestSubData;
import kr.co.core.thefiven.databinding.ActivityInterestSubBinding;
import kr.co.core.thefiven.fragment.Join06Frag;
import kr.co.core.thefiven.utility.AllOfDecoration;
import kr.co.core.thefiven.utility.StatusBarUtil;

public class InterestSubAct extends BasicAct {
    ActivityInterestSubBinding binding;
    Activity act;

    ArrayList<InterestSubData> list = new ArrayList<>();
    InterestSubAdapter adapter;
    LinearLayoutManager manager;

    private ArrayList<String> array_title;
    private ArrayList<String> array_image;

    private static final int TYPE_TV = 101;
    private static final int TYPE_MOVIE = 102;
    private static final int TYPE_SPORTS = 103;
    private static final int TYPE_MUSIC = 104;
    private static final int TYPE_TRAVEL = 105;
    private static final int TYPE_FASHION = 106;
    private static final int TYPE_TASTE = 107;
    private static final int TYPE_HEALTH = 108;
    private static final int TYPE_ENJOY = 109;
    private static final int TYPE_ANIMAL = 110;

    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_interest_sub);
        act = this;

        from = getIntent().getStringExtra("from");

        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        switch (getIntent().getIntExtra("type", -1)) {
            case TYPE_TV:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_tv)));
                binding.tvTitle.setText("TV");
                break;
            case TYPE_MOVIE:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_movie)));
                binding.tvTitle.setText("영화");
                break;
            case TYPE_SPORTS:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_sports)));
                binding.tvTitle.setText("스포츠");
                break;
            case TYPE_MUSIC:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_music)));
                binding.tvTitle.setText("음악");
                break;
            case TYPE_TRAVEL:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_travel)));
                binding.tvTitle.setText("여행");
                break;
            case TYPE_FASHION:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_fashion)));
                binding.tvTitle.setText("패션");
                break;
            case TYPE_TASTE:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_taste)));
                binding.tvTitle.setText("맛집,요리,술");
                break;
            case TYPE_HEALTH:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_health)));
                binding.tvTitle.setText("미용,건강");
                break;
            case TYPE_ENJOY:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_enjoy)));
                binding.tvTitle.setText("자동차,오토바이,게임");
                break;
            case TYPE_ANIMAL:
                array_title = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.interest_animal)));
                binding.tvTitle.setText("동물");
                break;
            default:
                finish();
                break;
        }

        if (from.equalsIgnoreCase("fromJoin")) {
            for (int i = 0; i < array_title.size(); i++) {
                list.add(Join06Frag.interest_map.get(array_title.get(i)));
            }
        } else {
            for (int i = 0; i < array_title.size(); i++) {
                list.add(MyInfoInterestAct.interest_map.get(array_title.get(i)));
            }
        }


        /* set recycler view */
        manager = new LinearLayoutManager(act);

        binding.rcvInterest.setLayoutManager(manager);
        binding.rcvInterest.setHasFixedSize(true);
        binding.rcvInterest.setItemViewCacheSize(20);
        adapter = new InterestSubAdapter(act, list, new InterestSubAdapter.ItemClickListener() {
            @Override
            public void clicked(InterestSubData data) {
                if(from.equalsIgnoreCase("fromJoin")) {
                    Join06Frag.interest_map.put(data.getTitle(), data);
                } else {
                    MyInfoInterestAct.interest_map.put(data.getTitle(), data);
                }
            }
        });
        binding.rcvInterest.setAdapter(adapter);
        binding.rcvInterest.addItemDecoration(new AllOfDecoration(act, "top10dp"));

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
