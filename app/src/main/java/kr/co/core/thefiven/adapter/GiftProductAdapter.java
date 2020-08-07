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
import kr.co.core.thefiven.activity.gift.GiftProductDetailAct;
import kr.co.core.thefiven.data.GiftListData;

public class GiftProductAdapter extends RecyclerView.Adapter<GiftProductAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<GiftListData> list;
    private String yidx, nick;

    public GiftProductAdapter(Activity act, ArrayList<GiftListData> list,String yidx, String nick) {
        this.act = act;
        this.list = list;
        this.yidx = yidx;
        this.nick = nick;
    }

    @NonNull
    @Override
    public GiftProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_product, parent, false);
        GiftProductAdapter.ViewHolder viewHolder = new GiftProductAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GiftProductAdapter.ViewHolder holder, final int i) {
        final GiftListData data = list.get(i);
        Glide.with(act).load(data.getGoodsImgS()).into(holder.iv_product);
        holder.tv_brand.setText(data.getBrandName());
        holder.tv_name.setText(data.getGoodsName());
        holder.tv_price.setText(data.getGoodsPrice());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.startActivity(new Intent(act, GiftProductDetailAct.class).putExtra("data", data).putExtra("yidx", yidx).putExtra("nick", nick));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<GiftListData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_brand, tv_name, tv_price;
        ImageView iv_product;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            tv_brand = view.findViewById(R.id.tv_brand);
            tv_name = view.findViewById(R.id.tv_name);
            tv_price = view.findViewById(R.id.tv_price);
            iv_product = view.findViewById(R.id.iv_product);

            itemView = view;
        }
    }

}
