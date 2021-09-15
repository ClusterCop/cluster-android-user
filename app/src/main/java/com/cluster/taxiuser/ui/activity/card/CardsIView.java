package com.cluster.taxiuser.ui.activity.card;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.Card;

import java.util.List;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface CardsIView extends MvpView{
    void onSuccess(List<Card> cardList);
    void onError(Throwable e);
}
