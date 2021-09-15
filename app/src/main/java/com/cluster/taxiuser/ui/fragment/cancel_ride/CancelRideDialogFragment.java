package com.cluster.taxiuser.ui.fragment.cancel_ride;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessaging;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseBottomSheetDialogFragment;
import com.cluster.taxiuser.common.CancelRequestInterface;
import com.cluster.taxiuser.data.network.model.Datum;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cluster.taxiuser.base.BaseActivity.DATUM;

public class CancelRideDialogFragment extends BaseBottomSheetDialogFragment implements CancelRideIView {

    @BindView(R.id.cancel_reason)
    EditText cancelReason;
    @BindView(R.id.dismiss)
    Button dismiss;
    @BindView(R.id.submit)
    Button submit;

    private CancelRidePresenter<CancelRideDialogFragment> presenter = new CancelRidePresenter<>();
    private CancelRequestInterface callback;

    public CancelRideDialogFragment() {
        // Required empty public constructor
    }

    @SuppressLint("ValidFragment")
    public CancelRideDialogFragment(CancelRequestInterface callback) {
        this.callback = callback;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_cancel_ride_dialog;
    }

    @Override
    public void initView(View view) {
        ButterKnife.bind(this, view);
        presenter.attachView(this);

    }

    @OnClick({R.id.dismiss, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dismiss:
                dismiss();
                break;
            case R.id.submit:
                cancelRequest();
                break;
        }
    }

    private void cancelRequest() {
        if (cancelReason.getText().toString().isEmpty()) {
            Toast.makeText(getContext(), getString(R.string.please_enter_cancel_reason), Toast.LENGTH_SHORT).show();
            return;
        }

        if (DATUM != null) {
            Datum datum = DATUM;
            HashMap<String, Object> map = new HashMap<>();
            map.put("request_id", datum.getId());
            map.put("cancel_reason", cancelReason.getText().toString());
            showLoading();
            presenter.cancelRequest(map);
        }
    }

    @Override
    public void onSuccess(Object object) {
        hideLoading();
        if (DATUM != null)
            FirebaseMessaging.getInstance().unsubscribeFromTopic(String.valueOf(DATUM.getId()));
        Intent intent = new Intent("INTENT_FILTER");
        getActivity().sendBroadcast(intent);
        callback.cancelRequestMethod();
        dismiss();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroyView() {
        presenter.onDetach();
        super.onDestroyView();
    }
}
