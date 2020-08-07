package kr.co.core.thefiven.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.DialogCgpmsCodeBinding;
import kr.co.core.thefiven.databinding.DialogCgpmsMatchingBinding;
import kr.co.core.thefiven.utility.AppPreference;

public class CgpmsPointDlg extends BasicDlg {
    DialogCgpmsMatchingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_cgpms_matching, null);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.tvCgpmsPoint.setText(getIntent().getStringExtra("point"));
        binding.tvCgpmsMatchingText.setText(getIntent().getStringExtra("explanation"));

        String cgpms_me = AppPreference.getProfilePref(getApplicationContext(), AppPreference.PREF_CGPMS);
        cgpms_me = cgpms_me.substring(cgpms_me.length()-2);
        String cgpms_other = getIntent().getStringExtra("code");

        int me_image;
        switch (cgpms_me) {
            case "CP":
            default:
                me_image = R.drawable.ic_popup_cp_copy;
                break;
            case "GP":
                me_image = R.drawable.ic_popup_gp;
                break;
            case "PP":
                me_image = R.drawable.ic_popup_pp;
                break;
            case "MP":
                me_image = R.drawable.ic_popup_mp;
                break;
            case "SP":
                me_image = R.drawable.ic_popup_sp;
                break;
        }

        Glide.with(getApplicationContext()).load(me_image).into(binding.ivCgpmsMe);

        int other_image;
        switch (cgpms_other) {
            case "CP":
            default:
                other_image = R.drawable.ic_popup_cp_copy;
                break;
            case "GP":
                other_image = R.drawable.ic_popup_gp;
                break;
            case "PP":
                other_image = R.drawable.ic_popup_pp;
                break;
            case "MP":
                other_image = R.drawable.ic_popup_mp;
                break;
            case "SP":
                other_image = R.drawable.ic_popup_sp;
                break;
        }

        Glide.with(getApplicationContext()).load(other_image).into(binding.ivCgpmsOther);
    }

    @Override
    public void onBackPressed() {
    }
}
