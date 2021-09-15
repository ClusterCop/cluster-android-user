package com.cluster.taxiuser.ui.fragment.rate;

import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface RatingIPresenter<V extends RatingIView> extends MvpPresenter<V> {
    void rate(HashMap<String, Object> obj);
}
