package com.cluster.taxiuser.ui.activity.social;

import com.cluster.taxiuser.base.MvpPresenter;

import java.util.HashMap;

/**
 * Created by santhosh@appoets.com on 19-05-2018.
 */
public interface SocialIPresenter<V extends SocialIView> extends MvpPresenter<V>{
    void loginGoogle(HashMap<String, Object> obj);
    void loginFacebook(HashMap<String, Object> obj);
}
