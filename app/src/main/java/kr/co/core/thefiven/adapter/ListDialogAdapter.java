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
import kr.co.core.thefiven.data.ListDialogData;

public class ListDialogAdapter extends RecyclerView.Adapter<ListDialogAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<ListDialogData> list;
    private String type;

    private int previousPos = 0;

    public interface InterClickListener {
        void select(String data);
    }

    InterClickListener clickListener;

    public ListDialogAdapter(Activity act, int selectedPos, ArrayList<ListDialogData> list, String type, InterClickListener clickListener) {
        this.act = act;
        this.list = list;
        this.type = type;
        this.previousPos = selectedPos;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ListDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_list, parent, false);
        ListDialogAdapter.ViewHolder viewHolder = new ListDialogAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListDialogAdapter.ViewHolder holder, final int i) {
        final ListDialogData data = list.get(i);

        holder.itemView.setSelected(data.isSelect());

        holder.tv_contents.setText(data.getContents());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.get(i).setSelect(true);

                if (previousPos != i) {
                    list.get(previousPos).setSelect(false);
                    previousPos = i;
                }

                notifyDataSetChanged();

                clickListener.select(data.getContents());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<ListDialogData> list) {
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
