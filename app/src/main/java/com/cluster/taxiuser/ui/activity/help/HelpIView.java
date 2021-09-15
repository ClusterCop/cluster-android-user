package com.cluster.taxiuser.ui.activity.help;

import com.cluster.taxiuser.base.MvpView;
import com.cluster.taxiuser.data.network.model.Help;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface HelpIView extends MvpView {
    void onSuccess(Help help);
    void onError(Throwable e);
}
