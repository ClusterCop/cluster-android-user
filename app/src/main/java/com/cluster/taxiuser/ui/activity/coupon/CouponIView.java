package com.cluster.taxiuser.ui.activity.coupon;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.PromoResponse;

public interface CouponIView extends MvpView {
    void onSuccess(PromoResponse object);
    void onError(Throwable e);
}
