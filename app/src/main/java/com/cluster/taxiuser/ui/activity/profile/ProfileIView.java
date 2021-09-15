package com.cluster.taxiuser.ui.activity.profile;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.User;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ProfileIView extends MvpView{
    void onSuccess(User user);
    void onError(Throwable e);
}
