package kr.co.core.thefiven.utility;

import android.app.Activity;
import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import kr.co.core.thefiven.R;


public class AllOfDecoration extends RecyclerView.ItemDecoration {
    private Activity act;
    private String type;

    public AllOfDecoration(Activity act, String type) {
        this.act = act;
        this.type = type;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {

        int position = parent.getChildAdapterPosition(view);

        switch (type) {
            case "imageReg":
                outRect.left = act.getResources().getDimensionPixelSize(R.dimen.image_reg_padding);
                outRect.right = act.getResources().getDimensionPixelSize(R.dimen.image_reg_padding);
                break;

            case "chattingBottom":
                outRect.left = act.getResources().getDimensionPixelSize(R.dimen.chatting_bottom_item);
                outRect.right = act.getResources().getDimensionPixelSize(R.dimen.chatting_bottom_item);
                break;

            case "interestTop":
                if(position == 0) {
                    outRect.left = act.getResources().getDimensionPixelSize(R.dimen.dimen_10);
                    outRect.right = act.getResources().getDimensionPixelSize(R.dimen.dimen_10);
                } else {
                    outRect.right = act.getResources().getDimensionPixelSize(R.dimen.dimen_10);
                }
                break;

            case "cgpms_member":
                if(position == 0) {
                    outRect.top = act.getResources().getDimensionPixelOffset(R.dimen.dimen_05);
                }
                break;

            case "top10dp":
                if(position == 0) {
                    outRect.top = act.getResources().getDimensionPixelOffset(R.dimen.dimen_10);
                }
                break;
            case "gift_product":
                if(position < 2) {
                    outRect.top = act.getResources().getDimensionPixelOffset(R.dimen.dimen_08);
                }
                break;
        }
    }
}