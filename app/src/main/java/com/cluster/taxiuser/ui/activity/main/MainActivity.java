package com.cluster.taxiuser.ui.activity.main;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.cluster.taxiuser.ui.fragment.LoadSelectFragment;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

//import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.PlaceLikelihoodBufferResponse;
//import com.google.android.gms.location.places.Places;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.cluster.taxiuser.BuildConfig;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.common.Constants;
import com.cluster.taxiuser.common.InfoWindowData;
import com.cluster.taxiuser.common.LocaleHelper;
import com.cluster.taxiuser.common.Utilities;
import com.cluster.taxiuser.common.fcm.MyFireBaseMessagingService;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.AddressResponse;
import com.cluster.taxiuser.data.network.model.DataResponse;
import com.cluster.taxiuser.data.network.model.Provider;
import com.cluster.taxiuser.data.network.model.User;
import com.cluster.taxiuser.ui.activity.awards.AwardsActivity;
import com.cluster.taxiuser.ui.activity.coupon.CouponActivity;
import com.cluster.taxiuser.ui.activity.help.HelpActivity;
import com.cluster.taxiuser.ui.activity.location_pick.LocationPickActivity;
import com.cluster.taxiuser.ui.activity.passbook.WalletHistoryActivity;
import com.cluster.taxiuser.ui.activity.payment.PaymentActivity;
import com.cluster.taxiuser.ui.activity.profile.ProfileActivity;
import com.cluster.taxiuser.ui.activity.setting.SettingsActivity;
import com.cluster.taxiuser.ui.activity.wallet.WalletActivity;
import com.cluster.taxiuser.ui.activity.your_trips.YourTripActivity;
import com.cluster.taxiuser.ui.fragment.book_ride.BookRideFragment;
import com.cluster.taxiuser.ui.fragment.invoice.InvoiceFragment;
import com.cluster.taxiuser.ui.fragment.rate.RatingDialogFragment;
import com.cluster.taxiuser.ui.fragment.schedule.ScheduleFragment;
import com.cluster.taxiuser.ui.fragment.searching.SearchingFragment;
import com.cluster.taxiuser.ui.fragment.service.RateCardFragment;
import com.cluster.taxiuser.ui.fragment.service.ServiceFragment;
import com.cluster.taxiuser.ui.fragment.service_flow.ServiceFlowFragment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.HttpException;
import retrofit2.Response;
//import com.google.android.libraries.places.api.Places;

import static com.cluster.taxiuser.MvpApplication.DEFAULT_ZOOM;
import static com.cluster.taxiuser.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.cluster.taxiuser.MvpApplication.PICK_LOCATION_REQUEST_CODE;
import static com.cluster.taxiuser.MvpApplication.mLastKnownLocation;

//      TODO: Payment Gateway
//import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
//import com.braintreepayments.api.models.PayPalAccountNonce;
//import com.braintreepayments.api.models.PaymentMethodNonce;

public class MainActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener,
        OnMapReadyCallback, GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener, DirectionCallback,
        MainIView,
//        PaymentMethodNonceCreatedListener,
        LocationListener {

    @BindView(R.id.container)
    FrameLayout container;
    @BindView(R.id.menu)
    ImageView menu;
    @BindView(R.id.ivBack)
    ImageView ivBack;
    @BindView(R.id.gps)
    ImageView gps;
    @BindView(R.id.source)
    TextView sourceTxt;
    @BindView(R.id.destination)
    TextView destinationTxt;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.top_layout)
    LinearLayout topLayout;
    @BindView(R.id.pick_location_layout)
    LinearLayout pickLocationLayout;

    @BindView(R.id.llPickHomeAdd)
    LinearLayout llPickHomeAdd;
    @BindView(R.id.llPickWorkAdd)
    LinearLayout llPickWorkAdd;
    private InfoWindowData destinationLeg;

    public static String currentStatus = "EMPTY";
    private boolean doubleBackToExitPressedOnce = false;

    private LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private FusedLocationProviderClient mFusedLocation;

    private BottomSheetBehavior bottomSheetBehavior;
    private MainPresenter<MainActivity> mainPresenter = new MainPresenter<>();

    private CircleImageView picture;
    private TextView name;
    private String STATUS = "";
    private TextView sub_name;
    private boolean initialProcess = true;
    private LatLng oldPosition = null, newPosition = null;
    private Marker marker;
    private HashMap<Integer, Marker> providersMarker = new HashMap<>();
