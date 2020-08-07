package kr.co.core.thefiven.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.TermsAct;
import kr.co.core.thefiven.databinding.FragmentSettingKnowPeople03Binding;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class SettingKnowPeopleFrag03 extends BasicFrag {
    private FragmentSettingKnowPeople03Binding binding;
    private AppCompatActivity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting_know_people_03, container, false);
        act = (AppCompatActivity) getActivity();

        binding.flUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.cbConfirm.isChecked()) {
                    if (checkPermission()) {
                        showAlert();
                    } else {
                        requestPermission();
                    }
                } else {
                    Common.showToast(act, "위의 내용을 확인해주신 후, 확인표시에 체크 부탁드립니다");
                }
            }
        });

        binding.flPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(act, TermsAct.class).putExtra("type", StringUtil.TERMS_PRIVATE));
            }
        });

        return binding.getRoot();
    }

    private void showAlert() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("아는사람 만나지 않기");
        alertDialog.setMessage("OK 버튼 클릭시 회원님의 연락처 정보가 the Five 서버로 전송됩니다. 연락처를 업로드 하시겠습니까? ");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getContacts(AppPreference.getProfilePref(act, AppPreference.PREF_PHONE));
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

    private void getContacts(final String hp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String total = "";

                // 1. Resolver 가져오기(데이터베이스 열어주기)
                ContentResolver resolver = act.getContentResolver();

                // 2. 전화번호가 저장되어 있는 테이블 주소값(Uri)을 가져오기
                Uri phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

                // 3. 테이블에 정의된 칼럼 가져오기
                String[] projection = {ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                        , ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                        , ContactsContract.CommonDataKinds.Phone.NUMBER};

                // 4. ContentResolver로 쿼리를 날림 -> resolver 가 provider 에게 쿼리하겠다고 요청
                Cursor cursor = resolver.query(phoneUri, projection, null, null, ContactsContract.Data.RAW_CONTACT_ID + " ASC");

                // 5. 커서로 리턴된다. 반복문을 돌면서 cursor 에 담긴 데이터를 하나씩 추출
                if (cursor != null) {
                    while (cursor.moveToNext()) {

                        // 4.1 이름으로 인덱스를 찾아준다
                        int idIndex = cursor.getColumnIndex(projection[0]); // 이름을 넣어주면 그 칼럼을 가져와준다.
                        int nameIndex = cursor.getColumnIndex(projection[1]);
                        int numberIndex = cursor.getColumnIndex(projection[2]);

                        // 4.2 해당 index 를 사용해서 실제 값을 가져온다.
                        String id = cursor.getString(idIndex);
                        String name = cursor.getString(nameIndex);
                        String number = cursor.getString(numberIndex);

                        Log.e(StringUtil.TAG, "id: " + id);
                        Log.e(StringUtil.TAG, "name: " + name);
                        Log.e(StringUtil.TAG, "number: " + number);
                        Log.e(StringUtil.TAG, "--------------------------------------------");


                        number = number.replace(" ", "");
                        number = number.replace("-", "");
                        number = number.replace("//", "");

                        if (!hp.equalsIgnoreCase(number)) {
                            if (StringUtil.isNull(total)) {
                                total = number;
                            } else {
                                total += "," + number;
                            }
                        }
                    }
                }
                // 데이터 계열은 반드시 닫아줘야 한다.
                cursor.close();

                Log.e(StringUtil.TAG, "total: " + total);


                //실제
                setKnowPeople(total);
                //테스트
//                setKnowPeople("01077475545");
            }
        }).start();
    }


    private void setKnowPeople(String address) {
        ReqBasic server = new ReqBasic(act, NetUrls.FRIEND_BLOCK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {

                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "value"));
                        }

                        Common.showToast(act, "성공적으로 업로드하였습니다");
                        act.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToast(act, "성공적으로 업로드하였습니다");
                        act.finish();
                    }
                } else {
                    Common.showToast(act, "성공적으로 업로드하였습니다");
                    act.finish();
                }
            }
        };

        server.setTag("Know People");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("address_list", address);
        server.execute(true, false);
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    act.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{
                    Manifest.permission.READ_CONTACTS
            }, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    act.checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
            ) {
                act.finish();
                Common.showToast(act, "다시 묻지 않음 을 선택한 경우, 설정 -> 애플리케이션(해당 앱) -> 앱 권한에서 승인 부탁드립니다");
            } else {
                showAlert();
            }
        }
    }

}