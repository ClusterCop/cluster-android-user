package com.cluster.taxiuser.ui.activity.passbook;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cluster.taxiuser.MvpApplication;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.common.EqualSpacingItemDecoration;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.Wallet;
import com.cluster.taxiuser.data.network.model.WalletResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class WalletHistoryActivity extends BaseActivity implements WalletHistoryIView {


    private WalletHistoryPresenter<WalletHistoryActivity> presenter = new WalletHistoryPresenter<>();

    @BindView(R.id.rvWallet)
    RecyclerView rvWallet;
    @BindView(R.id.tvNoWalletData)
    TextView tvNoWalletData;
    private List<Wallet> walletList = new ArrayList<>();
    private NumberFormat numberFormat;

    @Override
    public int getLayoutId() {
        return R.layout.activity_passbook;
    }

    @Override
    public void initView() {
        presenter.attachView(this);
        ButterKnife.bind(this);

        numberFormat = MvpApplication.getInstance().getNewNumberFormat();
        showLoading();
        presenter.wallet();

    }

    @Override
    public void onSuccess(WalletResponse response) {
        hideLoading();
        if (!response.getWallets().isEmpty()) {
            tvNoWalletData.setVisibility(View.GONE);
            walletList.clear();
            walletList.addAll(response.getWallets());
            WalletHistoryAdapter mWalletAdapter = new WalletHistoryAdapter(WalletHistoryActivity.this, walletList);
            rvWallet.setLayoutManager(new LinearLayoutManager(WalletHistoryActivity.this,
                    LinearLayoutManager.VERTICAL, false));
            rvWallet.setVisibility(View.VISIBLE);
            rvWallet.setItemAnimator(new DefaultItemAnimator());
            rvWallet.addItemDecoration(new EqualSpacingItemDecoration(16, EqualSpacingItemDecoration.HORIZONTAL));
            rvWallet.setAdapter(mWalletAdapter);
        } else {
            tvNoWalletData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    @Override
    public void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }

    private class WalletHistoryAdapter extends RecyclerView.Adapter<MyViewHolder> {

        private Context mContext;
        private List<Wallet> mWallet;


        public WalletHistoryAdapter(Context context,
                                    List<Wallet> walletList) {
            this.mContext = context;
            this.mWallet = walletList;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item_wallet_history, parent, false);
            return new MyViewHolder(itemView);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Wallet item = mWallet.get(position);
            holder.tvId.setText(item.getTransactionAlias());
            holder.tvAmountVal.setText(String.format("%s %s", SharedHelper.getKey(mContext, "currency"), numberFormat.format(item.getAmount())));
            holder.tvBalanceVal.setText(String.format("%s %s", SharedHelper.getKey(mContext, "currency"), numberFormat.format(item.getCloseBalance())));
        }

        @Override
        public int getItemCount() {
            return mWallet.size();
        }
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvId, tvAmountVal, tvBalanceVal;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvAmountVal = itemView.findViewById(R.id.tvAmountVal);
            tvBalanceVal = itemView.findViewById(R.id.tvBalanceVal);
        }
    }
}
