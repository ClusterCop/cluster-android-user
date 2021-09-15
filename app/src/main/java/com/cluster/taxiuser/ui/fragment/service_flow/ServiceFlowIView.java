package com.cluster.taxiuser.ui.fragment.service_flow;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.DataResponse;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceFlowIView extends MvpView{
    void onSuccess(DataResponse dataResponse);
    void onError(Throwable e);
}
