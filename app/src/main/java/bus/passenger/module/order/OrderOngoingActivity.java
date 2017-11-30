package bus.passenger.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import bus.passenger.bean.OrderStatus;
import bus.passenger.bean.event.OrderEvent;
import bus.passenger.bean.param.CancelCarParam;
import bus.passenger.bean.param.IsCancelCarParam;
import bus.passenger.data.HttpManager;
import bus.passenger.utils.EventBusUtls;
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
    public static final int STATUS_NORMAL = 3;

    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.btn_pay)
    Button btnPay;
    @BindView(R.id.text_des)
    TextView textDes;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    private OrderInfo mOrderInfo;
    private OrderOngoingrFragment mOrderOngoingrFragment;
    private HttpManager mHttpManager;

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
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(Constants.ORDER_INFO);
        }
        if (mOrderInfo == null) {
            finish();
        }
        OrderEvent orderEvent = new OrderEvent(OrderEvent.PULL_ORDER_STATUS_ENABLE, mOrderInfo.getOrderUuid());
        EventBusUtls.notifyPullOrderStatus(orderEvent);
        mOrderOngoingrFragment = OrderOngoingrFragment.newInstance(mOrderInfo);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mOrderOngoingrFragment, R.id.fl_content);
    }

    private void initView() {
        int subStatus = mOrderInfo.getSubStatus();
        if (subStatus < 300) {
            showView(STATUS_CAN_CANCEL);
        }else if (subStatus == 400) {
            showView(STATUS_PAY);
        } else {
            showView(STATUS_NORMAL);
        }
        textDes.setText(getTitle(subStatus));
    }

    /**
     * @param subStatus 订单子状态
     *                  订单子状态(100.等待应答（拼车中）
     *                  200.等待接驾-预约 201.等待接驾-已出发未到达 202.等待接驾-已到达
     *                  210.出发接乘客 220.司机到达等待乘客
     *                  300.行程开始未到达目的地 301到达目的地未确认费用
     *                  400.待支付
     *                  500.已完成(待评价) 501.已完成-已评价
     *                  600.取消-自主取消 601.取消-后台取消 602.取消-应答前取消)
     */
    private String getTitle(int subStatus) {
        String title = "";
        switch (subStatus) {
            case 100:
                title = "等待应答";
                break;
            case 200:
                title = "等待接驾(预约)";
                break;
            case 201:
                title = "司机已出发(预约)";
                break;
            case 202:
                title = "等待乘客上车(预约)";
                break;
            case 210:
                title = "司机已出发";
                break;
            case 220:
                title = "等待乘客上车";
                break;
            case 300:
                title = "行程进行中";
                break;
            case 301:
                title = "待确认费用";
                break;
            case 400:
                title = "待支付";
                break;
            case 500:
                title = "已完成(待评价)";
                break;
            case 501:
                title = "已完成（已评价）";
                break;
            case 600:
            case 601:
            case 602:
                title = "订单已取消";
                break;
        }
        return title;
    }

    private void showView(int status) {
        btnCancel.setVisibility(status == STATUS_CAN_CANCEL ? View.VISIBLE : View.GONE);
        btnPay.setVisibility(status == STATUS_PAY ? View.VISIBLE : View.GONE);
    }

    //订单状态改变通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderStatus event) {
        if (TextUtils.equals(event.getOrderUuid(), mOrderInfo.getOrderUuid()) && event.getSubStatus() != mOrderInfo.getSubStatus()) {
            mOrderInfo.setSubStatus(event.getSubStatus());
            EventBusUtls.notifyOrderChanged(mOrderInfo);
            initView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        OrderEvent event = new OrderEvent();
        event.setPullOrderStatus(OrderEvent.PULL_ORDER_STATUS_UNABLE);
        EventBusUtls.notifyPullOrderStatus(event);
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
                        EventBusUtls.notifyOrderChanged(new OrderInfo());
                        finish();
                    }
                });
    }
}
