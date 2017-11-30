package bus.passenger.module.route;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bus.passenger.R;
import bus.passenger.adapter.OrderListAdapter;
import bus.passenger.base.BaseActivity;
import bus.passenger.base.Constants;
import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.param.PageParam;
import bus.passenger.data.HttpManager;
import bus.passenger.module.order.OrderOngoingActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.http.ResultObserver;

import static bus.passenger.utils.RxUtils.wrapHttp;
import static lhy.lhylibrary.base.LhyApplication.getContext;

/**
 * Created by Liheyu on 2017/9/26.
 * 我的行程
 */

public class RouteActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private HttpManager mHttpManager;
    private OrderListAdapter mOrderAdapter;
    private PageParam mPageParam;
    private int mCurrentPage = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        initToolbar(getString(R.string.my_route));
        mHttpManager = HttpManager.instance();
        EventBus.getDefault().register(this);
        initView();
        getDataFormServer();
    }

    private void getDataFormServer() {
        mPageParam = new PageParam();
        mPageParam.setPageNo(1);
        mPageParam.setPageSize(15);
        wrapHttp(mHttpManager.getPassengerService().findTrip(mPageParam))
                .compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>(this, "正在加载...", true) {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        refreshAdapter(value);
                    }
                });
    }

    private void sort(List<OrderInfo> list) {
        Collections.sort(list, new Comparator<OrderInfo>() {
            @Override
            public int compare(OrderInfo o1, OrderInfo o2) {
                return o1.getSubStatus() - o2.getSubStatus();
            }
        });
    }

    private void refreshAdapter(List<OrderInfo> value) {
        sort(value);
        mOrderAdapter.setNewData(value);
        mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mOrderAdapter = new OrderListAdapter(null);
        mOrderAdapter.setEmptyView(R.layout.emptyview_route, (ViewGroup) recyclerView.getParent());
        recyclerView.setAdapter(mOrderAdapter);
        mOrderAdapter.setOnItemClickListener(this);
        mOrderAdapter.setOnLoadMoreListener(this, recyclerView);
        refreshLayout.setOnRefreshListener(this);
        //这句要想有效果必须放在监听器之后 要想不满屏时不能上拉加载，需要放在监听器之后 然后每次刷新数据都要再调用
        mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void getData(final boolean isLoadMore) {
        if (!isLoadMore) {
            mPageParam.setPageNo(1);
        } else {
            mPageParam.setPageNo(mCurrentPage + 1);
        }

        wrapHttp(mHttpManager.getPassengerService().findTrip(mPageParam))
                .compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>(true) {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        if (isLoadMore) {
                            if (value == null || value.size() == 0) {
                                mOrderAdapter.loadMoreEnd();
                            } else {
                                mCurrentPage++;
                                mOrderAdapter.addData(value);
                                mOrderAdapter.loadMoreComplete();
                            }
                        } else {
                            refreshLayout.setRefreshing(false);
                            refreshAdapter(value);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        if (isLoadMore) {
                            mOrderAdapter.loadMoreFail();
                        } else {
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        getData(false);
    }


    @Override
    public void onLoadMoreRequested() {
        getData(true);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        Logger.d(mOrderAdapter.getData().get(position));
        startActivity(new Intent(this, OrderOngoingActivity.class).putExtra(Constants.ORDER_INFO, mOrderAdapter.getData().get(position)));
    }


    /**
     * 订单信息有改变 在订单详情，行程中的订单 对订单状态就行了更改，此时回到这个页面，需要刷新数据
     *
     * @param orderInfo 更改的订单信息,备用
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(OrderInfo orderInfo) {
        getDataFormServer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
