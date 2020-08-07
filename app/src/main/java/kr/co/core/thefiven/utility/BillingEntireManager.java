package kr.co.core.thefiven.utility;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.co.core.thefiven.activity.PayMemberAct;
import kr.co.core.thefiven.activity.PayPointAct;
import kr.co.core.thefiven.server.ReqBasic;
import kr.co.core.thefiven.server.netUtil.HttpResult;
import kr.co.core.thefiven.server.netUtil.NetUrls;


public class BillingEntireManager implements PurchasesUpdatedListener {
    public interface AfterBilling {
        void sendMessage(String message, boolean isLong);
    }


    private BillingClient mBillingClient;
    private Context ctx;

    private List<SkuDetails> mSkuDetailsList_item;
    private List<SkuDetails> mSkuDetailsList_subs;

    // 아이템 소비 리스너
    private ConsumeResponseListener mConsumeListener;
    // 구독 소비 리스너
    private AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener;

    private String TAG = getClass().getSimpleName();

    private String state_subs = "N";
    private String state_item = "Y";

    /**
     * 구글 플레이스토어 계정 정보가 없습니다
     * 구글 결제 서버와 접속이 끊어졌습니다
     */
    private String manager_state = "Y";
    private String manager_state_message = "";

    private AfterBilling afterListener;

    public String getSubscription_state() {
        return this.state_subs;
    }

    public String getInapp_state() {
        return this.state_item;
    }

    public String getManager_state() {
        return this.manager_state;
    }

    public String getManager_state_message() {
        return this.manager_state_message;
    }


    public BillingEntireManager(Context ctx, final AfterBilling afterListener) {
        this.ctx = ctx;
        this.afterListener = afterListener;

        Log.d(TAG, "구글 결제 매니저를 초기화 하고 있습니다.");

        mBillingClient = BillingClient.newBuilder(ctx).setListener(this).enablePendingPurchases().build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "구글 결제 서버에 접속을 성공하였습니다.");

                    getSkuDetailList();


                    /* 인앱 */
                    // 인앱상품 체크
                    Purchase.PurchasesResult result_item = mBillingClient.queryPurchases(BillingClient.SkuType.INAPP);
                    Log.i(TAG, "item result: " + result_item);
                    Log.i(TAG, "item getPurchasesList: " + result_item.getPurchasesList());
                    Log.i(TAG, "item getResponseCode: " + result_item.getResponseCode());

                    // 카드승인이 늦어져서, 소모가 안된 상품 존재시
                    List<Purchase> list_item = result_item.getPurchasesList();

                    if (list_item.size() != 0) {
                        state_item = "pending";
                    }

