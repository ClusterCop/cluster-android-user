package com.cluster.taxiuser.ui.activity.setting;

import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;

public interface SettingsIPresenter<V extends SettingsIView> extends MvpPresenter<V>{
    void addAddress(HashMap<String, Object> params);
    void deleteAddress(Integer id, HashMap<String, Object> params);
    void address();
    void changeLanguage(String languageID);
}
