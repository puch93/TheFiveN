package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ChattingAct;
import kr.co.core.thefiven.data.ChattingBottomData;
import kr.co.core.thefiven.fragment.MenuBasicFrag;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class ChattingBottomAdapter extends RecyclerView.Adapter<ChattingBottomAdapter.ViewHolder> {
    private static final int FROM_PROFILE = 1002;
    private static final int PROFILE_DETAIL = 1003;

    private Activity act;
    private ArrayList<ChattingBottomData> list;

    private MenuBasicFrag frag;

    public ChattingBottomAdapter(Activity act, ArrayList<ChattingBottomData> list) {
        this.act = act;
        this.list = list;
    }

    @NonNull
    @Override
    public ChattingBottomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chatting_bottom, parent, false);
        ChattingBottomAdapter.ViewHolder viewHolder = new ChattingBottomAdapter.ViewHolder(view);

        int height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.chatting_bottom_subtract_dp)) / 2;

        if(height <= 0) {
            height = act.getResources().getDimensionPixelSize(R.dimen.chatting_bottom_default_dp);
        }

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.card_view.getLayoutParams();
        params.height = height;

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingBottomAdapter.ViewHolder holder, int i) {
        final ChattingBottomData data = list.get(i);

        checkImageState(holder.iv_profile_img, data.getProfile_img(), data.getProfile_img_ck(), data.getGender());

        holder.iv_login_state.setSelected(data.isLogin());

        holder.tv_info.setText(data.getNick() + " " + data.getAge());
        holder.tv_date.setText(data.getDate());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                frag.startActivityForResult(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()), PROFILE_DETAIL);
                checkRoom(data);
            }
        });
    }

    private void checkRoom(final ChattingBottomData data) {
        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_ROOM) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            JSONArray ja = jo.getJSONArray("datas");
                            JSONObject job = ja.getJSONObject(0);

                            String idx = StringUtil.getStr(job, "idx");
                            String type = StringUtil.getStr(job, "type");
                            String user_idx = StringUtil.getStr(job, "user_idx");
                            String r_userid = StringUtil.getStr(job, "r_userid");
                            String r_username = StringUtil.getStr(job, "r_username");
                            String created_at = StringUtil.getStr(job, "created_at");
                            String r_modify_date = StringUtil.getStr(job, "r_modify_date");
                            String r_type = StringUtil.getStr(job, "r_type");

                            Intent intent = new Intent(act, ChattingAct.class);
                            intent.putExtra("room_idx", idx);
                            intent.putExtra("yidx", data.getIdx());
                            intent.putExtra("otherNick", data.getNick());
                            intent.putExtra("otherImage", data.getProfile_img());
                            intent.putExtra("otherGender", data.getGender());
                            intent.putExtra("otherImageState", data.getProfile_img_ck());
                            intent.putExtra("room_type", r_type);
                            act.startActivity(intent);
                        } else {

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

        server.setTag("Check Room");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", data.getIdx());
        server.execute(true, false);
    }


    /**
     * 검수완료: Y / 검수중: N
     * */
    private void checkImageState(ImageView imageView, String dataUrl, String state, String gender) {
        if (state.equalsIgnoreCase("Y")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .into(imageView);
        }else if (state.equalsIgnoreCase("N") || state.equalsIgnoreCase("fail")) {
            Glide.with(act)
                    .load(dataUrl)
                    .centerCrop()
                    .transform(new BlurTransformation(10, 3))
                    .into(imageView);
        } else {
            if(StringUtil.isNull(gender)) {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .into(imageView);
            } else {
                if(gender.equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .into(imageView);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .into(imageView);
                }
            }
        }
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<ChattingBottomData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setFrag(MenuBasicFrag frag) {
        this.frag = frag;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile_img, iv_login_state;
        TextView tv_info, tv_date;
        CardView card_view;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            iv_profile_img = view.findViewById(R.id.iv_profile_img);
            iv_login_state = view.findViewById(R.id.iv_login_state);
            tv_date = view.findViewById(R.id.tv_date);
            tv_info = view.findViewById(R.id.tv_info);
            card_view = view.findViewById(R.id.card_view);

            itemView = view;
        }
    }
}
