package com.cluster.taxiuser;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.support.multidex.BuildConfig;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.cluster.taxiuser.common.ConnectivityReceiver;
import com.cluster.taxiuser.common.LocaleHelper;
import com.cluster.taxiuser.data.SharedHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;

//import com.facebook.stetho.Stetho;

public class MvpApplication extends Application {

    private static MvpApplication mInstance;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final int PICK_LOCATION_REQUEST_CODE = 3;
    public static final int PERMISSIONS_REQUEST_PHONE = 4;
    public static final int REQUEST_CHECK_SETTINGS = 5;
    public static float DEFAULT_ZOOM = 14;
    public static Location mLastKnownLocation;

    @Override
    public void onCreate() {
        super.onCreate();
        if (!BuildConfig.DEBUG)
            Fabric.with(this, new Crashlytics());
//        Stetho.initializeWithDefaults(this);
        mInstance = this;
        MultiDex.install(this);
    }

    public static synchronized MvpApplication getInstance() {
        return mInstance;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase, "es"));
        //super.attachBaseContext(newBase);
        MultiDex.install(newBase);
    }

    public NumberFormat getNumberFormat() {
        String currencyCode = SharedHelper.getKey(getApplicationContext(), "currency_code",
                SharedHelper.getKey(getApplicationContext(),"currency"));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        numberFormat.setCurrency(Currency.getInstance(currencyCode));
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat;
    }

    public double getNumber(double value) {
        long factor = (long) Math.pow(value, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public NumberFormat getNewNumberFormat() {
        DecimalFormat numberFormat = new DecimalFormat("0.00");
        return numberFormat;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }

}
