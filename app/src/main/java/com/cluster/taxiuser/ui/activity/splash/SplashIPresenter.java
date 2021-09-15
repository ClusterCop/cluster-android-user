package com.cluster.taxiuser.ui.activity.splash;

import com.cluster.taxiuser.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SplashIPresenter<V extends SplashIView> extends MvpPresenter<V>{
    void profile();
    void changeLanguage(String languageID);
}
