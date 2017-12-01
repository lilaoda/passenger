package bus.passenger.module.login;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.tbruyelle.rxpermissions2.RxPermissions;

import bus.passenger.R;
import bus.passenger.base.BaseActivity;
import bus.passenger.bean.LoginResult;
import bus.passenger.bean.param.LoginParam;
import bus.passenger.data.DbManager;
import bus.passenger.data.HttpManager;
import bus.passenger.data.local.entity.User;
import bus.passenger.module.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.utils.ValidateUtils;

import static bus.passenger.utils.RxUtils.wrapHttp;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_pwd)
    EditText editPwd;

    private HttpManager mHttpManager;
    private DbManager mDbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }

    private void initView() {
        mHttpManager = HttpManager.instance();
        mDbManager = DbManager.instance();
        User user = mDbManager.getUser();
        if (user != null) {
            editPhone.setText(user.getPhone());
            editPwd.setText(user.getPassword());
            user.setToken("");

        }
    }

    @OnClick({R.id.btn_login, R.id.text_forget, R.id.btn_register, R.id.ib_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_login:
                if (checkData()) {
                    clearToken();
//                    startActivity(new Intent(this,MainActivity.class));
                    doSignin();
                }
                break;
            case R.id.text_forget:
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void clearToken() {
        User user = mDbManager.getUser();
        if (user != null) {
            user.setToken("");
            mDbManager.updateUser(user);
        }
    }

    private void doSignin() {
        LoginParam loginParam = new LoginParam();
        loginParam.setAccountType("0");
        loginParam.setUserName(CommonUtils.getString(editPhone));
        loginParam.setPassword(CommonUtils.getString(editPwd));
        wrapHttp(mHttpManager.getPassengerService().login(loginParam))
                .compose(this.<LoginResult>bindToLifecycle())
                .subscribe(new ResultObserver<LoginResult>(this, "正在登陆", true) {
                    @Override
                    public void onSuccess(LoginResult value) {
                        saveUserInfo(value);
                        checkPerimission();
                    }
                });
    }

    private void checkPerimission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.ACCESS_FINE_LOCATION).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(@NonNull Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                } else {
                    finish();
                }
            }
        });
    }

    private void saveUserInfo(LoginResult value) {
        User user = new User();
        user.setPhone(CommonUtils.getString(editPhone));
        user.setPassword(CommonUtils.getString(editPwd));
        user.setToken(value.getToken());
        mDbManager.saveUser(user);
    }

    private boolean checkData() {
        String phone = CommonUtils.getString(editPhone);
        String pwd = CommonUtils.getString(editPwd);
        if (checkMobile(phone) && checkPwd(pwd)) {
            return true;
        }
        return false;
    }

    private boolean checkPwd(String pwd) {
        if (pwd.isEmpty()) {
            ToastUtils.showString(getString(R.string.please_input_pwd));
            return false;
        }
        if (pwd.length() < 6) {
            ToastUtils.showString(getString(R.string.pwd_too_small));
            return false;
        }
        if (pwd.length() > 18) {
            ToastUtils.showString(getString(R.string.pwd_too_long));
            return false;
        }
        return true;
    }


    private boolean checkMobile(String phone) {
        if (phone.isEmpty()) {
            ToastUtils.showString(getString(R.string.please_input_phone));
            return false;
        }
        if (!ValidateUtils.isTelephone(phone)) {
            ToastUtils.showString(getString(R.string.phone_not_yes));
            return false;
        }
        return true;
    }

}
