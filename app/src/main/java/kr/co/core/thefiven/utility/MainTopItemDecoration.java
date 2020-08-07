package kr.co.core.thefiven.utility;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import kr.co.core.thefiven.R;


public class MainTopItemDecoration extends RecyclerView.ItemDecoration {
    Activity act;

    public MainTopItemDecoration(Activity act) {
        this.act = act;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        if(position == 0) {
            outRect.left = act.getResources().getDimensionPixelSize(R.dimen.dimen_10);
        }
    }
}