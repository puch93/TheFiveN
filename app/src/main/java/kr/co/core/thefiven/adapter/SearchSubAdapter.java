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

import java.util.List;

import kr.co.core.thefiven.R;

public class SearchSubAdapter extends RecyclerView.Adapter<SearchSubAdapter.ViewHolder> {
    private Activity act;
    private List<String> list;
    private String type;

    public SearchSubAdapter(Activity act, List<String> list, String type) {
        this.act = act;
        this.list = list;
        this.type = type;
    }

    @NonNull
    @Override
    public SearchSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_sub, parent, false);
        SearchSubAdapter.ViewHolder viewHolder = new SearchSubAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchSubAdapter.ViewHolder holder, int i) {
        final String data = list.get(i);

        holder.tv_item.setText(data);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("value", data);
                intent.putExtra("type", type);
                act.setResult(Activity.RESULT_OK, intent);
                act.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item;
        ImageView iv_select;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            tv_item = view.findViewById(R.id.tv_item);
            iv_select = view.findViewById(R.id.iv_select);
            itemView = view;
        }
    }
}
