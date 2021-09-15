package com.cluster.taxiuser.ui.fragment.service_flow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.maps.model.LatLng;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseFragment;
import com.cluster.taxiuser.chat.ChatActivity;
import com.cluster.taxiuser.common.CancelRequestInterface;
import com.cluster.taxiuser.common.fcm.MyFireBaseMessagingService;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.DataResponse;
import com.cluster.taxiuser.data.network.model.Datum;
import com.cluster.taxiuser.data.network.model.Provider;
import com.cluster.taxiuser.data.network.model.ProviderService;
import com.cluster.taxiuser.data.network.model.ServiceType;
import com.cluster.taxiuser.ui.activity.main.MainActivity;
import com.cluster.taxiuser.ui.fragment.cancel_ride.CancelRideDialogFragment;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.cluster.taxiuser.MvpApplication.PERMISSIONS_REQUEST_PHONE;
import static com.cluster.taxiuser.base.BaseActivity.DATUM;
import static com.cluster.taxiuser.base.BaseActivity.RIDE_REQUEST;

public class ServiceFlowFragment extends BaseFragment
        implements ServiceFlowIView, CancelRequestInterface, DirectionCallback {

    Unbinder unbinder;

    @BindView(R.id.sos)
    TextView sos;
    @BindView(R.id.otp)
    TextView otp;
    @BindView(R.id.avatar)
    CircleImageView avatar;
    @BindView(R.id.first_name)
    TextView firstName;
    @BindView(R.id.status)
    TextView status;
    @BindView(R.id.rating)
    RatingBar rating;
    @BindView(R.id.cancel)
    Button cancel;
    @BindView(R.id.share_ride)
    Button sharedRide;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.service_type_name)
    TextView serviceTypeName;
    @BindView(R.id.service_number)
    TextView serviceNumber;
    @BindView(R.id.service_model)
    TextView serviceModel;
    @BindView(R.id.call)
    Button call;
    @BindView(R.id.chat)
    FloatingActionButton chat;
    @BindView(R.id.provider_eta)
    TextView providerEta;
    private Runnable runnable;
    private Handler handler;
    private int delay = 5000;

    private String providerPhoneNumber = null;
    private String shareRideText = "";
    private LatLng providerLatLng;
    private ServiceFlowPresenter<ServiceFlowFragment> presenter = new ServiceFlowPresenter<>();
    private CancelRequestInterface callback;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Log.d("latitude", "" + intent.getDoubleExtra("latitude", 0));
                Log.d("longitude", "" + intent.getDoubleExtra("longitude", 0));
                providerLatLng = new LatLng(intent.getDoubleExtra("latitude", 0), intent.getDoubleExtra("longitude", 0));
                ((MainActivity) context).addCar(providerLatLng);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public ServiceFlowFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_service_flow;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);
        activity().registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_PROVIDER));
        callback = this;
        presenter.attachView(this);

        if (DATUM != null) initView(DATUM);
        return view;
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        if (myReceiver != null) {
            try {
                activity().unregisterReceiver(myReceiver);
                myReceiver = null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }

        }
        super.onDestroyView();
    }

    @OnClick({R.id.sos, R.id.cancel, R.id.share_ride, R.id.call, R.id.chat})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.sos:
                sos();
                break;
            case R.id.cancel:
                CancelRideDialogFragment cancelRideDialogFragment = new CancelRideDialogFragment(callback);
                cancelRideDialogFragment.show(activity().getSupportFragmentManager(), cancelRideDialogFragment.getTag());
                break;
            case R.id.share_ride:
                sharedRide();
                break;
            case R.id.call:
                callPhoneNumber(providerPhoneNumber);
                break;
            case R.id.chat:
                if (DATUM != null) {
                    Intent i = new Intent(activity(), ChatActivity.class);
                    i.putExtra("request_id", String.valueOf(DATUM.getId()));
                    startActivity(i);
                }
                break;
        }
    }

    @SuppressLint({"StringFormatInvalid", "RestrictedApi"})
    private void initView(Datum datum) {
        Provider provider = datum.getProvider();
        if (provider != null) {
            firstName.setText(String.format("%s %s", provider.getFirstName(), provider.getLastName()));
            rating.setRating(Float.parseFloat(provider.getRating()));
            Glide.with(activity())
                    .load(provider.getAvatar())
                    .apply(RequestOptions
                            .placeholderOf(R.drawable.ic_user_placeholder)
                            .dontAnimate()
                            .error(R.drawable.ic_user_placeholder))
                    .into(avatar);
            providerPhoneNumber = provider.getMobile();
        }

        ServiceType serviceType = datum.getServiceType();
        if (serviceType != null) {
            serviceTypeName.setText(serviceType.getName());
            Glide.with(activity())
                    .load(serviceType.getImage())
                    .apply(RequestOptions.placeholderOf(R.drawable.ic_car)
                            .dontAnimate()
                            .error(R.drawable.ic_car))
                    .into(image);
        }

        if("PICKEDUP".equalsIgnoreCase(datum.getStatus())) {
            chat.setVisibility(View.GONE);
        } else {
            chat.setVisibility(View.VISIBLE);
        }

        if("STARTED".equalsIgnoreCase(datum.getStatus())) {

            handler = new Handler();
            runnable = () -> {
                try {
                    Double lat = (Double) RIDE_REQUEST.get("s_latitude");
                    Double lng = (Double) RIDE_REQUEST.get("s_longitude");
                    GoogleDirection
                            .withServerKey(getString(R.string.google_map_key))
                            .from(new LatLng(lat, lng))
                            .to(new LatLng(DATUM.getProvider().getLatitude(), DATUM.getProvider().getLongitude()))
                            .transportMode(TransportMode.DRIVING)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    if (direction.isOK()) {
                                        Route route = direction.getRouteList().get(0);
                                        if (!route.getLegList().isEmpty()) {
                                            Leg leg = route.getLegList().get(0);
                                            providerEta.setVisibility(View.VISIBLE);
                                            String arrivalTime = String.valueOf(leg.getDuration().getText());
                                            if (arrivalTime.contains("hours"))
                                                arrivalTime = arrivalTime.replace("hours", "h\n");
                                            else if (arrivalTime.contains("hour"))
                                                arrivalTime = arrivalTime.replace("hour", "h\n");
                                            if (arrivalTime.contains("mins"))
                                                arrivalTime = arrivalTime.replace("mins", "min");
                                            providerEta.setText("ETA :" + " " + arrivalTime);
                                        }
                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Un used
                                }
                            });
                    handler.postDelayed(runnable, delay);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            };
            handler.postDelayed(runnable, delay);
        }


        ProviderService providerService = datum.getProviderService();
        if (providerService != null) {
            serviceNumber.setText(providerService.getServiceNumber());
            serviceModel.setText(providerService.getServiceModel());
        }

