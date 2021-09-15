package com.cluster.taxiuser.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cluster.taxiuser.MvpApplication;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.common.Utilities;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.Token;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Currency;
import java.util.Locale;

import static com.cluster.taxiuser.base.BaseActivity.RIDE_REQUEST;

public abstract class BaseFragment extends Fragment implements MvpView {

    View view;
    ProgressDialog progressDialog;
    Fragment fragment = (Fragment) this;
    private BaseActivity baseActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(getLayoutId(), container, false);
            initView(view);
        }
        progressDialog = new ProgressDialog(activity());
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCancelable(false);

        //  checkConnection();

        return view;
    }

    public abstract int getLayoutId();

    public abstract View initView(View view);

    @Override
    public FragmentActivity activity() {
        return getActivity();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission, Activity activity) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                activity.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

    public String printJSON(Object o) {
        return ((BaseActivity) getActivity()).printJSON(o);
    }

    public void datePicker(DatePickerDialog.OnDateSetListener dateSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(activity(), dateSetListener, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    public void timePicker(TimePickerDialog.OnTimeSetListener timeSetListener) {
        Calendar myCalendar = Calendar.getInstance();
        TimePickerDialog mTimePicker = new TimePickerDialog(getContext(), timeSetListener, myCalendar.get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE), true);
        mTimePicker.show();
    }

    public NumberFormat getNumberFormat() {
        String currencyCode = SharedHelper.getKey(MvpApplication.getInstance(),
                "currency_code",
                SharedHelper.getKey(getContext(), "currency"));
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
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

    public void hideKeyboard() {
        if (baseActivity != null) baseActivity.hideKeyboard();
    }

    public void showKeyboard() {
        if (baseActivity != null) baseActivity.showKeyboard();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            this.baseActivity = (BaseActivity) context;
        }
    }

    public void initPayment(TextView paymentMode) {
        if (RIDE_REQUEST.containsKey("payment_mode"))
            switch (RIDE_REQUEST.get("payment_mode").toString()) {
                case Utilities.PaymentMode.cash:
                    paymentMode.setText(getString(R.string.cash));
                    //    paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                    break;
                case Utilities.PaymentMode.card:
                    if (RIDE_REQUEST.containsKey("card_last_four"))
                        paymentMode.setText(getString(R.string.card_, RIDE_REQUEST.get("card_last_four")));
                    else paymentMode.setText(getString(R.string.add_card_));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                    break;
                case Utilities.PaymentMode.payPal:
                    paymentMode.setText(getString(R.string.paypal));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                    break;
                case Utilities.PaymentMode.wallet:
                    paymentMode.setText(getString(R.string.wallet));
                    //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                    break;
            }
        else {
            if (BaseActivity.isCash) {
                RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.cash);
                paymentMode.setText(getString(R.string.cash));
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
            } else if (BaseActivity.isCard) {
                paymentMode.setText(R.string.add_card_);
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_card, 0, 0, 0);
                RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.card);
            }
        }
    }

    public void onErrorBase(Throwable t) {
        ((BaseActivity) getActivity()).onErrorBase(t);
    }

    public void handleError(Throwable e) {
        try {
            try {
                hideLoading();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        ((BaseActivity) getActivity()).handleError(e);
    }

    @Override
    public void onSuccessRefreshToken(Token token) {
        ((BaseActivity) getActivity()).onSuccessRefreshToken(token);
    }

    @Override
    public void onErrorRefreshToken(Throwable throwable) {
        ((BaseActivity) getActivity()).onErrorRefreshToken(throwable);
    }

    @Override
    public void onSuccessLogout(Object object) {
        ((BaseActivity) getActivity()).onSuccessLogout(object);
    }

    @Override
    public void onError(Throwable throwable) {
        ((BaseActivity) getActivity()).onError(throwable);
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
