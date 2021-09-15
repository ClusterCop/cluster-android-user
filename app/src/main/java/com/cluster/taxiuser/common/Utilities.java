package com.cluster.taxiuser.common;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.ui.activity.OnBoardActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import es.dmoral.toasty.Toasty;

public class Utilities {

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void printV(String TAG, String message) {
        System.out.println(TAG + "==>" + message);
    }

    public void hideKeypad(Context context, View view) {
        // Check if no view has focus:
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showAlert(final Context context, String message) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage(message)
                    .setTitle(context.getString(R.string.app_name))
                    .setCancelable(true)
                    //.setIcon(R.mipmap.ic_launcher)
                    .setPositiveButton("OK", (dialog, id) -> dialog.dismiss());
            final AlertDialog alert = builder.create();
            alert.setOnShowListener(arg -> {
                alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                alert.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            });
            alert.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public interface InvoiceFare {
        String min = "MIN";
        String hour = "HOUR";
        String distance = "DISTANCE";
        String distanceMin = "DISTANCEMIN";
        String distanceHour = "DISTANCEHOUR";
    }

    public interface PaymentMode {
        String cash = "CASH";
        String card = "CARD";
        String payPal = "PAYPAL";
        String wallet = "WALLET";
    }

    private static double milesToKm(double miles) {
        return miles * 1.60934;
    }

    private static double kmToMiles(double km) {
        return km * 0.621371;
    }


    public static void LogoutApp(Activity thisActivity, String logout_text) {

        logout_text = "Loggedout Successfully!";


        Toasty.success(thisActivity, logout_text, Toast.LENGTH_SHORT).show();
        SharedHelper.clearSharedPreferences(thisActivity);
        BaseActivity.RIDE_REQUEST.clear();
        NotificationManager notificationManager = (NotificationManager) thisActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        thisActivity.finishAffinity();
        Intent goToLogin = new Intent(thisActivity, OnBoardActivity.class);
        goToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        thisActivity.startActivity(goToLogin);
        thisActivity.finish();

    }
}
