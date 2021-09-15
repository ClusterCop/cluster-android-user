package com.cluster.taxiuser.ui.fragment.invoice;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.Message;

public interface InvoiceIView extends MvpView{
    void onSuccess(Message message);
    void onSuccess(Object o);
    void onError(Throwable e);
}
