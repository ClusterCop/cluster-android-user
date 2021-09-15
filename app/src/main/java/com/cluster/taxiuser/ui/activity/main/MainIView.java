package com.cluster.taxiuser.ui.activity.main;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.AddressResponse;
import com.cluster.taxiuser.data.network.model.DataResponse;
import com.cluster.taxiuser.data.network.model.Provider;
import com.cluster.taxiuser.data.network.model.User;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface MainIView extends MvpView{
    void onSuccess(User user);
    void onSuccess(DataResponse dataResponse);
    void onSuccessLogout(Object object);
    void onSuccess(AddressResponse response);
    void onSuccess(List<Provider> objects);
    void onError(Throwable e);
    void onCheckStatusError(Throwable e);
    void onLanguageChanged(Object object);

}
