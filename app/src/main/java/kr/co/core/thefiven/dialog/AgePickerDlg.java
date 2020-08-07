package kr.co.core.thefiven.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.NumberPicker;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.DialogAgePickerBinding;
import kr.co.core.thefiven.databinding.DialogCgpmsCodeBinding;
import kr.co.core.thefiven.utility.StringUtil;

public class AgePickerDlg extends BasicDlg {
    DialogAgePickerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_age_picker, null);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes(params);

        setFinishOnTouchOutside(false);

        int small = getIntent().getIntExtra("small", 20);
        int big = getIntent().getIntExtra("big", 20);

        binding.tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int left = binding.npLeft.getValue();
                int right = binding.npRight.getValue();
                Log.i(StringUtil.TAG, "left: " + left);
                Log.i(StringUtil.TAG, "right: " + right);

                Intent intent = new Intent();
                intent.putExtra("big", String.valueOf(left >= right ? left : right));
                intent.putExtra("small", String.valueOf(left <= right ? left : right));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        binding.tvReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        binding.npLeft.setMinValue(20);
        binding.npLeft.setMaxValue(80);
        //disable soft keyboard
        binding.npLeft.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //set wrap true or false, try it you will know the difference
        binding.npLeft.setWrapSelectorWheel(false);

        binding.npRight.setMinValue(20);
        binding.npRight.setMaxValue(80);

        //disable soft keyboard
        binding.npRight.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //set wrap true or false, try it you will know the difference
        binding.npRight.setWrapSelectorWheel(false);

        binding.npLeft.setValue(small);
        binding.npRight.setValue(big);
    }
}
