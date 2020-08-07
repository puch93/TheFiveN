package kr.co.core.thefiven.dialog;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.DialogCgpmsCodeBinding;
import kr.co.core.thefiven.utility.StringUtil;

public class CgpmsConfirmDlg extends BasicDlg {
    DialogCgpmsCodeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_cgpms_code, null);

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
        binding.tvCgpmsDescription.setText(getIntent().getStringExtra("cgpms_description"));
        binding.tvNick.setText(getIntent().getStringExtra("nick"));
        String cgpms_code = getIntent().getStringExtra("code");
        binding.tvCgpmsCode.setText(cgpms_code);
        cgpms_code = cgpms_code.substring(cgpms_code.length() - 2);
        switch (cgpms_code) {
            case "CP":
                binding.ivCgpmsCp.setSelected(true);
                binding.tvCgpmsDetail.setText("CP(CHILD PERSON)");
                break;
            case "GP":
                binding.ivCgpmsGp.setSelected(true);
                binding.tvCgpmsDetail.setText("GP(GROWN UP PERSON)");
                break;
            case "PP":
                binding.ivCgpmsPp.setSelected(true);
                binding.tvCgpmsDetail.setText("PP(PRIVATE PERSON)");
                break;
            case "MP":
                binding.ivCgpmsMp.setSelected(true);
                binding.tvCgpmsDetail.setText("MP(MASOCHIST PERSON)");
                break;
            case "SP":
                binding.ivCgpmsSp.setSelected(true);
                binding.tvCgpmsDetail.setText("SP(SADIST PERSON)");
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
