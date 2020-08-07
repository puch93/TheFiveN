package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.data.NoticeData;

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    private static final int FROM_PROFILE = 1002;

    private Activity act;
    private ArrayList<NoticeData> list;

    public NoticeAdapter(Activity act, ArrayList<NoticeData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice, parent, false);
        NoticeAdapter.ViewHolder viewHolder = new NoticeAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull NoticeAdapter.ViewHolder holder, final int i) {
        final NoticeData data = list.get(i);

        holder.tv_title.setText(data.getTitle());
        holder.tv_date.setText(data.getDate());
        holder.tv_detail.setText(data.getDetail());
        if(data.isSelect()) {
            holder.tv_detail.setVisibility(View.VISIBLE);
        } else {
            holder.tv_detail.setVisibility(View.GONE);
        }

        holder.ll_title_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setSelect(!data.isSelect());
                notifyItemChanged(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<NoticeData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title, tv_detail, tv_date;
        LinearLayout ll_title_area;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            tv_title = view.findViewById(R.id.tv_title);
            tv_detail = view.findViewById(R.id.tv_detail);
            tv_date = view.findViewById(R.id.tv_date);
            ll_title_area = view.findViewById(R.id.ll_title_area);

            itemView = view;
        }
    }
}
