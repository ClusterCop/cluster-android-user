package com.cluster.taxiuser.base;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.gson.Gson;
import com.cluster.taxiuser.BuildConfig;
import com.cluster.taxiuser.MvpApplication;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.common.ConnectivityReceiver;
import com.cluster.taxiuser.common.LocaleHelper;
import com.cluster.taxiuser.common.Utilities;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.Datum;
import com.cluster.taxiuser.data.network.model.Provider;
import com.cluster.taxiuser.data.network.model.Token;
import com.cluster.taxiuser.ui.activity.OnBoardActivity;
import com.cluster.taxiuser.ui.activity.login.EmailActivity;
import com.cluster.taxiuser.ui.activity.login.PasswordActivity;
import com.cluster.taxiuser.ui.activity.register.RegisterActivity;
import com.cluster.taxiuser.ui.activity.social.SocialLoginActivity;
import com.cluster.taxiuser.ui.activity.splash.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import pl.aprilapps.easyphotopicker.EasyImage;
import retrofit2.HttpException;
import retrofit2.Response;

public abstract class BaseActivity extends AppCompatActivity
        implements MvpView, ConnectivityReceiver.ConnectivityReceiverListener {

    public static boolean isCash = true;
    public static boolean isCard = true;

    private boolean isNetwork = false;
    private Dialog offlineDialog;
    private String error;
    private BasePresenter<BaseActivity> presenter = new BasePresenter<BaseActivity>();
    public static final int REQUEST_CODE_PICTURE = 23;
    public static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 100;
    public static int APP_REQUEST_CODE = 99;
    public static HashMap<String, Object> RIDE_REQUEST = new HashMap<>();
    public static HashMap<String, Object> RIDE_REQUEST_setting = new HashMap<>();
    public static Datum DATUM = null;
    public static Provider provider = null;
    public static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    Calendar myCalendar = Calendar.getInstance();
    ProgressDialog progressDialog;
    Activity activity;

    public static String getDisplayableTime(long value) {

        long difference;
        Long mDate = java.lang.System.currentTimeMillis();

        if (mDate > value) {
            difference = mDate - value;
            final long seconds = difference / 1000;
            final long minutes = seconds / 60;
            final long hours = minutes / 60;
            final long days = hours / 24;
            final long months = days / 31;
            final long years = days / 365;

            if (seconds < 86400) {
                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                return formatter.format(new Date(value));
                //return "not yet";
            } else if (seconds < 172800) // 48 * 60 * 60
                return "yesterday";
            else if (seconds < 2592000) // 30 * 24 * 60 * 60
                return days + " days ago";
            else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
                return months <= 1 ? "one month ago" : days + " months ago";
            else return years <= 1 ? "one year ago" : years + " years ago";
        }
        return null;
    }

    @Override
    public Activity activity() {
        return this;
    }

    public abstract int getLayoutId();

    public abstract void initView();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);
        presenter.attachView(this);
        initView();
        activity = this;
        checkConnection();

        try {
            @SuppressLint("PackageManagerGetSignatures") PackageInfo info = getPackageManager().getPackageInfo(
                    "com.libi.user",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException ignored) {
            Log.e("KeyHash", ignored.getMessage());
        }
    }

    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        if (activity instanceof SplashActivity || activity instanceof EmailActivity ||
                activity instanceof PasswordActivity || activity instanceof OnBoardActivity ||
                activity instanceof RegisterActivity || activity instanceof SocialLoginActivity) {
            if (!isNetwork) {
                showOfflineDialog(isConnected, 1);
            } else {
                isNetwork = false;
                if (!isConnected) {
                    hideLoading();
                    Toast.makeText(activity, getString(R.string.current_alternative), Toast.LENGTH_SHORT).show();
                } else {
                    hideLoading();
                    if (offlineDialog != null && offlineDialog.isShowing()) {
                        offlineDialog.dismiss();
                    }

                }
            }
        } else {
            showOfflineDialog(isConnected, 0);
        }

    }

    public void showOfflineDialog(boolean isConnected, int position) {
        if (!isConnected) {
            if (activity != null) {
                if (position == 0) {
                    try {
                        final Dialog offlineDialog = new Dialog(this);
                        offlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        offlineDialog.setCancelable(false);
                        offlineDialog.setCanceledOnTouchOutside(false);
                        offlineDialog.setContentView(R.layout.layout_offline);
                        Window window = offlineDialog.getWindow();
                        offlineDialog.show();
                        ImageView iv_retry = offlineDialog.findViewById(R.id.iv_retry);
                        Button btn_send_location = offlineDialog.findViewById(R.id.btn_send_location);
                        TextView no_thanks = offlineDialog.findViewById(R.id.no_thanks);
                        no_thanks.setOnClickListener(view -> {
                            offlineDialog.dismiss();
                            finishAffinity();
                        });
                        btn_send_location.setOnClickListener(view -> {
                            if (btn_send_location.getVisibility() == View.VISIBLE) {
                                offlineDialog.dismiss();
                                try {
                                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                                    smsIntent.setData(Uri.parse("smsto:"));
                                    smsIntent.setType("vnd.android-dir/mms-sms");
                                    smsIntent.putExtra("address", SharedHelper.getKey(getApplicationContext(), "appContact"));
                                    smsIntent.putExtra("sms_body", "I need a cab @" +
                                            SharedHelper.getKey(activity(), "latitude") + "," +
                                            SharedHelper.getKey(activity(), "longitude") + "( Please don't edit this SMS. Standard SMS charges of Rs.3 per SMS may apply )");
                                    startActivity(smsIntent);
                                } catch (ActivityNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        assert window != null;
                        WindowManager.LayoutParams param = window.getAttributes();
                        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
                        param.windowAnimations = R.style.DialogAnimation;
                        window.setAttributes(param);
                        Objects.requireNonNull(offlineDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        offlineDialog = new Dialog(this);
                        offlineDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        offlineDialog.setCancelable(false);
                        offlineDialog.setCanceledOnTouchOutside(false);
                        offlineDialog.setContentView(R.layout.layout_offline_alternative);
                        Window window = offlineDialog.getWindow();
                        offlineDialog.show();
                        ImageView iv_retry = offlineDialog.findViewById(R.id.iv_retry);
                        TextView no_thanks = offlineDialog.findViewById(R.id.no_thanks);
                        no_thanks.setOnClickListener(view -> {
                            offlineDialog.dismiss();
                            finishAffinity();
                        });
                        iv_retry.setOnClickListener(v -> {
                            isNetwork = true;
                            showLoading();
                            checkConnection();
                        });
                        assert window != null;
                        WindowManager.LayoutParams param = window.getAttributes();
                        param.gravity = Gravity.CENTER | Gravity.CENTER_HORIZONTAL;
                        param.windowAnimations = R.style.DialogAnimation;
                        window.setAttributes(param);
                        Objects.requireNonNull(offlineDialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base));
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showLoading() {
        if (!progressDialog.isShowing()) {
            System.out.println("BaseActivity.showLoading...." + this.activity);
            progressDialog.show();
        }
    }

    @Override
    public void hideLoading() {
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public void switchFragment(Fragment fragment, int containerId) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.replace(containerId, fragment).addToBackStack(fragment.getClass().getName()).commitAllowingStateLoss();
    }

    public String getAddress(LatLng currentLocation) {
        String address = null;
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(currentLocation.latitude, currentLocation.longitude, 1);
            if ((addresses != null) && !addresses.isEmpty()) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                if (returnedAddress.getMaxAddressLineIndex() > 0)
                    for (int j = 0; j < returnedAddress.getMaxAddressLineIndex(); j++)
                        strReturnedAddress.append(returnedAddress.getAddressLine(j)).append("");
                else strReturnedAddress.append(returnedAddress.getAddressLine(0)).append("");
                address = strReturnedAddress.toString();
            }
        } catch (Exception e) {
            Log.e("MAP", "getAddress: " + e);
        }
        return address;
    }

    public NumberFormat getNumberFormat() {
        Locale locale = new Locale("es", "co");
        String currencyCode = SharedHelper.getKey(BaseActivity.this, "currency_code",
                SharedHelper.getKey(this, "currency"));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setCurrency(Currency.getInstance(currencyCode));
        numberFormat.setMinimumFractionDigits(2);
        return numberFormat;
    }


    public NumberFormat getNewNumberFormat() {
        return new DecimalFormat("0.00");
    }

    public double getNumber(double value) {
        long factor = (long) Math.pow(value, 2);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void pickImage() {
        EasyImage.openChooserWithGallery(this, "", 0);
    }

    public void datePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        DatePickerDialog dialog = new DatePickerDialog(this, dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    public void timePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(this, timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }

    public void fbOtpVerify() {
        final Intent intent = new Intent(this, AccountKitActivity.class);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN);
        configurationBuilder.setReadPhoneStateEnabled(true);
        configurationBuilder.setReceiveSMS(true);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder.build());
        startActivityForResult(intent, APP_REQUEST_CODE);
    }

    public void alertLogout() {
        new AlertDialog.Builder(activity())
                .setMessage(R.string.are_sure_you_want_to_logout)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    SharedHelper.clearSharedPreferences(activity());
                    RIDE_REQUEST.clear();
                    finishAffinity();
                    startActivity(new Intent(activity(), SplashActivity.class));
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    @SuppressLint("StringFormatInvalid")
    public void shareApp() {
        try {
            String appName = getString(R.string.app_name);
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, appName);
//            i.putExtra(Intent.EXTRA_TEXT, "Hey Checkout this app, " + appName + "\nhttps://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
            i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_content, appName, BuildConfig.APPLICATION_ID));
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
    }

    public float bearingBetweenLocations(LatLng latLng1, LatLng latLng2) {
        double PI = 3.14159;
        double lat1 = latLng1.latitude * PI / 180;
        double long1 = latLng1.longitude * PI / 180;
        double lat2 = latLng2.latitude * PI / 180;
        double long2 = latLng2.longitude * PI / 180;

        double dLon = (long2 - long1);

        double y = Math.sin(dLon) * Math.cos(lat2);
        double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
                * Math.cos(lat2) * Math.cos(dLon);

        double brng = Math.atan2(y, x);

        brng = Math.toDegrees(brng);
        brng = (brng + 360) % 360;
        return (float) brng;
    }

    public void animateMarker(final LatLng startPosition,
                              final LatLng toPosition, Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;
        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    long elapsed = SystemClock.uptimeMillis() - start;
                    float t = interpolator.getInterpolation((float) elapsed / duration);
                    double lng = t * toPosition.longitude + (1 - t) * startPosition.longitude;
                    double lat = t * toPosition.latitude + (1 - t) * startPosition.latitude;

                    marker.setPosition(new LatLng(lat, lng));

                    // Post again 16ms later.
                    if (t < 1.0) handler.postDelayed(this, 16);
                    else {
                        marker.setVisible(true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void handleError(Throwable e) {
        hideLoading();

        if (e != null) {

            try {
                if (e instanceof ConnectException || e instanceof UnknownHostException ||
                        e instanceof SocketTimeoutException || e instanceof NoRouteToHostException) {
//                    Toasty.error(this, getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
                } else if (e instanceof HttpException) {
                    ResponseBody responseBody = ((HttpException) e).response().errorBody();
                    int responseCode = ((HttpException) e).response().code();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        if (responseCode == 400 || responseCode == 405 || responseCode == 500) {
                            Toasty.error(this, getErrorMessage(responseBody), Toast.LENGTH_SHORT).show();
                        } else if (responseCode == 404) {
                            if (PasswordActivity.TAG.equals("PasswordActivity")) {
                                Collection<Object> values = jsonToMap(jsonObject).values();
                                printIfContainsValue(jsonToMap(jsonObject), values.toString()
                                        .replaceAll("[\\[\\],]", ""));
                            } else {
                                Toasty.error(this, getErrorMessage(responseBody), Toast.LENGTH_SHORT).show();
                            }
                        } else if (responseCode == 401) {
                            refreshToken();
                        } else if (responseCode == 422) {
                            Collection<Object> values = jsonToMap(jsonObject).values();
                            printIfContainsValue(jsonToMap(jsonObject), values.toString()
                                    .replaceAll("[\\[\\],]", ""));
                        } else if (responseCode == 503) {
                            Toasty.error(this, getString(R.string.server_down), Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(this, getErrorMessage(responseBody), Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception exception) {
                        //  Toast.makeText(getApplicationContext(), getString(R.string.some_thing_wrong), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // Toast.makeText(this, getString(R.string.some_thing_wrong), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

        /*hideLoading();
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("message"))
                    Toast.makeText(activity(), jObjError.optString("message"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("error"))
                    Toast.makeText(activity(), jObjError.optString("error"), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }*/
    }

    private void refreshToken() {
        if (!SharedHelper.getKey(this, "refresh_token").equalsIgnoreCase("")) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("grant_type", "refresh_token");
            map.put("refresh_token", SharedHelper.getKey(this, "refresh_token"));
            map.put("client_secret", BuildConfig.CLIENT_SECRET);
            map.put("client_id", BuildConfig.CLIENT_ID);
            map.put("scope", "");
            showLoading();
            presenter.refreshToken(map);
        } else {
            Toasty.error(this, getString(R.string.refresh_invaild), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSuccessRefreshToken(Token token) {
        hideLoading();
        SharedHelper.putKey(this, token.getAccessToken(), "access_token");
        Toasty.error(this, getString(R.string.please_try_again), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onErrorRefreshToken(Throwable throwable) {
        hideLoading();
        if (throwable != null) {
            showLoading();
            presenter.logout(SharedHelper.getKey(this, "user_id"));
        }
    }

    @Override
    public void onSuccessLogout(Object object) {
        hideLoading();
        Utilities.LogoutApp(activity(), "");
    }

    @Override
    public void onError(Throwable throwable) {
        hideLoading();
        throwable.printStackTrace();
    }

    public static Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();
        if (json != JSONObject.NULL) retMap = toMap(json);
        return retMap;
    }

    public static Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();
        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);
            if (value instanceof JSONArray) value = toList((JSONArray) value);
            else if (value instanceof JSONObject) value = toMap((JSONObject) value);
            map.put(key, value);
        }
        return map;
    }

    public static List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) value = toList((JSONArray) value);
            else if (value instanceof JSONObject) value = toMap((JSONObject) value);
            list.add(value);
        }
        return list;
    }

    public void printIfContainsValue(Map mp, String value) {
        /*String finalMessage = "";
        Set<String> set= new HashSet<String>(Arrays.asList(value.split("\\.")));
        Iterator iterator = set.iterator();
        while (iterator.hasNext()){
            if (TextUtils.isEmpty(finalMessage))
                finalMessage = (String) iterator.next();
            else
                finalMessage = finalMessage +"\n"+iterator.next();
        }*/
        Toasty.error(this, value, Toast.LENGTH_LONG).show();
    }

    private String getErrorMessage(ResponseBody responseBody) {
        try {
            JSONObject jsonObject = new JSONObject(responseBody.string());
            if (jsonObject.has("message")) {
                error = jsonObject.getString("message");
            } else if (jsonObject.has("error")) {
                error = jsonObject.getString("error");
            } else if (jsonObject.has("email")) {
                error = jsonObject.optString("email");
            } else {
                error = getString(R.string.some_thing_wrong);
            }
            return error;
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public String printJSON(Object o) {
        return new Gson().toJson(o);
    }

    @Override
    public void onResume() {
        super.onResume();
        MvpApplication.getInstance().setConnectivityListener(this);
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        //showOfflineDialog(isConnected, 1);
        checkConnection();
    }

    public void initPayment(String mode, TextView paymentMode, ImageView paymentImage) {

        switch (mode) {
            case Utilities.PaymentMode.cash:
                paymentMode.setText(getString(R.string.cash));
                paymentImage.setImageResource(R.drawable.ic_money);
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                break;
            case Utilities.PaymentMode.card:
                paymentMode.setText(getString(R.string.card));
                paymentImage.setImageResource(R.drawable.ic_card);
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                break;
            case Utilities.PaymentMode.payPal:
                paymentMode.setText(getString(R.string.paypal));
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                break;
            case Utilities.PaymentMode.wallet:
                paymentMode.setText(getString(R.string.wallet));
                //paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            default:
                break;
        }
    }

    public void onErrorBase(Throwable e) {
        hideLoading();
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                JSONObject jObjError = new JSONObject(response.errorBody().string());
                if (jObjError.has("message"))
                    Toast.makeText(activity(), jObjError.optString("message"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("error"))
                    Toast.makeText(activity(), jObjError.optString("error"), Toast.LENGTH_SHORT).show();
                else if (jObjError.has("email"))
                    Toast.makeText(activity(), jObjError.optString("email"), Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity(), R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }

}