package com.cluster.taxiuser.ui.activity.past_trip_detail;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.Datum;

import java.util.List;

public interface PastTripDetailsIView extends MvpView {

    void onSuccess(List<Datum> pastTripDetails);
    void onError(Throwable e);
}
