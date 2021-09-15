package com.cluster.taxiuser.ui.activity.coupon;

import com.cluster.taxiuser.base.MvpPresenter;

public interface CouponIPresenter<V extends CouponIView> extends MvpPresenter<V> {
    void coupon();
}
