package com.cluster.taxiuser.ui.fragment.service;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseFragment;
import com.cluster.taxiuser.common.Constants;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.Service;

import java.text.NumberFormat;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class RateCardFragment extends BaseFragment {

    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.capacity)
    TextView capacity;
    @BindView(R.id.base_fare)
    TextView baseFare;
    @BindView(R.id.fare_type)
    TextView fareType;
    @BindView(R.id.fare_km)
    TextView fareKm;
    @BindView(R.id.tvFareDistance)
    TextView tvFareDistance;
    @BindView(R.id.done)
    Button done;
    Unbinder unbinder;

    NumberFormat numberFormat = getNewNumberFormat();

    public static Service SERVICE = new Service();

    public RateCardFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_rate_card;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        initView(SERVICE);
        return view;
    }

    @SuppressLint("SetTextI18n")
    void initView(@NonNull Service service) {
        capacity.setText(String.valueOf(service.getCapacity()));
        baseFare.setText(SharedHelper.getKey(Objects.requireNonNull(getContext()), "currency") + " " +
                numberFormat.format(service.getFixed()));
        fareKm.setText(SharedHelper.getKey(getContext(), "currency") + " " +
                numberFormat.format(service.getPrice()));
        fareType.setText(service.getCalculator().toLowerCase());

        if (SharedHelper.getKey(getContext(), "measurementType").equalsIgnoreCase(Constants.MeasurementType.KM))
            tvFareDistance.setText(getString(R.string.fare_km));
        else tvFareDistance.setText(getString(R.string.fare_miles));

        YoYo.with(Techniques.BounceInRight)
                .duration(1000)
                .pivot(YoYo.CENTER_PIVOT, YoYo.CENTER_PIVOT)
                .interpolate(new AccelerateDecelerateInterpolator())
                .playOn(image);
        Glide.with(activity()).load(service.getImage()).into(image);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.done)
    public void onViewClicked() {
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
