package com.cluster.taxiuser.ui.fragment.service;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.EstimateFare;
import com.cluster.taxiuser.data.network.model.Service;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceIView extends MvpView{
    void onSuccess(List<Service> serviceList);
    void onSuccess(EstimateFare estimateFare);
    void onError(Throwable e);
    void onSuccess(Object object);
}
