package com.cluster.taxiuser.ui.fragment.searching;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseBottomSheetDialogFragment;
import com.cluster.taxiuser.data.network.model.Datum;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cluster.taxiuser.base.BaseActivity.DATUM;

public class SearchingFragment extends BaseBottomSheetDialogFragment implements SearchingIView {

    private SearchingPresenter<SearchingFragment> presenter = new SearchingPresenter<>();

    public SearchingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_searching;
    }

    @Override
    public void initView(View view) {
        setCancelable(false);
        getDialog().setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            View bottomSheetInternal = d.findViewById(android.support.design.R.id.design_bottom_sheet);
            BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
        });
        ButterKnife.bind(this, view);
        presenter.attachView(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @OnClick(R.id.cancel)
    public void onViewClicked() {
        alertCancel();
    }

    private void alertCancel() {
        new AlertDialog.Builder(getContext())
                .setMessage(R.string.are_sure_you_want_to_cancel_the_request)
                .setCancelable(true)
                .setPositiveButton(getString(R.string.yes), (dialog, which) -> {
                    if (DATUM != null) {
                        showLoading();
                        Datum datum = DATUM;
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("request_id", datum.getId());
                        presenter.cancelRequest(map);
                    }
                }).setNegativeButton(getString(R.string.no), (dialog, which) -> dialog.cancel())
                .show();
    }

    @Override
    public void onSuccess(Object object) {
        hideLoading();
        Intent intent = new Intent("INTENT_FILTER");
        activity().sendBroadcast(intent);
        dismissAllowingStateLoss();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
        dismissAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
