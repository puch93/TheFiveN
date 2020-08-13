package kr.co.core.thefiven.activity.myinfo;

import android.app.Activity;
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
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kr.co.core.thefiven.BuildConfig;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.BasicAct;
import kr.co.core.thefiven.adapter.ImageRegAdapter;
import kr.co.core.thefiven.data.RegImageData;
import kr.co.core.thefiven.databinding.ActivityMyInfoImageBinding;
import kr.co.core.thefiven.dialog.PickDialog;
import kr.co.core.thefiven.dialog.SimplePopupDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AllOfDecoration;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;

public class MyInfoImageAct extends BasicAct implements View.OnClickListener {
    ActivityMyInfoImageBinding binding;
    Activity act;

    private ImageRegAdapter adapter;
    private ArrayList<RegImageData> imageList = new ArrayList<>();
    private Uri photoUri;
    private String mImgFilePath;

    private static final int PICK_DIALOG = 100;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;

    private ItemTouchHelper itemTouchHelper;

    public static boolean isFirst = true;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            final int fromPosition = viewHolder.getAdapterPosition();
            final int toPosition = target.getAdapterPosition();
            if (fromPosition < toPosition) {
                if (fromPosition < imageList.size() && toPosition < imageList.size()) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        Collections.swap(imageList, i, i + 1);
                    }

                    isFirst = false;
                } else {
                    return false;
                }
            } else {
                if (fromPosition < imageList.size()) {
                    for (int i = fromPosition; i > toPosition; i--) {
                        Collections.swap(imageList, i, i - 1);
                    }

                    isFirst = false;
                } else {
                    return false;
                }
            }
            adapter.notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            ImageRegAdapter.ViewHolder1 svH = (ImageRegAdapter.ViewHolder1) viewHolder;
//            int index = svH.currentPosition();
//            imageList.remove(index);
//            adapter.notifyItemRemoved(index);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_info_image, null);
        act = this;


        binding.flBack.setOnClickListener(this);
        binding.tvSave.setOnClickListener(this);

        /* set recycler view */
        binding.rcvImage.setLayoutManager(new GridLayoutManager(act, 3));
        binding.rcvImage.setHasFixedSize(true);
        binding.rcvImage.setItemViewCacheSize(20);
        adapter = new ImageRegAdapter(act, imageList, new ImageRegAdapter.ImageAddListener() {
            @Override
            public void selectClickListener() {
                if (imageList.size() < 6) {
                    startActivityForResult(new Intent(act, PickDialog.class), PICK_DIALOG);
                } else {
                    Common.showToast(act, "최대 6장까지 등록 가능합니다");
                }
            }
        });

        binding.rcvImage.setAdapter(adapter);
        binding.rcvImage.addItemDecoration(new AllOfDecoration(act, "imageReg"));

        startActivity(new Intent(act, SimplePopupDlg.class).putExtra("type", StringUtil.DLG_CHECK));

        itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(binding.rcvImage);

        getMyInfo();
    }

    private void getMyInfo() {
        imageList = new ArrayList<>();

        ReqBasic server = new ReqBasic(act, NetUrls.INFO_ME) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            final JSONObject job = jo.getJSONObject("value");

                            //프로필 사진관련
                            if(!StringUtil.isNull(StringUtil.getStr(job, "piimg"))) {
                                JSONArray img_array = job.getJSONArray("piimg");
                                for (int i = img_array.length() - 1; i >= 0; i--) {
                                    JSONObject img_object = img_array.getJSONObject(i);
                                    String profile_img = StringUtil.getStr(img_object, "pi_img");
                                    String profile_img_ck = StringUtil.getStr(img_object, "pi_img_chk");

                                    imageList.add(new RegImageData(profile_img, true, profile_img_ck));
                                }
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.setList(imageList);
                                }
                            });
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

        server.setTag("My Profile");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private File downloadImage(String imgUrl) {
        Bitmap img = null;
        File f = null;
        Log.e(StringUtil.TAG, "imgUrl: " + imgUrl);

        try {
            f = createImageFile();
            URL url = new URL(imgUrl);
            URLConnection conn = url.openConnection();

            int nSize = conn.getContentLength();
            BufferedInputStream bis = new BufferedInputStream(conn.getInputStream(), nSize);
            img = BitmapFactory.decodeStream(bis);

            bis.close();

            FileOutputStream out = new FileOutputStream(f);
            img.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();

            img.recycle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return f;
    }



    private void doRegImage() {
        final ReqBasic server = new ReqBasic(act, NetUrls.EDIT_IMAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "정상적으로 등록되었습니다");
                            setResult(RESULT_OK);
                            finish();
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

        server.setTag("Edit Image");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));


        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < imageList.size(); i++) {
                    if (imageList.get(i).isFromServer()) {
                        File file = downloadImage(imageList.get(i).getImage());
                        Log.i(StringUtil.TAG, "file name" + i + ": " + file.getName());
                        server.addFileParams("image" + (i + 1), file);
                    } else {
                        File img = new File(imageList.get(i).getImage());
                        Log.i(StringUtil.TAG, "file name" + i + ": " + img.getName());
                        server.addFileParams("image" + (i + 1), img);
                    }
                }

                server.execute(true, true);
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } else {
                            resize = Bitmap.createScaledBitmap(bm, width, height, true);
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        }
                        Log.e("TEST_HOME", "mImgFilePath: " + mImgFilePath);


                        // 사진추가
                        imageList.add(new RegImageData(mImgFilePath, false, "Y"));

                        // 리싸이클러뷰 갱신
                        act.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.setList(imageList);
                                isFirst = false;
                                Log.e(StringUtil.TAG, "isFirst: ");
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isFirst = true;
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
//        String timeStamp = new SimpleDateFormat("HHmmss").format(new Date());
//        String imageFileName = "thefiven" + timeStamp;
        String imageFileName = String.valueOf(System.currentTimeMillis());

        File storageDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/TheFiveN");

        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        return File.createTempFile(imageFileName, ".png", storageDir);
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
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.tv_save:
                if (imageList.size() < 2) {
                    Common.showToast(act, "최소 2장이상 등록하여야 합니다");
                } else if (imageList.size() > 6) {
                    Common.showToast(act, "최대 6장까지 등록 가능합니다");
                } else {
                    if (!isFirst) {
                        doRegImage();
                    } else {
                        finish();
                    }
                }
                break;
        }
    }
}
