package com.cluster.taxiuser.ui.activity.location_pick;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.PlaceDetectionClient;
//import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.tasks.Task;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.common.RecyclerItemClickListener;
import com.cluster.taxiuser.data.network.model.Address;
import com.cluster.taxiuser.data.network.model.AddressResponse;
import com.cluster.taxiuser.ui.adapter.PlacesAutoCompleteAdapter;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;


import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cluster.taxiuser.MvpApplication.DEFAULT_ZOOM;
import static com.cluster.taxiuser.MvpApplication.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.cluster.taxiuser.MvpApplication.mLastKnownLocation;

public class LocationPickActivity extends BaseActivity
        implements OnMapReadyCallback,
        GoogleMap.OnCameraMoveListener,
        GoogleMap.OnCameraIdleListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        PlacesAutoCompleteAdapter.ClickListener,
        LocationPickIView {

    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.source)
    EditText source;
    @BindView(R.id.destination)
    EditText destination;
    @BindView(R.id.destination_layout)
    LinearLayout destinationLayout;
    @BindView(R.id.home_address_layout)
    LinearLayout homeAddressLayout;
    @BindView(R.id.work_address_layout)
    LinearLayout workAddressLayout;
    @BindView(R.id.home_address)
    TextView homeAddress;
    @BindView(R.id.work_address)
    TextView workAddress;
    @BindView(R.id.locations_rv)
    RecyclerView locationsRv;
    @BindView(R.id.location_bs_layout)
    CardView locationBsLayout;
    @BindView(R.id.dd)
    CoordinatorLayout dd;

    private PlaceDetectionClient mPlaceDetectionClient;
    private boolean isLocationRvClick = false;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private boolean mLocationPermissionGranted;
    private GoogleMap mGoogleMap;
    private String s_address;
    private Double s_latitude;
    private Double s_longitude;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private BottomSheetBehavior mBottomSheetBehavior;
    private Boolean isEditable = true;
    private Address home, work = null;

    private LocationPickPresenter<LocationPickActivity> presenter = new LocationPickPresenter<>();

    private EditText selectedEditText;
    boolean isEnableIdle = false;
    private boolean isMapKeyShow = false;
    protected GoogleApiClient mGoogleApiClient;
    private PlacesAutoCompleteAdapter mAutoCompleteAdapter;
    private String isSetting;
    private static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(-0, 0), new LatLng(0, 0));

    @Override
    public int getLayoutId() {
        return R.layout.activity_location_pick;
    }

    @Override
    public void initView() {
        buildGoogleApiClient();
        ButterKnife.bind(this);
        presenter.attachView(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);
        Places.initialize(this, getResources().getString(R.string.google_map_key));

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        String sourceEdit = getIntent().getStringExtra("srcClick");
        String destEdit = getIntent().getStringExtra("destClick");
        String fieldClicked = getIntent().getStringExtra("fieldClicked");
        String previousDestination = getIntent().getStringExtra("destination");

        if (!TextUtils.isEmpty(previousDestination) && !TextUtils.isEmpty(destEdit))
            if (Objects.requireNonNull(previousDestination).equalsIgnoreCase(getString(R.string.where_to))
                    || Objects.requireNonNull(destEdit).equalsIgnoreCase("isDest")) {
                source.setCursorVisible(false);
                destination.setCursorVisible(true);
                destination.setHint(previousDestination);
            } else {
                source.setCursorVisible(false);
                destination.setCursorVisible(true);
                destination.setText(previousDestination);
            }
        if (sourceEdit != null && !sourceEdit.isEmpty())
            if (sourceEdit.equalsIgnoreCase("isSource")) {
                source.setCursorVisible(true);
                destination.setCursorVisible(false);
            }
        if (isMapKeyShow) hideKeyboard();
        else showKeyboard();

        if (!TextUtils.isEmpty(fieldClicked))
            if (fieldClicked.equalsIgnoreCase("pickupAddress")) {
                source.requestFocus();
                selectedEditText = source;
            } else if (fieldClicked.equalsIgnoreCase("dropAddress")) {
                destination.requestFocus();
                selectedEditText = destination;
            }

        mBottomSheetBehavior = BottomSheetBehavior.from(locationBsLayout);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        mAutoCompleteAdapter = new PlacesAutoCompleteAdapter(this, R.layout.list_item_location, mGoogleApiClient, BOUNDS_INDIA, null);

        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        locationsRv.setLayoutManager(mLinearLayoutManager);
        locationsRv.setAdapter(mAutoCompleteAdapter);
        mAutoCompleteAdapter.setClickListener(this);
        mAutoCompleteAdapter.notifyDataSetChanged();


        source.addTextChangedListener(filterTextWatcher);
        destination.addTextChangedListener(filterTextWatcher);

        source.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = source;
        });

        destination.setOnFocusChangeListener((view, hasFocus) -> {
            if (hasFocus) selectedEditText = destination;
        });

        destination.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                setResult(Activity.RESULT_OK, new Intent());
                finish();
                return true;
            }
            return false;
        });

        isSetting = getIntent().getStringExtra("isSetting");

