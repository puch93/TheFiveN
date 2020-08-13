package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.data.BlockData;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class BlockAdapter extends RecyclerView.Adapter<BlockAdapter.ViewHolder> {
    private Activity act;
    private ArrayList<BlockData> list;

    public BlockAdapter(Activity act, ArrayList<BlockData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public BlockAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_block, parent, false);
        BlockAdapter.ViewHolder viewHolder = new BlockAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull BlockAdapter.ViewHolder holder, final int i) {
        final BlockData data = list.get(i);

        checkImageState(holder.iv_profile, data.getProfile_img(), data.getProfile_img_ck(), data.getGender());

        holder.tv_nick.setText(data.getNick());
        holder.tv_other_info.setText(data.getAge() + " " + data.getLocation());
        holder.tv_unblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog(data.getIdx(), i);
            }
        });
    }

    /**
     * 검수완료: Y / 검수중: N
     * */
    private void checkImageState(ImageView imageView, String dataUrl, String state, String gender) {
        gender = AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male") ? "female" : "male";

        if (state.equalsIgnoreCase("Y")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(imageView);
        } else if (state.equalsIgnoreCase("N") || state.equalsIgnoreCase("fail")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .transform(new BlurTransformation(10, 3), new CircleCrop())
                    .into(imageView);
        } else {
            if(StringUtil.isNull(gender)) {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(imageView);
            } else {
                if(gender.equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(imageView);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(imageView);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<BlockData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        TextView tv_nick, tv_other_info, tv_unblock;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_nick = view.findViewById(R.id.tv_nick);
            tv_other_info = view.findViewById(R.id.tv_other_info);
            tv_unblock = view.findViewById(R.id.tv_unblock);

            itemView = view;
        }
    }

    private void showAlertDialog(final String yidx, final int position) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("차단 해제");
        alertDialog.setMessage("해당 회원을 차단해제 하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doBlockCancel(yidx, position);
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

    private void doBlockCancel(String yidx, final int position) {
        ReqBasic server = new ReqBasic(act, NetUrls.BLOCK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    list.remove(position);
                                    notifyDataSetChanged();
                                }
                            });

                            AppPreference.setMemberStateAll(act);
                            Common.showToast(act, "차단 해제되었습니다");
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "comment"));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Block");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.execute(true, false);
    }
}
