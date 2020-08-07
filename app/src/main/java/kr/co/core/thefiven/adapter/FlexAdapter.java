package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.thefiven.R;

public class FlexAdapter extends RecyclerView.Adapter<FlexAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<String> list;

    public FlexAdapter(Activity act, ArrayList<String> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public FlexAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flex, parent, false);
        FlexAdapter.ViewHolder viewHolder = new FlexAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull FlexAdapter.ViewHolder holder, int i) {
        final String data = list.get(i);

        holder.tv_contents.setText(data);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<String> list) {
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_contents;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            tv_contents = view.findViewById(R.id.tv_contents);

            itemView = view;
        }
    }
}
