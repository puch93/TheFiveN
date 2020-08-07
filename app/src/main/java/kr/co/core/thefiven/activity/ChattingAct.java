package kr.co.core.thefiven.activity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import jp.wasabeef.glide.transformations.BlurTransformation;
import kr.co.core.thefiven.BuildConfig;
import kr.co.core.thefiven.R;
import kr.co.core.thefiven.activity.gift.GiftBrandAct;
import kr.co.core.thefiven.adapter.ChattingAdapter;
import kr.co.core.thefiven.data.ChattingData;
import kr.co.core.thefiven.databinding.ActivityChattingBinding;
import kr.co.core.thefiven.dialog.PickDialog;
import kr.co.core.thefiven.dialog.WarningPopupDlg;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;
import kr.co.core.thefiven.utility.AppPreference;
import kr.co.core.thefiven.utility.ChatValues;
import kr.co.core.thefiven.utility.Common;
import kr.co.core.thefiven.utility.StringUtil;
import okhttp3.OkHttpClient;

public class ChattingAct extends BasicAct implements View.OnClickListener, PopupMenu.OnMenuItemClickListener {
    ActivityChattingBinding binding;
    public static Activity act;
    public static Activity real_act;
    private boolean exitState = false;

    private InputMethodManager imm;

    private ArrayList<ChattingData> list = new ArrayList<>();
    private ChattingAdapter adapter;

    private io.socket.client.Socket mSocket;
    public static String room_idx = "";
    private String yidx, otherImage, otherImageState, otherNick, otherGender;


    private boolean isMatching = false; // false -> 첫번째 매시지 아님, true -> 첫 번째 메시지
    private boolean isFirstMsg = true; // false -> 첫번째 매시지 아님, true -> 첫 번째 메시지

    private static final int INFO_DIALOG = 100;
    private static final int WARNING_BLOCK = 101;


    /* 이미지 보내기 관련 */
    private Uri photoUri;
    private String mImgFilePath;

