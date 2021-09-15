package com.cluster.taxiuser.ui.activity.payment;

import com.cluster.taxiuser.base.BasePresenter;
import com.cluster.taxiuser.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class PaymentPresenter<V extends PaymentIView> extends BasePresenter<V> implements PaymentIPresenter<V> {

    @Override
    public void deleteCard(String cardId) {

        getCompositeDisposable().add(APIClient.getAPIClient().deleteCard(cardId,"DELETE")
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(deleteResponse -> getMvpView().onSuccess(deleteResponse),
                        throwable -> getMvpView().onError(throwable)));
    }

    @Override
    public void card() {

        getCompositeDisposable().add(APIClient.getAPIClient().card()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(cards -> getMvpView().onSuccess(cards),
                        throwable -> getMvpView().onError(throwable)));
    }
}
