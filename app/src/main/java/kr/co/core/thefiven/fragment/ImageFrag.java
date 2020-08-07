package kr.co.core.thefiven.fragment;

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

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.data.OtherProfileImageData;
import kr.co.core.thefiven.databinding.ItemProfileImageBinding;
import kr.co.core.thefiven.utility.StringUtil;

public class ImageFrag extends BasicFrag {
    private ItemProfileImageBinding binding;
    private AppCompatActivity act;
    private OtherProfileImageData imageData;
    private String gender;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_profile_image, container, false);
        act = (AppCompatActivity) getActivity();

        checkImageState(binding.ivProfile, imageData.getProfile_img(), imageData.getProfile_img_ck());

        return binding.getRoot();
    }

    /**
     * 검수완료: Y / 검수중: N
     * */
    private void checkImageState(ImageView imageView, String dataUrl, String state) {
        if (state.equalsIgnoreCase("Y")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .into(imageView);
        } else if (state.equalsIgnoreCase("N") || state.equalsIgnoreCase("fail")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .transform(new BlurTransformation(30, 3))
                    .into(imageView);
        } else {
            if(!StringUtil.isNull(gender)) {
                if (gender.equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .into(imageView);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .into(imageView);
                }
            } else {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .into(imageView);
            }
        }
    }
    public void setData(OtherProfileImageData data, String gender) {
        this.imageData = data;
        this.gender = gender;
        Log.i(StringUtil.TAG, "setData: " + data);
    }
}