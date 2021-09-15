package com.cluster.taxiuser.ui.activity.main;

import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface MainIPresenter<V extends MainIView> extends MvpPresenter<V> {
    void profile();
    void logout(String id);
    void checkStatus();
    void address();
    void providers(HashMap<String, Object> params);
    void changeLanguage(String languageID);
}
