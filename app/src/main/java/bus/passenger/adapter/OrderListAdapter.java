package bus.passenger.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import bus.passenger.R;
import bus.passenger.bean.OrderInfo;


/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * 我的行程页面
 */

public class OrderListAdapter extends BaseQuickAdapter<OrderInfo, BaseViewHolder> {

    public OrderListAdapter(@Nullable List<OrderInfo> data) {
        super(R.layout.item_order_list, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderInfo item) {
        helper.setText(R.id.text_start, "始点: " + item.getOriginAddress())
                .setText(R.id.text_end, "终点: " + item.getDestAddress())
                .setText(R.id.text_orderid, "订单编号: " + item.getOrderUuid())
                .setText(R.id.text_status,  getTitle(item.getSubStatus()));
    }

    private String getOrderStatus(int status) {
        // 订单主状态1：订单初识化,2订单进行中3：订单结束（待支付）4：支付完成5.取消
        if (status == 1) {
            return "订单初始化";
        } else if (status == 2) {
            return "订单进行中";
        } else if (status == 3) {
            return "订单结束（待支付）";
        } else if (status == 4) {
            return "支付完成";
        } else {
            return "取消";
        }
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


}