    private static final int PICK_DIALOG = 1000;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chatting, null);
        act = this;
        real_act = this;

        room_idx = getIntent().getStringExtra("room_idx");
        yidx = getIntent().getStringExtra("yidx");
        otherImage = getIntent().getStringExtra("otherImage");
        otherImageState = getIntent().getStringExtra("otherImageState");
        otherNick = getIntent().getStringExtra("otherNick");
        otherGender = getIntent().getStringExtra("otherGender");
        isMatching = getIntent().getStringExtra("room_type").equalsIgnoreCase("heartmatching");

        setLayout();

        if (isMatching) {
            isFirstMsg();
            checkChattingPay();
        } else {
            setupSocketClient();
        }
    }


    //내가보낸 첫번째 메시지인지 체크
    private void isFirstMsg() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_FIRST_MSG) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            //첫번째 메시지 아닐때
                            isFirstMsg = false;
                        } else {
                            //첫번째 메시지 일때
                            isFirstMsg = true;
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

        server.setTag("Is First Msg");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    private void checkChattingPay() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_PAY_CHECK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            adapter.setPay(true);
                        } else {
//                            Common.showToast(act, StringUtil.getStr(jo, "msg"));
                            adapter.setPay(false);
                        }

                        setupSocketClient();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Check Chatting Pay");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("TEST_TEST", "onDestroy: ");
        if (!exitState) {
            if (mSocket != null && mSocket.connected()) {
                mSocket.disconnect();
            }
            real_act = null;
            exitState = true;
        }
    }

    private void setLayout() {
        if (otherImageState.equalsIgnoreCase("Y")) {
            Glide.with(act)
                    .load(otherImage)
                    .centerCrop()
                    .transform(new CircleCrop())
                    .into(binding.ivProfileImg);
        } else if (otherImageState.equalsIgnoreCase("N") || otherImageState.equalsIgnoreCase("fail")) {
            Glide.with(act)
                    .load(otherImage)
                    .centerCrop()
                    .transform(new BlurTransformation(10, 3), new CircleCrop())
                    .into(binding.ivProfileImg);
        } else {
            if(StringUtil.isNull(otherGender)) {
                Glide.with(act)
                        .load(R.drawable.img_unknown_m)
                        .centerCrop()
                        .transform(new CircleCrop())
                        .into(binding.ivProfileImg);
            } else {
                if(otherGender.equalsIgnoreCase("male")) {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_m)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(binding.ivProfileImg);
                } else {
                    Glide.with(act)
                            .load(R.drawable.img_unknown_w)
                            .centerCrop()
                            .transform(new CircleCrop())
                            .into(binding.ivProfileImg);
                }
            }
        }

        binding.tvNick.setText(otherNick);

        /* set click listener */
        binding.flBack.setOnClickListener(this);
        binding.ivFav.setOnClickListener(this);
        binding.ivMore.setOnClickListener(this);

        binding.flAddPhoto.setOnClickListener(this);
        binding.tvSend.setOnClickListener(this);

        /* EditText 포커스될때 키보드가 UI 가리는 것 막음 */
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);


        /* set recycler view */
        binding.rcvChatting.setLayoutManager(new LinearLayoutManager(act));

        adapter = new ChattingAdapter(act, room_idx, list, AppPreference.getProfilePref(act, AppPreference.PREF_IMAGE), otherNick, otherImage, otherImageState, otherGender);
        binding.rcvChatting.setAdapter(adapter);
        binding.rcvChatting.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    binding.rcvChatting.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            binding.rcvChatting.scrollToPosition(adapter.getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case INFO_DIALOG:
                    Common.showToastDevelop(act);
                    break;
                case WARNING_BLOCK:
                    doBlock();
                    break;

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


//                        // 이미지 전송
//                        if (isGuest) {
//                            Log.e(StringUtil.TAG, "isGuest true");
//                            sendImage();
//                        } else {
//                            Log.e(StringUtil.TAG, "isGuest false");
//                            //첫 번째 보내는 메시지면
//                            if (isFirstMsg) {
//                                Log.e(StringUtil.TAG, "isFirstMsg true");
//                                sendImage();
//                            } else {
//                                Log.e(StringUtil.TAG, "isFirstMsg false");
//                                //상대한테 응답 왔으면
//                                if (isReplyOk) {
//                                    Log.e(StringUtil.TAG, "isReplyOk true");
//                                    sendImage();
//                                } else {
//                                    Log.e(StringUtil.TAG, "isReplyOk false");
//                                    checkReply(false);
//                                }
//                            }
//                        }


                        if (adapter.isPay()) {
                            Log.e(StringUtil.TAG, "1 Point Pay true");
                            sendImage();
                        } else {
                            Log.e(StringUtil.TAG, "1 Point Pay false");
                            if (isFirstMsg) {
                                Log.e(StringUtil.TAG, "isFirstMsg true");
                                sendImage();
                            } else {
                                Log.e(StringUtil.TAG, "isFirstMsg false");
                                showAlertPay();
                            }
                        }

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

    private void sendImage() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_SEND_IMAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    Date date = new Date(System.currentTimeMillis());
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String date_s = dateFormat.format(date);

                    String msg = resultData.getResult().replaceAll("\"", "");

                    sendMessage(msg);
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        File image = new File(mImgFilePath);
        server.setTag("Chat Send Image");
        server.addFileParams("pimg", image);
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


    private void leave() {
        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_LEAVE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            Common.showToast(act, "채팅방을 나갑니다");
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

        server.setTag("Chat Leave");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }

    private void doBlock() {
        ReqBasic server = new ReqBasic(act, NetUrls.BLOCK) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            AppPreference.setMemberStateAll(act);
                            Common.showToast(act, "차단 되었습니다");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "comment"));
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

        server.setTag("Block");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("yidx", yidx);
        server.execute(true, false);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_back:
                finish();
                break;
            case R.id.iv_fav:
                binding.ivFav.setSelected(!binding.ivFav.isSelected());
                startActivity(new Intent(act, GiftBrandAct.class).putExtra("yidx", yidx).putExtra("nick", otherNick));
                break;
            case R.id.iv_more:
                PopupMenu popup = new PopupMenu(this, view);
                popup.setOnMenuItemClickListener(this);
                popup.inflate(R.menu.menu_chat);
                popup.show();
                break;

            case R.id.fl_add_photo:
                startActivityForResult(new Intent(act, PickDialog.class), PICK_DIALOG);
                break;

            case R.id.tv_send:
                if (binding.etContents.length() == 0) {
                    Common.showToast(act, "내용을 입력해주세요");
                } else {
                    if (!isMatching) {
                        Log.e(StringUtil.TAG, "isMatching false");
                        sendMessage(binding.etContents.getText().toString());
                        binding.etContents.setText("");
                    } else {
//                        Log.e(StringUtil.TAG, "isMatching true");
//                        //첫 번째 보내는 메시지면
//                        if (isFirstMsg) {
//                            Log.e(StringUtil.TAG, "isFirstMsg true");
//                            sendMessage(binding.etContents.getText().toString());
//                            binding.etContents.setText("");
//                        } else {
//                            Log.e(StringUtil.TAG, "isFirstMsg false");
//                            if(adapter.isPay()) {
//                                Log.e(StringUtil.TAG, "1 Point Pay true");
//                                sendMessage(binding.etContents.getText().toString());
//                                binding.etContents.setText("");
//                            } else {
//                                Log.e(StringUtil.TAG, "1 Point Pay false");
//                            }
//                        }

                        if (adapter.isPay()) {
                            Log.e(StringUtil.TAG, "1 Point Pay true");
                            sendMessage(binding.etContents.getText().toString());
                            binding.etContents.setText("");
                        } else {
                            Log.e(StringUtil.TAG, "1 Point Pay false");
                            if (isFirstMsg) {
                                Log.e(StringUtil.TAG, "isFirstMsg true");
                                sendMessage(binding.etContents.getText().toString());
                                binding.etContents.setText("");
                            } else {
                                Log.e(StringUtil.TAG, "isFirstMsg false");
                                showAlertPay();
                            }
                        }
                    }
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        binding.rcvChatting.scrollToPosition(adapter.getItemCount() - 1);
                    }
                }, 300);
                break;
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
                                    Common.showToast(act, "결제가 완료되었습니다.");
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

    private void setPayState() {
        adapter.setPay(true);

        for (int i = 0; i < list.size(); i++) {
            ChattingData data = list.get(i);
            data.setPay(true);
            list.set(i, data);
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }

//    private void checkReply(final boolean isText) {
//        ReqBasic server = new ReqBasic(act, NetUrls.CHAT_REPLY_CHECK) {
//            @Override
//            public void onAfter(int resultCode, HttpResult resultData) {
//                if (resultData.getResult() != null) {
//                    try {
//                        JSONObject jo = new JSONObject(resultData.getResult());
//
//                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
//                            if (isText) {
//                                sendMessage(binding.etContents.getText().toString());
//                                binding.etContents.setText("");
//                            } else {
//                                sendImage();
//                            }
//                            isReplyOk = true;
//                        } else {
//                            Common.showToast(act, "상대에게서 답변을 받은후 채팅하실 수 있습니다다");
//                            isReplyOk = false;
//                        }
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                        Common.showToastNetwork(act);
//                    }
//                } else {
//                    Common.showToastNetwork(act);
//                }
//            }
//        };
//
//        server.setTag("Check Reply");
//        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
//        server.addParams("room_idx", room_idx);
//        server.execute(true, false);
//    }


    //채팅푸시전송
    private void sendMessage(final String contents) {
        ReqBasic server = new ReqBasic(act, NetUrls.SEND_MESSAGE) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y") || StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS)) {
                            //실제채팅전송
                            JSONObject sendData = new JSONObject();
                            sendData.put(ChatValues.ROOMIDX, room_idx);
                            sendData.put(ChatValues.TALKER, AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
                            sendData.put(ChatValues.MSG, contents);
                            mSocket.emit(ChatValues.SEND_MSG, sendData);

                            isFirstMsg = false;
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

        server.setTag("Send Message");
        server.addParams("midx", AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
        server.addParams("contents", contents);
        server.addParams("room_idx", room_idx);
        server.execute(true, false);
    }


    /* 소켓 */
    private void setupSocketClient() {
        try {
            Log.i(StringUtil.TAG, "setupSocketClient");
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] myTrustedAnchors = new X509Certificate[0];
                    return myTrustedAnchors;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .sslSocketFactory(sc.getSocketFactory()).build();

            // default settings for all sockets
            IO.setDefaultOkHttpWebSocketFactory(okHttpClient);
            IO.setDefaultOkHttpCallFactory(okHttpClient);

            // set as an option
            IO.Options opts = new IO.Options();
            opts.callFactory = okHttpClient;
            opts.webSocketFactory = okHttpClient;

            mSocket = IO.socket(ChatValues.SOCKET_URL);
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(ChatValues.CHATTING_HISTORY, onMessageReceived);
            mSocket.on(ChatValues.CHATTING_SYSTEM_MSG, onChatReceive);
            mSocket.connect();
            System.out.println("socket setup!!! ");
        } catch (URISyntaxException e) {
            Log.i(StringUtil.TAG, "URISyntaxException");
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            Log.i(StringUtil.TAG, "NoSuchAlgorithmException");
            e.printStackTrace();
        } catch (KeyManagementException e) {
            Log.i(StringUtil.TAG, "KeyManagementException");
            e.printStackTrace();
        }
    }


    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject sendData = new JSONObject();
            Log.e(StringUtil.TAG_CHAT, "onConnect");
            System.out.println("socket onConnect : " + sendData);
            try {
                sendData.put(ChatValues.ROOMIDX, room_idx);
                sendData.put(ChatValues.TALKER, AppPreference.getProfilePref(act, AppPreference.PREF_MIDX));
                mSocket.emit(ChatValues.CHATTING_HISTORY, sendData);

                Log.e(StringUtil.TAG, "onConnect Put: " + sendData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    // 채팅 내역(이전 대화 내용)
    private Emitter.Listener onMessageReceived = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(StringUtil.TAG_CHAT, "onMessageReceived");
            JSONObject rcvData = (JSONObject) args[0];

            try {
                list = new ArrayList<>();

                JSONArray ja = new JSONArray(StringUtil.getStr(rcvData, "chats"));
                if (ja.length() > 0) {
                    for (int i = 0; i < ja.length(); i++) {
                        JSONObject jo = ja.getJSONObject(i);
                        Log.e(StringUtil.TAG, "chat_list(" + i + "): " + jo);

                        // 유저 인덱스
                        String u_idx = StringUtil.getStr(jo, "user_idx");

                        // 메시지
                        String contents = StringUtil.getStr(jo, "msg");

                        // 보낸 시간
                        String send_time = StringUtil.converTime(StringUtil.getStr(jo, "created_at"), "a hh:mm");

                        // 데이트라인 데이터 추가
                        String dateLine = StringUtil.converTime(StringUtil.getStr(jo, "created_at"), "yyyy년 M월 d일");

                        /* 시스템 메시지면 추가 */
                        if (u_idx.equalsIgnoreCase("0")) {
                            list.add(new ChattingData(true, u_idx, "none", "none", "system", contents, true));
                            continue;
                        }

                        // 읽음 처리
                        boolean isRead = false;
                        String[] idxs = StringUtil.getStr(jo, "read_user_idx").split(",");
                        if (idxs.length > 1)
                            isRead = true;

                        // 데이트라인 확인 후 추가
                        if (i > 0) {
                            if (!list.get(list.size() - 1).getDate_line().equals(dateLine)) {
                                ChattingData data = new ChattingData(dateLine, ChatValues.MSG_DATELINE);
                                list.add(data);
                            }
                        } else {
                            ChattingData data = new ChattingData(dateLine, ChatValues.MSG_DATELINE);
                            list.add(data);
                        }

                        if (isMatching) {
                            if (u_idx.equalsIgnoreCase(AppPreference.getProfilePref(act, AppPreference.PREF_MIDX))) {
                                list.add(new ChattingData(true, u_idx, send_time, dateLine, isImage(contents), contents, isRead));
                            } else {
                                if (adapter.isPay()) {
                                    list.add(new ChattingData(true, u_idx, send_time, dateLine, isImage(contents), contents, isRead));
                                } else {
                                    list.add(new ChattingData(false, u_idx, send_time, dateLine, isImage(contents), contents, isRead));
                                }
                            }
                        } else {
                            list.add(new ChattingData(true, u_idx, send_time, dateLine, isImage(contents), contents, isRead));
                        }
                    }


                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.setList(list);
                            binding.rcvChatting.scrollToPosition(adapter.getItemCount() - 1);

                            if (MainAct.act != null) {
                                ((MainAct) MainAct.act).refreshAboutChatting();
                            }
                        }
                    });
                }

            } catch (
                    JSONException e) {
                Log.i(StringUtil.TAG, "JSONException: " + e.toString());
                e.printStackTrace();
            }

        }
    };


    private String isImage(String msg) {
        String reg = "^([\\S]+(\\.(?i)(jpg|png|jpeg))$)";

        return msg.matches(reg) ? "image" : "text";
    }


    // 실시간 메세지 처리
    private Emitter.Listener onChatReceive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(StringUtil.TAG_CHAT, "onChatReceive");
            JSONObject rcvData = (JSONObject) args[0];
            String selectDate = null;

            try {
                JSONObject from = new JSONObject(rcvData.getString("from"));
                Log.e(StringUtil.TAG, "onChatReceive (from): " + from);

                JSONObject chat = new JSONObject(from.getString("chat"));
                Log.e(StringUtil.TAG, "onChatReceive (chat): " + chat);

                // 유저 인덱스
                String u_idx = StringUtil.getStr(chat, "user_idx");

                // 메시지
                String contents = StringUtil.getStr(chat, "msg");

                // 보낸 시간
                String send_time = StringUtil.converTime(StringUtil.getStr(chat, "created_at"), "a hh:mm");

                //읽음처리
                boolean isRead = false;
                String[] idxs = StringUtil.getStr(chat, "read_user_idx").split(",");
                if (idxs.length > 1)
                    isRead = true;


                if (isMatching) {
                    if (u_idx.equalsIgnoreCase(AppPreference.getProfilePref(act, AppPreference.PREF_MIDX))) {
                        list.add(new ChattingData(true, u_idx, send_time, "", isImage(contents), contents, isRead));
                    } else {
                        if (adapter.isPay()) {
                            list.add(new ChattingData(true, u_idx, send_time, "", isImage(contents), contents, isRead));
                        } else {
                            list.add(new ChattingData(false, u_idx, send_time, "", isImage(contents), contents, isRead));
                        }
                    }
                } else {
                    list.add(new ChattingData(true, u_idx, send_time, "", isImage(contents), contents, isRead));
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.setList(list);
                        binding.rcvChatting.smoothScrollToPosition(adapter.getItemCount());
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private void showAlertLeave() {
        androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(act);

        alertDialog.setCancelable(false);
        alertDialog.setTitle("채팅방 나가기");
        alertDialog.setMessage("채팅방을 나가시겠습니까?");

        // ok
        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        leave();
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.block:
                startActivityForResult(new Intent(act, WarningPopupDlg.class).putExtra("type", StringUtil.DLG_BLOCK).putExtra("nick", otherNick), WARNING_BLOCK);
                return true;
            case R.id.leave:
                showAlertLeave();
                return true;

            default:
                return false;
        }
    }
}
