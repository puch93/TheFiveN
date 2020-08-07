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

public class ListOtherDlg extends BasicDlg {
    private DialogListBinding binding;
    private Activity act;

    private List array;
    private ArrayList<ListDialogData> list = new ArrayList<>();

    private String type, data;
    private String selectedData;
    private int selectedPos = 0;
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
            case StringUtil.POPUP_CONTACT:
                array = Arrays.asList(getResources().getStringArray(R.array.array_contact));
                break;

            case StringUtil.POPUP_REPORT01:
                array = Arrays.asList(getResources().getStringArray(R.array.array_report01));
                break;

            case StringUtil.POPUP_REPORT02:
                array = Arrays.asList(getResources().getStringArray(R.array.array_report02));
                break;

        }

        // 혹시 에러나면 끝내기
        if(array == null) {
            finish();
        }

        // 선택된 데이터 선택 표시
        for (int i = 0; i < array.size(); i++) {
            if(!StringUtil.isNull(data) && ((String) array.get(i)).equalsIgnoreCase(data)) {
                list.add(new ListDialogData((String) array.get(i), true));
                selectedPos = i;
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
