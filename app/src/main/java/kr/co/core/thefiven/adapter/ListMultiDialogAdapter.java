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
import kr.co.core.thefiven.utility.Common;

public class ListMultiDialogAdapter extends RecyclerView.Adapter<ListMultiDialogAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<ListDialogData> list;
    private String type;
    private int selected_size;

    public interface InterClickListener {
        void select(String data);
    }

    InterClickListener clickListener;

    public ListMultiDialogAdapter(Activity act, ArrayList<ListDialogData> list, String type, int selected_size, InterClickListener clickListener) {
        this.act = act;
        this.list = list;
        this.type = type;
        this.selected_size = selected_size;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ListMultiDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_list, parent, false);
        ListMultiDialogAdapter.ViewHolder viewHolder = new ListMultiDialogAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ListMultiDialogAdapter.ViewHolder holder, final int i) {
        final ListDialogData data = list.get(i);

        holder.itemView.setSelected(data.isSelect());

        holder.tv_contents.setText(data.getContents());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(data.isSelect()) {
                    --selected_size;

                    list.get(i).setSelect(false);

                    notifyDataSetChanged();
                } else {
                    if (selected_size >= 3) {
                        Common.showToast(act, "최대 3가지만 선택 가능합니다");
                    } else {
                        ++selected_size;

                        list.get(i).setSelect(true);

                        notifyDataSetChanged();

                        clickListener.select(data.getContents());
                    }
                }
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
