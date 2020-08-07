package kr.co.core.thefiven.fragment;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.HashMap;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.InterestSubAct;
import kr.co.core.thefiven.activity.JoinAct;
import kr.co.core.thefiven.data.InterestSubData;
import kr.co.core.thefiven.databinding.FragmentJoin06Binding;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class Join06Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin06Binding binding;
    private AppCompatActivity act;

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


    private String[] array;
    private TypedArray array_drawable;
    private String interest = "";
    public static HashMap<String, InterestSubData> interest_map = new HashMap<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_06, container, false);
        act = (AppCompatActivity) getActivity();

        /* set interest data list */
        array = getResources().getStringArray(R.array.array_interest);
        array_drawable = getResources().obtainTypedArray(R.array.array_interest_int);
        for (int i = 0; i < array.length; i++) {
            interest_map.put(array[i], new InterestSubData(array[i], array_drawable.getResourceId(i, 0), false));
        }
        array_drawable.recycle();

        /* set click listener */
        binding.flNext.setOnClickListener(this);
        binding.flSkip.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        binding.llTv.setOnClickListener(this);
        binding.llMovie.setOnClickListener(this);
        binding.llSports.setOnClickListener(this);
        binding.llMusic.setOnClickListener(this);
        binding.llTravel.setOnClickListener(this);
        binding.llFashion.setOnClickListener(this);
        binding.llTaste.setOnClickListener(this);
        binding.llHealth.setOnClickListener(this);
        binding.llEnjoy.setOnClickListener(this);
        binding.llAnimal.setOnClickListener(this);

        /* set tag */
        binding.llTv.setTag(TYPE_TV);
        binding.llMovie.setTag(TYPE_MOVIE);
        binding.llSports.setTag(TYPE_SPORTS);
        binding.llMusic.setTag(TYPE_MUSIC);
        binding.llTravel.setTag(TYPE_TRAVEL);
        binding.llFashion.setTag(TYPE_FASHION);
        binding.llTaste.setTag(TYPE_TASTE);
        binding.llHealth.setTag(TYPE_HEALTH);
        binding.llEnjoy.setTag(TYPE_ENJOY);
        binding.llAnimal.setTag(TYPE_ANIMAL);

        /* set image */
        setImage(binding.ivTv, R.drawable.tv_main);
        setImage(binding.ivMovie, R.drawable.movie_main);
        setImage(binding.ivSports, R.drawable.sports_main);
        setImage(binding.ivMusic, R.drawable.music_main);
        setImage(binding.ivTravel, R.drawable.travel_main);
        setImage(binding.ivFashion, R.drawable.fashion_main);
        setImage(binding.ivTaste, R.drawable.taste_main);
        setImage(binding.ivBeauty, R.drawable.beauty_main);
        setImage(binding.ivCar, R.drawable.car_main);
        setImage(binding.ivAnimal, R.drawable.animal_image01);

        /* 싱글팝업 */
        startActivity(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_INTEREST));

        return binding.getRoot();
    }

    private void setImage(ImageView imageView, int resource) {
        Glide.with(act)
                .load(resource)
                .centerCrop()
                .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                .into(imageView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_tv:
            case R.id.ll_movie:
            case R.id.ll_sports:
            case R.id.ll_music:
            case R.id.ll_travel:
            case R.id.ll_fashion:
            case R.id.ll_taste:
            case R.id.ll_health:
            case R.id.ll_enjoy:
            case R.id.ll_animal:
                startActivity(new Intent(act, InterestSubAct.class).putExtra("type", (int) view.getTag()).putExtra("from", "fromJoin"));
                break;

            case R.id.fl_next:
                boolean isFirst = true;
                interest = "";
                for (String key : array) {
                    InterestSubData data = interest_map.get(key);
                    if(data.isSelected()) {
                        if (isFirst) {
                            interest += data.getTitle();
                            isFirst = false;
                        } else {
                            interest += "," + data.getTitle();
                        }
                    }
                }

                Log.i(StringUtil.TAG, "interest: " + interest);

                if (StringUtil.isNull(interest)) {
                    Common.showToast(act, "관심사를 선택해주세요");
                } else {
                    nextProcess(false);
                }
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
            JoinAct.joinData.setInterests("");
        } else {
            JoinAct.joinData.setInterests(interest);
        }

        BasicFrag fragment = new Join07Frag();
        ((JoinAct) act).replaceFragment(fragment);
    }
}