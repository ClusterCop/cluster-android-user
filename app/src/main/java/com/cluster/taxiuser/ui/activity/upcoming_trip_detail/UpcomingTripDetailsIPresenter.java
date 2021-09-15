package com.cluster.taxiuser.ui.activity.upcoming_trip_detail;

import com.cluster.taxiuser.base.MvpPresenter;

public interface UpcomingTripDetailsIPresenter<V extends UpcomingTripDetailsIView> extends MvpPresenter<V> {

    void getUpcomingTripDetails(Integer requestId);
}
