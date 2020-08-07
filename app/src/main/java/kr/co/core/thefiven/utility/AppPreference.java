package kr.co.core.thefiven.utility;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreference {
    /* profile string key */
    public static final String PREF_MIDX = "midx";
    public static final String PREF_FCM = "fcm";
    public static final String PREF_DEVICE_ID = "device_id";
    public static final String PREF_ID = "id";
    public static final String PREF_PW = "pw";
    public static final String PREF_GENDER = "gender";
    public static final String PREF_PHONE = "phone";
    public static final String PREF_IMAGE = "image";
    public static final String PREF_CGPMS = "cgpms";

    public static final String PREF_POINT = "point";
    public static final String PREF_LIKE = "point";

    /* profile boolean key */
    public static final String PREF_AUTO_LOGIN_STATE = "auto_login";
    public static final String PREF_PAY_MEMBER = "pay_member";

    // member_state boolean
    public static final String LIST_MAIN_STATE = "main_state";
    public static final String LIST_CGPMS_STATE = "cgpms_state";
    public static final String LIST_LIKE_STATE = "like_state";
    public static final String LIST_CHAT_STATE = "chat_state";
    public static final String LIST_READ_ME_STATE = "read_me_state";

    /* alarm key */
    public static final String ALARM_LIKE = "like";
    public static final String ALARM_LIKE_MESSAGE = "like_message";
    public static final String ALARM_MATCHING = "matching";
    public static final String ALARM_CHATTING = "chatting";
    public static final String ALARM_OTHER = "other";
    public static final String ALARM_VISIT = "visit";
    public static final String ALARM_OFFLINE = "offline";


    // profile string
    public static void setProfilePref(Context context, String key, String value) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(key, value);
        editor.commit();
    }
    public static String getProfilePref(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        return pref.getString(key, null);
    }


    // profile boolean
    public static void setProfilePrefBool(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static Boolean getProfilePrefBool(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("profile", context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }



    // alarm
    public static void setAlarmPref(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences("alarm", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static Boolean getAlarmPref(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("alarm", context.MODE_PRIVATE);
        return pref.getBoolean(key, true);
    }


    // member_state boolean
    public static void setMemberStateAll(Context context) {
        SharedPreferences pref = context.getSharedPreferences("member_state", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(LIST_MAIN_STATE, true);
        editor.putBoolean(LIST_CGPMS_STATE, true);
        editor.putBoolean(LIST_LIKE_STATE, true);
        editor.putBoolean(LIST_CHAT_STATE, true);
        editor.putBoolean(LIST_READ_ME_STATE, true);
        editor.commit();
    }

    // member_state boolean
    public static void setMemberState(Context context, String key, boolean value) {
        SharedPreferences pref = context.getSharedPreferences("member_state", context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }
    public static Boolean getMemberState(Context context, String key) {
        SharedPreferences pref = context.getSharedPreferences("member_state", context.MODE_PRIVATE);
        return pref.getBoolean(key, false);
    }

}
