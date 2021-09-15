package com.cluster.taxiuser.ui.activity.help;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.view.View;

import com.cluster.taxiuser.BuildConfig;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.data.network.model.Help;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cluster.taxiuser.MvpApplication.PERMISSIONS_REQUEST_PHONE;

public class HelpActivity extends BaseActivity implements HelpIView {
    private String ContactNumber = null;
    private String Mail = null;
    private HelpPresenter<HelpActivity> presenter = new HelpPresenter<>();

    @Override
    public int getLayoutId() {
        return R.layout.activity_help;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        presenter.attachView(this);
        presenter.help();
    }

    @Override
    public void onSuccess(Help help) {
        ContactNumber = help.getContactNumber();
        Mail = help.getContactEmail();
    }

    @Override
    public void onError(Throwable e) {
        handleError(e);
    }

    private void callContactNumber() {
        if (ContactNumber != null) {

            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ContactNumber));
            startActivity(intent);

//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
//                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ContactNumber));
//                startActivity(intent);
//            } else {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_PHONE);
//            }
        }
    }

    private void sendMail() {
        if (Mail != null) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto: " + Mail));
            startActivity(Intent.createChooser(emailIntent, "Send feedback"));
        }
    }

    private void openWeb() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(BuildConfig.BASE_URL));
        startActivity(i);
    }


    @OnClick({R.id.call, R.id.mail, R.id.web})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.call:
                callContactNumber();
                break;
            case R.id.mail:
                sendMail();
                break;
            case R.id.web:
                openWeb();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    callContactNumber();
            }
        }
    }

    @Override
    protected void onDestroy() {
        presenter.onDetach();
        super.onDestroy();
    }
}
