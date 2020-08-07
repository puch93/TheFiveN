package kr.co.core.thefiven.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.databinding.DialogDatePickerBinding;


public class DatePickerDialog extends BasicAct {
    DialogDatePickerBinding binding;
    Activity act = this;

    private static final String TAG = "TEST_HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_date_picker, null);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);

        int year = getIntent().getIntExtra("year", 1970);
        int month = getIntent().getIntExtra("month", 1);
        int day = getIntent().getIntExtra("day", 1);

        binding.dataPicker.init(year, month - 1, day, null);

        binding.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("year", binding.dataPicker.getYear());
                resultIntent.putExtra("month", binding.dataPicker.getMonth() + 1);
                resultIntent.putExtra("day", binding.dataPicker.getDayOfMonth());

                setResult(RESULT_OK, resultIntent);
                finish();
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