//        if (RIDE_REQUEST.containsKey("s_address")) {
//            if (sourceEdit != null && !sourceEdit.isEmpty())
//                if (sourceEdit.equalsIgnoreCase("isSource")) {
//                    source.setCursorVisible(true);
//                    destination.setCursorVisible(false);
//                }
//            if (previousDestination != null && !previousDestination.isEmpty()
//                    && destEdit != null && !destEdit.isEmpty())
//                if (previousDestination.equalsIgnoreCase(getString(R.string.where_to))
//                        && destEdit.equalsIgnoreCase("isDest")) {
//                    destination.setHint(previousDestination);
//                } else destination.setText(previousDestination);
//            isEditable = false;
//            source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
//          //  destination.setText(String.valueOf(RIDE_REQUEST.get("d_address")));
//            isEditable = true;
//        }
//
//        if (RIDE_REQUEST.containsKey("d_address")) {
//            isEditable = false;
//            if(previousDestination != null && !previousDestination.isEmpty() &&
//                    destEdit != null && !destEdit.isEmpty() ) {
//                if(previousDestination.equalsIgnoreCase(getString(R.string.where_to))&&
//                        destEdit.equalsIgnoreCase("isDest")) {
//                    source.setCursorVisible(false);
//                    destination.setCursorVisible(true);
//                    destination.setHint(previousDestination);
//                } else {
//                    source.setCursorVisible(false);
//                    destination.setCursorVisible(true);
//                    destination.setText(previousDestination);
//                }
//            }
//            isEditable = true;
//        }

//        source.setText(String.valueOf(RIDE_REQUEST.get("s_address")));
        destination.setText(RIDE_REQUEST.containsKey("d_address")
                ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get("d_address")).toString())
                ? ""
                : String.valueOf(RIDE_REQUEST.get("d_address"))
                : "");
        if (!Objects.requireNonNull(getIntent().getExtras()).getBoolean("isHideDestination")) {
            source.setText(RIDE_REQUEST.containsKey("s_address")
                    ? TextUtils.isEmpty(Objects.requireNonNull(RIDE_REQUEST.get("s_address")).toString())
                    ? ""
                    : String.valueOf(RIDE_REQUEST.get("s_address"))
                    : "");
        } else {
            source.setHint(getString(R.string.select_location));
        }


