package bus.passenger.module.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import bus.passenger.R;
import bus.passenger.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Lilaoda on 2017/10/12.
 * Email:749948218@qq.com
 */

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.text_des)
    TextView textDes;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        initToolbar("订单详情");

    }
}
