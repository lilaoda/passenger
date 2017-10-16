package bus.passenger.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import bus.passenger.R;
import bus.passenger.base.BaseActivity;
import bus.passenger.base.BaseApplication;
import bus.passenger.bean.LoginResult;
import bus.passenger.bean.RegisterResult;
import bus.passenger.bean.param.LoginParam;
import bus.passenger.bean.param.RegistParam;
import bus.passenger.data.DbManager;
import bus.passenger.data.HttpManager;
import bus.passenger.data.local.entity.User;
import bus.passenger.data.remote.HttpResult;
import bus.passenger.module.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.http.exception.ApiException;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.utils.ValidateUtils;

import static bus.passenger.utils.RxUtils.wrapHttp;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    public static final int ZERO_TIME = 0;
    public static final int LONG_DELAY = 1000;//每次推迟1秒执行

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.identify_code)
    TextView identifyCode;
    @BindView(R.id.edit_pwd)
    EditText editPwd;

    private int mExtraTime = 60;
    private Timer mTimer;
    private TimerTask mTimeTask;

    //更改验证码剩余秒数
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            identifyCode.setText(--mExtraTime + "s");
            if (mExtraTime == ZERO_TIME) {
                mExtraTime = 60;
                mTimer.cancel();
                mTimer.purge();
                mTimeTask = null;
                mTimer = null;
                mHandler.removeCallbacksAndMessages(null);
                identifyCode.setClickable(true);
                identifyCode.setEnabled(true);
                identifyCode.setText(getString(R.string.get_identify_code));
            }
        }
    };
    private HttpManager mHttpManager;
    private DbManager mDbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mHttpManager = HttpManager.instance();
        mDbManager = DbManager.instance();
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }


    private boolean checkPhone() {
        String phone = CommonUtils.getString(editPhone);
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

    private boolean checkPwd() {
        String pwd = CommonUtils.getString(editPwd);
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

    private boolean checkCode() {
        String code = editCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showString(getString(R.string.please_input_code));
            return false;
        }
        if (code.length() != 4) {
            ToastUtils.showString(getString(R.string.input_four_code));
            return false;
        }
        return true;
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        ToastUtils.showString("发送验证码");
    }

    /**
     * 下一步，同时提交手机号和验证码成功后跳到输入密码页面
     */
    private void doNext() {
        RegistParam registParam = new RegistParam();
        registParam.setAccountType(0);
        registParam.setMobile(CommonUtils.getString(editPhone));
        registParam.setPassword(CommonUtils.getString(editPwd));
        registParam.setUserAccount(CommonUtils.getString(editPhone));
        LoginParam loginParam = new LoginParam();
        loginParam.setAccountType("0");
        loginParam.setUserName(CommonUtils.getString(editPhone));
        loginParam.setPassword(CommonUtils.getString(editPwd));

        Observable<HttpResult<RegisterResult>> regist = mHttpManager.getPassengerService().regist(registParam);
        final Observable<HttpResult<LoginResult>> login = mHttpManager.getPassengerService().login(loginParam);

        wrapHttp(regist.flatMap(new Function<HttpResult<RegisterResult>, ObservableSource<HttpResult<LoginResult>>>() {
            @Override
            public ObservableSource<HttpResult<LoginResult>> apply(@NonNull HttpResult<RegisterResult> registerResultHttpResult) throws Exception {
                if (registerResultHttpResult.isResult()) {
                    return login;
                } else {
                    throw new ApiException(registerResultHttpResult.getMessage());
                }
            }
        })).compose(this.<LoginResult>bindToLifecycle())
                .subscribe(new ResultObserver<LoginResult>(this, "正在注册...", true) {
                    @Override
                    public void onSuccess(LoginResult value) {
                        saveUserInfo(value);
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                        BaseApplication.getInstance().finishTheActivity(LoginActivity.class);
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

    @OnClick({R.id.identify_code, R.id.btn_next, R.id.ib_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.identify_code:
                if (checkPhone()) {
                    sendCode();
                }
                break;
            case R.id.btn_next:
                if (checkPhone() && checkPwd()) {
                    clearToken();
                    doNext();
                }
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


    /**
     * 开启定时器
     */
    private void beginTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimeTask == null) {
            mTimeTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessageDelayed(ZERO_TIME, ZERO_TIME);
                }
            };
        }
        mTimer.schedule(mTimeTask, ZERO_TIME, LONG_DELAY);


//        subscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(20);
//                for (int i = 19; i > 0; i--) {
//                    Thread.sleep(1000);
//                    e.onNext(i);
//                }
//                e.onComplete();
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        identifyCode.setText(integer + "s");
//                        if (integer == 1) {
//                            mExtraTime = 20;
//                            identifyCode.setClickable(true);
//                            identifyCode.setEnabled(true);
//                            identifyCode.setText(getString(R.string.get_identify_code));
//                        }
//                    }
//                });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimeTask = null;
            mTimer = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
