package com.cluster.taxiuser.ui.activity.past_trip_detail;

import com.cluster.taxiuser.base.MvpPresenter;

public interface PastTripDetailsIPresenter<V extends PastTripDetailsIView> extends MvpPresenter<V> {

    void getPastTripDetails(Integer requestId);
}
