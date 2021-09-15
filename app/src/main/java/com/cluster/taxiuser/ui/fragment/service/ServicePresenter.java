package com.cluster.taxiuser.ui.fragment.service;

import com.cluster.taxiuser.base.BasePresenter;
import com.cluster.taxiuser.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ServicePresenter<V extends ServiceIView> extends BasePresenter<V> implements ServiceIPresenter<V> {

    @Override
    public void services() {
        getCompositeDisposable().add(APIClient.getAPIClient().services()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(serviceResponse -> getMvpView().onSuccess(serviceResponse),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void estimateFare(HashMap<String, Object> obj) {

       /* getCompositeDisposable().add(APIClient.getAPIClient().estimateFare(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.newThread())
                .subscribe(estimateResponse -> getMvpView().onSuccess(estimateResponse),
                        throwable -> getMvpView().onError(throwable)));*/
    }

    @Override
    public void rideNow(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient.getAPIClient().sendRequest(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(sendRequestResponse -> getMvpView().onSuccess(sendRequestResponse),
                        throwable -> getMvpView().onError(throwable)));
    }
}
