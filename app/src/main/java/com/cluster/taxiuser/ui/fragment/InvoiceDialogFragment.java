package com.cluster.taxiuser.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cluster.taxiuser.MvpApplication;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseBottomSheetDialogFragment;
import com.cluster.taxiuser.common.Constants;
import com.cluster.taxiuser.common.Utilities;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.Datum;
import com.cluster.taxiuser.data.network.model.Payment;
import com.cluster.taxiuser.data.network.model.ServiceType;

import java.text.NumberFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cluster.taxiuser.base.BaseActivity.DATUM;
import static com.cluster.taxiuser.data.SharedHelper.getKey;

public class InvoiceDialogFragment extends BaseBottomSheetDialogFragment {

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
    @BindView(R.id.close)
    Button close;
    @BindView(R.id.time_fare)
    TextView timeFare;
    @BindView(R.id.tips)
    TextView tips;
    @BindView(R.id.tips_layout)
    LinearLayout tipsLayout;
    private NumberFormat numberFormat = MvpApplication.getInstance().getNewNumberFormat();
    @BindView(R.id.distance_constainer)
    LinearLayout distanceConstainer;
    @BindView(R.id.time_container)
    LinearLayout timeContainer;
    @BindView(R.id.wallet_deduction)
    TextView walletDeduction;
    @BindView(R.id.discount)
    TextView discount;
    @BindView(R.id.walletLayout)
    LinearLayout walletLayout;
    @BindView(R.id.discountLayout)
    LinearLayout discountLayout;

    public InvoiceDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_invoice_dialog;
    }

    @SuppressLint("StringFormatInvalid")
    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);

        if (DATUM != null) {
            Datum datum = DATUM;
            bookingId.setText(datum.getBookingId());
            if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase
                    (Constants.MeasurementType.KM)) {
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

            Payment payment = datum.getPayment();
            if (payment != null) {
                fixed.setText(String.format("%s %s",
                        SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payment.getFixed())));
                tax.setText(String.format("%s %s",
                        SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(payment.getTax())));
                Double pastTripTotal = payment.getTotal() + payment.getTips();
                total.setText(String.format("%s %s",
                        SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(pastTripTotal)));
                Double payableValue = payment.getTotal() - (payment.getWallet()+ payment.getDiscount());
                Double pastTripPayable = payableValue + payment.getTips();
                payable.setText(String.format("%s %s",
                        SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                        numberFormat.format(pastTripPayable)));
                if(payment.getTips() == 0 || payment.getTips() == 0.0) {
                    tipsLayout.setVisibility(View.GONE);
                } else {
                    tipsLayout.setVisibility(View.VISIBLE);
                    tips.setText(String.format("%s %s",
                            SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                            payment.getTips()));
                }

                if (payment.getWallet() == 0 || payment.getWallet() == 0.0) {
                    walletLayout.setVisibility(View.GONE);
                } else  {
                    walletLayout.setVisibility(View.VISIBLE);
                    walletDeduction.setText(String.format("%s %s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            numberFormat.format(payment.getWallet())));
                }
                if (payment.getDiscount() == 0 || payment.getDiscount() == 0.0) {
                    discountLayout.setVisibility(View.GONE);
                } else  {
                    discountLayout.setVisibility(View.VISIBLE);
                    discount.setText(String.format("%s -%s",
                            getKey(Objects.requireNonNull(getContext()), "currency"),
                            numberFormat.format(payment.getDiscount())));
                }

                ServiceType serviceType = datum.getServiceType();
                if (serviceType != null) {
                    String serviceCalculator = serviceType.getCalculator();
                    switch (serviceCalculator) {
                        case Utilities.InvoiceFare.min:
                            distanceConstainer.setVisibility(View.GONE);
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    numberFormat.format(payment.getMinute())));
                            break;
                        case Utilities.InvoiceFare.hour:
                            distanceConstainer.setVisibility(View.GONE);
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    numberFormat.format(payment.getHour())));
                            break;
                        case Utilities.InvoiceFare.distance:
                            distanceConstainer.setVisibility(View.VISIBLE);
                            timeContainer.setVisibility(View.GONE);
                            distanceFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    numberFormat.format(payment.getDistance())));
                            break;
                        case Utilities.InvoiceFare.distanceMin:
                            distanceConstainer.setVisibility(View.VISIBLE);
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    numberFormat.format(payment.getMinute())));
                            distanceFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    numberFormat.format(payment.getDistance())));
                            break;
                        case Utilities.InvoiceFare.distanceHour:
                            distanceConstainer.setVisibility(View.VISIBLE);
                            timeContainer.setVisibility(View.VISIBLE);
                            timeFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    numberFormat.format(payment.getHour())));
                            distanceFare.setText(String.format("%s %s",
                                    SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency"),
                                    numberFormat.format(payment.getDistance())));
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    @OnClick(R.id.close)
    public void onViewClicked() {
        dismiss();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
