package bus.passenger.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import bus.passenger.R;
import bus.passenger.base.BaseActivity;
import bus.passenger.base.Constants;
import bus.passenger.bean.CancelCarResult;
import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.param.CancelCarParam;
import bus.passenger.bean.param.IsCancelCarParam;
import bus.passenger.data.AMapManager;
import bus.passenger.data.HttpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.ActivityUtils;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.passenger.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 * <p>
 * 正在进行中的订单
 */

public class OrderOngoingActivity extends BaseActivity {

    public static final int STATUS_CAN_CANCEL = 1;
    public static final int STATUS_PAY = 2;
    public static final int STATUS_OVER = 3;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.text_des)
    TextView textDes;

    private int mCurrentStatus = STATUS_CAN_CANCEL;

    private OrderInfo mOrderInfo;
    private OrderOngoingrFragment mOrderOngoingrFragment;
    private HttpManager mHttpManager;
    private AMapManager mAMapManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initToolbar("订单详情");
        initData();
        initView();
    }

    private void initData() {
        mHttpManager = HttpManager.instance();
        mAMapManager = AMapManager.instance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(Constants.ORDER_INFO);
        }
        if (mOrderInfo == null) {
            finish();
        }
        mOrderOngoingrFragment = OrderOngoingrFragment.newInstance(mOrderInfo);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mOrderOngoingrFragment, R.id.fl_content);
    }


    private void initView() {
        int subStatus = mOrderInfo.getSubStatus();
        if (subStatus < 300) {
            showView(STATUS_CAN_CANCEL);
            //开始未到达目的地
        } else if (subStatus == 301) {
            showView(STATUS_CAN_CANCEL);
            //等待司机确认费用
        } else if (subStatus == 400) {
            showView(STATUS_PAY);
            //确认费用了未支付
        } else if (subStatus == 500 || subStatus == 501) {
            //行程已结束已经付费
            showView(STATUS_OVER);
            textDes.setText("行程已结束");
        } else {
            //行程已取消
            ToastUtils.showString("行程已取消");
            finish();
        }
    }

    //订单状态改变通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderInfo event) {

    }

    private void showView(int status) {
        btnCancel.setVisibility(status == STATUS_CAN_CANCEL ? View.VISIBLE : View.GONE);
        btnPay.setVisibility(status == STATUS_PAY ? View.VISIBLE : View.GONE);
        textDes.setVisibility(status == STATUS_OVER ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.btn_cancel, R.id.btn_pay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel:
                isCanCancelCar();
                break;
            case R.id.btn_pay:
                pay();
                break;
        }
    }

    private void pay() {
        ToastUtils.showString("支付宝或微信支付");
    }

    private void isCanCancelCar() {
        IsCancelCarParam param = new IsCancelCarParam();
        param.setOrderUuid(mOrderInfo.getOrderUuid());
        wrapHttp(mHttpManager.getPassengerService().isCancelCar(param))
                .compose(this.<OrderInfo>bindToLifecycle())
                .subscribe(new ResultObserver<OrderInfo>(this, "正在加载...", true) {
                    @Override
                    public void onSuccess(OrderInfo value) {
                        cancelCallCar();
                    }
                });
    }

    private void cancelCallCar() {
        CancelCarParam cancelCarParam = new CancelCarParam();
        cancelCarParam.setOrderUuid(mOrderInfo.getOrderUuid());
        wrapHttp(mHttpManager.getPassengerService().cancelCar(cancelCarParam))
                .compose(this.<CancelCarResult>bindToLifecycle())
                .subscribe(new ResultObserver<CancelCarResult>(this, "正在取消订单...", true) {
                    @Override
                    public void onSuccess(CancelCarResult value) {
                        ToastUtils.showString("取消成功");
                        finish();
                    }
                });
    }
}
