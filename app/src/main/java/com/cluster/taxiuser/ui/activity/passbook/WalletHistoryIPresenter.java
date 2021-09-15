package com.cluster.taxiuser.ui.activity.passbook;

import com.cluster.taxiuser.base.MvpPresenter;

public interface WalletHistoryIPresenter<V extends WalletHistoryIView> extends MvpPresenter<V> {
    void wallet();
}
