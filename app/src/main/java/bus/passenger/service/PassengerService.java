package bus.passenger.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bus.passenger.R;
import bus.passenger.base.Constants;
import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.OrderStatus;
import bus.passenger.bean.event.LocationEvent;
import bus.passenger.bean.event.LocationResultEvent;
import bus.passenger.bean.event.OrderEvent;
import bus.passenger.bean.param.OrderStatusParam;
import bus.passenger.data.HttpManager;
import bus.passenger.module.order.OrderDetailActivity;
import bus.passenger.utils.EventBusUtls;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.service.AliveService;

import static bus.passenger.utils.RxUtils.wrapHttp;
import static lhy.lhylibrary.base.LhyApplication.getContext;


/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 * <p>
 * 订单服务接口 用于循环获取司机是否接单
 */

public class PassengerService extends AliveService implements AMapLocationListener {

    public static final String TAG = "PassengerService";
    //默认是北京的经纬度
    public static double latitude = 39.904989;
    public static double longitude = 116.405285;

    private AMapLocationClient mlocationClient;
    private boolean isNeedResult = false;

    /**
     * 获取推送订单的间隔
     */
    public static final int INTERVAL_PULL_ORDER = 10;

    private AlertDialog mOrderDialog;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNotificationManager;
    private HttpManager mHttpManager;
    private Disposable mOrderDisposable;
    private Disposable mOrderStatusDisposable;
    private String mOrderUuid;
    private OrderStatusParam mPullOrderStatusParam;

    public static void start(Activity context) {
        context.startService(new Intent(context, PassengerService.class));
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mHttpManager = HttpManager.instance();
        initNotification();
        initLocationClient();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mlocationClient != null) {
            mlocationClient.startLocation();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initLocationClient() {
        mlocationClient = new AMapLocationClient(getContext());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setNeedAddress(true);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setInterval(3000); //设置定位间隔,单位毫秒,默认为2000ms
        mlocationClient.setLocationOption(mLocationOption);   //设置定位参数
        mlocationClient.setLocationListener(this);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            Log.i(TAG, "onLocationChanged: " + aMapLocation.toStr());
            if (aMapLocation.getErrorCode() == 0) {
                longitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();

                if (isNeedResult) {
                    EventBusUtls.notifyLocationResult(new LocationResultEvent(true));
                    isNeedResult = false;
                }
            } else {
                if (isNeedResult) {
                    String errorInfo = "定位失败:" + aMapLocation.getErrorCode() + "," + aMapLocation.getErrorInfo();
                    EventBusUtls.notifyLocationResult(new LocationResultEvent(false, errorInfo));
                    isNeedResult = false;
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationEvent event) {
        switch (event) {
            case LOCATION_ENABLE:
                mlocationClient.startLocation();
                break;
            case LOCATION_UNABLE:
                mlocationClient.stopLocation();
                break;
            case LOCATION_REFRESH:
                isNeedResult = true;
                mlocationClient.startLocation();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderEvent event) {
        Log.i(TAG, "onMessageEvent: OrderEvent" + event.toString());
        switch (event.getPullOrderStatus()) {
            case OrderEvent.PULL_ORDER_STATUS_ENABLE:
                this.mOrderUuid = event.getOrderUuid();
//                startPullOrderResult();
                startPullOrderStatus();
                break;
            case OrderEvent.PULL_ORDER_STATUS_UNABLE:
                this.mOrderUuid = "";
                stopPullOrderStatus();
//                stopPullOrderResult();
                break;
        }
    }

    private void stopPullOrderStatus() {
        Log.i(TAG, "stopPullOrderStatus: " + "取消循环拉取订单订单状态");
        if (mOrderStatusDisposable != null && !mOrderStatusDisposable.isDisposed()) {
            mOrderStatusDisposable.dispose();
        }
    }

    private void stopPullOrderResult() {
        Log.i(TAG, "stopPullOrder: " + "取消循环拉取订单是否被接单");
        if (mOrderDisposable != null && !mOrderDisposable.isDisposed()) {
            mOrderDisposable.dispose();
        }
    }

    private void startPullOrderResult() {
        Log.i(TAG, "stopPullOrder: " + "开始循环拉取订单是否被接单");
        if (mOrderDisposable != null && !mOrderDisposable.isDisposed()) return;
        mOrderDisposable = Flowable.interval(INTERVAL_PULL_ORDER, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        pullOrderResult();
                    }
                });
    }

    private void pullOrderResult() {
        wrapHttp(mHttpManager.getPushService().pushOrderConfirm()).subscribe(new ResultObserver<List<OrderInfo>>() {
            @Override
            public void onSuccess(List<OrderInfo> value) {
                EventBus.getDefault().post(value.get(0));
            }
        });
    }

    private void startPullOrderStatus() {
        Log.i(TAG, "stopPullOrder: " + "开始循环拉取订单状态");
        if (mOrderStatusDisposable != null && !mOrderStatusDisposable.isDisposed()) return;
        mOrderStatusDisposable = Flowable.interval(INTERVAL_PULL_ORDER, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        pullOrderStatus();
                    }
                });
    }

    private void pullOrderStatus() {
        if (mOrderUuid.isEmpty()) {
            return;
        }
//        if (mPullOrderStatusParam == null) {
//            mPullOrderStatusParam = new OrderStatusParam();
//        }
//        mPullOrderStatusParam.setOrderUuid(mOrderUuid);
        wrapHttp(mHttpManager.getPushService().pushOrderStatus(mOrderUuid)).subscribe(new ResultObserver<OrderStatus>() {
            @Override
            public void onSuccess(OrderStatus value) {
                if (value != null) {
                    EventBusUtls.notifyOrderStatus(value);
                }
            }
        });
    }

    //初始化通知
    private void initNotification() {
        mNotifyBuilder = new NotificationCompat.Builder(this);
        mNotifyBuilder.setContentTitle("订单通知")
                .setContentText("点击查看详情")
                .setSmallIcon(R.mipmap.loading)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.mis_asv))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true);
        mNotificationManager = (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void notifyOrder(OrderInfo orderInfo) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(Constants.ORDER_INFO, orderInfo);
        Notification notification = mNotifyBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        if (mOrderDialog != null) {
            mOrderDialog.dismiss();
            mOrderDialog = null;
        }
        if (mOrderDisposable != null) {
            if (!mOrderDisposable.isDisposed()) {
                mOrderDisposable.dispose();
            }
            mOrderDisposable = null;
        }
    }

    /**
     * 判断应用是否处于后台
     */
    public boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Logger.i(TAG, "处于前台：" + appProcess.processName);
                    return false;
                } else {
                    Logger.i(TAG, "处于后台：" + appProcess.processName);
                    return true;
                }
            }
        }
        return true;
    }
}
