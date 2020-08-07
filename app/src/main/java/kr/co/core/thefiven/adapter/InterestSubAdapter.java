package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

import kr.co.core.thefiven.R;

import kr.co.core.thefiven.data.InterestSubData;

public class InterestSubAdapter extends RecyclerView.Adapter<InterestSubAdapter.ViewHolder>{
    private Activity act;
    private ArrayList<InterestSubData> list = new ArrayList<>();

    public interface ItemClickListener {
        void clicked(InterestSubData data);
    }
    private InterestSubAdapter.ItemClickListener itemClickListener;

    public InterestSubAdapter(Activity act, ArrayList<InterestSubData> list, ItemClickListener itemClickListener) {
        this.act = act;
        this.list = list;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public InterestSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest, parent, false);
        return new InterestSubAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestSubAdapter.ViewHolder holder, int position) {
        final InterestSubData data = list.get(position);
        holder.tv_title.setText(data.getTitle());

        Glide.with(act)
                .load(data.getImage())
                .centerCrop()
                .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                .into(holder.iv_image);

        holder.itemView.setSelected(data.isSelected());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.setSelected(!data.isSelected());
                itemClickListener.clicked(data);

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView iv_image;

        View itemView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;

            tv_title = itemView.findViewById(R.id.tv_title);
            iv_image = itemView.findViewById(R.id.iv_image);
        }
    }
}