//    private PlaceDetectionClient mPlaceDetectionClient;
    private PlacesClient placesClient;
    private DataResponse checkStatusResponse = new DataResponse();

    private Runnable r;
    private Handler h;
    private int delay = 5000;
    private com.cluster.taxiuser.data.network.model.Address home = null, work = null;
    private String language = Constants.Language.SPANISH;

    List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS);

    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mainPresenter.checkStatus();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initView() {
        if (Build.VERSION.SDK_INT >= 21)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        ButterKnife.bind(this);

        Places.initialize(this, getString(R.string.google_map_key));
        placesClient = Places.createClient(this);
//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        registerReceiver(myReceiver, new IntentFilter(MyFireBaseMessagingService.INTENT_FILTER));

        mainPresenter.attachView(this);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        picture = headerView.findViewById(R.id.picture);
        name = headerView.findViewById(R.id.name);
        sub_name = headerView.findViewById(R.id.sub_name);
        headerView.setOnClickListener(v -> {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, picture, ViewCompat.getTransitionName(picture));
            startActivity(new Intent(MainActivity.this, ProfileActivity.class), options.toBundle());
        });

        mFusedLocation = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        bottomSheetBehavior = BottomSheetBehavior.from(container);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, @BottomSheetBehavior.State int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        BottomSheetBehavior.from(container).setHideable(true);
                        break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

