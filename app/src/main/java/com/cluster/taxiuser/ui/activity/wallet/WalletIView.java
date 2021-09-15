package com.cluster.taxiuser.ui.activity.wallet;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.AddWallet;

public interface WalletIView extends MvpView {
    void onSuccess(AddWallet object);
    void onError(Throwable e);
}
