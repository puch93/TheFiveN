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

public class InterestProfileAdapter extends RecyclerView.Adapter<InterestProfileAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<InterestSubData> list = new ArrayList<>();

    public interface ItemClickListener {
        void clicked(InterestSubData data);
    }

    public InterestProfileAdapter(Activity act, ArrayList<InterestSubData> list) {
        this.act = act;
        this.list = list;
    }

    public void setList(ArrayList<InterestSubData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InterestProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_interest_profile, parent, false);
        return new InterestProfileAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestProfileAdapter.ViewHolder holder, int position) {
        final InterestSubData data = list.get(position);
        holder.tv_title.setText(data.getTitle());

        Glide.with(act)
                .load(data.getImage())
                .centerCrop()
                .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                .into(holder.iv_image);

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_title;
        ImageView iv_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_title = itemView.findViewById(R.id.tv_title);
            iv_image = itemView.findViewById(R.id.iv_image);
        }
    }
}
