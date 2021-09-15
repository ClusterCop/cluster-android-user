package com.cluster.taxiuser.ui.activity.passbook;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.WalletResponse;

public interface WalletHistoryIView extends MvpView {
    void onSuccess(WalletResponse response);
    void onError(Throwable e);
}
