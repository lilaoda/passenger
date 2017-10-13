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
        helper.setText(R.id.text_start, "始点: "+item.getOriginAddress())
                .setText(R.id.text_end, "终点: "+item.getDestAddress())
                .setText(R.id.text_orderid, "订单编号: " + item.getOrderUuid())
                .setText(R.id.text_status, "订单状态：" + item.getSubStatus() + "");
    }
}
