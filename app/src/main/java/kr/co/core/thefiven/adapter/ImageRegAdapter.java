package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import java.util.ArrayList;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.myinfo.MyInfoImageAct;
import kr.co.core.thefiven.data.RegImageData;
import kr.co.core.thefiven.utility.StringUtil;

public class ImageRegAdapter extends RecyclerView.Adapter<ImageRegAdapter.ViewHolder> {

    Activity act;
    ArrayList<RegImageData> imageList;

    private static final int VIEW_ITEM = 0;
    private static final int VIEW_SELECT = 1;

    public interface ImageAddListener {
        void selectClickListener();
    }

    private ImageAddListener addListener;

    public ImageRegAdapter(Activity act, ArrayList<RegImageData> imageList, ImageAddListener addListener) {
        this.act = act;
        this.imageList = imageList;
        this.addListener = addListener;
    }

    public ArrayList<RegImageData> getList() {
        return this.imageList;
    }

    public void setList(ArrayList<RegImageData> imageList) {
        this.imageList = imageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.image_reg_subtract_dp)) / 3;
        Log.e(StringUtil.TAG, "width: " + height);

        if(height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.image_reg_default_dp);
        }

        View view = null;
        switch (viewType) {
            case VIEW_ITEM:
                view = inflater.inflate(R.layout.item_image_reg, parent, false);
                ViewHolder1 viewHolder1 = new ViewHolder1(view);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) viewHolder1.iv_image.getLayoutParams();
                params.height = height;
                return viewHolder1;

            case VIEW_SELECT:
                view = inflater.inflate(R.layout.item_image_select, parent, false);
                ViewHolder2 viewHolder2 = new ViewHolder2(view);
                LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams) viewHolder2.iv_select.getLayoutParams();
                params2.height = height;
                return viewHolder2;
            default:
                return null;
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == imageList.size()) {
            return VIEW_SELECT;
        } else if (position < imageList.size()) {
            return VIEW_ITEM;
        } else {
            return super.getItemViewType(position);
        }
    }

    /**
     * 검수완료: Y / 검수중: N
     * */
    private void checkImageState(ImageView imageView1, ImageView imageView2, String dataUrl, String state) {
        Glide.with(act)
                .load(dataUrl)
                .centerCrop()
                .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                .into(imageView1);


        if (state.equalsIgnoreCase("Y")) {
            imageView2.setVisibility(View.GONE);
        } else if (state.equalsIgnoreCase("N")) {
            Glide.with(act).load(R.drawable.icon_confirm).into(imageView2);
        } else {
            Glide.with(act).load(R.drawable.icon_fail).into(imageView2);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        int viewType = getItemViewType(i);

        if (viewType == VIEW_ITEM) {
            ViewHolder1 holder1 = (ViewHolder1) holder;

            checkImageState(holder1.iv_image, holder1.iv_image_ck, imageList.get(i).getImage(), imageList.get(i).getImage_ck());
            Glide.with(act)
                    .load(imageList.get(i).getImage())
                    .centerCrop()
                    .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                    .into(holder1.iv_image);

            holder1.iv_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);

                    alertDialog.setCancelable(false);
                    alertDialog.setTitle("사진 삭제");
                    alertDialog.setMessage("해당 사진을 삭제하시겠습니까?");

                    // ok
                    alertDialog.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    imageList.remove(i);
                                    notifyDataSetChanged();
                                    MyInfoImageAct.isFirst = false;
                                    dialog.cancel();
                                }
                            });
                    // cancel
                    alertDialog.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();
                }
            });

        } else if(viewType == VIEW_SELECT) {
            ViewHolder2 holder2 = (ViewHolder2) holder;

            Glide.with(act)
                    .load(R.drawable.box_add)
                    .centerCrop()
                    .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                    .into(holder2.iv_select);

            holder2.iv_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(null != addListener) {
                        addListener.selectClickListener();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return imageList == null ? 0 : imageList.size() + 1;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View v) {
            super(v);
        }
    }

    public class ViewHolder1 extends ViewHolder {
        ImageView iv_image;
        ImageView iv_image_ck;

        View itemView;

        ViewHolder1(View v) {
            super(v);
            iv_image = v.findViewById(R.id.iv_image);
            iv_image_ck = v.findViewById(R.id.iv_image_ck);
            itemView = v;
        }

        public int currentPosition() {
            return getAdapterPosition();
        }
    }

    public class ViewHolder2 extends ViewHolder {
        ImageView iv_select;

        ViewHolder2(View v) {
            super(v);
            iv_select = v.findViewById(R.id.iv_select);
        }
    }
}
