package com.cluster.taxiuser.ui.activity.setting;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.AddressResponse;

public interface SettingsIView extends MvpView {

    void onSuccessAddress(Object object);

    void onLanguageChanged(Object object);

    void onSuccess(AddressResponse address);

    void onError(Throwable e);
}
