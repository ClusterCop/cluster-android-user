package com.cluster.taxiuser.ui.activity.wallet;

import com.cluster.taxiuser.base.MvpPresenter;
import java.util.HashMap;

public interface WalletIPresenter<V extends WalletIView> extends MvpPresenter<V>{
    void addMoney(HashMap<String, Object> obj);
}
