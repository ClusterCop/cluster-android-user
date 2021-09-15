package com.cluster.taxiuser.ui.activity.add_card;

import com.cluster.taxiuser.base.BasePresenter;
import com.cluster.taxiuser.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AddCardPresenter<V extends AddCardIView> extends BasePresenter<V> implements AddCardIPresenter<V> {
    @Override
    public void card(String cardId) {

        getCompositeDisposable().add(APIClient.getAPIClient().card(cardId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(object -> getMvpView().onSuccess(object),
                        throwable -> getMvpView().onError(throwable)));
    }
}
