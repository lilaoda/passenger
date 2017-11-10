package bus.passenger.module.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import bus.passenger.R;
import bus.passenger.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ToastUtils;

/**
 * 修改登陆密码第二步
 * Created by lilaoda on 2016/12/15.
 */
public class ChangePwd2Activity extends BaseActivity {

    @BindView(R.id.edit_newpwd1)
    EditText editNewpwd1;
    @BindView(R.id.edit_newpwd2)
    EditText editNewpwd2;
    private String mNewPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login_pwd2);
        ButterKnife.bind(this);
    }


    @OnClick({ R.id.btn_second})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_second:
                if (checkNewPwd()) {
                    doSubmit();
                }
                break;
        }
    }

    private void doSubmit() {
//        showLoadingDialog();
//        RetrofitUtils.getPubService().changeLoginPwd(getAccount().getPassword(), mNewPwd).enqueue(new Callback<HttpResult>() {
//            @Override
//            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
//                dismissLoadingDialog();
//                HttpResult body = response.body();
//                ToastUtils.showString(getApplicationContext(), body.info);
//                if (body.status == 1) {
//                    BaseApplication application = (BaseApplication) getApplication();
//                    application.finishPreviousActivity(ChangePwd2Activity.this);
//                    startActivity(new Intent(ChangePwd2Activity.this, LoginActivity.class));
//                    finish();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<HttpResult> call, Throwable t) {
//                dismissLoadingDialog();
//                ToastUtils.showNetError(getApplicationContext());
//            }
//        });
    }

    private boolean checkNewPwd() {
        mNewPwd = CommonUtils.getString(editNewpwd1);
        String newPwd2 = CommonUtils.getString(editNewpwd2);
        if (TextUtils.isEmpty(mNewPwd) || TextUtils.isEmpty(newPwd2)) {
            ToastUtils.showString(getString(R.string.toast_empty_pwd));
            return false;
        }
        if (!TextUtils.equals(mNewPwd, newPwd2)) {
            ToastUtils.showString(getString(R.string.toast_notsame_pwd));
            return false;
        }
        if (mNewPwd.length() < 6) {
            ToastUtils.showString(getString(R.string.pwd_too_small));
            return false;
        }
        if (mNewPwd.length() > 18) {
            ToastUtils.showString(getString(R.string.pwd_too_long));
            return false;
        }
        return true;

    }
}
