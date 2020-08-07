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
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ChattingAct;
import kr.co.core.thefiven.data.ChattingTopData;
import kr.co.core.thefiven.utility.StringUtil;

public class ChattingTopAdapter extends RecyclerView.Adapter<ChattingTopAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<ChattingTopData> list;

    public ChattingTopAdapter(Activity act, ArrayList<ChattingTopData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ChattingTopAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_top, parent, false);
        ChattingTopAdapter.ViewHolder viewHolder = new ChattingTopAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingTopAdapter.ViewHolder holder, int i) {
        final ChattingTopData data = list.get(i);

        checkImageState(holder.iv_profile_img, data.getProfile_img(), data.getProfile_img_ck(), data.getGender());

        holder.iv_login_state.setSelected(data.isLogin());
        if(!data.getNo_read_count().equalsIgnoreCase("0")) {
            holder.tv_message_count.setVisibility(View.VISIBLE);
            holder.tv_message_count.setText(data.getNo_read_count());
        } else {
            holder.tv_message_count.setVisibility(View.INVISIBLE);
        }

        holder.tv_nick.setText(data.getNick());
        holder.tv_other_info.setText(data.getAge() + " " + data.getLocation());
        holder.tv_contents.setText(data.getContents());
        holder.tv_date.setText(data.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(act, ChattingAct.class);
                intent.putExtra("room_idx", data.getRoom_idx());
                intent.putExtra("yidx", data.getIdx());
                intent.putExtra("otherNick", data.getNick());
                intent.putExtra("otherImage", data.getProfile_img());
                intent.putExtra("otherGender", data.getGender());
                intent.putExtra("otherImageState", data.getProfile_img_ck());
                intent.putExtra("room_type", data.getRoom_type());
                act.startActivity(intent);
            }
        });
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
        return list.size();
    }

    public void setList(ArrayList<ChattingTopData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile_img, iv_login_state;
        TextView tv_nick, tv_other_info, tv_contents, tv_date, tv_message_count;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            iv_profile_img = view.findViewById(R.id.iv_profile_img);
            iv_login_state = view.findViewById(R.id.iv_login_state);
            tv_nick = view.findViewById(R.id.tv_nick);
            tv_message_count = view.findViewById(R.id.tv_message_count);
            tv_other_info = view.findViewById(R.id.tv_other_info);
            tv_contents = view.findViewById(R.id.tv_contents);
            tv_date = view.findViewById(R.id.tv_date);

            itemView = view;
        }
    }
}
