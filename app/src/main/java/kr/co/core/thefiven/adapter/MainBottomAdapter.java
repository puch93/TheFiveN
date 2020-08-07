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

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ProfileDetailAct;
import kr.co.core.thefiven.data.MainBottomData;
import kr.co.core.thefiven.fragment.MainFrag;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class MainBottomAdapter extends RecyclerView.Adapter<MainBottomAdapter.ViewHolder> {
    private static final int FROM_PROFILE = 1002;

    private Activity act;
    private ArrayList<MainBottomData> list;

    private static final int PROFILE_DETAIL = 1002;

    private MainFrag mainFrag;

    public void setPayMember(boolean payMember) {
        isPayMember = payMember;
    }

    private boolean isPayMember;

    public MainBottomAdapter(Activity act, ArrayList<MainBottomData> list) {
        this.act = act;
        this.list = list;

        isPayMember = AppPreference.getProfilePrefBool(act, AppPreference.PREF_PAY_MEMBER);
    }


    public void setMainFrag(MainFrag mainFrag) {
        this.mainFrag = mainFrag;
    }

    @NonNull
    @Override
    public MainBottomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_main_bottom, parent, false);

//        int height = 0;
//        height = (parent.getMeasuredWidth() - act.getResources().getDimensionPixelSize(R.dimen.dimen_34)) / 2;
//        Log.e(StringUtil.TAG, "width: " + height);
//
//        if(height <= 0 ) {
//            height = act.getResources().getDimensionPixelSize(R.dimen.main_bottom_default_dp);
//        }
        MainBottomAdapter.ViewHolder viewHolder = new MainBottomAdapter.ViewHolder(view);
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewHolder.iv_profile.getLayoutParams();
//        params.height = height;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MainBottomAdapter.ViewHolder holder, int i) {
        final MainBottomData data = list.get(i);

        checkImageState(holder.iv_profile, data.getProfile_img(), data.getProfile_img_ck(), data.getGender());

        if(data.getProfile_img_count().equalsIgnoreCase("0")) {
            holder.tv_indicator.setText("0/" + data.getProfile_img_count());
        } else {
            holder.tv_indicator.setText("1/" + data.getProfile_img_count());
        }

        holder.tv_nick.setText(data.getNick());
        holder.tv_age.setText(data.getAge());
        holder.tv_location.setText(data.getLocation());

        holder.tv_cgpms_kind.setText(data.getCgpms_kind());
        if(isPayMember) {
            holder.tv_cgpms_point.setText(data.getCgpms_point());
        }

        if (data.isLike_double_state()) {
            Glide.with(act).load(R.drawable.btn_double_heart_on).into(holder.iv_like);
        } else if (data.isLike_state()) {
            Glide.with(act).load(R.drawable.btn_double_heart_off).into(holder.iv_like);
        } else {
            Glide.with(act).load(R.drawable.btn_main_heart_shake_off).into(holder.iv_like);
        }

        holder.iv_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!data.isLike_state()) {
                    doLike(NetUrls.DO_LIKE, data, holder);
                } else if(!data.isLike_double_state()) {
                    doLike(NetUrls.DO_LIKE_DOUBLE, data, holder);
                } else {
                    Common.showToast(act, "이미 심쿵x2 하였습니다");
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mainFrag != null) {
                    mainFrag.startActivityForResult(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()), PROFILE_DETAIL);
                } else {
                    act.startActivity(new Intent(act, ProfileDetailAct.class).putExtra("yidx", data.getIdx()));
                }
            }
        });
    }

    private void doLike(final String type, final MainBottomData data, final ViewHolder holder) {
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
                                        data.setLike_state(true);
                                        Common.showToastLike(act);
                                    } else {
                                        data.setLike_double_state(true);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<MainBottomData> list) {
        this.list = list;
        notifyDataSetChanged();
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
        } else if (state.equalsIgnoreCase("N") || state.equalsIgnoreCase("fail")) {
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


    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iv_profile;
        ImageView iv_like;
        TextView tv_nick;
        TextView tv_age;
        TextView tv_location;
        TextView tv_cgpms_point;
        TextView tv_cgpms_kind;
        TextView tv_indicator;

        View itemView;

        ViewHolder(@NonNull View view) {
            super(view);
            iv_like = view.findViewById(R.id.iv_like);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_nick = view.findViewById(R.id.tv_nick);
            tv_age = view.findViewById(R.id.tv_age);
            tv_location = view.findViewById(R.id.tv_location);
            tv_cgpms_point = view.findViewById(R.id.tv_cgpms_point);
            tv_cgpms_kind = view.findViewById(R.id.tv_cgpms_kind);
            tv_indicator = view.findViewById(R.id.tv_indicator);

            itemView = view;
        }
    }
}
