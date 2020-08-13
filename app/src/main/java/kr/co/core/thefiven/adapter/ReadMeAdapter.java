package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ProfileDetailAct;
import kr.co.core.thefiven.data.ReadMeData;
import kr.co.core.thefiven.dialog.LikeMessageDlg;
import kr.co.core.thefiven.fragment.LikeFrag;
import kr.co.core.thefiven.fragment.MenuBasicFrag;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class ReadMeAdapter extends RecyclerView.Adapter<ReadMeAdapter.ViewHolder> {
    private static final int FROM_PROFILE = 1002;
    private static final int PROFILE_DETAIL = 1003;
    private static final int LIKE_MESSAGE = 101;

    private Activity act;
    private Fragment frag;
    private ArrayList<ReadMeData> list;

    public ReadMeAdapter(Activity act, Fragment frag, ArrayList<ReadMeData> list) {
        this.act = act;
        this.list = list;
        this.frag = frag;
    }

    @NonNull
    @Override
    public ReadMeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_read_me, parent, false);
        ReadMeAdapter.ViewHolder viewHolder = new ReadMeAdapter.ViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull final ReadMeAdapter.ViewHolder holder, final int i) {
        final ReadMeData data = list.get(i);

        checkImageState(holder.iv_profile_img, data.getProfile_img(), data.getProfile_img_ck(), data.getGender());

        holder.iv_login_state.setSelected(data.isLogin());

        if (data.isLike_double()) {
            Glide.with(act).load(R.drawable.btn_shake_heart_double_on).into(holder.iv_like);
        } else if (data.isLike()) {
            Glide.with(act).load(R.drawable.btn_shake_heart_double_off).into(holder.iv_like);
        } else {
            Glide.with(act).load(R.drawable.btn_shake_heart).into(holder.iv_like);
        }

        holder.fl_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!data.isLike()) {
                    doLike(NetUrls.DO_LIKE, data, holder);
                } else if(!data.isLike_double()) {
                    doLike(NetUrls.DO_LIKE_DOUBLE, data, holder);
                } else {
                    Common.showToast(act, "이미 심쿵x2 하였습니다");
                }
            }
        });

        holder.fl_chatting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!data.isLike_message()) {
                    if(frag == null) {
                        act.startActivityForResult(new Intent(act, LikeMessageDlg.class).putExtra("yidx", data.getIdx()), LIKE_MESSAGE);
                    } else {
                        frag.startActivityForResult(new Intent(act, LikeMessageDlg.class).putExtra("yidx", data.getIdx()), LIKE_MESSAGE);
                    }
                } else {
                    Common.showToast(act, "이미 심쿵메시지를 보냈습니다");
                }
            }
        });

        holder.fl_chatting.setSelected(data.isLike_message());

        holder.tv_intro.setText(data.getIntro());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(frag == null) {
                    act.startActivity(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()));
                } else {
                    if(frag instanceof LikeFrag) {
                        frag.startActivityForResult(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()).putExtra("from", "like"), PROFILE_DETAIL);
                    } else {
                        frag.startActivityForResult(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()), PROFILE_DETAIL);
                    }
                }
            }
        });
    }

    private void doLike(final String type, final ReadMeData data, final ReadMeAdapter.ViewHolder holder) {
        ReqBasic server = new ReqBasic(act, type) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (jo.getString("result").equalsIgnoreCase("Y")) {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (type.equalsIgnoreCase(NetUrls.DO_LIKE)) {
                                        data.setLike(true);
                                        Common.showToastLike(act);
                                    } else {
                                        data.setLike_double(true);
                                        Common.showToastLikeDouble(act);
                                    }

                                    notifyDataSetChanged();
                                }
                            });
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
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

        if (type.equalsIgnoreCase(NetUrls.DO_LIKE)) {
            server.setTag("Like");
        } else {
            server.setTag("Like Double");
        }
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", data.getIdx());
        server.execute(true, false);
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

    public void setList(ArrayList<ReadMeData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    public void setFrag(MenuBasicFrag frag) {
        this.frag = frag;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout fl_chatting, fl_like;
        ImageView iv_profile_img, iv_login_state, iv_like;
        TextView tv_intro;
        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            iv_like = view.findViewById(R.id.iv_like);
            fl_like = view.findViewById(R.id.fl_like);
            fl_chatting = view.findViewById(R.id.fl_chatting);
            iv_profile_img = view.findViewById(R.id.iv_profile_img);
            iv_login_state = view.findViewById(R.id.iv_login_state);
            tv_intro = view.findViewById(R.id.tv_intro);

            itemView = view;
        }
    }
}
