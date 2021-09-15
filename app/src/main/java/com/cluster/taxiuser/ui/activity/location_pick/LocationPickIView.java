package com.cluster.taxiuser.ui.activity.location_pick;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.AddressResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface LocationPickIView extends MvpView {

    void onSuccess(AddressResponse address);
    void onError(Throwable e);
}
