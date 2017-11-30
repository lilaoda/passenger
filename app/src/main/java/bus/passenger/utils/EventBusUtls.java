package bus.passenger.utils;

import org.greenrobot.eventbus.EventBus;

import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.OrderStatus;
import bus.passenger.bean.event.LocationEvent;
import bus.passenger.bean.event.LocationResultEvent;
import bus.passenger.bean.event.OrderEvent;
import bus.passenger.module.route.RouteActivity;
import bus.passenger.service.PassengerService;


/**
 * Created by Lilaoda on 2017/11/10.
 * Email:749948218@qq.com
 */

public class EventBusUtls {


    /**
     * 开启定位事件 的定位服务里接受通知 {@link bus.passenger.module.main.MainFragment}
     * @param event 定位事件
     */
    public static void notifyLocation(LocationEvent event){
        EventBus.getDefault().post(event);
    }

    /**
     * 发送定位结果 {@link bus.passenger.service.PassengerService}
     * @param event 定位结果
     */
    public static void notifyLocationResult(LocationResultEvent event){
        EventBus.getDefault().post(event);
    }

    /**
     * 通知是否轮循获取订单状态 {@link bus.passenger.service.PassengerService}
     * @param event 定位结果
     */
    public static void notifyPullOrderStatus(OrderEvent event){
        EventBus.getDefault().post(event);
    }

    /**
     * 通知订单信息有改变 在我的行程 订单列表接受通知{@link RouteActivity}
     * @param orderInfo 改变的订单信息
     */
    public static void notifyOrderChanged(OrderInfo orderInfo){
        EventBus.getDefault().post(orderInfo);
    }

    /**
     * 发送订单状态{@link PassengerService}
     * @param orderStatus 改变的订单信息
     */
    public static void notifyOrderStatus(OrderStatus orderStatus){
        EventBus.getDefault().post(orderStatus);
    }
}
