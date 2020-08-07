package kr.co.core.thefiven.utility;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class StringUtil {
    public static final String TAG = "TEST_HOME";
    public static final String TAG_PUSH = "TEST_PUSH";
    public static final String TAG_PAY = "TEST_PAY";
    public static final String TAG_CHAT = "TEST_CHAT";

    public static boolean isNull(String str) {
        if (str == null || str.length() == 0 || str.equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    public static String converTime(String original, String pattern) {
        //아이템별 시간
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
        Date date1 = null;
        try {
            date1 = dateFormat1.parse(original);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat2 = new SimpleDateFormat(pattern, java.util.Locale.getDefault());
        return dateFormat2.format(date1);
    }

    public static String setNumComma(int price) {
        DecimalFormat format = new DecimalFormat("###,###");
        return format.format(price);
    }

    public static String calcAge(String byear) {
        if (byear.equalsIgnoreCase("미입력")) {
            return "미입력";
        } else {

            // 현재 연도에서 출생 연도를 뺀다. (2018 - 2000 = 18)
            // 1살을 더한다. (18 + 1 = 19)
            Calendar c = Calendar.getInstance();
//        Log.i(TAG,"year: "+(c.get(Calendar.YEAR)-Integer.parseInt(byear)+1));
            int lastYear = c.get(Calendar.YEAR) - Integer.parseInt(byear) + 1;


            return String.valueOf(lastYear);
        }
    }

    public static String getStr(JSONObject jo, String key) {
        String s = null;
        try {
            if (jo.has(key)) {
                s = jo.getString(key);
                if (isNull(s) || s.equalsIgnoreCase("none")) {
                    s = "";
                }
            } else {
                s = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static String getProfileStr(JSONObject jo, String key) {
        String s = null;
        try {
            if (jo.has(key)) {
                s = jo.getString(key);
                if (isNull(s) || s.equalsIgnoreCase("none")) {
                    s = "미입력";
                }
            } else {
                s = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    // 안드로이드10 부터 디바이스id 가져오는거 안되서 다른방법 사용
    public static String getDeviceId(Context ctx) {
        String newId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            newId = Settings.Secure.getString(ctx.getContentResolver(), Settings.Secure.ANDROID_ID);
        } else {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {
                newId = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            }

            if (StringUtil.isNull(newId)) {
                newId = "35" +
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                        Build.USER.length() % 10;
            }
        }

        if (!StringUtil.isNull(newId)) {
            AppPreference.setProfilePref(ctx, AppPreference.PREF_DEVICE_ID, newId);
            Log.e(StringUtil.TAG, "device id: " + newId);
        }

        return newId;
    }

    public static void logLargeString(String str) {
        if (str.length() > 1500) {
            Log.i(StringUtil.TAG, str.substring(0, 1500));
            logLargeString(str.substring(1500));
        } else {
            Log.i(StringUtil.TAG, str); // continuation
        }
    }


    /* 회원정보 정리 */
    public static final String PROF_GENDER = "gender";
    public static final String PROF_JOB = "job";
    public static final String PROF_SALARY = "salary";
    public static final String PROF_MARRIAGE = "marriage";
    public static final String PROF_PERSONALITY = "personality";
    public static final String PROF_NATIONALITY = "nationality";
    public static final String PROF_BLOOD = "blood";
    public static final String PROF_LOCATION = "location";
    public static final String PROF_EDU = "edu";
    public static final String PROF_HOLIDAY = "holiday";
    public static final String PROF_FAMILY = "family";
    public static final String PROF_HEIGHT = "height";
    public static final String PROF_BODY = "body";
    public static final String PROF_DRINK = "drink";
    public static final String PROF_SMOKE = "smoke";
    public static final String PROF_NO_DATA = "선택안함";

    /* 검색 */
    public static final String SEARCH_AGE = "age";
    public static final String SEARCH_JOB = "job";
    public static final String SEARCH_SALARY = "salary";
    public static final String SEARCH_MARRIAGE = "marriage";
    public static final String SEARCH_PERSONALITY = "personality";
    public static final String SEARCH_NATIONALITY = "nationality";
    public static final String SEARCH_BLOOD = "blood";
    public static final String SEARCH_LOCATION = "location";
    public static final String SEARCH_EDU = "edu";
    public static final String SEARCH_HOLIDAY = "holiday";
    public static final String SEARCH_FAMILY = "family";
    public static final String SEARCH_HEIGHT = "height";
    public static final String SEARCH_BODY = "body";
    public static final String SEARCH_DRINK = "drink";
    public static final String SEARCH_SMOKE = "smoke";
    public static final String SEARCH_CGPMS = "cgpms";

    public static final String SEARCH_INTRO = "intro";
    public static final String SEARCH_IMAGES = "images";
    public static final String SEARCH_JOIN3DAYS = "join3day";
    public static final String SEARCH_INTEREST = "interest";

    /* 약관 */
    public static final String TERMS_USE = "app_term_condition"; // 이용약관
    public static final String TERMS_PRIVATE = "app_customer_notice"; // 개인정보보호정책
    public static final String TERMS_PAY = "app_paylaw"; // 자금결제법에 근거한 표시
    public static final String TERMS_TRADE = "app_merchantlaw"; // 특정상거래법에 근거한 표시

    /* 설명페이지 */
    public static final String EXPLANATION_HELP = "help"; // 도움말
    public static final String EXPLANATION_FIVE = "five"; // FIVE란?
    public static final String EXPLANATION_24HOUR = "24hour"; // 24시간 감시체제
    public static final String EXPLANATION_SECURITY = "security"; // 안심의 보안체제
    public static final String EXPLANATION_GUIDE = "guide"; // 안심/안전가이드
    public static final String EXPLANATION_COMMUNITY = "comunity"; // 커뮤니티 가이드라인에 대하여
    public static final String EXPLANATION_INTRODUCE = "introduce"; // 회사개요

    /* 신고하기 */
    public static final String POPUP_REPORT01 = "violation01";
    public static final String POPUP_REPORT02 = "violation02";

    /* 문의하기 */
    public static final String POPUP_CONTACT = "contact";

    /* 갤러리 */
    public static final String SORT_GALLERY = "gallery";

    /* 팝업 */
    // 기본알림
    public static final String DLG_POINT = "point";
    public static final String DLG_BIRTH = "birth";
    public static final String DLG_INTEREST = "interest";
    public static final String DLG_INTRO = "intro";
    public static final String DLG_JOIN_COMPLETE = "join";
    public static final String DLG_CHECK = "check";
    public static final String DLG_PASSWORD = "password";

    // 경고알림
    public static final String DLG_BLOCK = "block";
    public static final String DLG_CANCEL = "block_cancel";
    public static final String DLG_POINT_NONE = "point_none";


    /* 인앱 */
    // 구독
    public static final String SUBS_01_CODE = "subs_01";
    public static final String SUBS_01_NAME = "1개월";
    public static final String SUBS_01_PRICE = "32000";

    public static final String SUBS_02_CODE = "subs_02";
    public static final String SUBS_02_NAME = "3개월";
    public static final String SUBS_02_PRICE = "60000";

    public static final String SUBS_03_CODE = "subs_03";
    public static final String SUBS_03_NAME = "6개월";
    public static final String SUBS_03_PRICE = "77000";

    public static final String SUBS_04_CODE = "subs_04";
    public static final String SUBS_04_NAME = "12개월";
    public static final String SUBS_04_PRICE = "98000";

    // 인앱
    public static final String POINT_01_CODE = "point_01";
    public static final String POINT_01_NAME = "10P";
    public static final String POINT_01_PRICE = "12000";

    public static final String POINT_02_CODE = "point_02";
    public static final String POINT_02_NAME = "20P";
    public static final String POINT_02_PRICE = "21000";

    public static final String POINT_03_CODE = "point_03";
    public static final String POINT_03_NAME = "30P";
    public static final String POINT_03_PRICE = "29000";

    public static final String POINT_04_CODE = "point_04";
    public static final String POINT_04_NAME = "40P";
    public static final String POINT_04_PRICE = "37000";

    public static final String POINT_05_CODE = "point_05";
    public static final String POINT_05_NAME = "50P";
    public static final String POINT_05_PRICE = "45000";

    public static final String POINT_06_CODE = "point_06";
    public static final String POINT_06_NAME = "100P";
    public static final String POINT_06_PRICE = "79000";


    //심쿵
    public static final String LIKE_ITEM_01 = "sim01";
    public static final String LIKE_ITEM_02 = "sim02";
    public static final String LIKE_ITEM_03 = "sim03";
    public static final String LIKE_ITEM_04 = "sim04";
    public static final String LIKE_ITEM_05 = "sim05";

}
