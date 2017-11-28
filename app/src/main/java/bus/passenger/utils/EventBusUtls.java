package bus.passenger.utils;

import org.greenrobot.eventbus.EventBus;

import bus.passenger.bean.event.LocationEvent;
import bus.passenger.bean.event.LocationResultEvent;


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
}
