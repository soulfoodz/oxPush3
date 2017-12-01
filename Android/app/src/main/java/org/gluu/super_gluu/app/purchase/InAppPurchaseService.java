package org.gluu.super_gluu.app.purchase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.PurchaseState;
import com.anjlab.android.iab.v3.TransactionDetails;

import org.gluu.super_gluu.app.GluuMainActivity;
import org.gluu.super_gluu.app.settings.Settings;

/**
 * Created by nazaryavornytskyy on 6/30/17.
 */

public class InAppPurchaseService {

    private static final String TAG = "InAppPurchaseService";

    // PRODUCT & SUBSCRIPTION IDS
    private static final String SUBSCRIPTION_ID = "org.gluu.monthly.ad.free";
//    private static final String SUBSCRIPTION_ID_TEST = "android.test.purchased";
    private static final String LICENSE_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyYw9xTiyhyjQ6mnWOwEWduDkOM84BkqHfN+jrAu82M0xBwg3RAorPwT/38sMcOZMAwcWudN0vjQo7uXAl2j4+N7BiMI2qlO2x33wY8fDvlN4ue54BBdZExZhTpkVEAmIm9cLCI3i+nOlUZgiwX6+sQOb5K+7q9WiNuSBDWRR2WDNOY7QmQdI1VzbHBPQoM00N9/0UDSFCw4LCRngm7ZeuW8AQMyYo6r5K3dy8m+Ys0JWGKA+xuQY4ZutSb47IYX4m7lzxbN0mqH9TLeA3V6audrhs5i0OYYKwbCd68NikB7Wco6L/HOzh1y6LoxIFXZ6M+vnZ6OLfTJuVmEfTOOhIwIDAQAB";
    private static final String MERCHANT_ID=null;

    public boolean readyToPurchase = false;
    public boolean isSubscribed = false;

    private BillingProcessor bp;

    public interface OnInAppServiceListener {
        public void onSubscribed(Boolean isSubscribed);
    }

    private OnInAppServiceListener inAppListener; //listener

    //setting the listener
    public void setCustomEventListener(OnInAppServiceListener eventListener) {
        this.inAppListener = eventListener;
    }

    public void initInAppService(final Context context){
        if(!BillingProcessor.isIabServiceAvailable(context)) {
            Log.e(TAG, "In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(context, LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(String productId, TransactionDetails details) {
                Log.e(TAG, "onProductPurchased: " + productId);
                isSubscribed = details.purchaseInfo.purchaseData.autoRenewing;
                //Init GoogleMobile AD
                //isSubscribed &&
                if (inAppListener != null){
                    inAppListener.onSubscribed(isSubscribed);
                }
                Settings.setPurchase(context, isSubscribed);
            }
            @Override
            public void onBillingError(int errorCode, Throwable error) {
                Log.e(TAG, "onBillingError: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                Log.e(TAG, "onBillingInitialized");
                readyToPurchase = true;
                TransactionDetails transactionDetails = bp.getSubscriptionTransactionDetails(SUBSCRIPTION_ID);
                if (transactionDetails != null) {
                    isSubscribed = transactionDetails.purchaseInfo.purchaseData.autoRenewing;
                }
                TransactionDetails transactionDetails2 = bp.getPurchaseTransactionDetails(SUBSCRIPTION_ID);
                if (transactionDetails2 != null) {
                    isSubscribed = transactionDetails2.purchaseInfo.purchaseData.purchaseState == PurchaseState.PurchasedSuccessfully;
                }
                //Init GoogleMobile AD
                if (inAppListener != null){
                    inAppListener.onSubscribed(isSubscribed);
                }
//                initGoogleADS(isSubscribed);
                Settings.setPurchase(context, isSubscribed);
            }
            @Override
            public void onPurchaseHistoryRestored() {
                Log.e(TAG, "onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.e(TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.e(TAG, "Owned Subscription: " + sku);
            }
        });
    }

    public void purchase(final Activity activity){
        bp.subscribe(activity, SUBSCRIPTION_ID);
    }

    public void restorePurchase(){
        bp.loadOwnedPurchasesFromGoogle();
    }


    public void reloadPurchaseService(){
        if (bp != null){
            bp.loadOwnedPurchasesFromGoogle();
        }
    }

    public void deInitPurchaseService(){
        if (bp != null) bp.release();
    }

    public Boolean isHandleResult(int requestCode, int resultCode, Intent data){
        return !bp.handleActivityResult(requestCode, resultCode, data);
    }

}
