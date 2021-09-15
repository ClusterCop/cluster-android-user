package com.cluster.taxiuser.ui.fragment.book_ride;

import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;


public interface BookRideIPresenter<V extends BookRideIView> extends MvpPresenter<V> {
    void rideNow(HashMap<String, Object> obj);
    void getCouponList();
}
