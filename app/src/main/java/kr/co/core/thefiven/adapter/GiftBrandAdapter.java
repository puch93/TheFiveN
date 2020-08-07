package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.gift.GiftProductAct;
import kr.co.core.thefiven.data.GiftListData;

public class GiftBrandAdapter extends RecyclerView.Adapter<GiftBrandAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<String> brandMap_Key;
    private HashMap<String, ArrayList<GiftListData>> brandMap = new HashMap<>();
    private String yidx, nick;

    public GiftBrandAdapter(Activity act, ArrayList<String> brandMap_Key, HashMap<String, ArrayList<GiftListData>> brandMap, String yidx, String nick) {
        this.act = act;
        this.brandMap_Key = brandMap_Key;
        this.brandMap = brandMap;
        this.yidx = yidx;
        this.nick = nick;
    }

    @NonNull
    @Override
    public GiftBrandAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gift_brand, parent, false);
        GiftBrandAdapter.ViewHolder viewHolder = new GiftBrandAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull GiftBrandAdapter.ViewHolder holder, final int i) {
        final String data = brandMap_Key.get(i);
        holder.tv_brand.setText(data);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                act.startActivity(new Intent(act, GiftProductAct.class).putExtra("productList", brandMap.get(data)).putExtra("yidx", yidx).putExtra("nick", nick));
            }
        });
    }

    @Override
    public int getItemCount() {
        return brandMap_Key.size();
    }

    public void setList(ArrayList<String> brandMap_Key, HashMap<String, ArrayList<GiftListData>> brandMap) {
        this.brandMap_Key = brandMap_Key;
        this.brandMap = brandMap;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_brand;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            tv_brand = view.findViewById(R.id.tv_brand);

            itemView = view;
        }
    }

}