//        locationsRv.addOnItemTouchListener(new RecyclerItemClickListener(this, (view, position) -> {
//                    if (mAutoCompleteAdapter.getItemCount() == 0) return;
//                    final PlacesAutoCompleteAdapter.PlaceAutocomplete item = mAutoCompleteAdapter.getItem(position);
//                    final String placeId = String.valueOf(item.placeId);
//                    Log.i("LocationPickActivity", "Autocomplete item selected: " + item.address);
//                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
//                    placeResult.setResultCallback(places -> {
//                        if (places.getCount() == 1) {
//                            LatLng latLng = places.get(0).getLatLng();
//                            isLocationRvClick = true;
//                            isMapKeyShow = false;
//                            setLocationText(String.valueOf(item.address), latLng, isLocationRvClick);
//                            //Toast.makeText(getApplicationContext(), String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
//                        } else
//                            Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();
//                    });
//
//                    Log.i("LocationPickActivity", "Clicked: " + item.address);
//                    Log.i("LocationPickActivity", "Called getPlaceById to get Place details for " + item.placeId);
//                })
//        );

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            destinationLayout.setVisibility(extras.getBoolean("isHideDestination", false) ? View.GONE : View.VISIBLE);
            source.setHint(getString(R.string.select_location));
        } else source.setHint(getString(R.string.pickup_location));
        presenter.address();
    }

    private void setLocationText(String address, LatLng latLng, boolean isLocationRvClick) {
        if (address != null && latLng != null) {
            isEditable = false;
            selectedEditText.setText(address);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                s_address = address;
                s_latitude = latLng.latitude;
                s_longitude = latLng.longitude;
                RIDE_REQUEST.put("s_address", address);
                RIDE_REQUEST.put("s_latitude", latLng.latitude);
                RIDE_REQUEST.put("s_longitude", latLng.longitude);
            }

            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.put("d_address", address);
                RIDE_REQUEST.put("d_latitude", latLng.latitude);
                RIDE_REQUEST.put("d_longitude", latLng.longitude);

                if (isLocationRvClick) {
                    //  Done functionality called...
                    setResult(Activity.RESULT_OK, new Intent());
                    finish();
                }
            }
        } else {
            isEditable = false;
            selectedEditText.setText("");
            locationsRv.setVisibility(View.GONE);
            isEditable = true;

            if (selectedEditText.getTag().equals("source")) {
                RIDE_REQUEST.remove("s_address");
                RIDE_REQUEST.remove("s_latitude");
                RIDE_REQUEST.remove("s_longitude");
            }
            if (selectedEditText.getTag().equals("destination")) {
                RIDE_REQUEST.remove("d_address");
                RIDE_REQUEST.remove("d_latitude");
                RIDE_REQUEST.remove("d_longitude");
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mGoogleApiClient.isConnected() && !mGoogleApiClient.isConnecting()) {
            Log.v("Google API", "Connecting");
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            Log.v("Google API", "Dis-Connecting");
            mGoogleApiClient.disconnect();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
//                .addApi(Places.GEO_DATA_API)
                .build();
    }

    @OnClick({R.id.source, R.id.destination, R.id.reset_source, R.id.reset_destination, R.id.home_address_layout, R.id.work_address_layout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.source:
                break;
            case R.id.destination:
                break;
            case R.id.reset_source:
                selectedEditText = source;
                source.requestFocus();
                setLocationText(null, null, isLocationRvClick);
                break;
            case R.id.reset_destination:
                destination.requestFocus();
                selectedEditText = destination;
                setLocationText(null, null, isLocationRvClick);
                break;
            case R.id.home_address_layout:
                if (home != null)
                    setLocationText(home.getAddress(), new LatLng(home.getLatitude(), home.getLongitude()), isLocationRvClick);
                break;
            case R.id.work_address_layout:
                if (work != null)
                    setLocationText(work.getAddress(), new LatLng(work.getLatitude(), work.getLongitude()), isLocationRvClick);
                break;
        }
    }

    @Override
    public void onCameraIdle() {
        try {
            CameraPosition cameraPosition = mGoogleMap.getCameraPosition();
            if (isEnableIdle) {
                String address = getAddress(cameraPosition.target);
                System.out.println("onCameraIdle " + address);
                isMapKeyShow = true;
                if (isMapKeyShow) hideKeyboard();
                else showKeyboard();
                setLocationText(address, cameraPosition.target, isLocationRvClick);
            }
            isEnableIdle = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onCameraMove() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            boolean success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.style_json));
        } catch (Resources.NotFoundException e) {
            Log.d("Map:Style", "Can't find style. Error: ");
        }
        this.mGoogleMap = googleMap;
        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();
    }

    void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mGoogleMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(new LatLng(
                                        mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()
                                ), DEFAULT_ZOOM));
                    } else {
                        Log.d("Map", "Current location is null. Using defaults.");
                        Log.e("Map", "Exception: %s", task.getException());
                        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            mLocationPermissionGranted = true;
        else
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
    }

    private void updateLocationUI() {
        if (mGoogleMap == null) return;
        try {
            if (mLocationPermissionGranted) {
                mGoogleMap.setMyLocationEnabled(true);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mGoogleMap.setOnCameraMoveListener(this);
                mGoogleMap.setOnCameraIdleListener(this);
            } else {
                mGoogleMap.setMyLocationEnabled(false);
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    updateLocationUI();
                    getDeviceLocation();
                }
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v("Google API Callback", "Connection Suspended");
        Log.v("Code", String.valueOf(i));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.v("Error Code", String.valueOf(connectionResult.getErrorCode()));
        Toast.makeText(this, "API_NOT_CONNECTED", Toast.LENGTH_SHORT).show();
    }

    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
            if (isEditable) if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                locationsRv.setVisibility(View.VISIBLE);
                mAutoCompleteAdapter.getFilter().filter(s.toString());
                if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                    mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (!mGoogleApiClient.isConnected()) {
                Log.e("ERROR", "API_NOT_CONNECTED");
            }
            if (s.toString().equals("")) {
                locationsRv.setVisibility(View.GONE);
            }
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

    };

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.location_pick_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_done:
                try {
                    if (isSetting != null) {
                        if (isSetting.equalsIgnoreCase("homeSetting") ||
                                isSetting.equalsIgnoreCase("workSetting")) {
                            Intent intent = new Intent();
                            intent.putExtra("s_address", s_address);
                            intent.putExtra("s_latitude", s_latitude);
                            intent.putExtra("s_longitude", s_longitude);
                            setResult(Activity.RESULT_OK, intent);
                            finish();
                        } else if (isSetting.equalsIgnoreCase("source") ||
                                isSetting.equalsIgnoreCase("destination")) {
                            setResult(Activity.RESULT_OK, new Intent());
                            finish();
                        } else {
                            setResult(Activity.RESULT_OK, new Intent());
                            finish();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
//            case android.R.id.home:
//                Toast.makeText(getApplicationContext(), "Back button clicked", Toast.LENGTH_SHORT).show();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSuccess(AddressResponse address) {
        if (address.getHome().isEmpty()) homeAddressLayout.setVisibility(View.GONE);
        else {
            home = address.getHome().get(address.getHome().size() - 1);
            homeAddress.setText(home.getAddress());
            homeAddressLayout.setVisibility(View.VISIBLE);
        }

        if (address.getWork().isEmpty()) workAddressLayout.setVisibility(View.GONE);
        else {
            work = address.getWork().get(address.getWork().size() - 1);
            workAddress.setText(work.getAddress());
            workAddressLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    @Override
    public void click(Place place) {
        if (mAutoCompleteAdapter.getItemCount() == 0) return;

        if (place != null) {
            LatLng latLng = place.getLatLng();
            isLocationRvClick = true;
            isMapKeyShow = false;
            setLocationText(String.valueOf(place.getAddress()), latLng, isLocationRvClick);
        } else
            Toast.makeText(getApplicationContext(), "SOMETHING WRONG", Toast.LENGTH_SHORT).show();
    }
}
