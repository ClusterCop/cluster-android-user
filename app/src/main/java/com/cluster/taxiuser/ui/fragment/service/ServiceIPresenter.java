package com.cluster.taxiuser.ui.fragment.service;

import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceIPresenter<V extends ServiceIView> extends MvpPresenter<V> {
    void services();
    void estimateFare(HashMap<String, Object> obj);
    void rideNow(HashMap<String, Object> obj);
}
