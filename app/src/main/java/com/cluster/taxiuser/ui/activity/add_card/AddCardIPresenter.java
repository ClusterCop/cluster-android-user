package com.cluster.taxiuser.ui.activity.add_card;

import com.cluster.taxiuser.base.MvpPresenter;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface AddCardIPresenter<V extends AddCardIView> extends MvpPresenter<V> {
    void card(String stripeToken);
}
