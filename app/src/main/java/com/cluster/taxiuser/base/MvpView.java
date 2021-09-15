package com.cluster.taxiuser.base;

import android.app.Activity;

import com.cluster.taxiuser.data.network.model.Token;

public interface MvpView {

    Activity activity();

    void showLoading();

    void hideLoading();

    void onErrorRefreshToken(Throwable throwable);

    void onSuccessRefreshToken(Token token);

    void onSuccessLogout(Object object);

    void onError(Throwable throwable);
}
