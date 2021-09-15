package com.cluster.taxiuser.ui.fragment.service;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseFragment;
import com.cluster.taxiuser.common.EqualSpacingItemDecoration;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.APIClient;
import com.cluster.taxiuser.data.network.model.EstimateFare;
import com.cluster.taxiuser.data.network.model.Provider;
import com.cluster.taxiuser.data.network.model.Service;
import com.cluster.taxiuser.ui.activity.main.MainActivity;
import com.cluster.taxiuser.ui.activity.payment.PaymentActivity;
import com.cluster.taxiuser.ui.adapter.ServiceAdapter;
import com.cluster.taxiuser.ui.fragment.LoadSelectFragment;
import com.cluster.taxiuser.ui.fragment.book_ride.BookRideFragment;
import com.cluster.taxiuser.ui.fragment.schedule.ScheduleFragment;

import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.cluster.taxiuser.base.BaseActivity.RIDE_REQUEST;
import static com.cluster.taxiuser.ui.activity.payment.PaymentActivity.PICK_PAYMENT_METHOD;

public class ServiceFragment extends BaseFragment implements ServiceIView {

    private ServicePresenter<ServiceFragment> presenter = new ServicePresenter<>();

    @BindView(R.id.service_rv)
    RecyclerView serviceRv;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.payment_type)
    TextView paymentType;
    @BindView(R.id.error_layout)
    TextView errorLayout;
    Unbinder unbinder;
    ServiceAdapter adapter;
    List<Service> mServices = new ArrayList<>();
    @BindView(R.id.use_wallet)
    CheckBox useWallet;
    @BindView(R.id.wallet_balance)
    TextView walletBalance;
    @BindView(R.id.surge_value)
    TextView surgeValue;
    @BindView(R.id.tv_demand)
    TextView tvDemand;

    private NumberFormat numberFormat;
    private boolean isFromAdapter = true;
    private int servicePos = 0;
    private EstimateFare mEstimateFare;
    private int walletAmount;
    private int surge;

    public ServiceFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        presenter.attachView(this);
        numberFormat = getNewNumberFormat();
        presenter.services();
        return view;
    }

    @OnClick({R.id.payment_type, R.id.get_pricing, R.id.schedule_ride, R.id.ride_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.payment_type:
                ((MainActivity) Objects.requireNonNull(getActivity())).updatePaymentEntities();
                startActivityForResult(new Intent(getActivity(), PaymentActivity.class), PICK_PAYMENT_METHOD);
                break;
            case R.id.get_pricing:
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        RIDE_REQUEST.put("service_type", service.getId());
                        if (RIDE_REQUEST.containsKey("service_type") && RIDE_REQUEST.get("service_type") != null) {
                            showLoading();
                            estimatedApiCall();
                        }
                    }
                }
                break;
            case R.id.schedule_ride:
                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(new ScheduleFragment());
                break;
            case R.id.ride_now:
                sendRequest();
                break;
            default:
                break;
        }
    }

    private void estimatedApiCall() {
        Call<EstimateFare> call = APIClient.getAPIClient().estimateFare(RIDE_REQUEST);
        Log.d("111111111111", "Estimate Request::::" + RIDE_REQUEST.toString());
        call.enqueue(new Callback<EstimateFare>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EstimateFare> call,
                                   @NonNull Response<EstimateFare> response) {
                hideLoading();
                if (response.body() != null) {
                    EstimateFare estimateFare = response.body();

                    mEstimateFare = estimateFare;
                    surge = estimateFare.getSurge();
                    walletAmount = Integer.valueOf(estimateFare.getWalletBalance().intValue());
                    if (estimateFare.getWalletBalance() != null) {
                        SharedHelper.putKey(getContext(), "wallet", walletAmount);
                    }
                    if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
                    else {
                        walletBalance.setVisibility(View.VISIBLE);
                        walletBalance.setText(
                                SharedHelper.getKey(getContext(), "currency") + " "
                                        + numberFormat.format(Double.parseDouble(String.valueOf(walletAmount))));
                    }
                    if (surge == 0) {
                        surgeValue.setVisibility(View.GONE);
                        tvDemand.setVisibility(View.GONE);
                    } else {
                        surgeValue.setVisibility(View.VISIBLE);
                        surgeValue.setText(estimateFare.getSurgeValue());
                        tvDemand.setVisibility(View.VISIBLE);
                    }

                    Log.d("111111111111", "Estimate Response::::" + isFromAdapter);

                    if (isFromAdapter) {
                        mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());
                        RIDE_REQUEST.put("distance", estimateFare.getDistance());
                        adapter.setEstimateFare(mEstimateFare);
                        adapter.notifyDataSetChanged();
                        if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                        else errorLayout.setVisibility(View.GONE);
                    } else {
                        if (adapter != null) {
                            isFromAdapter = false;
                            Service service = adapter.getSelectedService();
                            if (service != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("service_name", service.getName());
                                bundle.putSerializable("mService", service);
                                bundle.putSerializable("estimate_fare", estimateFare);
                                bundle.putDouble("use_wallet", walletAmount);
                                BookRideFragment bookRideFragment = new BookRideFragment();
                                bookRideFragment.setArguments(bundle);
                                ((MainActivity) Objects.requireNonNull(getActivity())).changeFragment(bookRideFragment);
                            }
                        }
                    }
                } else if (response.raw().code() == 500) {
                    try {
                        JSONObject object = new JSONObject(response.errorBody().string());
                        if (object.has("error"))
                            Toast.makeText(activity(), object.optString("error"), Toast.LENGTH_SHORT).show();

                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<EstimateFare> call, @NonNull Throwable t) {
                hideLoading();
                onErrorBase(t);
                System.out.println("call = [" + call + "], t = [" + t + "]");
            }
        });
    }

    @Override
    public void onSuccess(List<Service> services) {
        hideLoading();
        if (services != null && !services.isEmpty()) {
            RIDE_REQUEST.put("service_type", 1);
            // estimatedApiCall();
            mServices.clear();
            mServices.addAll(services);
            adapter = new ServiceAdapter(getActivity(), mServices, mListener, capacity, mEstimateFare);
            serviceRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            serviceRv.setItemAnimator(new DefaultItemAnimator());
            serviceRv.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            serviceRv.setAdapter(adapter);

            if (adapter != null) {
                Service mService = adapter.getSelectedService();
                if (mService != null) RIDE_REQUEST.put("service_type", mService.getId());
            }
            mListener.whenClicked(0);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onSuccess(EstimateFare estimateFare) {
        hideLoading();
        if (estimateFare != null) {
            mEstimateFare = estimateFare;
            double walletAmount = estimateFare.getWalletBalance();
            SharedHelper.putKey(getContext(), "wallet",
                    String.valueOf(estimateFare.getWalletBalance()));
            if (walletAmount == 0) walletBalance.setVisibility(View.GONE);
            else {
                walletBalance.setVisibility(View.VISIBLE);
                walletBalance.setText(
                        SharedHelper.getKey(getContext(), "currency") + " "
                                + numberFormat.format(Double.parseDouble(String.valueOf(walletAmount))));
            }
            if (estimateFare.getSurge() == 0) {
                surgeValue.setVisibility(View.GONE);
                tvDemand.setVisibility(View.GONE);
            } else {
                surgeValue.setVisibility(View.VISIBLE);
                surgeValue.setText(estimateFare.getSurgeValue());
                tvDemand.setVisibility(View.VISIBLE);
            }

            Log.d("111111111111", "Estimate Response 222222222::::" + isFromAdapter);

            if (isFromAdapter) {
                mServices.get(servicePos).setEstimatedTime(estimateFare.getTime());
                RIDE_REQUEST.put("distance", estimateFare.getDistance());
                adapter.notifyDataSetChanged();
                if (mServices.isEmpty()) errorLayout.setVisibility(View.VISIBLE);
                else errorLayout.setVisibility(View.GONE);
            } else {
                if (adapter != null) {
                    isFromAdapter = false;
                    Service service = adapter.getSelectedService();
                    if (service != null) {
                        Bundle bundle = new Bundle();
                        bundle.putString("service_name", service.getName());
                        bundle.putSerializable("mService", service);
                        bundle.putSerializable("estimate_fare", estimateFare);
                        bundle.putDouble("use_wallet", walletAmount);
                        BookRideFragment bookRideFragment = new BookRideFragment();
                        bookRideFragment.setArguments(bundle);
                        ((MainActivity) getActivity()).changeFragment(bookRideFragment);
                    }
                }
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_PAYMENT_METHOD && resultCode == Activity.RESULT_OK) {
            RIDE_REQUEST.put("payment_mode", data.getStringExtra("payment_mode"));
            if (data.getStringExtra("payment_mode").equals("CARD")) {
                RIDE_REQUEST.put("card_id", data.getStringExtra("card_id"));
                RIDE_REQUEST.put("card_last_four", data.getStringExtra("card_last_four"));
            }
            initPayment(paymentType);
        }
    }

    private ServiceListener mListener = pos -> {

        Log.d("1111111111111", "Service Loads Select::::" + mServices.get(pos).getType() + ":::" + pos);

        isFromAdapter = true;
        servicePos = pos;
        RIDE_REQUEST.put("service_type", mServices.get(pos).getId());
        showLoading();
        estimatedApiCall();
        List<Provider> providers = new ArrayList<>();
        for (Provider provider : SharedHelper.getProviders(Objects.requireNonNull(getActivity())))
            if (provider.getProviderService().getServiceTypeId().equals(mServices.get(pos).getId()))
                providers.add(provider);

        ((MainActivity) getActivity()).setSpecificProviders(providers);

    };

    public interface ServiceListener {
        void whenClicked(int pos);
    }

    private void sendRequest() {
        HashMap<String, Object> map = new HashMap<>(RIDE_REQUEST);
        map.put("use_wallet", useWallet.isChecked() ? 1 : 0);
        showLoading();
        presenter.rideNow(map);
    }

    @Override
    public void onSuccess(@NonNull Object object) {
        hideLoading();
        activity().sendBroadcast(new Intent("INTENT_FILTER"));
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        initPayment(paymentType);
    }
}
