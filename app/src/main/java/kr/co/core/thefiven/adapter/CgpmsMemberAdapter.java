package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ProfileDetailAct;
import kr.co.core.thefiven.data.MainBottomData;
import kr.co.core.thefiven.utility.Common;

public class CgpmsMemberAdapter extends RecyclerView.Adapter<CgpmsMemberAdapter.ViewHolder> {
    private static final int FROM_PROFILE = 1002;

    private Activity act;
    private ArrayList<MainBottomData> list;

    public CgpmsMemberAdapter(Activity act, ArrayList<MainBottomData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public CgpmsMemberAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_bottom, parent, false);

        CgpmsMemberAdapter.ViewHolder viewHolder = new CgpmsMemberAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CgpmsMemberAdapter.ViewHolder holder, int i) {
        final MainBottomData data = list.get(i);

        Glide.with(act)
                .load(data.getProfile_img())
                .centerCrop()
                .into(holder.iv_profile);
        holder.tv_indicator.setText("1/" + data.getProfile_img_count());

        holder.tv_nick.setText(data.getNick());
        holder.tv_age.setText(data.getAge());
        holder.tv_location.setText(data.getLocation());

        holder.tv_cgpms_kind.setText(data.getCgpms_kind());
        holder.tv_cgpms_point.setText(data.getCgpms_point());

        holder.iv_like.setSelected(data.isLike_state());
        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.isLike_state()) {
                    data.setLike_state(false);
                } else {
                    data.setLike_state(true);
                    Common.showToastLike(act);
                }

                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                act.startActivity(new Intent(act, ProfileDetailAct.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<MainBottomData> list) {
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        ImageView iv_like;
        TextView tv_nick;
        TextView tv_age;
        TextView tv_location;
        TextView tv_cgpms_point;
        TextView tv_cgpms_kind;
        TextView tv_indicator;

        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            iv_like = view.findViewById(R.id.iv_like);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_nick = view.findViewById(R.id.tv_nick);
            tv_age = view.findViewById(R.id.tv_age);
            tv_location = view.findViewById(R.id.tv_location);
            tv_cgpms_point = view.findViewById(R.id.tv_cgpms_point);
            tv_cgpms_kind = view.findViewById(R.id.tv_cgpms_kind);
            tv_indicator = view.findViewById(R.id.tv_indicator);

            itemView = view;
        }
    }
}
