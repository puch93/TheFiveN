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
import kr.co.core.thefiven.adapter.ListMultiDialogAdapter;
import kr.co.core.thefiven.data.ListDialogData;
import kr.co.core.thefiven.databinding.DialogListBinding;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class ListProfileMultiDlg extends BasicDlg {
    private DialogListBinding binding;
    private Activity act;

    private List array;
    private ArrayList<ListDialogData> list = new ArrayList<>();

    private String type, data;
    private String[] datas;
    private String resultData;

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
        datas = data.split(",");

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


        int selected_count = 0;
        // 선택된 데이터 선택 표시
        for (int i = 0; i < array.size(); i++) {
            if (!StringUtil.isNull(data)) {
                String d = (String) array.get(i);
                for (int j = 0; j < datas.length; j++) {
                    if (d.equalsIgnoreCase(datas[j])) {
                        ++selected_count;
                        list.add(new ListDialogData((String) array.get(i), true));
                        break;
                    } else {
                        if (j == datas.length-1) {
                            list.add(new ListDialogData((String) array.get(i), false));
                        }
                    }
                }
            } else {
                list.add(new ListDialogData((String) array.get(i), false));
            }
        }


        // 리싸이클러뷰 설정
        binding.rcvList.setLayoutManager(new LinearLayoutManager(act));
        binding.rcvList.setHasFixedSize(true);
        binding.rcvList.setItemViewCacheSize(20);
        binding.rcvList.setAdapter(new ListMultiDialogAdapter(act, list, type, selected_count, new ListMultiDialogAdapter.InterClickListener() {
            @Override
            public void select(String data) {
                if (!StringUtil.isNull(resultData)) {
                    resultData += "," + data;
                } else {
                    resultData += data;
                }
            }
        }));


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
                resultData = "";
                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).isSelect()) {
                        if (StringUtil.isNull(resultData)) {
                            resultData += list.get(i).getContents();
                        } else {
                            resultData += "," + list.get(i).getContents();
                        }
                    }
                }

//                Common.showToast(act, resultData);

                if(StringUtil.isNull(resultData)) {
                    resultData = StringUtil.PROF_NO_DATA;
                }

                Intent intent = new Intent();
                intent.putExtra("value", resultData);
                intent.putExtra("type", type);
                act.setResult(Activity.RESULT_OK, intent);
                act.finish();
            }
        });
    }
}
