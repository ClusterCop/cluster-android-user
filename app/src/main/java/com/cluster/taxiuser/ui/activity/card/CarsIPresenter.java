package com.cluster.taxiuser.ui.activity.card;

import com.cluster.taxiuser.base.MvpPresenter;


public interface CarsIPresenter<V extends CardsIView> extends MvpPresenter<V> {
    void card();
}
