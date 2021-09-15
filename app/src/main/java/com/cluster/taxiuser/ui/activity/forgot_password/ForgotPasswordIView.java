package com.cluster.taxiuser.ui.activity.forgot_password;

import com.cluster.taxiuser.base.MvpView;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ForgotPasswordIView extends MvpView {
    void onSuccess(Object object);
    void onError(Throwable e);
}
