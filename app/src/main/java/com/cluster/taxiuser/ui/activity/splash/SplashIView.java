package com.cluster.taxiuser.ui.activity.splash;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.User;


public interface SplashIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
    void onLanguageChanged(Object object);
}