//        mainPresenter.changeLanguage(language);

        h = new Handler();
        r = () -> {
            mainPresenter.checkStatus();
            h.postDelayed(r, delay);
        };
        h.postDelayed(r, delay);
    }

    @Override
    public void onResume() {
        super.onResume();
        mainPresenter.profile();
        mainPresenter.address();
        mainPresenter.checkStatus();
    }

    @Override
    protected void onDestroy() {
        mainPresenter.onDetach();
        unregisterReceiver(myReceiver);
        h.removeCallbacks(r);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
                if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                    mainPresenter.checkStatus();
                    changeFlow("EMPTY");
                }
            } else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            else {
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, getString(R.string.please_click_back_again_to_exit), Toast.LENGTH_SHORT).show();
            }
        }

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_payment:
                startActivity(new Intent(this, PaymentActivity.class));
                break;
            case R.id.nav_your_trips:
                startActivity(new Intent(this, YourTripActivity.class));
                break;
            case R.id.nav_coupon:
                startActivity(new Intent(this, CouponActivity.class));
                break;
            case R.id.nav_awards:
                startActivity(new Intent(this, AwardsActivity.class));
                break;
            case R.id.nav_wallet:
                startActivity(new Intent(this, WalletActivity.class));
                break;
            case R.id.nav_passbook:
                startActivity(new Intent(this, WalletHistoryActivity.class));
                break;
            case R.id.nav_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_help:
                startActivity(new Intent(this, HelpActivity.class));
                break;
            case R.id.nav_share:
                shareApp();
                break;
            case R.id.nav_become_driver:
                alertBecomeDriver();
                break;
            case R.id.nav_logout:
               // alertLogout();
                ShowLogoutPopUp();
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    public void ShowLogoutPopUp() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder
                .setMessage(getString(R.string.are_sure_you_want_to_logout))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.yes), (dialog, id) -> {
                    mainPresenter.logout(SharedHelper.getKey(this,"user_id"));
                }).setNegativeButton(getString(R.string.no), (dialog, id) -> {
            dialog.cancel();
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void alertBecomeDriver() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.libi.provider"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Override
    public void onCameraIdle() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        if (STATUS.equals("SERVICE") || STATUS.equals("EMPTY")) try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            HashMap<String, Object> map = new HashMap<>();
            map.put("latitude", cameraPosition.target.latitude);
            map.put("longitude", cameraPosition.target.longitude);
            mainPresenter.providers(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.menu, R.id.gps, R.id.source, R.id.destination, R.id.ivBack, R.id.llPickHomeAdd, R.id.llPickWorkAdd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.menu:
                if (drawerLayout.isDrawerOpen(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else {
                    User user = new Gson().fromJson(SharedHelper.getKey(this, "userInfo"), User.class);
                    if (user != null) {
                        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
                        sub_name.setText(user.getEmail());
                        SharedHelper.putKey(activity(), "picture", user.getPicture());
                        Glide.with(activity())
                                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                                        .dontAnimate()
                                        .error(R.drawable.ic_user_placeholder))
                                .into(picture);
                    }
                    drawerLayout.openDrawer(Gravity.START);
                }

                break;
            case R.id.ivBack:
                onBackPressed();
                break;
            case R.id.gps:
                if (mLastKnownLocation != null) {
                    LatLng currentLatLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                    mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM));
                }
                break;
            case R.id.source:
                Intent sourceIntent = new Intent(this, LocationPickActivity.class);
                sourceIntent.putExtra("srcClick", "isSource");
                sourceIntent.putExtra("isSetting", "source");
                sourceIntent.putExtra("destination", destinationTxt.getText().toString());
                sourceIntent.putExtra("fieldClicked", "pickupAddress");
                startActivityForResult(sourceIntent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.destination:
                Intent intent = new Intent(this, LocationPickActivity.class);
                intent.putExtra("destClick", "isDest");
                intent.putExtra("isSetting", "destination");
                intent.putExtra("destination", destinationTxt.getText().toString());
                intent.putExtra("fieldClicked", "dropAddress");
                startActivityForResult(intent, PICK_LOCATION_REQUEST_CODE);
                break;
            case R.id.llPickHomeAdd:
                updateSavedAddress(home);
                break;
            case R.id.llPickWorkAdd:
                updateSavedAddress(work);
                break;
        }
    }

    private void updateSavedAddress(com.cluster.taxiuser.data.network.model.Address address) {
        RIDE_REQUEST.put("d_address", address.getAddress());
        RIDE_REQUEST.put("d_latitude", address.getLatitude());
        RIDE_REQUEST.put("d_longitude", address.getLongitude());
        destinationTxt.setText(String.valueOf(RIDE_REQUEST.get("d_address")));

        if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
            LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
            LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
            drawRoute(origin, destination);
            currentStatus = "SERVICE";
            changeFlow(currentStatus);
        }
    }

    @Override
    public void onCameraMove() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.mGoogleMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
       // showLoading();
        showCurrentPlace();
    }

    @Override
    public void onSuccess(DataResponse dataResponse) {
        this.checkStatusResponse = dataResponse;
        updatePaymentEntities();
        SharedHelper.putKey(this, "sosNumber", dataResponse.getSos());

        try {
            if (!dataResponse.getData().isEmpty()) {
                System.out.println("RRR MainActivity currentStatus = " + dataResponse.getData().get(0).getStatus());
                System.out.println("RRR MainActivity isPaid = " + dataResponse.getData().get(0).getPaid());
            }

            if (dataResponse.getData() != null && !dataResponse.getData().isEmpty() &&
                    dataResponse.getData().get(0).getProvider() != null) {
                DATUM = dataResponse.getData().get(0);
                provider = DATUM.getProvider();
                provider.setLatitude(provider.getLatitude());
                provider.setLongitude(provider.getLongitude());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!dataResponse.getData().isEmpty()) {
            if (!currentStatus.equals(dataResponse.getData().get(0).getStatus())) {
                DATUM = dataResponse.getData().get(0);
                currentStatus = DATUM.getStatus();
                changeFlow(currentStatus);
                pickLocationLayout.setVisibility(View.GONE);
            }
        } else if (currentStatus.equals("SERVICE")) {
            //      Do nothing
        } else {
            currentStatus = "EMPTY";
            changeFlow(currentStatus);
            pickLocationLayout.setVisibility(View.VISIBLE);
        }
        if (currentStatus.equals("ARRIVED")
                || currentStatus.equals("PICKEDUP")
                || currentStatus.equals("DROPPED"))
            removeAllMarkerAddDriverMarker(DATUM.getProvider());

        if (currentStatus.equals("STARTED")) updateDriverNavigation(DATUM.getProvider());
    }

    private void updateDriverNavigation(Provider provider) {
        addCar(new LatLng(provider.getLatitude(), provider.getLongitude()));
    }

    public void changeFlow(String status) {
        STATUS = status;
        llPickHomeAdd.setVisibility(View.INVISIBLE);
        llPickWorkAdd.setVisibility(View.INVISIBLE);
        dismissDialog("SEARCHING");
        dismissDialog("INVOICE");
        dismissDialog("RATING");
        System.out.println("From status: " + status);
        RatingDialogFragment ratingDialogFragment = new RatingDialogFragment();
        switch (status) {
            case "EMPTY":
                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                mGoogleMap.clear();
                providersMarker.clear();
                RIDE_REQUEST.remove("s_address");
                RIDE_REQUEST.remove("s_latitude");
                RIDE_REQUEST.remove("s_longitude");
                RIDE_REQUEST.remove("d_address");
                RIDE_REQUEST.remove("d_latitude");
                RIDE_REQUEST.remove("d_longitude");
                showCurrentPlace();
                addDriverMarkers(SharedHelper.getProviders(this));
                destinationTxt.setText(getString(R.string.where_to));
                changeFragment(null);
                if (home != null) llPickHomeAdd.setVisibility(View.VISIBLE);
                else llPickHomeAdd.setVisibility(View.INVISIBLE);
                if (work != null) llPickWorkAdd.setVisibility(View.VISIBLE);
                else llPickWorkAdd.setVisibility(View.INVISIBLE);
                break;
            case "SERVICE":
               // canCallCurrentLocation = false;
                ivBack.setVisibility(View.VISIBLE);
                menu.setVisibility(View.GONE);
                updatePaymentEntities();
                changeFragment(new ServiceFragment());
                break;
            case "SEARCHING":
                updatePaymentEntities();
                SearchingFragment searchingFragment = new SearchingFragment();
                searchingFragment.show(getSupportFragmentManager(), "SEARCHING");
                break;
            case "STARTED":
                ivBack.setVisibility(View.GONE);
                menu.setVisibility(View.VISIBLE);
                if (DATUM != null) {
                    initialProcess = true;
                    FirebaseMessaging.getInstance().subscribeToTopic(String.valueOf(DATUM.getId()));
                }
                changeFragment(new ServiceFlowFragment());
                break;
            case "ARRIVED":
                changeFragment(new ServiceFlowFragment());
                break;
            case "PICKEDUP":
                changeFragment(new ServiceFlowFragment());
                break;
            case "DROPPED":
            case "COMPLETED":
                try {
                    if (DATUM.getPayment().getPayable() > 0)
                        changeFragment(InvoiceFragment.newInstance());
                    else if (DATUM.getPaid() == 1)
                        if (!ratingDialogFragment.isVisible()) changeFlow("RATING");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case "RATING":
                changeFragment(null);
                if (DATUM != null)
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
                ratingDialogFragment.show(getSupportFragmentManager(), "RATING");
                RIDE_REQUEST.clear();
                mGoogleMap.clear();
                pickLocationLayout.setVisibility(View.VISIBLE);
                sourceTxt.setText("");
                sourceTxt.setHint(getString(R.string.fetching_current_location));
                destinationTxt.setText("");
                break;
            default:
                break;
        }
    }

    public void changeFragment(Fragment fragment) {
        if (isFinishing()) return;

        if (fragment != null) {
            if (fragment instanceof BookRideFragment || fragment instanceof ServiceFragment ||
                    fragment instanceof ServiceFlowFragment || fragment instanceof RateCardFragment || fragment instanceof LoadSelectFragment)
                container.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            else container.setBackgroundColor(getResources().getColor(R.color.white));

            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

            if (fragment instanceof RateCardFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ScheduleFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof ServiceFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof BookRideFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());
            else if (fragment instanceof LoadSelectFragment)
                fragmentTransaction.addToBackStack(fragment.getTag());

            try {
                fragmentTransaction.replace(R.id.container, fragment, fragment.getTag());
                fragmentTransaction.commitAllowingStateLoss();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        } else {
            for (Fragment fragmentd : getSupportFragmentManager().getFragments()) {
                if (fragmentd instanceof ServiceFlowFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
                if (fragmentd instanceof InvoiceFragment)
                    getSupportFragmentManager().beginTransaction().remove(fragmentd).commitAllowingStateLoss();
            }
            container.removeAllViews();
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    void dismissDialog(String tag) {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
        if (fragment instanceof SearchingFragment) {
            SearchingFragment df = (SearchingFragment) fragment;
            df.dismissAllowingStateLoss();
        }
        if (fragment instanceof RatingDialogFragment) {
            RatingDialogFragment df = (RatingDialogFragment) fragment;
            df.dismissAllowingStateLoss();
        }
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocation.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));

                        SharedHelper.putKey(activity(), "latitude", String.valueOf(mLastKnownLocation.getLatitude()));
                        SharedHelper.putKey(activity(), "longitude", String.valueOf(mLastKnownLocation.getLongitude()));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        mDefaultLocation = new LatLng(
                                Double.valueOf(SharedHelper.getKey(activity(), "latitude", "-33.8523341")),
                                Double.valueOf(SharedHelper.getKey(activity(), "longitude", "151.2106085"))
                        );
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getLocalizedMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.getUiSettings().setCompassEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                    showCurrentPlace();
                }
        }
    }

    public void drawRoute(LatLng source, LatLng destination) {
        GoogleDirection
                .withServerKey(getString(R.string.google_map_key))
                .from(source)
                .to(destination)
                .transportMode(TransportMode.DRIVING)
                .execute(this);
    }

    @Override
    public void onDirectionSuccess(Direction direction, String rawBody) {
        if (direction.isOK()) {
            initialProcess = true;
            mGoogleMap.clear();
            Route route = direction.getRouteList().get(0);
            if (!route.getLegList().isEmpty()) {

                Leg leg = route.getLegList().get(0);
                InfoWindowData originLeg = new InfoWindowData();
                originLeg.setAddress(leg.getStartAddress());
                originLeg.setArrival_time(null);
                originLeg.setDistance(leg.getDistance().getText());

                destinationLeg = new InfoWindowData();
                destinationLeg.setAddress(leg.getEndAddress());
                destinationLeg.setArrival_time(leg.getDuration().getText());
                destinationLeg.setDistance(leg.getDistance().getText());

                LatLng origin = new LatLng(leg.getStartLocation().getLatitude(), leg.getStartLocation().getLongitude());
                LatLng destination = new LatLng(leg.getEndLocation().getLatitude(), leg.getEndLocation().getLongitude());
                if (currentStatus.equals("SERVICE")) {
                    mGoogleMap.addMarker(new MarkerOptions().position(origin)
                            .icon(BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView())));
                    if (specificProviders != null)
                        for (Provider provider : specificProviders) {
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .anchor(0.5f, 0.5f)
                                    .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                                    .rotation(0.0f)
                                    .snippet("" + provider.getId())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
                            providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
                        }
                } else {
                    Marker mark = mGoogleMap.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.src_icon))
                            .position(origin));
                    mark.setTag(originLeg);
                }
                mGoogleMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.des_icon))
                        .position(destination))
                        .setTag(destinationLeg);
            }

            ArrayList<LatLng> directionPositionList = route.getLegList().get(0).getDirectionPoint();
            mGoogleMap.addPolyline(DirectionConverter.createPolyline(this, directionPositionList, 3, getResources().getColor(R.color.colorAccent)));
            setCameraWithCoordinationBounds(route);

        } else {
            System.out.println("RRR onDirectionFailure = [");
            changeFlow("EMPTY");
            Toast.makeText(this, getString(R.string.root_not_available), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap getMarkerBitmapFromView() {

        //HERE YOU CAN ADD YOUR CUSTOM VIEW
        View mView = ((LayoutInflater) this.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE)).
                inflate(R.layout.map_custom_infowindow, null);

        //IN THIS EXAMPLE WE ARE TAKING TEXTVIEW BUT YOU CAN ALSO TAKE ANY KIND OF VIEW LIKE IMAGEVIEW, BUTTON ETC.
        TextView tvEtaVal = mView.findViewById(R.id.tvEstimatedFare);
        String arrivalTime = destinationLeg.getArrival_time();
        if (arrivalTime.contains("hours")) arrivalTime = arrivalTime.replace("hours", "h\n");
        else if (arrivalTime.contains("hour")) arrivalTime = arrivalTime.replace("hour", "h\n");
        if (arrivalTime.contains("mins")) arrivalTime = arrivalTime.replace("mins", "min");
        tvEtaVal.setText(arrivalTime);
        mView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        mView.layout(0, 0, mView.getMeasuredWidth(), mView.getMeasuredHeight());
        mView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(mView.getMeasuredWidth(),
                mView.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = mView.getBackground();
        if (drawable != null) drawable.draw(canvas);
        mView.draw(canvas);
        return returnedBitmap;
    }

    @Override
    public void onDirectionFailure(Throwable t) {
        System.out.println("RRR onDirectionFailure = [" + t.getMessage() + "]");
        Toast.makeText(this, t.getMessage(), Toast.LENGTH_SHORT).show();
    }

    private void setCameraWithCoordinationBounds(Route route) {
        LatLng southwest = route.getBound().getSouthwestCoordination().getCoordination();
        LatLng northeast = route.getBound().getNortheastCoordination().getCoordination();
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);
        try {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 250));
        } catch (Exception e) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 90));
        }
    }

    public void addCar(LatLng latLng) {
        final String[] eta = {""};

        if (isFinishing()) return;

        if (latLng != null && latLng.latitude != 0 && latLng.longitude != 0) {
            if (newPosition != null)
                oldPosition = newPosition;
            newPosition = latLng;

            GoogleDirection
                    .withServerKey(getString(R.string.google_map_key))
                    .from(latLng)
                    .to(new LatLng(DATUM.getProvider().getLatitude(), DATUM.getProvider().getLongitude()))
                    .transportMode(TransportMode.DRIVING)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                Route route = direction.getRouteList().get(0);
                                if (!route.getLegList().isEmpty()) {
                                    Leg leg = route.getLegList().get(0);
                                    eta[0] = leg.getDuration().getText();
                                }
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {

                        }
                    });
            if (initialProcess) {
                initialProcess = false;
                marker = mGoogleMap.addMarker(new MarkerOptions().position(latLng)
                        .anchor(0.5f, 0.75f)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2)));
            } else {
                animateMarker(oldPosition, newPosition, marker);
                marker.setRotation(bearingBetweenLocations(oldPosition, newPosition));
            }

            if (marker != null && !TextUtils.isEmpty(eta[0])) {
                marker.setTitle("ETA");
                marker.setSnippet(eta[0]);
                marker.showInfoWindow();
            } else marker.hideInfoWindow();
        }
    }

    @Override
    public void onSuccess(@NonNull User user) {
        String dd = LocaleHelper.getLanguage(this);
        String userLanguage = (user.getLanguage() == null) ? Constants.Language.SPANISH : user.getLanguage();
        if (!userLanguage.equalsIgnoreCase(dd)) {
            LocaleHelper.setLocale(getApplicationContext(), user.getLanguage());
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_NEW_TASK));
        }
        SharedHelper.putKey(this,"stripe_publishable_key",user.getStripePublishableKey());
        SharedHelper.putKey(this, "currency", user.getCurrency());
        SharedHelper.putKey(this, "measurementType", user.getMeasurement());
        SharedHelper.putKey(this, "walletBalance", String.valueOf(user.getWalletBalance()));
        SharedHelper.putKey(this, "userInfo", printJSON(user));
        name.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        sub_name.setText(user.getEmail());
        SharedHelper.putKey(activity(), "picture", user.getPicture());
        Glide.with(activity())
                .load(BuildConfig.BASE_IMAGE_URL + user.getPicture())
                .apply(RequestOptions.placeholderOf(R.drawable.ic_user_placeholder)
                        .dontAnimate()
                        .error(R.drawable.ic_user_placeholder))
                .into(picture);
    }

    private void removeAllMarkerAddDriverMarker(Provider provider) {
        if (providersMarker.size() == 1) {


            Marker marker = providersMarker.get(provider.getId());
//            providersMarker.clear();
            LatLng startPosition = marker.getPosition();
            LatLng newPos = new LatLng(provider.getLatitude(), provider.getLongitude());
            marker.setPosition(newPos);
            animateMarker(startPosition, newPos, marker);
            marker.setRotation(bearingBetweenLocations(startPosition, newPos));

//            MarkerOptions markerOptions = new MarkerOptions()
//                    .anchor(0.5f, 0.5f)
//                    .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
//                    .rotation(0.0f)
//                    .snippet("" + provider.getId())
//                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
//            providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
        } else {
            providersMarker.clear();
            MarkerOptions markerOptions = new MarkerOptions()
                    .anchor(0.5f, 0.5f)
                    .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                    .rotation(0.0f)
                    .snippet("" + provider.getId())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
            providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
        }
    }

    @Override
    public void onSuccessLogout(Object object) {
        Utilities.LogoutApp(activity(), "");
    }

    @Override
    public void onSuccess(AddressResponse response) {
        home = (response.getHome().isEmpty()) ? null : response.getHome().get(response.getHome().size() - 1);
        work = (response.getWork().isEmpty()) ? null : response.getWork().get(response.getWork().size() - 1);
        if (currentStatus.equalsIgnoreCase("EMPTY")) {
            if (home != null) llPickHomeAdd.setVisibility(View.VISIBLE);
            else llPickHomeAdd.setVisibility(View.INVISIBLE);
            if (work != null) llPickWorkAdd.setVisibility(View.VISIBLE);
            else llPickWorkAdd.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onSuccess(List<Provider> providerList) {
        System.out.println("RRR providerList = " + printJSON(providerList));
        SharedHelper.putProviders(this, printJSON(providerList));
        if (providerList != null)
            addDriverMarkers(providerList);
    }

    private void addDriverMarkers(List<Provider> providers) {
        if (providers != null) {
            for (Provider provider : providers)
                if (providersMarker.containsKey(provider.getId())) {
                    Marker marker = providersMarker.get(provider.getId());
                    LatLng startPosition = marker.getPosition();
                    LatLng newPos = new LatLng(provider.getLatitude(), provider.getLongitude());
                    marker.setPosition(newPos);
                    animateMarker(startPosition, newPos, marker);
                    marker.setRotation(bearingBetweenLocations(startPosition, newPos));
                } else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .anchor(0.5f, 0.5f)
                            .position(new LatLng(provider.getLatitude(), provider.getLongitude()))
                            .rotation(0.0f)
                            .snippet("" + provider.getId())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_2));
                    providersMarker.put(provider.getId(), mGoogleMap.addMarker(markerOptions));
                }
        }
    }

    private List<Provider> specificProviders;

    public void setSpecificProviders(List<Provider> specificProviders) {
        this.specificProviders = specificProviders;
        printJSON("RRR setSpecificProvidersMarker " + specificProviders);
        LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
        LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
        drawRoute(origin, destination);
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onCheckStatusError(Throwable e) {
        Log.d("Error", "My Error" + e.getLocalizedMessage());

        if (e instanceof HttpException) {
            Response response = ((HttpException) e).response();
            Log.e("onError", response.code() + "");
        }
    }

    @Override
    public void onLanguageChanged(Object object) {
        hideLoading();
        LocaleHelper.setLocale(getApplicationContext(), language);
    }

    //  private boolean canCallCurrentLocation = false;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_LOCATION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                   // canCallCurrentLocation = data.getBooleanExtra("canCallCurrentLocation", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (RIDE_REQUEST.containsKey("s_address"))
                    sourceTxt.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
                else sourceTxt.setText("");
                if (RIDE_REQUEST.containsKey("d_address"))
                    destinationTxt.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
                else destinationTxt.setText("");
                if (RIDE_REQUEST.containsKey("s_address") && RIDE_REQUEST.containsKey("d_address")) {
                    LatLng origin = new LatLng((Double) RIDE_REQUEST.get("s_latitude"), (Double) RIDE_REQUEST.get("s_longitude"));
                    LatLng destination = new LatLng((Double) RIDE_REQUEST.get("d_latitude"), (Double) RIDE_REQUEST.get("d_longitude"));
                    drawRoute(origin, destination);
                    currentStatus = "SERVICE";
                    changeFlow(currentStatus);
                } else changeFlow("EMPTY");
            }
        }
    }

    //      TODO: Payment Gateway

