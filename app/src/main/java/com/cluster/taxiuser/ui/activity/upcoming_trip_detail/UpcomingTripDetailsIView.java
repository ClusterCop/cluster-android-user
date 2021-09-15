package com.cluster.taxiuser.ui.activity.upcoming_trip_detail;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.Datum;

import java.util.List;

public interface UpcomingTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> upcomingTripDetails);
    void onError(Throwable e);
}
