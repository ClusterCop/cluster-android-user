package com.cluster.taxiuser.ui.activity.login;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.ForgotResponse;
import com.cluster.taxiuser.data.network.model.Token;

public interface LoginIView extends MvpView{
    void onSuccess(Token token);
    void onSuccess(ForgotResponse object);
    void onError(Throwable e);
}
