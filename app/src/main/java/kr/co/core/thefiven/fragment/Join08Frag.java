package kr.co.core.thefiven.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import kr.co.core.thefiven.BuildConfig;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.JoinAct;
import kr.co.core.thefiven.activity.MainAct;
import kr.co.core.thefiven.adapter.ImageRegAdapter;
import kr.co.core.thefiven.data.RegImageData;
import kr.co.core.thefiven.data.UserData;
import kr.co.core.thefiven.databinding.FragmentJoin08Binding;
import kr.co.core.thefiven.dialog.PickDialog;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AllOfDecoration;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

import static android.app.Activity.RESULT_OK;

public class Join08Frag extends BasicFrag implements View.OnClickListener {
    private FragmentJoin08Binding binding;
    private AppCompatActivity act;

    private ImageRegAdapter adapter;
    private Uri photoUri;
    private String mImgFilePath;
    private ArrayList<RegImageData> imageList = new ArrayList<>();

    private static final int PICK_DIALOG = 100;
    private static final int COMPLETE = 101;

    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_08, container, false);
        act = (AppCompatActivity) getActivity();

        startActivity(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_CHECK));

        binding.flComplete.setOnClickListener(this);
        binding.flBack.setOnClickListener(this);

        /* set recycler view */
        binding.rcvImage.setLayoutManager(new GridLayoutManager(act, 3));
        binding.rcvImage.setHasFixedSize(true);
        binding.rcvImage.setItemViewCacheSize(20);
        adapter = new ImageRegAdapter(act, imageList, new ImageRegAdapter.ImageAddListener() {
            @Override
            public void selectClickListener() {
                if (imageList.size() < 3) {
                    startActivityForResult(new Intent(act, PickDialog.class), PICK_DIALOG);
                } else {
                    Common.showToast(act, "가입시 최대 3장만 등록 가능합니다.");
                }
            }
        });

        binding.rcvImage.setAdapter(adapter);
        binding.rcvImage.addItemDecoration(new AllOfDecoration(act, "imageReg"));

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            switch (requestCode) {
                case PICK_DIALOG:
                    String value = data.getStringExtra("value");
                    if (StringUtil.isNull(value))
                        return;

                    if (value.equalsIgnoreCase("camera")) {
                        //촬영하기
                        takePhoto();
                    } else {
                        //갤러리
                        getAlbum();
                    }
                    break;

                case COMPLETE:
                    doLogin();
                    break;


                //사진 갤러리 결과
                case PHOTO_GALLERY:
                    if (data == null) {
                        Common.showToast(act, "사진불러오기 실패! 다시 시도해주세요.");
                        return;
                    }

                    photoUri = data.getData();
                    cropImage();
                    break;

                //사진 촬영 결과
                case PHOTO_TAKE:
                    cropImage();
                    break;

                //사진 크롭 결과
                case PHOTO_CROP:
                    mImgFilePath = photoUri.getPath();
                    if (StringUtil.isNull(mImgFilePath)) {
                        Common.showToast(act, "사진자르기 실패! 다시 시도해주세요.");
                        return;
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(mImgFilePath, options);

                    Bitmap resize = null;
                    try {
                        File resize_file = new File(mImgFilePath);
                        FileOutputStream out = new FileOutputStream(resize_file);

                        int width = bm.getWidth();
                        int height = bm.getHeight();

                        if (width > 1024) {
                            int resizeHeight = 0;
                            if (height > 768) {
                                resizeHeight = 768;
                            } else {
                                resizeHeight = height / (width / 1024);
                            }

                            resize = Bitmap.createScaledBitmap(bm, 1024, resizeHeight, true);
                            resize.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        } else {
                            resize = Bitmap.createScaledBitmap(bm, width, height, true);
                            resize.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        }
                        Log.e("TEST_HOME", "mImgFilePath: " + mImgFilePath);


                        // 사진추가
                        imageList.add(new RegImageData(mImgFilePath, false, "Y"));

                        // 리싸이클러뷰 갱신
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setList(imageList);
                            }
                        });
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    MediaScannerConnection.scanFile(act, new String[]{photoUri.getPath()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {

                                }
                            });
                    break;
            }
        }
    }

    private void doLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.LOGIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "정상적으로 로그인되었습니다");

                            JSONObject job = jo.getJSONObject("value");
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "idx"));

                            AppPreference.setProfilePref(act, AppPreference.PREF_ID, JoinAct.joinData.getId());
                            AppPreference.setProfilePref(act, AppPreference.PREF_PW, JoinAct.joinData.getPw());
                            AppPreference.setProfilePref(act, AppPreference.PREF_GENDER, StringUtil.getStr(job, "gender"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_PHONE, StringUtil.getStr(job, "phone"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_IMAGE, StringUtil.getStr(job, "p_image1"));
                            AppPreference.setProfilePref(act, AppPreference.PREF_CGPMS, StringUtil.getStr(job, "cgpms"));
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_AUTO_LOGIN_STATE, true);

                            checkPayMember();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "value"));
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

        server.setTag("Login");
        server.addParams("id", JoinAct.joinData.getId());
        server.addParams("pw", JoinAct.joinData.getPw());
        server.addParams("os", "ANDROID");
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));
        server.execute(true, false);
    }

    private void checkPayMember() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHECK_PAY_MEMBER) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_PAY_MEMBER, true);
                        } else {
                            AppPreference.setProfilePrefBool(act, AppPreference.PREF_PAY_MEMBER, false);
                        }

                        startActivity(new Intent(act, MainAct.class));
                        act.setResult(RESULT_OK);
                        act.finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Check Pay Member");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }


    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PHOTO_GALLERY);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Common.showToast(act, "이미지 처리 오류! 다시 시도해주세요.");
            e.printStackTrace();
        }

        if (photoFile != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(act,
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
            } else {
                photoUri = Uri.fromFile(photoFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, PHOTO_TAKE);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
        String imageFileName = "thefiven" + timeStamp;

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TheFiveN");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    private void cropImage() {
        Intent cropIntent = new Intent("com.android.camera.action.CROP");

        cropIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        cropIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        cropIntent.setDataAndType(photoUri, "image/*");

        // 파일 생성
        try {
            File albumFile = createImageFile();
            Log.e(StringUtil.TAG, "cropImage: " + albumFile.getAbsolutePath());

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                photoUri = FileProvider.getUriForFile(act, BuildConfig.APPLICATION_ID + ".provider", albumFile);
            } else {
                photoUri = Uri.fromFile(albumFile);
            }

        } catch (IOException e) {
            Log.e(StringUtil.TAG, "cropImage: 에러");
            e.printStackTrace();
        }

        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        cropIntent.putExtra("scale", true);
        cropIntent.putExtra("output", photoUri);

        // 여러 카메라어플중 기본앱 세팅
        List<ResolveInfo> list = act.getPackageManager().queryIntentActivities(cropIntent, 0);

        Intent i = new Intent(cropIntent);
        ResolveInfo res = list.get(0);

        i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        act.grantUriPermission(res.activityInfo.packageName, photoUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

        i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
        startActivityForResult(i, PHOTO_CROP);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_complete:
//                if (imageList.size() < 2) {
//                    Common.showToast(act, "이미지를 등록해주세요.");
//                    return;
//                }

                nextProcess();
                break;

            case R.id.fl_back:
                act.onBackPressed();
                break;
        }
    }

    private void nextProcess() {
        ArrayList<String> list = new ArrayList<>();

        for (int i = 0; i < imageList.size(); i++) {
            list.add(imageList.get(i).getImage());
        }

        JoinAct.joinData.setImages(list);

        doJoin();
        Log.i(StringUtil.TAG, "last data: " + JoinAct.joinData);
    }

    private void doJoin() {
        ReqBasic server = new ReqBasic(act, NetUrls.JOIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS) || StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {

                            JSONArray ja = jo.getJSONArray("value");
                            JSONObject job = ja.getJSONObject(0);
                            AppPreference.setProfilePref(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "idx"));

                            startActivityForResult(new Intent(act, SimplePopupDlg.class)
                                            .putExtra("type", StringUtil.DLG_JOIN_COMPLETE)
                                            .putExtra("code", StringUtil.getStr(job, "cgpms"))
                                    , COMPLETE);

                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "value"));
