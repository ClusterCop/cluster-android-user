package com.cluster.taxiuser.ui.activity.help;


import com.cluster.taxiuser.base.BasePresenter;
import com.cluster.taxiuser.data.network.APIClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class HelpPresenter<V extends HelpIView> extends BasePresenter<V> implements HelpIPresenter<V> {


    @Override
    public void help() {

        getCompositeDisposable().add(APIClient.getAPIClient().
                help()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(help -> getMvpView().onSuccess(help),
                        throwable -> getMvpView().onError(throwable)));
    }
}
