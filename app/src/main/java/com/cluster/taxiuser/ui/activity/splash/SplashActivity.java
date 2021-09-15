package com.cluster.taxiuser.ui.activity.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.cluster.taxiuser.BuildConfig;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.common.Constants;
import com.cluster.taxiuser.common.LocaleHelper;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.User;
import com.cluster.taxiuser.ui.activity.OnBoardActivity;
import com.cluster.taxiuser.ui.activity.main.MainActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.cluster.taxiuser.MvpApplication.REQUEST_CHECK_SETTINGS;

public class SplashActivity extends BaseActivity implements SplashIView {

    @BindView(R.id.note)
    TextView note;
    private SplashPresenter<SplashActivity> presenter = new SplashPresenter<>();
    private String language = Constants.Language.SPANISH;

    @Override
    public int getLayoutId() {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        return R.layout.activity_splash;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        LocaleHelper.setLocale(getApplicationContext(), language);

        note.setText(getString(R.string.version,
                String.valueOf(BuildConfig.VERSION_CODE)));
        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    BuildConfig.APPLICATION_ID,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Log.d("FCM", "FCM Token: " + SharedHelper.getKey(activity(), "device_token"));
    }

    private void init(){
            new Handler().postDelayed(() -> {
                Log.d("Loggedin", String.valueOf(SharedHelper.getBoolKey(SplashActivity.this, "logged_in", false)));
                String device_token = String.valueOf(SharedHelper.getKey(SplashActivity.this, "device_token"));
//                presenter.changeLanguage(language);
                Log.d("device_token", device_token);
                if (SharedHelper.getBoolKey(SplashActivity.this, "logged_in", false))
                    presenter.profile();
                else startActivity(new Intent(SplashActivity.this, OnBoardActivity.class));
            }, 3000);
    }

    @Override
    public void onResume() {
        super.onResume();
        enableGPS();
    }

    @Override
    protected void onStart() {
        super.onStart();
        enableGPS();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onSuccess(User user) {
        SharedHelper.putKey(this,"stripe_publishable_key",user.getStripePublishableKey());
        SharedHelper.putKey(this,"user_id",String.valueOf(user.getId()));
        SharedHelper.putKey(this,"appContact",user.getAppContact());
        SharedHelper.putKey(this,"currency",user.getCurrency());
        SharedHelper.putKey(this,"walletBalance",String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "logged_in", true);
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        finishAffinity();
        startActivity(new Intent(SplashActivity.this, MainActivity.class));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
       /* if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            Log.e("onError", response.code() + "");

            if (response.code() == 500) note.setText(getString(R.string.internal_server_error));
            *//*if (response.code() == 401) {
                SharedHelper.clearSharedPreferences(activity());
                finishAffinity();
                startActivity(new Intent(activity(), OnBoardActivity.class));
            }*//*
        }*/
    }

    @Override
    public void onLanguageChanged(Object object) {
        hideLoading();
        Log.d("111111111", "Language Response:::" + object.toString());
        LocaleHelper.setLocale(getApplicationContext(), language);
    }

    protected void enableGPS() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000 * 10);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> init());

        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) try {
                ResolvableApiException resolvable = (ResolvableApiException) e;
                resolvable.startResolutionForResult(SplashActivity.this, REQUEST_CHECK_SETTINGS);
            } catch (IntentSender.SendIntentException sendEx) {
                Toast.makeText(this, sendEx.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        init();
                        break;
                    case Activity.RESULT_CANCELED:
                        finish();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

}
