package kr.co.core.thefiven.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.adapter.ListDialogAdapter;
import kr.co.core.thefiven.data.ListDialogData;
import kr.co.core.thefiven.databinding.DialogListBinding;
import kr.co.core.thefiven.utility.StringUtil;

public class ListProfileDlg extends BasicDlg {
    private DialogListBinding binding;
    private Activity act;

    private List array;
    private ArrayList<ListDialogData> list = new ArrayList<>();

    private String type, data;

    private String selectedData;
    private int selectedPos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.dialog_list, null);
        act = this;

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        getWindow().setAttributes(params);


        type = getIntent().getStringExtra("type");
        data = getIntent().getStringExtra("data");

        switch (type) {
            case StringUtil.PROF_GENDER:
                array = Arrays.asList(getResources().getStringArray(R.array.array_gender));
                break;

            case StringUtil.PROF_JOB:
                array = Arrays.asList(getResources().getStringArray(R.array.array_job));
                break;

            case StringUtil.PROF_SALARY:
                array = Arrays.asList(getResources().getStringArray(R.array.array_salary));
                break;

            case StringUtil.PROF_MARRIAGE:
                array = Arrays.asList(getResources().getStringArray(R.array.array_marriage));
                break;

            case StringUtil.PROF_PERSONALITY:
                array = Arrays.asList(getResources().getStringArray(R.array.array_personality));
                break;

            case StringUtil.PROF_NATIONALITY:
                array = Arrays.asList(getResources().getStringArray(R.array.array_nationality));
                break;

            case StringUtil.PROF_BLOOD:
                array = Arrays.asList(getResources().getStringArray(R.array.array_blood));
                break;

            case StringUtil.PROF_LOCATION:
                array = Arrays.asList(getResources().getStringArray(R.array.array_location));
                break;

            case StringUtil.PROF_EDU:
                array = Arrays.asList(getResources().getStringArray(R.array.array_edu));
                break;

            case StringUtil.PROF_HOLIDAY:
                array = Arrays.asList(getResources().getStringArray(R.array.array_holiday));
                break;

            case StringUtil.PROF_FAMILY:
                array = Arrays.asList(getResources().getStringArray(R.array.array_family));
                break;

            case StringUtil.PROF_HEIGHT:
                array = Arrays.asList(getResources().getStringArray(R.array.array_height));
                break;

            case StringUtil.PROF_BODY:
                array = Arrays.asList(getResources().getStringArray(R.array.array_body));
                break;

            case StringUtil.PROF_DRINK:
                array = Arrays.asList(getResources().getStringArray(R.array.array_drink));
                break;

            case StringUtil.PROF_SMOKE:
                array = Arrays.asList(getResources().getStringArray(R.array.array_smoke));
                break;
        }

        // 첫 번째 데이터 세팅
        list.add(new ListDialogData(StringUtil.PROF_NO_DATA, StringUtil.isNull(data)));
        selectedData = data;
        selectedPos = 0;


        // 선택된 데이터 선택 표시
        for (int i = 0; i < array.size(); i++) {
            if(!StringUtil.isNull(data) && ((String) array.get(i)).equalsIgnoreCase(data)) {
                list.add(new ListDialogData((String) array.get(i), true));
                selectedData = data;
                selectedPos = i + 1;
            } else {
                list.add(new ListDialogData((String) array.get(i), false));
            }
        }


        // 리싸이클러뷰 설정
        binding.rcvList.setLayoutManager(new LinearLayoutManager(act));
        binding.rcvList.setHasFixedSize(true);
        binding.rcvList.setItemViewCacheSize(20);
        binding.rcvList.setAdapter(new ListDialogAdapter(act, selectedPos ,list, type, new ListDialogAdapter.InterClickListener() {
            @Override
            public void select(String data) {
                selectedData = data;
            }
        }));
        binding.rcvList.scrollToPosition(selectedPos);




        // 클릭 리스너
        binding.tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("value", selectedData);
                intent.putExtra("type", type);
                act.setResult(Activity.RESULT_OK, intent);
                act.finish();
            }
        });
    }
}
