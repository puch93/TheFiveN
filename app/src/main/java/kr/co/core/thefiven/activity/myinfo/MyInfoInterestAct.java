package kr.co.core.thefiven.activity.myinfo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.activity.InterestSubAct;
import kr.co.core.thefiven.data.InterestSubData;
import kr.co.core.thefiven.databinding.ActivityMyInfoInterestBinding;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class MyInfoInterestAct extends BasicAct implements View.OnClickListener {
    ActivityMyInfoInterestBinding binding;
    Activity act;

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
    private ArrayList<InterestSubData> interest_list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_info_interest, null);
        act = this;

        interest_list = (ArrayList<InterestSubData>) getIntent().getSerializableExtra("list");

        /* set interest data list */
        array = getResources().getStringArray(R.array.array_interest);
        array_drawable = getResources().obtainTypedArray(R.array.array_interest_int);
        for (int i = 0; i < array.length; i++) {
            interest_map.put(array[i], new InterestSubData(array[i], array_drawable.getResourceId(i, 0), false));
        }
        array_drawable.recycle();

        // 선택한 데이터 체크표시
        for (int i = 0; i < interest_list.size(); i++) {
            interest_map.put(interest_list.get(i).getTitle(), interest_list.get(i));
        }


        /* set click listener */
        binding.flBack.setOnClickListener(this);
        binding.flSave.setOnClickListener(this);

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
                startActivity(new Intent(act, InterestSubAct.class).putExtra("type", (int) view.getTag()).putExtra("from", "fromInfo"));
                break;

            case R.id.fl_save:
                boolean isFirst = true;
                interest = "";
                for (String key : array) {
                    InterestSubData data = interest_map.get(key);
                    if (data.isSelected()) {
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
                    // 관심사 리턴값 설정
                    interest_list = new ArrayList<>();
                    interest = interest.replace(", ", "/");
                    String[] interests = interest.split(",");
                    for (String interest : interests) {
                        InterestSubData data = interest_map.get(interest);
                        if (data != null) {
                            data.setSelected(true);
                            interest_list.add(data);
                        }
                    }

                    // 실제 저장함수
                    setInterest();
                }
                break;

            case R.id.fl_back:
                finish();
                break;
        }
    }

    private void setInterest() {
        ReqBasic server = new ReqBasic(act, NetUrls.EDIT_INTEREST) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "return").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "return").equalsIgnoreCase(NetUrls.SUCCESS)) {

                            Intent intent = new Intent();
                            intent.putExtra("list", interest_list);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Set Interest");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("interest", interest);
        server.execute(true, false);
    }
}
