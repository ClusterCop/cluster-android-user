package com.cluster.taxiuser.ui.activity.change_password;


import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;


public interface ChangePasswordIPresenter<V extends ChangePasswordIView> extends MvpPresenter<V> {
    void changePassword(HashMap<String, Object> parms);
}