//                            Common.showToast(act, "사진 용량 초과입니다. 처음부터 다시 시도해주시기 바랍니다.");
//                            act.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    imageList = new ArrayList<>();
//                                    adapter.setList(imageList);
//                                    adapter.notifyDataSetChanged();
//                                }
//                            });
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

        UserData data = JoinAct.joinData;

        server.setTag("Join");
        server.addParams("join_type", "normal");
        server.addParams("uniq", AppPreference.getProfilePref(act, AppPreference.PREF_DEVICE_ID));
        server.addParams("fcm", AppPreference.getProfilePref(act, AppPreference.PREF_FCM));

        //정상
        server.addParams("id", data.getId());
        server.addParams("pw", data.getPw());
        server.addParams("pw_confirm", data.getPw());
        server.addParams("phone", data.getPhone());

        //테스트
//        String id = "create21@naver.com";
//        String pw = "aaaaaaaa1!";
//        String phone = "01077441102";
//        JoinAct.joinData.setId(id);
//        JoinAct.joinData.setPw(pw);
//        server.addParams("id", id);
//        server.addParams("pw", pw);
//        server.addParams("pw_confirm", pw);
//        server.addParams("phone", phone);

        //페이스북 테스트
//        server.addParams("id", data.getId());
//        server.addParams("pw", data.getPw());
//        server.addParams("pw_confirm", data.getPw());
//        server.addParams("phone", "01078475555");

        server.addParams("nick", data.getNick());
        server.addParams("gender", data.getGender());
        server.addParams("birth", data.getBirth());
        server.addParams("birth_type", data.getBirth_type());
        server.addParams("job", data.getJob());
        server.addParams("salary", data.getSalary());
        server.addParams("marriage", data.getMarriage());
        server.addParams("personality", data.getPersonality());
        server.addParams("nationality", data.getNationality());
        server.addParams("interest", data.getInterests());

        if (data.getImages().size() != 0) {
            for (int i = 0; i < data.getImages().size(); i++) {
                File img = new File(data.getImages().get(i));
                server.addFileParams("image" + (i + 1), img);
            }
        }

        server.addParams("blood", data.getBlood());
        server.addParams("location", data.getLocation());
        server.addParams("education", data.getEdu());
        server.addParams("holiday", data.getHoliday());
        server.addParams("family", data.getFamily());
        server.addParams("height", data.getHeight());
        server.addParams("body", data.getBody());
        server.addParams("drink", data.getDrink());
        server.addParams("smoke", data.getSmoke());
        server.addParams("intro", data.getIntro());
        server.addParams("twins_chk", data.getBirth_twin());

        server.execute(true, false);
    }
}