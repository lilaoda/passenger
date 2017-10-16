package bus.passenger.service;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.concurrent.TimeUnit;

import bus.passenger.R;
import bus.passenger.base.Constants;
import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.event.OrderEvent;
import bus.passenger.data.HttpManager;
import bus.passenger.module.order.OrderDetailActivity;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import lhy.lhylibrary.base.LhyApplication;
import lhy.lhylibrary.http.ResultObserver;

import static bus.passenger.utils.RxUtils.wrapHttp;


/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 * <p>
 * 订单服务接口 用于循环获取司机是否接单
 */

public class OrderService extends Service {

    public static final String TAG = "orderService";

    /**
     * 获取推送订单的间隔
     */
    public static final int INTERVAL_PULL_ORDER = 10;

    private AlertDialog mOrderDialog;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNotificationManager;
    private HttpManager mHttpManager;
    private Disposable mOrderDisposable;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mHttpManager = HttpManager.instance();
        initNotification();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand: ");
        return super.onStartCommand(intent, flags, startId);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderEvent event) {
        Log.i(TAG, "onMessageEvent: OrderEvent" + event.getLocationValue());
        switch (event) {
            case PULL_RESULT_ENABLE:
                startPullOrderResult();
                break;
            case PULL_RESULT_UNABLE:
                stopPullOrderResult();
                break;
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
//                if (value.size() > 0) {
//                    LhyActivity currentActivity = BaseApplication.getInstance().getCurrentActivity();
//                    if (GlobeConstants.ORDER_STATSU == GlobeConstants.ORDER_STATSU_ONDOING || GlobeConstants.DRIVER_STATSU == GlobeConstants.DRIVER_STATSU_REST) {
//                        return;
//                    }
//                    if (isBackground() || currentActivity == null || !currentActivity.isResume()) {
//                        notifyOrder(value.get(0));
//                    } else {
//                        if (GlobeConstants.ORDER_STATSU == GlobeConstants.ORDER_STATSU_NO) {
//                            if (mOrderDialog != null && mOrderDialog.isShowing() || currentActivity instanceof CaptureOrderActivity)
//                                return;
//                            showOrderDialog(currentActivity, value.get(0));
//                        }
//                    }
//                }
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
        mNotificationManager = (NotificationManager) LhyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
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
