package com.cluster.taxiuser.ui.activity.register;

import com.cluster.taxiuser.base.BasePresenter;
import com.cluster.taxiuser.data.network.APIClient;

import java.util.HashMap;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RegisterPresenter<V extends RegisterIView>
        extends BasePresenter<V>
        implements RegisterIPresenter<V> {

    @Override
    public void register(HashMap<String, Object> obj) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .register(obj)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onError));
    }

    @Override
    public void verifyEmail(String email) {
        getCompositeDisposable().add(APIClient
                .getAPIClient()
                .verifyEmail(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(getMvpView()::onSuccess, getMvpView()::onVerifyEmailError));
    }
}
