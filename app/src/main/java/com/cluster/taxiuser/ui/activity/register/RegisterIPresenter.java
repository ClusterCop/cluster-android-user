package com.cluster.taxiuser.ui.activity.register;

import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;

public interface RegisterIPresenter<V extends RegisterIView> extends MvpPresenter<V>{
    void register(HashMap<String, Object> obj);
    void verifyEmail(String email);
}
