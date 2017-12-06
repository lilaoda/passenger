package bus.passenger.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

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
    private IWXAPI wxapi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initToolbar("订单详情");
        initData();
        initView();
        //  registWeiPay();
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
        } else if (subStatus == 400) {
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
            if (mOrderInfo.getSubStatus() == 400) {
                textDes.setText("" + event.getTotalFare());
            }
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

    private void registWeiPay() {
        wxapi = WXAPIFactory.createWXAPI(getApplicationContext(), null);
        // 将该app注册到微信
        wxapi.registerApp("wxd930ea5d5a258f4f");
    }

    private void pay() {
        ToastUtils.showString("支付宝或微信支付");
//        PayReq req = new PayReq();
//        req.appId = "wxf8b4f85f3a794e77";  // 测试用appId
//        PayReq request = new PayReq();
//        request.appId = "wxd930ea5d5a258f4f";//微信开放平台审核通过的应用APPID
//        request.partnerId = "1900000109";//微信支付分配的商户号
//        request.prepayId = "1101000000140415649af9fc314aa427";//微信返回的支付交易会话ID
//        request.packageValue = "Sign=WXPay";//暂填写固定值Sign=WXPay
//        request.nonceStr = "1101000000140429eb40476f8896f4c9";//随机字符串，不长于32位。推荐随机数生成算法
//        request.timeStamp = "1398746574";//时间戳
//        request.sign = "7FFECB600D7157C5AA49810D2D8F28BC2811827B";//签名
//        Toast.makeText(this, "正常调起支付", Toast.LENGTH_SHORT).show();
//        // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信
//        wxapi.sendReq(req);
//        //0 成功 展示成功页面
//        //-1 错误 可能的原因：签名错误、未注册APPID、项目设置APPID不正确、注册的APPID与设置的不匹配、其他异常等。
//       // -2 用户取消 无需处理。发生场景：用户不支付了，点击取消，返回APP。
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
