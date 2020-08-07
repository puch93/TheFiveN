package kr.co.core.thefiven.activity.gift;

import android.os.Bundle;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.data.GiftStorageData;
import kr.co.core.thefiven.databinding.ActivityGiftStorageDetailBinding;

public class GiftStorageDetailAct extends BasicAct {
    ActivityGiftStorageDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gift_storage_detail, null);

        GiftStorageData data = (GiftStorageData) getIntent().getSerializableExtra("data");
        Glide.with(this).load(data.getP_barcode_image()).into(binding.ivBarcodeImage);
        binding.tvLimitDate.setText(data.getP_limit_date());
        binding.tvName.setText(data.getP_name());

        binding.flBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
