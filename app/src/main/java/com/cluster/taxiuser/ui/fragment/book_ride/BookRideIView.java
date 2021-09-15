package com.cluster.taxiuser.ui.fragment.book_ride;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.PromoResponse;


public interface BookRideIView extends MvpView{
    void onSuccess(Object object);
    void onError(Throwable e);
    void onSuccessCoupon(PromoResponse promoResponse);
}
