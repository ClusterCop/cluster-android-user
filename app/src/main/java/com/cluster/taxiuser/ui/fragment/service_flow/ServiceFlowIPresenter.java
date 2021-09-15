package com.cluster.taxiuser.ui.fragment.service_flow;

import com.cluster.taxiuser.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface ServiceFlowIPresenter<V extends ServiceFlowIView> extends MvpPresenter<V> {
    void checkStatus();
}
