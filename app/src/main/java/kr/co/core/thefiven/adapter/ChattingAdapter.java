package kr.co.core.thefiven.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.EnlargeAct;
import kr.co.core.thefiven.data.ChattingData;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class ChattingAdapter extends RecyclerView.Adapter<ChattingAdapter.ViewHolder> {
    private static final int TYPE_ME_TEXT = 1;
    private static final int TYPE_ME_IMAGE = 3;

    private static final int TYPE_YOU_TEXT = 2;
    private static final int TYPE_YOU_IMAGE = 4;

    private static final int TYPE_DATE_LINE = 100;
    private static final int TYPE_SYSTEM = 101;

    private Activity act;
    private ArrayList<ChattingData> list;

    private int current_idx = -1;
    private int previous_idx = -1;

    private String myImage, otherNick, otherImage, otherImageState, otherGender;

    private String room_idx;

    private boolean isPay = false;

    public void setPay(boolean pay) {
        this.isPay = pay;
    }

    public boolean isPay() {
        return this.isPay;
    }

    public ChattingAdapter(Activity act, String room_idx, ArrayList<ChattingData> list,
                           String myImage, String otherNick, String otherImage, String otherImageState, String otherGender) {
        this.act = act;
        this.list = list;
        this.myImage = myImage;
        this.otherNick = otherNick;
        this.otherImage = otherImage;
        this.otherImageState = otherImageState;
        this.otherGender = otherGender;
        this.room_idx = room_idx;
    }

    private void setPayState() {
        isPay = true;

        for (int i = 0; i < list.size(); i++) {
            ChattingData data = list.get(i);
            data.setPay(true);
            list.set(i, data);
        }

        notifyDataSetChanged();
    }

    public void addItem(ChattingData item) {
        if (list == null) {
            list = new ArrayList<>();
        }
        list.add(item);
        notifyDataSetChanged();
    }

    public void setItem(ChattingData item) {
        list.set(list.size() - 1, item);
        notifyDataSetChanged();
    }

    public void setList(ArrayList<ChattingData> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChattingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_ME_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me_text, parent, false);
                return new ViewHolder1(view);
            case TYPE_YOU_TEXT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other_text, parent, false);
                return new ViewHolder2(view);

            case TYPE_ME_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_me_image, parent, false);
                return new ViewHolder3(view);
            case TYPE_YOU_IMAGE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_other_image, parent, false);
                return new ViewHolder4(view);


            case TYPE_DATE_LINE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_dateline, parent, false);
                return new ViewHolder100(view);
            case TYPE_SYSTEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_system, parent, false);
                return new ViewHolder101(view);

            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        ChattingData data = list.get(position);

        String type = data.getData_type();

        String midx = AppPreference.getProfilePref(act, AppPreference.PREF_MIDX);

        switch (type) {
            case "text":
                if (data.getUser_idx().equalsIgnoreCase(midx))
                    return TYPE_ME_TEXT;
                else
                    return TYPE_YOU_TEXT;

            case "image":
                if (data.getUser_idx().equalsIgnoreCase(midx))
                    return TYPE_ME_IMAGE;
                else
                    return TYPE_YOU_IMAGE;

            case "dateline":
                return TYPE_DATE_LINE;

            case "system":
                return TYPE_SYSTEM;

            default:
                return super.getItemViewType(position);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ChattingAdapter.ViewHolder viewHolder, int i) {
        final ChattingData data = list.get(i);
        int viewType = getItemViewType(i);

        switch (viewType) {
            case TYPE_ME_TEXT:
                ViewHolder1 holder1 = (ViewHolder1) viewHolder;

                // set me data
                setMeData(holder1.iv_profile);

                // set text contents
                holder1.tv_contents.setText(data.getContents());

                // set send time
                holder1.tv_send_time.setText(data.getSend_time());

                // 읽음처리
                if (data.isRead()) {
                    holder1.tv_read.setVisibility(View.GONE);
                } else {
                    holder1.tv_read.setVisibility(View.VISIBLE);
                }

                break;

            case TYPE_YOU_TEXT:
                ViewHolder2 holder2 = (ViewHolder2) viewHolder;

                // set other data
                setOtherData(holder2.tv_nick, holder2.iv_profile);

                // set text contents
                if (data.isPay()) {
                    holder2.tv_contents.setText(data.getContents());
                } else {
                    holder2.tv_contents.setText("결제 후 확인가능합니다");
                }
                holder2.tv_contents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!data.isPay()) {
                            showAlertPay();
                        }
                    }
                });

                // set send time
                holder2.tv_send_time.setText(data.getSend_time());

                // 읽음처리
                if (data.isRead()) {
                    holder2.tv_read.setVisibility(View.GONE);
                } else {
                    holder2.tv_read.setVisibility(View.VISIBLE);
                }


                break;

            case TYPE_ME_IMAGE:
                ViewHolder3 holder3 = (ViewHolder3) viewHolder;

                // set me data
                setMeData(holder3.iv_profile);

                // set image contents
                Glide.with(act)
                        .load(data.getContents())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                        .into(holder3.iv_send_image);

                // set send time
                holder3.tv_send_time.setText(data.getSend_time());


                holder3.iv_send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        act.startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", data.getContents()));
                    }
                });

                // 읽음처리
                if (data.isRead()) {
                    holder3.tv_read.setVisibility(View.GONE);
                } else {
                    holder3.tv_read.setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_YOU_IMAGE:
                ViewHolder4 holder4 = (ViewHolder4) viewHolder;

                // set other data
                setOtherData(holder4.tv_nick, holder4.iv_profile);

                // set image contents
                if (data.isPay()) {
                    holder4.iv_send_image.setVisibility(View.VISIBLE);
                    holder4.tv_contents.setVisibility(View.GONE);

                    Glide.with(act)
                            .load(data.getContents())
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .transform(new MultiTransformation<Bitmap>(new CenterCrop(), new RoundedCorners(40)))
                            .into(holder4.iv_send_image);

                    holder4.iv_send_image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            act.startActivity(new Intent(act, EnlargeAct.class).putExtra("imageUrl", data.getContents()));
                        }
                    });
                } else {
                    holder4.iv_send_image.setVisibility(View.GONE);
                    holder4.tv_contents.setVisibility(View.VISIBLE);

                    holder4.tv_contents.setText("결제 후 확인가능합니다");
                }

                holder4.tv_contents.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!data.isPay()) {
                            showAlertPay();
                        }
                    }
                });

                // set send time
                holder4.tv_send_time.setText(data.getSend_time());

                // set image enlarge
                holder4.iv_send_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        enlargeImage(data.getMsg());
                    }
                });

                // 읽음처리
                if (data.isRead()) {
                    holder4.tv_read.setVisibility(View.GONE);
                } else {
                    holder4.tv_read.setVisibility(View.VISIBLE);
                }
                break;

            case TYPE_DATE_LINE:
                ViewHolder100 holder100 = (ViewHolder100) viewHolder;

                // set date
                holder100.tv_date.setText(data.getDate_line());
                break;

            case TYPE_SYSTEM:
                ViewHolder101 holder101 = (ViewHolder101) viewHolder;

                // set system msg
                holder101.tv_system.setText(data.getContents());
                break;
        }

    }

    private void setMeData(ImageView iv_profile) {
        Log.i(StringUtil.TAG, "myImage: " + myImage);
        // set profile image

        if(StringUtil.isNull(myImage)) {
            if(StringUtil.isNull(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER))) {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(iv_profile);
            } else {
                if(AppPreference.getProfilePref(act, AppPreference.PREF_GENDER).equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(iv_profile);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(iv_profile);
                }
            }
        } else {
            Glide.with(act)
                    .load(myImage)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(iv_profile);
        }


