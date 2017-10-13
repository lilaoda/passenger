package bus.passenger.bean.event;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 * 订单事件
 */

public enum OrderEvent {

    /**
     * 循环获取订单是否被接单 可用
     */
    PULL_RESULT_ENABLE(1),
    /**
     * 循环获取订单是否被接单 不可用
     */
    PULL_RESULT_UNABLE(2);

    private int OrderEventValue;

    OrderEvent(int locationValue) {
        this.OrderEventValue = locationValue;
    }

    public int getLocationValue() {
        return OrderEventValue;
    }
}
