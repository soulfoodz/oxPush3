package org.gluu.super_gluu.app.activities;

import android.app.Application;
import android.util.Log;

/**
 * Created by nazaryavornytskyy on 5/9/16.
 */
public class GluuApplication extends Application {

    private static GluuApplication sInstance;

    private static boolean isAppInForeground;
    public static boolean isTrustAllCertificates =false;

    public static boolean isIsAppInForeground() {
        return isAppInForeground;
    }

    public static void applicationResumed() {
        isAppInForeground = true;
        Log.d(String.valueOf(GluuApplication.class), "APP RESUMED");
    }

    public static void applicationPaused() {
        isAppInForeground = false;
        Log.d(String.valueOf(GluuApplication.class), "APP PAUSED");
    }

//    private final Billing mBilling = new Billing(this, new Billing.DefaultConfiguration() {
//        @Override
//        public String getPublicKey() {
//            return "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyYw9xTiyhyjQ6mnWOwEWduDkOM84BkqHfN+jrAu82M0xBwg3RAorPwT/38sMcOZMAwcWudN0vjQo7uXAl2j4+N7BiMI2qlO2x33wY8fDvlN4ue54BBdZExZhTpkVEAmIm9cLCI3i+nOlUZgiwX6+sQOb5K+7q9WiNuSBDWRR2WDNOY7QmQdI1VzbHBPQoM00N9/0UDSFCw4LCRngm7ZeuW8AQMyYo6r5K3dy8m+Ys0JWGKA+xuQY4ZutSb47IYX4m7lzxbN0mqH9TLeA3V6audrhs5i0OYYKwbCd68NikB7Wco6L/HOzh1y6LoxIFXZ6M+vnZ6OLfTJuVmEfTOOhIwIDAQAB";//"Your public key, don't forget abput encryption";
//        }
//    });

    public GluuApplication() {
        sInstance = this;
    }

    public static GluuApplication get() {
        return sInstance;
    }

//    public Billing getBilling() {
//        return mBilling;
//    }
}
