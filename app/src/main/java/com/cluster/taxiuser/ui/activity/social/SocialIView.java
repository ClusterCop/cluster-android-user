package com.cluster.taxiuser.ui.activity.social;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.Token;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIView extends MvpView{
    void onSuccess(Token token);
    void onError(Throwable e);
}
