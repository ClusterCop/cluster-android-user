package com.cluster.taxiuser.ui.activity.upcoming_trip_detail;

import com.cluster.taxiuser.base.BasePresenter;
import com.cluster.taxiuser.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpcomingTripDetailsPresenter<V extends UpcomingTripDetailsIView> extends BasePresenter<V>
        implements UpcomingTripDetailsIPresenter<V> {

    @Override
    public void getUpcomingTripDetails(Integer requestId) {

        getCompositeDisposable().add(APIClient.getAPIClient().upcomingTripDetails(requestId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(data -> getMvpView().onSuccess(data),
                        throwable -> getMvpView().onError(throwable)));
    }
}