                    for (int i = 0; i < list_item.size(); i++) {
                        Purchase purchase = list_item.get(i);
                        Log.e(TAG, "check billing(" + i + "): " + purchase);

                        // 계속 보류중일때
                        if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
                            Log.e(TAG, "purchase.getPurchaseState(): " + purchase.getPurchaseState());
                            afterListener.sendMessage("구매하신 상품에대한 결제가 카드사 승인중입니다. 몇 분 후에 재로그인해주시기 바랍니다.", true);
                        } else {
                            // 카드사 승인된경우
                            handlePurchase(list_item.get(i));
                        }
                    }


                    /* 구독 */
                    //구독여부체크
                    Purchase.PurchasesResult result_subs = mBillingClient.queryPurchases(BillingClient.SkuType.SUBS);
                    Log.i(TAG, "subs result: " + result_subs);
                    Log.i(TAG, "subs getPurchasesList: " + result_subs.getPurchasesList());
                    Log.i(TAG, "subs getResponseCode: " + result_subs.getResponseCode());

                    BillingResult result_billing2 = result_subs.getBillingResult();
                    Log.i(TAG, "subs result_billing.getResponseCode: " + result_billing2.getResponseCode());
                    Log.i(TAG, "subs result_billing.getDebugMessage: " + result_billing2.getDebugMessage());

                    List<Purchase> list_subs = result_subs.getPurchasesList();

                    //구독중 아님
                    if (list_subs.size() == 0) {
                        state_subs = "N";
                    } else {
                        //구독중
                        try {
                            JSONObject job = new JSONObject(list_subs.get(0).getOriginalJson());
                            if (job.getBoolean("acknowledged"))
                                state_subs = "Y";
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    //오류
                } else {
                    Log.d(TAG, "구글 결제 서버 접속에 실패하였습니다.\n오류코드: " + billingResult.getResponseCode());
                    manager_state = "N";
                    manager_state_message = "구글 플레이스토어 계정 정보가 없습니다";
//                    afterListener.sendMessage("구글 플레이스토어 계정 정보가 없습니다");
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                Log.d(TAG, "구글 결제 서버와 접속이 끊어졌습니다.");
                manager_state = "N";
                manager_state_message = "구글 결제 서버와 접속이 끊어졌습니다";
//                afterListener.sendMessage("구글 결제 서버와 접속이 끊어졌습니다");
            }
        });

        mConsumeListener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "상품을 성공적으로 소모하였습니다. 소모된 상품 => " + purchaseToken);
                    return;
                } else {
                    Log.d(TAG, "상품 소모에 실패하였습니다. 오류코드 (" + billingResult.getResponseCode() + "), 대상 상품 코드: " + purchaseToken);
                    return;
                }
            }
        };

        acknowledgePurchaseResponseListener = new AcknowledgePurchaseResponseListener() {
            @Override
            public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "구독을 성공적으로 소모하였습니다.");
                    return;
                } else {
                    Log.d(TAG, "구독 소모에 실패하였습니다. 오류코드 (" + billingResult.getResponseCode() + ")");
                    return;
                }
            }
        };
    }

    //구입 가능한 상품의 리스트를 받아 오는 메소드
    private void getSkuDetailList() {
        //구글 상품 정보들의 ID를 만들어 줌
        List<String> Sku_ID_List_INAPP = new ArrayList<>();
        List<String> Sku_ID_List_SUBS = new ArrayList<>();

        Sku_ID_List_INAPP.add(StringUtil.POINT_01_CODE);
        Sku_ID_List_INAPP.add(StringUtil.POINT_02_CODE);
        Sku_ID_List_INAPP.add(StringUtil.POINT_03_CODE);
        Sku_ID_List_INAPP.add(StringUtil.POINT_04_CODE);
        Sku_ID_List_INAPP.add(StringUtil.POINT_05_CODE);
        Sku_ID_List_INAPP.add(StringUtil.POINT_06_CODE);

        Sku_ID_List_SUBS.add(StringUtil.SUBS_01_CODE);
        Sku_ID_List_SUBS.add(StringUtil.SUBS_02_CODE);
        Sku_ID_List_SUBS.add(StringUtil.SUBS_03_CODE);
        Sku_ID_List_SUBS.add(StringUtil.SUBS_04_CODE);


        //인앱 에 대한 SkuDetailsList 객체를 만듬
        SkuDetailsParams.Builder params_item = SkuDetailsParams.newBuilder();
        params_item.setSkusList(Sku_ID_List_INAPP).setType(BillingClient.SkuType.INAPP);
        //구독 에 대한 SkuDetailsList 객체를 만듬
        SkuDetailsParams.Builder params_subs = SkuDetailsParams.newBuilder();
        params_subs.setSkusList(Sku_ID_List_SUBS).setType(BillingClient.SkuType.SUBS);


        //인앱 --> 비동기 상태로 앱의 정보를 가지고 옴
        mBillingClient.querySkuDetailsAsync(params_item.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                //상품 정보를 가지고 오지 못한 경우
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "(인앱) 상품 정보를 가지고 오던 중 오류가 발생했습니다.\n오류코드: " + billingResult.getResponseCode());
                    return;
                }

                if (skuDetailsList == null) {
                    Log.d(TAG, "(인앱) 상품 정보가 존재하지 않습니다.");
                    return;
                }

                //응답 받은 데이터들의 숫자를 출력
                Log.d(TAG, "(인앱) 응답 받은 데이터 숫자: " + skuDetailsList.size());

                //받아온 상품 정보를 차례로 호출
                for (int sku_idx = 0; sku_idx < skuDetailsList.size(); sku_idx++) {
                    //해당 인덱스의 객체를 가지고 옴
                    SkuDetails _skuDetail = skuDetailsList.get(sku_idx);

                    //해당 인덱스의 상품 정보를 출력
                    Log.d(TAG, _skuDetail.getSku() + ": " + _skuDetail.getTitle() + ", " + _skuDetail.getPrice());
                    Log.d(TAG, _skuDetail.getOriginalJson());
                }

                //받은 값을 멤버 변수로 저장
                mSkuDetailsList_item = skuDetailsList;
            }
        });

        //인앱 --> 비동기 상태로 앱의 정보를 가지고 옴
        mBillingClient.querySkuDetailsAsync(params_subs.build(), new SkuDetailsResponseListener() {
            @Override
            public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                //상품 정보를 가지고 오지 못한 경우
                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "(구독) 상품 정보를 가지고 오던 중 오류가 발생했습니다.\n오류코드: " + billingResult.getResponseCode());
                    return;
                }

                if (skuDetailsList == null) {
                    Log.d(TAG, "(구독) 상품 정보가 존재하지 않습니다.");
                    return;
                }

                //응답 받은 데이터들의 숫자를 출력
                Log.d(TAG, "(구독) 응답 받은 데이터 숫자: " + skuDetailsList.size());

                //받아온 상품 정보를 차례로 호출
                for (int sku_idx = 0; sku_idx < skuDetailsList.size(); sku_idx++) {
                    //해당 인덱스의 객체를 가지고 옴
                    SkuDetails _skuDetail = skuDetailsList.get(sku_idx);

                    //해당 인덱스의 상품 정보를 출력
                    Log.d(TAG, _skuDetail.getSku() + ": " + _skuDetail.getTitle() + ", " + _skuDetail.getPrice());
                    Log.d(TAG, _skuDetail.getOriginalJson());
                }

                //받은 값을 멤버 변수로 저장
                mSkuDetailsList_subs = skuDetailsList;
            }
        });
    }


    //실제 구입 처리를 하는 메소드
    public void purchase(String item, boolean isInApp, Activity act) {
        SkuDetails skuDetails = null;
        List<SkuDetails> mSkuDetailsList = null;

        if (isInApp) {
            mSkuDetailsList = mSkuDetailsList_item;
        } else {
            mSkuDetailsList = mSkuDetailsList_subs;
        }

        if (null != mSkuDetailsList) {
            for (int i = 0; i < mSkuDetailsList.size(); i++) {
                SkuDetails details = mSkuDetailsList.get(i);
                if (details.getSku().equals(item)) {
                    skuDetails = details;
                    break;
                }
            }

            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();

            mBillingClient.launchBillingFlow(act, flowParams);
//            BillingResult billingResult = mBillingClient.launchBillingFlow(act, flowParams);
        }
    }


    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //결제에 성공한 경우
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            Log.d(TAG, "결제에 성공했으며, 아래에 구매한 상품들이 나열됨");

            for (Purchase _pur : purchases) {
                Log.e(TAG, "purchases: " + purchases);
                handlePurchase(_pur);
            }
        }

        //사용자가 결제를 취소한 경우
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            Log.d(TAG, "사용자에 의해 결제취소");
        }

        //그 외에 다른 결제 실패 이유
        else {
            Log.d(TAG, "결제가 취소 되었습니다. 종료코드: " + billingResult.getResponseCode());

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
//                afterListener.sendMessage("이미 진행중인 결제입니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.", true);
            }
        }
    }


    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            // Grant entitlement to the user.
            // 인앱 소비
            if (!purchase.getSku().contains("subs")) {
                state_item = "Y";

                Log.e(TAG, "purchase: " + purchase);
                sendPurchaseResult(purchase, "point");

                ConsumeParams consumeParams =
                        ConsumeParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                mBillingClient.consumeAsync(consumeParams, mConsumeListener);
            } else {
                // 구독 소비
                // Acknowledge the purchase if it hasn't already been acknowledged.
                if (!purchase.isAcknowledged()) {
                    state_subs = "Y";

                    Log.e(TAG, "subs: " + purchase);
                    sendPurchaseResult(purchase, "ticket");

                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    mBillingClient.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
                }
            }

        } else if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
            // Here you can confirm to the user that they've started the pending
            // purchase, and to complete it, they should follow instructions that
            // are given to them. You can also choose to remind the user in the
            // future to complete the purchase if you detect that it is still
            // pending.

            if (!purchase.getSku().equalsIgnoreCase("subscription_01")) {
                state_item = "pending";
                afterListener.sendMessage("결제사 승인이 늦어지고 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.", true);
            } else {
                state_subs = "pending";
                afterListener.sendMessage("결제사 승인이 늦어지고 있습니다. 몇분 후 앱을 재실행하여 결제가 정상적으로 진행되었는지 확인해주시기 바랍니다.", true);
            }
        }
    }

    private void sendPurchaseResult(final Purchase purchase, final String type) {
        ReqBasic server = new ReqBasic(ctx, NetUrls.PAY_RESULT) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase(NetUrls.SUCCESS) || StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            afterListener.sendMessage("최근주문건에 대한 결제 완료되었습니다", true);
                            if(PayPointAct.act != null) {
                                PayPointAct.act.finish();
                            }

                            if(PayMemberAct.act != null) {
                                PayMemberAct.act.finish();
                            }

                            if(type.equalsIgnoreCase("ticket")) {
                                AppPreference.setProfilePrefBool(ctx, AppPreference.PREF_PAY_MEMBER, true);
                            }
                        } else {

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {

                }
            }
        };

        String name;
        String price;
        switch (purchase.getSku()) {
            case StringUtil.POINT_01_CODE:
                name = StringUtil.POINT_01_NAME;
                price = StringUtil.POINT_01_PRICE;
                break;
            case StringUtil.POINT_02_CODE:
                name = StringUtil.POINT_02_NAME;
                price = StringUtil.POINT_02_PRICE;
                break;
            case StringUtil.POINT_03_CODE:
                name = StringUtil.POINT_03_NAME;
                price = StringUtil.POINT_03_PRICE;
                break;
            case StringUtil.POINT_04_CODE:
                name = StringUtil.POINT_04_NAME;
                price = StringUtil.POINT_04_PRICE;
                break;
            case StringUtil.POINT_05_CODE:
                name = StringUtil.POINT_05_NAME;
                price = StringUtil.POINT_05_PRICE;
                break;
            case StringUtil.POINT_06_CODE:
                name = StringUtil.POINT_06_NAME;
                price = StringUtil.POINT_06_PRICE;
                break;


            case StringUtil.SUBS_01_CODE:
                name = StringUtil.SUBS_01_NAME;
                price = StringUtil.SUBS_01_PRICE;
                break;
            case StringUtil.SUBS_02_CODE:
                name = StringUtil.SUBS_02_NAME;
                price = StringUtil.SUBS_02_PRICE;
                break;
            case StringUtil.SUBS_03_CODE:
                name = StringUtil.SUBS_03_NAME;
                price = StringUtil.SUBS_03_PRICE;
                break;
            case StringUtil.SUBS_04_CODE:
                name = StringUtil.SUBS_04_NAME;
                price = StringUtil.SUBS_04_PRICE;
                break;
            default:
                return;
        }

        server.setTag("Pay Result");

        server.addParams("midx", AppPreference.getProfilePref(ctx, AppPreference.PREF_MIDX));
        server.addParams("itype", type);
        server.addParams("isubject", name);
        server.addParams("icode", purchase.getSku());

        server.addParams("p_order_id", purchase.getOrderId());
        server.addParams("p_store_type", "GOOGLE");
        server.addParams("p_purchase_time", String.valueOf(purchase.getPurchaseTime()));
        server.addParams("p_purchase_price", price);
        server.addParams("p_signature", purchase.getPurchaseToken());
        server.addParams("p_info", purchase.getOriginalJson());

        server.execute(true, false);
    }
}
