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
import java.util.HashMap;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.gift.GiftProductAct;
import kr.co.core.thefiven.activity.gift.GiftStorageDetailAct;
import kr.co.core.thefiven.data.GiftListData;
import kr.co.core.thefiven.data.GiftStorageData;

public class GiftStorageAdapter extends RecyclerView.Adapter<GiftStorageAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<GiftStorageData> list = new ArrayList<>();

    public GiftStorageAdapter(Activity act, ArrayList<GiftStorageData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public GiftStorageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_storage, parent, false);
        GiftStorageAdapter.ViewHolder viewHolder = new GiftStorageAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GiftStorageAdapter.ViewHolder holder, final int i) {
        final GiftStorageData data = list.get(i);
        holder.tv_name.setText(data.getP_name());
        holder.tv_valid_date.setText(data.getP_limit_date());

        Glide.with(act).load(data.getP_title_image()).into(holder.iv_product);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.startActivity(new Intent(act, GiftStorageDetailAct.class).putExtra("data", data));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<GiftStorageData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_valid_date, tv_name;
        ImageView iv_product;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            tv_valid_date = view.findViewById(R.id.tv_valid_date);
            tv_name = view.findViewById(R.id.tv_name);
            iv_product = view.findViewById(R.id.iv_product);

            itemView = view;
        }
    }

}
