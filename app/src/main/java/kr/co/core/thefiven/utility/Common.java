package kr.co.core.thefiven.utility;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.thefiven.R;


public class Common {
    /* toast setting */
    public static void showToast(final Activity act, final String msg) {
        if(null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showToastLong(final Activity act, final String msg) {
        if(null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void showToastNetwork(final Activity act) {
        if(null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, act.getString(R.string.toast_network), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showToastDevelop(final Activity act) {
        if(null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(act, act.getString(R.string.toast_develop), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static void showToastLike(final Activity act) {
        if(null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = new Toast(act);
                    LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.toast_like, null);
                    Glide.with(act).load(R.drawable.icon_main_popup_heart_shake).into((ImageView) view.findViewById(R.id.iv_like));
                    ((TextView)view.findViewById(R.id.tv_like)).setText("심쿵!");
                    toast.setView(view);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }

    public static void showToastLikeDouble(final Activity act) {
        if(null != act) {
            act.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast toast = new Toast(act);
                    LayoutInflater inflater = (LayoutInflater) act.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view = inflater.inflate(R.layout.toast_like, null);
                    Glide.with(act).load(R.drawable.icon_main_popup_double_heart).into((ImageView) view.findViewById(R.id.iv_like));
                    ((TextView)view.findViewById(R.id.tv_like)).setText("심쿵x2!");
                    toast.setView(view);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            });
        }
    }

    public static boolean checkCellnum(String cellnum) {
        cellnum = PhoneNumberUtils.formatNumber(cellnum);

        boolean returnValue = false;
        try {
            String regex = "^\\s*(010|011|016|017|018|019)(-|\\)|\\s)*(\\d{3,4})(-|\\s)*(\\d{4})\\s*$";

            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(cellnum);
            if (m.matches()) {
                returnValue = true;
            }

            if (returnValue && cellnum != null
                    && cellnum.length() > 0
                    && cellnum.startsWith("010")) {
                cellnum = cellnum.replace("-", "");
                if (cellnum.length() < 10) {
                    returnValue = false;
                }
            }
            return returnValue;
        } catch (Exception e) {
            return false;
        }
    }



    // get device id
    public static String getDeviceId(Context ctx) {
        String deviceID = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ctx.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                deviceID = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            }
        } else {
            deviceID = ((TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
        }

        if (StringUtil.isNull(deviceID)) {
            deviceID = "35" +
                    Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                    Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                    Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                    Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                    Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                    Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                    Build.USER.length() % 10;
            if (TextUtils.isEmpty(deviceID)) {
                deviceID = UUID.randomUUID().toString();
            }
        }

        return deviceID;
    }

    public static String converTimeSimpleLong(long original) {
        Date date = new Date(original);
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("m:ss", java.util.Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String converTimeSimpleLInt(int original) {
        Date date = new Date(original);
        SimpleDateFormat dateFormat;
        dateFormat = new SimpleDateFormat("m:ss", java.util.Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String converTime(String original) {
        //아이템별 시간
        String time1 = original;
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", java.util.Locale.getDefault());
        Date date1 = null;
        try {
            date1 = dateFormat1.parse(time1);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("a hh:mm", java.util.Locale.getDefault());
        String time2 = dateFormat2.format(date1);
        return time2;
    }

    public static boolean isAppTopRun(Context ctx, String baseClassName) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info;
        info = activityManager.getRunningTasks(1);
        if (info == null || info.size() == 0) {
            return false;
        }
        if (info.get(0).baseActivity.getClassName().equals(baseClassName)) {
            return true;
        } else {
            return false;
        }
    }

    private static class TIME_MAXIMUM {
        static final int SEC = 60;
        static final int MIN = 60;
        static final int HOUR = 24;
        static final int DAY = 30;
        static final int MONTH = 12;
    }

    public static String formatImeString(Date tempDate, Activity act) {
        long curTime = System.currentTimeMillis();
        long regTime = tempDate.getTime();
        long diffTime = (curTime - regTime) / 1000;

        String msg = null;
        if (diffTime < 0) {
            msg = "0" + act.getString(R.string.ago_seconds);
        } else if (diffTime < TIME_MAXIMUM.SEC) {
            msg = diffTime + act.getString(R.string.ago_seconds);
        } else if ((diffTime /= TIME_MAXIMUM.SEC) < TIME_MAXIMUM.MIN) {
            msg = diffTime + act.getString(R.string.ago_minutes);
        } else if ((diffTime /= TIME_MAXIMUM.MIN) < TIME_MAXIMUM.HOUR) {
            msg = (diffTime) + act.getString(R.string.ago_hours);
        } else if ((diffTime /= TIME_MAXIMUM.HOUR) < TIME_MAXIMUM.DAY) {
            msg = (diffTime) + act.getString(R.string.ago_days);
        } else if ((diffTime /= TIME_MAXIMUM.DAY) < TIME_MAXIMUM.MONTH) {
            msg = (diffTime) + act.getString(R.string.ago_months);
        } else {
            msg = (diffTime) + act.getString(R.string.ago_years);
        }

        return msg;
    }
}
