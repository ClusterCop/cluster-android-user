package com.cluster.taxiuser.ui.activity.register;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.accountkit.Account;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.AccountKitCallback;
import com.facebook.accountkit.AccountKitError;
import com.facebook.accountkit.PhoneNumber;
import com.google.firebase.iid.FirebaseInstanceId;
import com.cluster.taxiuser.BuildConfig;
import com.cluster.taxiuser.R;
import com.cluster.taxiuser.base.BaseActivity;
import com.cluster.taxiuser.data.SharedHelper;
import com.cluster.taxiuser.data.network.model.RegisterResponse;
import com.cluster.taxiuser.ui.activity.main.MainActivity;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterActivity extends BaseActivity implements RegisterIView {

    @BindView(R.id.email)
    EditText email;
    @BindView(R.id.first_name)
    EditText firstName;
    @BindView(R.id.last_name)
    EditText lastName;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.password_confirmation)
    EditText passwordConfirmation;
    @BindView(R.id.chkTerms)
    CheckBox chkTerms;

    private RegisterPresenter registerPresenter = new RegisterPresenter();
    private boolean isEmailAvailable = true;

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView() {
        ButterKnife.bind(this);
        registerPresenter.attachView(this);
        clickFunctions();
    }

    private void clickFunctions() {
        email.setOnFocusChangeListener((v, hasFocus) -> {
            isEmailAvailable = true;
            if (!hasFocus && !TextUtils.isEmpty(email.getText().toString()))
                if (Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches())
                    registerPresenter.verifyEmail(email.getText().toString().trim());
        });
    }

    @OnClick({R.id.next, R.id.lblTerms})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:
                if (validate()) fbOtpVerify();
                break;
            case R.id.lblTerms:
                showTermsConditionsDialog();
                break;
        }
    }

    private void showTermsConditionsDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(getText(R.string.terms_and_conditions));

        WebView wv = new WebView(this);
        wv.loadUrl(BuildConfig.TERMS_CONDITIONS);
        wv.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        alert.setView(wv);
        alert.setNegativeButton("Close", (dialog, id) -> dialog.dismiss());
        alert.show();
    }

    private void register() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("first_name", firstName.getText().toString());
        map.put("last_name", lastName.getText().toString());
        map.put("email", email.getText().toString());
        map.put("password", password.getText().toString());
        map.put("password_confirmation", passwordConfirmation.getText().toString());
        map.put("device_token", SharedHelper.getKey(this, "device_token"));
        map.put("device_id", SharedHelper.getKey(this, "device_id"));
        map.put("mobile", SharedHelper.getKey(this,"dial_code")+
                SharedHelper.getKey(this, "mobile"));
        map.put("dial_code", SharedHelper.getKey(this, "dial_code"));
        map.put("device_type", BuildConfig.DEVICE_TYPE);
        map.put("login_by", "manual");
        showLoading();
        registerPresenter.register(map);
    }

    private boolean validate() {
        if (email.getText().toString().isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()) {
            Toast.makeText(this, getString(R.string.invalid_email), Toast.LENGTH_SHORT).show();
            return false;
        } else if (firstName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_first_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (lastName.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_last_name), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.getText().toString().length() < 6) {
            Toast.makeText(this, getString(R.string.invalid_password_length), Toast.LENGTH_SHORT).show();
            return false;
        } else if (passwordConfirmation.getText().toString().isEmpty()) {
            Toast.makeText(this, getString(R.string.invalid_confirm_password), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!password.getText().toString().equals(passwordConfirmation.getText().toString())) {
            Toast.makeText(this, getString(R.string.password_should_be_same), Toast.LENGTH_SHORT).show();
            return false;
        } else if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.w("RegisterActivity", "getInstanceId failed", task.getException());
                    return;
                }
                Log.d("FCM_TOKEN", task.getResult().getToken());

                SharedHelper.putKey(RegisterActivity.this, "device_token", task.getResult().getToken());
            });
            return false;
        } else if (SharedHelper.getKey(this, "device_id").isEmpty()) {
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            SharedHelper.putKey(this, "device_id", deviceId);
            Toast.makeText(this, getString(R.string.invalid_device_id), Toast.LENGTH_SHORT).show();
            return false;
        } else if (!chkTerms.isChecked()) {
            Toast.makeText(this, getString(R.string.please_accept_terms_condition), Toast.LENGTH_SHORT).show();
            return false;
        } else if (isEmailAvailable) {
            Toast.makeText(this, getString(R.string.email_already_exist), Toast.LENGTH_SHORT).show();
            email.requestFocus();
            return false;
        } else return true;
    }

    @Override
    public void onSuccess(RegisterResponse response) {
        hideLoading();
        Toast.makeText(this, getString(R.string.you_have_been_successfully_registered), Toast.LENGTH_SHORT).show();
        SharedHelper.putKey(this, "access_token", "Bearer " + response.getAccessToken());
        SharedHelper.putKey(this, "logged_in", true);
        finishAffinity();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onSuccess(Object object) {
        isEmailAvailable = false;
    }

    @Override
    public void onError(Throwable e) {
       handleError(e);
    }

    @Override
    public void onVerifyEmailError(Throwable e) {
        isEmailAvailable = true;
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE && data != null) {
            AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
                @Override
                public void onSuccess(Account account) {
                    Log.d("AccountKit", "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
                    if (AccountKit.getCurrentAccessToken().getToken() != null) {
                        //SharedHelper.putKey(RegisterActivity.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());
                        PhoneNumber phoneNumber = account.getPhoneNumber();
                        SharedHelper.putKey(RegisterActivity.this, "dial_code", "+" +
                                phoneNumber.getCountryCode());
                        SharedHelper.putKey(RegisterActivity.this, "mobile", phoneNumber.getPhoneNumber());
                        register();
                    }
                }

                @Override
                public void onError(AccountKitError accountKitError) {
                    Log.e("AccountKit", "onError: Account Kit" + accountKitError);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        registerPresenter.onDetach();
        super.onDestroy();
    }
}
