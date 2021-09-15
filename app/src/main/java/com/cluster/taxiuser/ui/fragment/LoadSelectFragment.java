package com.cluster.taxiuser.ui.fragment;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseFragment;
import com.cluster.taxiuser.data.network.model.Service;
import com.daimajia.androidanimations.library.specials.out.TakingOffAnimator;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class LoadSelectFragment extends BaseFragment {

    @BindView(R.id.load_rv)
    RecyclerView loadRv;
    @BindView(R.id.error_layout)
    TextView errorLayout;


    Unbinder unbinder;
    public static Service SERVICE = new Service();

    public LoadSelectFragment() {

    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_load_select;
    }

    @Override
    public View initView(View view) {
        unbinder = ButterKnife.bind(this, view);

        Log.d("111111111111", "Service Loads::::" + SERVICE.getCapacity() + "::::" + SERVICE.toString());
        return view;
    }

    @OnClick(R.id.get_loads)
    public void onViewClicked(View view) {

        switch (view.getId()) {
            case R.id.get_loads:
                Toast.makeText(getContext(), "Get Loads Items:::", Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }
}