//        otp.setText(getString(R.string.otp_, datum.getOtp()));
        shareRideText = getString(R.string.app_name) + ": "
                + datum.getUser().getFirstName() + " " + datum.getUser().getLastName() + " is riding in "
                + datum.getServiceType().getName() + " would like to share his ride "
                + "http://maps.google.com/maps?q=loc:" + datum.getDLatitude() + "," + datum.getDLongitude();

        switch (datum.getStatus()) {
            case "STARTED":
                providerLatLng = new LatLng(provider.getLatitude(), provider.getLongitude());
                LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
                getDistance(providerLatLng, origin);
                status.setText(R.string.driver_accepted_your_request);
                break;
            case "ARRIVED":
                status.setText(R.string.driver_has_arrived_your_location);
                break;
            case "PICKEDUP":
                status.setText(R.string.you_are_on_ride);
                cancel.setVisibility(View.GONE);
                sharedRide.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }

        if("STARTED".equalsIgnoreCase(datum.getStatus())) {
            LatLng source = new LatLng(datum.getProvider().getLatitude(), datum.getProvider().getLongitude());
            LatLng destination = new LatLng(datum.getSLatitude(), datum.getSLongitude());
            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(source, destination);
        } else {
            LatLng origin = new LatLng(datum.getSLatitude(), datum.getSLongitude());
            LatLng destination = new LatLng(datum.getDLatitude(), datum.getDLongitude());
            ((MainActivity) Objects.requireNonNull(getActivity())).drawRoute(origin, destination);
        }

    }

    private void sos() {
        new AlertDialog.Builder(getContext())
                .setTitle(getContext().getResources().getString(R.string.sos_alert))
                .setMessage(R.string.are_sure_you_want_to_emergency_alert)
                .setCancelable(true)
                .setPositiveButton(getContext().getResources().getString(R.string.yes), (dialog, which) -> callPhoneNumber(SharedHelper.getKey(getContext(), "sosNumber")))
                .setNegativeButton(getContext().getResources().getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    private void callPhoneNumber(String mobileNumber) {
        if (mobileNumber != null && !mobileNumber.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + mobileNumber));
            startActivity(intent);

//            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber)));

//            if (ActivityCompat.checkSelfPermission(activity(), Manifest.permission.CALL_PHONE)
//                    == PackageManager.PERMISSION_GRANTED)
//                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mobileNumber)));
//            else ActivityCompat.requestPermissions(activity(),
//                    new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
        }
    }

    private void sharedRide() {
        try {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareRideText);
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        } catch (Exception e) {
            Toast.makeText(activity(), "applications not found!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        System.out.println("RRR ServiceFlowFragment checkStatusResponse = " + printJSON(dataResponse));
        if (!dataResponse.getData().isEmpty()) initView(dataResponse.getData().get(0));
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(activity(), "Permission Granted. Try Again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void cancelRequestMethod() {
    }

    public void getDistance(LatLng source, LatLng destination) {
        GoogleDirection.withServerKey(getString(R.string.google_map_key))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (isAdded()) {
            if (direction.isOK()) {
                Route route = direction.getRouteList().get(0);
                if (!route.getLegList().isEmpty()) {
                    Leg leg = route.getLegList().get(0);
                    //      TODO: Commented by Rajaganapathi... cos some time screens blinks
                    //      status.setText(getString(R.string.driver_accepted_your_request_, leg.getDuration().getText()));
                }
            } else
                Toast.makeText(activity(), direction.getErrorMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDirectionFailure(Throwable t) {

    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
