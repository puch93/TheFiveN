package kr.co.core.thefiven.utility;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import kr.co.core.thefiven.R;


public class MainBottomItemDecoration extends RecyclerView.ItemDecoration {
    Activity act;

    public MainBottomItemDecoration(Activity act) {
        this.act = act;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        outRect.left = act.getResources().getDimensionPixelSize(R.dimen.dimen_07);
        outRect.right = act.getResources().getDimensionPixelSize(R.dimen.dimen_07);
    }
}