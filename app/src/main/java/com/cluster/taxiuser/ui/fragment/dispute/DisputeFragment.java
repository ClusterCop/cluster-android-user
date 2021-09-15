package com.cluster.taxiuser.ui.fragment.dispute;

import android.view.View;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseBottomSheetDialogFragment;

public class DisputeFragment extends BaseBottomSheetDialogFragment implements DisputeIView {

    private DisputePresenter<DisputeFragment> presenter = new DisputePresenter<>();

    public DisputeFragment() {
        // Required empty public constructor
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_dispute;
    }

    @Override
    public void initView(View view) {
        presenter.attachView(this);

    }

    @Override
    public void onSuccess(Object object) {

    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }
}
