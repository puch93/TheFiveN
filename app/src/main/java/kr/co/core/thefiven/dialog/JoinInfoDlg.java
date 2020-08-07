package kr.co.core.thefiven.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.NumberPicker;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.databinding.DialogJoinInfoBinding;
import kr.co.core.thefiven.utility.Common;

public class JoinInfoDlg extends BasicDlg {
    private DialogJoinInfoBinding binding;
    private Activity act;

    private String[] array;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_join_info, null);
        act = this;

        type = getIntent().getStringExtra("type");
        switch (type) {
            case "blood":
                array = getResources().getStringArray(R.array.array_blood);
                break;
            case "location":
                array = getResources().getStringArray(R.array.array_location);
                break;
            case "height":
                array = getResources().getStringArray(R.array.array_height);
                break;
            case "body":
                array = getResources().getStringArray(R.array.array_body);
                break;
            case "drink":
                array = getResources().getStringArray(R.array.array_drink);
                break;
            case "smoke":
                array = getResources().getStringArray(R.array.array_smoke);
                break;

            case "gender":
                array = getResources().getStringArray(R.array.array_gender);
                break;
        }

        binding.picker.setMinValue(0);
        binding.picker.setMaxValue(array.length - 1);
        binding.picker.setDisplayedValues(array);
        //disable soft keyboard
        binding.picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        //set wrap true or false, try it you will know the difference
        binding.picker.setWrapSelectorWheel(false);

        binding.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {                
                String selected = array[binding.picker.getValue()];
                
                Intent intent = new Intent();
                intent.putExtra("value", selected);
                intent.putExtra("type", type);
                setResult(RESULT_OK, intent);
                finish();

                Common.showToast(act, selected);
            }
        });

        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
