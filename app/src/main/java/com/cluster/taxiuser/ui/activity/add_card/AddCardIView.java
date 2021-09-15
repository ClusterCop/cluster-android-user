package com.cluster.taxiuser.ui.activity.add_card;

import com.cluster.taxiuser.base.MvpView;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface AddCardIView extends MvpView{
    void onSuccess(Object card);
    void onError(Throwable e);
}