//        // set profile image enlarge
//        iv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!StringUtil.isNull(imageUrl))
//                    enlargeImage(imageUrl);
//            }
//        });
    }

    private void setOtherData(TextView tv_nick, ImageView iv_profile) {
        // set name
        tv_nick.setText(otherNick);

        if (otherImageState.equalsIgnoreCase("Y")) {
            Glide.with(act)
                    .load(otherImage)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(iv_profile);
        } else if (otherImageState.equalsIgnoreCase("N") || otherImageState.equalsIgnoreCase("fail")) {
            Glide.with(act)
                    .load(otherImage)
                    .centerCrop()
                    .transform(new BlurTransformation(10, 3), new CircleCrop())
                    .into(iv_profile);
        } else {
            if(StringUtil.isNull(otherGender)) {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(iv_profile);
            } else {
                if(otherGender.equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(iv_profile);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(iv_profile);
                }
            }
        }


//        // set profile image enlarge
//        iv_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!StringUtil.isNull(imageUrl))
//                    enlargeImage(imageUrl);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(@NonNull View view) {
            super(view);
        }
    }

    class ViewHolder1 extends ViewHolder {
        TextView tv_contents, tv_send_time, tv_read;
        ImageView iv_profile;

        ViewHolder1(@NonNull View view) {
            super(view);
            tv_contents = view.findViewById(R.id.tv_contents);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_read = view.findViewById(R.id.tv_read);
        }
    }

    class ViewHolder2 extends ViewHolder {
        TextView tv_contents, tv_send_time, tv_nick, tv_read;
        ImageView iv_profile;

        ViewHolder2(@NonNull View view) {
            super(view);
            tv_contents = view.findViewById(R.id.tv_contents);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            tv_nick = view.findViewById(R.id.tv_nick);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_read = view.findViewById(R.id.tv_read);

        }
    }

    class ViewHolder3 extends ViewHolder {
        TextView tv_send_time, tv_read;
        ImageView iv_profile, iv_send_image;

        ViewHolder3(@NonNull View view) {
            super(view);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            tv_read = view.findViewById(R.id.tv_read);
            iv_send_image = (ImageView) view.findViewById(R.id.iv_send_image);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_send_image.setClipToOutline(true);
            }
        }
    }

    class ViewHolder4 extends ViewHolder {
        TextView tv_send_time, tv_nick, tv_read, tv_contents;
        ImageView iv_profile, iv_send_image;

        ViewHolder4(@NonNull View view) {
            super(view);
            tv_read = view.findViewById(R.id.tv_read);
            tv_nick = view.findViewById(R.id.tv_nick);
            iv_profile = view.findViewById(R.id.iv_profile);
            tv_send_time = view.findViewById(R.id.tv_send_time);
            iv_send_image = (ImageView) view.findViewById(R.id.iv_send_image);
            tv_contents = view.findViewById(R.id.tv_contents);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                iv_send_image.setClipToOutline(true);
            }
        }
    }

    class ViewHolder100 extends ViewHolder {
        TextView tv_date;

        ViewHolder100(@NonNull View view) {
            super(view);
            tv_date = view.findViewById(R.id.tv_date);
        }
    }

    class ViewHolder101 extends ViewHolder {
        TextView tv_system;

        ViewHolder101(@NonNull View view) {
            super(view);
            tv_system = view.findViewById(R.id.tv_system);
        }
    }

    private void showAlertPay() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("채팅 결제");
        alertDialog.setMessage("채팅 보내기 및 채팅 내용 확인을 위해 1P를 결제하시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        doPayChat();
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

    private void doPayChat() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_CONFIRM_REPLY) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            act.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setPayState();
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

        server.setTag("Chatting Pay");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }
}
