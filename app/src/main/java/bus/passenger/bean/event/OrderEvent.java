package bus.passenger.bean.event;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 * 订单事件
 */

public class OrderEvent {

    /**
     * 循环获取订单是否被接单 可用
     */
    public static final int PULL_ORDER_STATUS_ENABLE = 1;
    /**
     * 循环获取订单是否被接单 不可用
     */
    public static final int PULL_ORDER_STATUS_UNABLE = 2;

    private int pullOrderStatus;
    private String orderUuid;

    public OrderEvent() {
    }

    public OrderEvent(int pullOrderStatus, String orderUuid) {
        this.pullOrderStatus = pullOrderStatus;
        this.orderUuid = orderUuid;
    }

    public int getPullOrderStatus() {
        return pullOrderStatus;
    }

    public void setPullOrderStatus(int pullOrderStatus) {
        this.pullOrderStatus = pullOrderStatus;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    @Override
    public String toString() {
        return "OrderEvent{" +
                "pullOrderStatus=" + pullOrderStatus +
                ", orderUuid='" + orderUuid + '\'' +
                '}';
    }
}
