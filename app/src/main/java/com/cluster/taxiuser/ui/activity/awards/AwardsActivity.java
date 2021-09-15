package com.cluster.taxiuser.ui.activity.awards;

import android.content.Intent;
import android.net.Uri;
import android.widget.ImageView;
import android.widget.TextView;

import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.data.network.APIClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AwardsActivity extends BaseActivity {

    @BindView(R.id.img_redirect)
    ImageView redirect;
    @BindView(R.id.txt_awardsCount)
    TextView count;

    private String website = "https://libipremios.com";

    @Override
    public int getLayoutId() {
        return R.layout.activity_awards;
    }


    @Override
    public void initView() {
        ButterKnife.bind(this);
        getCounts();
    }

    @OnClick(R.id.img_redirect)
    public void onViewClicked() {
        Uri uri = Uri.parse(website);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void getCounts() {

        showLoading();
        APIClient apiClient = new APIClient();

        Call<ResponseBody> response = apiClient.getAPIClient().getTripCounts();
        response.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                hideLoading();
                String cnt = "";
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    if (response.isSuccessful()) {
                        cnt = object.getString("counts");
                        count.setText(cnt + "  VIAJES");
                    }

                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                hideLoading();
            }
        });

    }
}