//    @Override
//    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {
//        String nonce = paymentMethodNonce.getNonce();
//        Log.d("PayPal", "onPaymentMethodNonceCreated " + nonce);
//        if (paymentMethodNonce instanceof PayPalAccountNonce) {
//            PayPalAccountNonce payPalAccountNonce = (PayPalAccountNonce) paymentMethodNonce;
//            String email = payPalAccountNonce.getEmail();
//        }
//    };

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            String cityName = null;
            Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
            List<Address> addresses;
            try {
                addresses = gcd.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                if (addresses.size() > 0) {
                    System.out.println(addresses.get(0).getLocality());
                    cityName = addresses.get(0).getLocality();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            String s = location.getLatitude() + "\n" + location.getLongitude()
                    + "\n\nMy Current City is: "
                    + cityName;
            Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Prompts the user to select the current place from a list of likely places, and shows the
     * current place on the map - provided the user has granted location permission.
     */
    private void showCurrentPlace() {
        if (mGoogleMap == null) return;

        if (mLocationPermissionGranted) {
            // Get the likely places - that is, the businesses and other points of interest that
            // are the best match for the device's current location.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                return;

            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.builder(placeFields).build();

            Task<FindCurrentPlaceResponse> placeResponseTask = placesClient.findCurrentPlace(request);
            placeResponseTask.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {
                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse response = task.getResult();
                        sourceTxt.setText(response.getPlaceLikelihoods().get(0).getPlace().getAddress());
                        RIDE_REQUEST.put("s_address", response.getPlaceLikelihoods().get(0).getPlace().getAddress());
                        String placeId = response.getPlaceLikelihoods().get(0).getPlace().getId();

                        FetchPlaceRequest detailRequest = FetchPlaceRequest.builder(placeId, Arrays.asList(Place.Field.LAT_LNG)).build();
                        placesClient.fetchPlace(detailRequest)
                                .addOnCompleteListener(new OnCompleteListener<FetchPlaceResponse>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FetchPlaceResponse> task) {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            Place place = task.getResult().getPlace();
                                            RIDE_REQUEST.put("s_latitude", place.getLatLng().latitude);
                                            RIDE_REQUEST.put("s_longitude", place.getLatLng().longitude);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            hideLoading();
        } else getLocationPermission();
    }

    public void updatePaymentEntities() {
        if (checkStatusResponse != null) {
            isCash = checkStatusResponse.getCash() == 1;
            isCard = checkStatusResponse.getCard() == 1;
            SharedHelper.putKey(this, "currency", checkStatusResponse.getCurrency());
            if (isCash) RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.cash);
            else if (isCard) RIDE_REQUEST.put("payment_mode", Utilities.PaymentMode.card);
        }
    }

}
