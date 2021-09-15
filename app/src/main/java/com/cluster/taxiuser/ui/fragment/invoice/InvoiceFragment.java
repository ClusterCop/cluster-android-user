package com.cluster.taxiuser.ui.fragment.invoice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.models.PayPalRequest;
import com.cluster.taxiuser.BuildConfig;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseFragment;
import com.cluster.taxiuser.common.Constants;
import com.cluster.taxiuser.common.Utilities;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.Datum;
import com.cluster.taxiuser.data.network.model.Message;
import com.cluster.taxiuser.data.network.model.Payment;
import com.cluster.taxiuser.ui.activity.main.MainActivity;
import com.cluster.taxiuser.ui.activity.payment.PaymentActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.cluster.taxiuser.base.BaseActivity.DATUM;
import static com.cluster.taxiuser.base.BaseActivity.RIDE_REQUEST;
import static com.cluster.taxiuser.data.SharedHelper.getKey;
import static com.cluster.taxiuser.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class InvoiceFragment extends BaseFragment implements InvoiceIView {

    @BindView(R.id.payment_mode)
    TextView paymentMode;
    @BindView(R.id.pay_now)
    Button payNow;
    @BindView(R.id.done)
    Button done;
    @BindView(R.id.booking_id)
    TextView bookingId;
    @BindView(R.id.distance)
    TextView distance;
    @BindView(R.id.travel_time)
    TextView travelTime;
    @BindView(R.id.fixed)
    TextView fixed;
    @BindView(R.id.distance_fare)
    TextView distanceFare;
    @BindView(R.id.tax)
    TextView tax;
    @BindView(R.id.total)
    TextView total;
    @BindView(R.id.payable)
    TextView payable;
    @BindView(R.id.wallet_detection)
    TextView walletDetection;
    @BindView(R.id.time_fare)
    TextView timeFare;
    @BindView(R.id.llDistanceFareContainer)
    LinearLayout llDistanceFareContainer;
    @BindView(R.id.llTimeFareContainer)
    LinearLayout llTimeFareContainer;
    @BindView(R.id.llTipContainer)
    LinearLayout llTipContainer;
    @BindView(R.id.llWalletDeductionContainer)
    LinearLayout llWalletDeductionContainer;
    @BindView(R.id.llDiscountContainer)
    LinearLayout llDiscountContainer;
    @BindView(R.id.tvChange)
    TextView tvChange;
    @BindView(R.id.tvGiveTip)
    TextView tvGiveTip;
    @BindView(R.id.tvTipAmt)
    TextView tvTipAmt;
    @BindView(R.id.tvDiscount)
    TextView tvDiscount;

    private InvoicePresenter<InvoiceFragment> presenter = new InvoicePresenter<>();
    private NumberFormat numberFormat;
    private BraintreeFragment mBrainTreeFragment;
    private Payment payment;
    private String payment_mode;
    private Double tips = 0.0;
    public static boolean isInvoiceCashToCard = false;


    public InvoiceFragment() {
    }

    public static InvoiceFragment newInstance() {
        return new InvoiceFragment();
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_invoice;
    }

    @Override
    public View initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);
        try {
            numberFormat = getNewNumberFormat();
            mBrainTreeFragment = BraintreeFragment.newInstance(activity(), BuildConfig.PAYPAL_CLIENT_TOKEN);
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            Toast.makeText(activity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
        if (DATUM != null) initView(DATUM);

        return view;
    }

    @SuppressLint("StringFormatInvalid")
    private void initView(@NonNull Datum datum) {
        bookingId.setText(datum.getBookingId());
        if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM)) {
            if (datum.getDistance() > 1 || datum.getDistance() > 1.0) {
                distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.KM));
            } else {
                distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.km)));
            }
        } else {
            if (datum.getDistance() > 1 || datum.getDistance() > 1.0) {
                distance.setText(String.format("%s %s", datum.getDistance(), Constants.MeasurementType.MILES));
            } else {
                distance.setText(String.format("%s %s", datum.getDistance(), getString(R.string.mile)));
            }
        }
        travelTime.setText(getString(R.string._min, datum.getTravelTime()));

        /*
              0 - Payment Incomplete: Comes only with -Cash- payment. Once payment is done manually goes to -1-
              1 - Payment Done: -0- not applicable for -Card- payment.
        */
       /* RIDE_REQUEST.put("payment_mode", datum.getPaymentMode().equalsIgnoreCase("CASH")
                ? Utilities.PaymentMode.cash : Utilities.PaymentMode.card);
*/
        initPaymentView(datum.getPaymentMode(), "", false);
        if (datum.getPaymentMode() != null) {
            payment_mode = datum.getPaymentMode();
        }

        if (datum.getPaid() == 0) {
            if (datum.getPaymentMode().equalsIgnoreCase("CASH")) {
                done.setVisibility(View.VISIBLE);
                payNow.setVisibility(View.GONE);

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toasty.info(getContext(),"Payment not confirmed from the driver.",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } else if(datum.getPaid() == 1) {
            if (datum.getPaymentMode().equalsIgnoreCase("CASH")) {
                done.setVisibility(View.VISIBLE);
                payNow.setVisibility(View.GONE);
            }
        }
        /*if (datum.getPaid() == 0) {
            if (!datum.getPaymentMode().equalsIgnoreCase("CASH")) {
                payNow.setVisibility(View.VISIBLE);
                done.setVisibility(View.GONE);
            }
        } else if (datum.getPaid() == 1) {
            payNow.setVisibility(View.GONE);
            done.setVisibility(View.VISIBLE);
        }*/


        payment = datum.getPayment();
        try {
            if (payment != null) {
                fixed.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payment.getFixed())));
                tax.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payment.getTax())));
                total.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payment.getTotal())));
                payable.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payment.getPayable())));
                if (payment.getWallet() > 0) {
                    llWalletDeductionContainer.setVisibility(View.VISIBLE);
                    walletDetection.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            numberFormat.format(payment.getWallet())));
                } else llWalletDeductionContainer.setVisibility(View.GONE);
                if (payment.getDiscount() > 0) {
                    llDiscountContainer.setVisibility(View.VISIBLE);
                    tvDiscount.setText(String.format("%s -%s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            numberFormat.format(payment.getDiscount())));
                } else llDiscountContainer.setVisibility(View.GONE);

                //      MIN,    HOUR,   DISTANCE,   DISTANCEMIN,    DISTANCEHOUR
                if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.min)
                        || datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.hour)) {
                    llTimeFareContainer.setVisibility(View.VISIBLE);
                    llDistanceFareContainer.setVisibility(View.GONE);
                    distanceFare.setText(R.string.time_fare);
                    if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.min))
                        timeFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                numberFormat.format(payment.getMinute())));
                    else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.hour))
                        timeFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                numberFormat.format(payment.getHour())));
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distance)) {
                    llTimeFareContainer.setVisibility(View.GONE);
                    llDistanceFareContainer.setVisibility(View.VISIBLE);
                    distanceFare.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            numberFormat.format(payment.getDistance())));
                } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distanceMin)
                        || datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distanceHour)) {
                    llTimeFareContainer.setVisibility(View.VISIBLE);
                    llDistanceFareContainer.setVisibility(View.VISIBLE);
                    if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distanceMin)) {
                        distanceFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                numberFormat.format(payment.getDistance())));
                        timeFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                numberFormat.format(payment.getMinute())));
                    } else if (datum.getServiceType().getCalculator().equalsIgnoreCase(Utilities.InvoiceFare.distanceHour)) {
                        distanceFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                numberFormat.format(payment.getDistance())));
                        timeFare.setText(String.format("%s %s",
                                getKey(Objects.requireNonNull(getContext()), "currency"),
                                numberFormat.format(payment.getHour())));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (payment_mode != null) {
            if (payment_mode.equals("CASH")) {
                llTipContainer.setVisibility(View.GONE);
                tvChange.setVisibility(View.VISIBLE);
                payNow.setVisibility(View.GONE);
                done.setVisibility(View.VISIBLE);
                isInvoiceCashToCard = false;
            } else if (payment_mode.equals("CARD")) {
                llTipContainer.setVisibility(View.VISIBLE);
                tvChange.setVisibility(View.GONE);
                payNow.setVisibility(View.VISIBLE);
                done.setVisibility(View.GONE);
                isInvoiceCashToCard = true;
            }
        }
    }

    @Override
    public void onSuccess(Object obj) {
        payNow.setVisibility(View.VISIBLE);
        done.setVisibility(View.GONE);
        hideLoading();
    }

    @Override
    public void onSuccess(Message message) {
        hideLoading();
        Toast.makeText(getContext(), R.string.you_have_successfully_paid, Toast.LENGTH_SHORT).show();
        ((MainActivity) Objects.requireNonNull(getContext())).changeFlow("RATING");
    }

    @Override
    public void onError(Throwable e) {
        hideLoading();
        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            try {
                JSONObject jObjError = new JSONObject(Objects.requireNonNull(response.errorBody()).string());
                if (jObjError.has("message"))
                    Toast.makeText(activity(), jObjError.optString("message"), Toast.LENGTH_SHORT).show();
                if (jObjError.has("error"))
                    Toast.makeText(activity(), jObjError.optString("error"), Toast.LENGTH_SHORT).show();
                if (jObjError.has("card_id")) {
                    JSONArray card_id = jObjError.getJSONArray("card_id");
                    Toast.makeText(activity(), card_id.optString(0), Toast.LENGTH_SHORT).show();
                }
            } catch (Exception exp) {
                Log.e("Error", exp.getMessage());
            }
        }
    }

    @OnClick({R.id.payment_mode, R.id.pay_now, R.id.done, R.id.tvChange, R.id.tvGiveTip, R.id.tvTipAmt, R.id.ivInvoice})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tvChange:
            case R.id.payment_mode:
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.pay_now:
                if (DATUM != null) {
                    Datum datum = DATUM;
                    switch (datum.getPaymentMode()) {
                        case Utilities.PaymentMode.card:
                            showLoading();
                            presenter.payment(datum.getId(), tips);
                            break;
                        case Utilities.PaymentMode.payPal:
                            PayPalRequest request = new PayPalRequest(String.valueOf(datum.getPayment().getPayable()))
                                    .currencyCode(getKey(activity(), "currency_code"))
                                    .intent(PayPalRequest.INTENT_AUTHORIZE);
                            PayPal.requestOneTimePayment(mBrainTreeFragment, request);
                            break;
                        case Utilities.PaymentMode.cash:
                            if (isInvoiceCashToCard) {
                                showLoading();
                                presenter.payment(datum.getId(), tips);
                            }
                            break;
                    }
                }
                break;
            case R.id.done:
            case R.id.ivInvoice:
                ((MainActivity) Objects.requireNonNull(getContext())).changeFlow("RATING");
                break;
            case R.id.tvTipAmt:
            case R.id.tvGiveTip:
                showTipDialog(payment.getPayable());
                break;
        }
    }

    private void showTipDialog(double totalAmount) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_tip);
        EditText etAmount = dialog.findViewById(R.id.etAmount);
        Button percent10 = dialog.findViewById(R.id.bt10Percent);
        Button percent15 = dialog.findViewById(R.id.bt15Percent);
        Button percent20 = dialog.findViewById(R.id.bt20Percent);
        TextView tvSubmit = dialog.findViewById(R.id.tvSubmit);

        percent10.setOnClickListener(v -> etAmount.setText(String.valueOf((totalAmount * 10) / 100)));

        percent15.setOnClickListener(v -> etAmount.setText(String.valueOf((totalAmount * 15) / 100)));

        percent20.setOnClickListener(v -> etAmount.setText(String.valueOf((totalAmount * 20) / 100)));

        tvSubmit.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(etAmount.getText()) && Double.parseDouble(etAmount.getText().toString()) > 0) {
                tvGiveTip.setVisibility(View.GONE);
                tvTipAmt.setVisibility(View.VISIBLE);
                tips = Double.parseDouble(etAmount.getText().toString());
                Double payableCal = payment.getPayable() + tips;
                tvTipAmt.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(tips)));
                payable.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payableCal)));
                dialog.dismiss();
            } else {
                tvGiveTip.setVisibility(View.VISIBLE);
                tvTipAmt.setVisibility(View.GONE);
                payable.setText(String.format("%s %s",
                        getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payment.getPayable())));
                tips = 0.0;
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        HashMap<String, Object> map = new HashMap<>();

        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals(Utilities.PaymentMode.card)) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
                llTipContainer.setVisibility(View.VISIBLE);
                tvChange.setVisibility(View.GONE);
                isInvoiceCashToCard = true;
            } else if (data.getStringExtra("payment_mode").equals(Utilities.PaymentMode.cash)) {
                RIDE_REQUEST.put("card_id", null);
                RIDE_REQUEST.put("card_last_four", null);
                llTipContainer.setVisibility(View.GONE);
                tvChange.setVisibility(View.VISIBLE);
                isInvoiceCashToCard = false;
            }

            // initPayment(paymentMode);
            initPaymentView(data.getStringExtra("payment_mode"),
                    data.getStringExtra("card_last_four"), true);

            showLoading();

            map.put("request_id", DATUM.getId());
            map.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals(Utilities.PaymentMode.card))
                map.put("card_id", data.getStringExtra("card_id"));

            presenter.updateRide(map);

        }
    }

//    @Override
//    public void onSaveInstanceState(@NonNull Bundle outState) {
//
//    }

    void initPaymentView(String payment_mode, String value, boolean payment) {

        switch (payment_mode) {
            case "CASH":
                paymentMode.setText(payment_mode);
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_money, 0, 0, 0);
                break;
            case "CARD":
                if (payment) {
                    if (!value.equals("")) {
                        paymentMode.setText(getString(R.string.card_, value));
                    }
                } else {
                    paymentMode.setText(payment_mode);
                }
                // paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_visa, 0, 0, 0);
                break;
            case "PAYPAL":
                paymentMode.setText(getString(R.string.paypal));
                //  paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_paypal, 0, 0, 0);
                break;
            case "WALLET":
                paymentMode.setText(getString(R.string.wallet));
                // paymentMode.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_wallet, 0, 0, 0);
                break;
            default:
                break;
        }
    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
