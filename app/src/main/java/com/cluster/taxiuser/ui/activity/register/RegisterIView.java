package com.cluster.taxiuser.ui.activity.register;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.RegisterResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface RegisterIView extends MvpView{
    void onSuccess(RegisterResponse object);
    void onSuccess(Object object);
    void onError(Throwable e);
    void onVerifyEmailError(Throwable e);
}
