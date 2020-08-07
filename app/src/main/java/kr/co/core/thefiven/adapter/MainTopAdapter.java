package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ProfileDetailAct;
import kr.co.core.thefiven.data.MainTopData;
import kr.co.core.thefiven.utility.StringUtil;

public class MainTopAdapter extends RecyclerView.Adapter<MainTopAdapter.ViewHolder> {
    private static final int FROM_PROFILE = 1002;

    private Activity act;
    private ArrayList<MainTopData> list;

    public MainTopAdapter(Activity act, ArrayList<MainTopData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public MainTopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_top, parent, false);
        MainTopAdapter.ViewHolder viewHolder = new MainTopAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainTopAdapter.ViewHolder holder, int i) {
        if (i == 0) {
            holder.first_area.setVisibility(View.VISIBLE);
            holder.other_area.setVisibility(View.GONE);
        } else {
            final MainTopData data = list.get(i - 1);

            holder.other_area.setVisibility(View.VISIBLE);
            holder.first_area.setVisibility(View.GONE);

            checkImageState(holder.iv_profile, data.getProfile_img(), data.getProfile_img_ck(), data.getGender());

            holder.tv_info.setText(data.getLocation() + " " + data.getAge());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    act.startActivity(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()));
                }
            });
        }
    }

    /**
     * 검수완료: Y / 검수중: N
     * */
    private void checkImageState(ImageView imageView, String dataUrl, String state, String gender) {
        if (state.equalsIgnoreCase("Y")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(imageView);
        } else if (state.equalsIgnoreCase("N") || state.equalsIgnoreCase("fail")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .transform(new BlurTransformation(10, 3), new CircleCrop())
                    .into(imageView);
        } else {
            if(StringUtil.isNull(gender)) {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(imageView);
            } else {
                if(gender.equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(imageView);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(imageView);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size() + 1;
    }

    public void setList(ArrayList<MainTopData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout other_area, first_area;
        ImageView iv_profile;
        TextView tv_info;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            other_area = view.findViewById(R.id.other_area);
            first_area = view.findViewById(R.id.first_area);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_info = view.findViewById(R.id.tv_info);

            itemView = view;
        }
    }
}
