package kr.co.core.thefiven.firebase;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.ChattingAct;
import kr.co.core.thefiven.activity.MainAct;
import kr.co.core.thefiven.activity.PushAct;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.StringUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private Context ctx;

    private static final String TYPE_CHAT = "chatting";
    private static final String TYPE_LIKE = "single_heartattack";
    private static final String TYPE_LIKE_X2 = "double_heartattack";
    private static final String TYPE_MATCHING = "single_heartattack_matching";
    private static final String TYPE_LIKE_MESSAGE = "message_heartattack";
    private static final String TYPE_LIKE_MESSAGE_CHAT = "message_heartattack_chat";
    private static final String TYPE_CGPMS = "top";
    private static final String TYPE_GIFT = "giftishow";
    private static final String TYPE_LOGOUT = "logout";

    @Override
    public void onNewToken(String token) {
        Log.e(StringUtil.TAG_PUSH, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {

    }

    /**
     * 푸시종류
     * 1. 심쿵
     * 2. 매칭
     * 3. 채팅
     * 4. 그외의 알림 (CGPMS 등)
     */

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        ctx = getApplicationContext();

        Log.e(StringUtil.TAG_PUSH, "remoteMessage.getData: " + remoteMessage.getData());
        JSONObject jo = new JSONObject(remoteMessage.getData());

        String type = StringUtil.getStr(jo, "type");

        switch (type) {
            case TYPE_CHAT:
                // 알람설정 켰는지 확인
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_CHATTING)) {
                    // 채팅액티비티 인지 or 해당회원 채팅방인지
                    if (ChattingAct.real_act == null || !ChattingAct.room_idx.equalsIgnoreCase(StringUtil.getStr(jo, "room_idx"))) {

                        String target_idx = StringUtil.getStr(jo, "target_idx");
                        // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                        if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                            // 메인액티비티 살아있을때 채팅 프래그먼트 리프래쉬
                            if (MainAct.act != null) {
                                ((MainAct) MainAct.act).refreshAboutChatting();
                            }

                            if (StringUtil.getStr(jo, "room_type").equalsIgnoreCase("heartmessage")) {
                                sendChattingNotification(jo, true);
                            } else {
                                checkChattingPay(jo, StringUtil.getStr(jo, "room_idx"));
                            }
                        }
                    }
                }
                break;

            case TYPE_LIKE:
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_LIKE)) {

                    String target_idx = StringUtil.getStr(jo, "target_idx");
                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                        sendSystemNotification(jo, "심쿵", 2);
                    }
                }
                break;

            case TYPE_LIKE_X2:
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_LIKE)) {

                    String target_idx = StringUtil.getStr(jo, "target_idx");
                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                        sendSystemNotification(jo, "심쿵x2", 15);
                    }
                }
                break;

            case TYPE_MATCHING:
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_MATCHING)) {
                    String target_idx = StringUtil.getStr(jo, "target_idx");
                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                        sendSystemNotification(jo, "매칭", 3);
                    }
                }
                break;


            case TYPE_LIKE_MESSAGE:
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_LIKE_MESSAGE)) {

                    String target_idx = StringUtil.getStr(jo, "target_idx");
                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                        sendMessageNotification(jo, 7);
                    }
                }
                break;

            case TYPE_LIKE_MESSAGE_CHAT:
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_LIKE_MESSAGE)) {

                    String target_idx = StringUtil.getStr(jo, "target_idx");
                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                        sendChattingRoomNotification(jo, 5);
                    }
                }
                break;

            case TYPE_CGPMS:
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_OTHER)) {
                    sendCgpmsNotification(jo, 4);
                }
                break;

            case TYPE_GIFT:
                if (AppPreference.getAlarmPref(ctx, AppPreference.ALARM_OTHER)) {
                    String target_idx = StringUtil.getStr(jo, "target_idx");
                    // 나에게 오는 푸시가 맞으면 (인덱스로 비교)
                    if (target_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX))) {
                        sendSystemNotification(jo, "기프티콘", 6);
                    }
                }
                break;

            case TYPE_LOGOUT:
                AppPreference.setProfilePrefBool(ctx, AppPreference.PREF_AUTO_LOGIN_STATE, false);
                AppPreference.setProfilePref(ctx, AppPreference.PREF_MIDX, null);

                Intent intent = new Intent(this, PushAct.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("type", "logout");

                PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 10, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                break;
        }
    }


    private void checkChattingPay(final JSONObject json, final String room_idx) {
        ReqBasic server = new ReqBasic(ctx, NetUrls.CHAT_PAY_CHECK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            sendChattingNotification(json, true);
                        } else {
                            sendChattingNotification(json, false);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        };

        server.setTag("Check Chatting Pay");
        server.addParams("midx", AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }


    private void sendCgpmsNotification(JSONObject jo, int id) {
        String status = StringUtil.getStr(jo, "status");
        String idx = StringUtil.getStr(jo, "idx");
        String msg = StringUtil.getStr(jo, "msg");
        String url = StringUtil.getStr(jo, "url");
        String send = StringUtil.getStr(jo, "send");
        String type = StringUtil.getStr(jo, "type");
        String cgpms = StringUtil.getStr(jo, "cgpms");
        String edate = StringUtil.getStr(jo, "edate");
        String sdate = StringUtil.getStr(jo, "sdate");
        String title = StringUtil.getStr(jo, "title");
        String regdate = StringUtil.getStr(jo, "regdate");
        String senddate = StringUtil.getStr(jo, "senddate");


        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "더 파이브", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("더 파이브 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = null;
        if (StringUtil.isNull(url)) {
            intent = new Intent(ctx, PushAct.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(msg)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(id, notification);
    }


    private void sendSystemNotification(JSONObject jo, String title, int id) {
        String msg_from = StringUtil.getStr(jo, "msg_from");
        String gender = StringUtil.getStr(jo, "gender");
        String room_idx = StringUtil.getStr(jo, "room_idx");
        String type = StringUtil.getStr(jo, "type");
        String target_idx = StringUtil.getStr(jo, "target_idx");
        String message = StringUtil.getStr(jo, "message");
        String sender_img = StringUtil.getStr(jo, "sender_img");

        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "더 파이브", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("더 파이브 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = new Intent(ctx, PushAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(id, notification);
    }

    private void sendMessageNotification(JSONObject jo, int id) {
        String msg_from = StringUtil.getStr(jo, "msg_from");
        String gender = StringUtil.getStr(jo, "gender");
        String room_idx = StringUtil.getStr(jo, "room_idx");
        String room_type = StringUtil.getStr(jo, "room_type");
        String type = StringUtil.getStr(jo, "type");
        String target_idx = StringUtil.getStr(jo, "target_idx");
        String nick = StringUtil.getStr(jo, "nick");
        String message = StringUtil.getStr(jo, "message");
        String sender_img = StringUtil.getStr(jo, "sender_img");
        String title = "심쿵 메시지";

        if (room_type.equalsIgnoreCase("heartmessage")) {
            title = nick + "님의 심쿵 메시지 답변";
        } else {
            title = nick + "님의 심쿵 메시지";
        }

        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "더 파이브", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("더 파이브 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = new Intent(ctx, PushAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(id, notification);
    }

    private void sendChattingRoomNotification(JSONObject jo, int id) {
        String msg_from = StringUtil.getStr(jo, "msg_from");
        String gender = StringUtil.getStr(jo, "gender");
        String room_idx = StringUtil.getStr(jo, "room_idx");
        String type = StringUtil.getStr(jo, "type");
        String target_idx = StringUtil.getStr(jo, "target_idx");
        String nick = StringUtil.getStr(jo, "nick");
        String message = StringUtil.getStr(jo, "message");
        String sender_img = StringUtil.getStr(jo, "sender_img");
        String title = "심쿵 메시지";

        title = "더 파이브";
//        message = message.substring(0,message.length()-15);
//        message += "님과 채팅방이 생성되었습니다.";

        //매니저 설정
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        //채널설정
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default", "더 파이브", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("더 파이브 알림설정");

            notificationManager.createNotificationChannel(channel);
        }

        //인텐트 설정
        Intent intent = new Intent(ctx, PushAct.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        //노티 설정
        Notification notification = new NotificationCompat.Builder(ctx, "default")
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.app_icon)
                .setContentIntent(pendingIntent)
                .build();

        //푸시 날리기
        notificationManager.notify(id, notification);
    }


    private boolean isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg);
    }

    private void sendChattingNotification(JSONObject jo, boolean isPay) {
        try {
            String msg_from = StringUtil.getStr(jo, "msg_from");
            String gender = StringUtil.getStr(jo, "gender");
            String room_type = StringUtil.getStr(jo, "room_type");
            String host_idx = StringUtil.getStr(jo, "host_idx");
            String room_idx = StringUtil.getStr(jo, "room_idx");
            String nick = StringUtil.getStr(jo, "nick");
            String type = StringUtil.getStr(jo, "type");
            String target_idx = StringUtil.getStr(jo, "target_idx");
            String message = StringUtil.getStr(jo, "message");

            String title = nick + "님의 채팅";

            boolean isGuest = host_idx.equalsIgnoreCase(AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX));

            //프로필 사진관련
            JSONArray img_array = new JSONArray(StringUtil.getStr(jo, "sender_img"));
            JSONObject img_object = img_array.getJSONObject(0);
            String profile_img = StringUtil.getStr(img_object, "pi_img");
            String pi_img_chk = StringUtil.getStr(img_object, "pi_img_chk");


            for (int j = 0; j < img_array.length(); j++) {
                JSONObject object = img_array.getJSONObject(j);
                String profile_img_tmp = StringUtil.getStr(object, "pi_img");
                String pi_img_chk_tmp = StringUtil.getStr(object, "pi_img_chk");

                if(pi_img_chk_tmp.equalsIgnoreCase("Y")) {
                    profile_img = profile_img_tmp;
                    pi_img_chk = pi_img_chk_tmp;
                    break;
                }
            }


            if (isPay) {
                if (isImage(message)) {
                    message = "이미지";
                }
            } else {
                message = "결제 후 확인가능합니다";
            }


            //매니저 설정
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            //채널설정
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("default", "더 파이브", NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription("더 파이브 알림설정");

                notificationManager.createNotificationChannel(channel);
            }

            //인텐트 설정
            Intent intent = new Intent(ctx, ChattingAct.class);
            intent.putExtra("room_idx", room_idx);
            intent.putExtra("yidx", msg_from);
            intent.putExtra("otherNick", nick);
            intent.putExtra("otherImage", profile_img);
            intent.putExtra("otherImageState", pi_img_chk);
            intent.putExtra("otherGender", gender);
            intent.putExtra("room_type", room_type);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            //노티 설정
            Notification notification = new NotificationCompat.Builder(ctx, "default")
                    .setContentTitle(title)
                    .setContentText(message)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentIntent(pendingIntent)
                    .build();

            //푸시 날리기
            notificationManager.notify(0, notification);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private boolean isAppRun(Context context) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(9999);

        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).topActivity.getPackageName().equalsIgnoreCase(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
