package bus.passenger.module.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import bus.passenger.R;
import bus.passenger.adapter.OrderListAdapter;
import bus.passenger.base.BaseActivity;
import bus.passenger.bean.OrderInfo;
import bus.passenger.data.HttpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.http.ResultObserver;

import static bus.passenger.utils.RxUtils.wrapHttp;
import static lhy.lhylibrary.base.LhyApplication.getContext;

/**
 * Created by Liheyu on 2017/9/26.
 * 我的行程
 *
 */

public class RouteActivity extends BaseActivity implements BaseQuickAdapter.RequestLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener, BaseQuickAdapter.OnItemClickListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private HttpManager mHttpManager;
    private OrderListAdapter mOrderAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        initToolbar(getString(R.string.my_route));
        mHttpManager = HttpManager.instance();
        initView();
        getDataFormServer();
    }

    private void getDataFormServer() {
        wrapHttp(mHttpManager.getPassengerService().findTrip())
                .compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>(this, "正在加载...", true) {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        refreshAdapter(value);
                    }
                });
    }

    private void refreshAdapter(List<OrderInfo> value) {
        mOrderAdapter.setNewData(value);
      //  mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mOrderAdapter = new OrderListAdapter(null);
        mOrderAdapter.setEmptyView(R.layout.emptyview_route, (ViewGroup) recyclerView.getParent());
        recyclerView.setAdapter(mOrderAdapter);
        mOrderAdapter.setOnItemClickListener(this);
       // 第一期先不做分页
       // mOrderAdapter.setOnLoadMoreListener(this, recyclerView);
        refreshLayout.setOnRefreshListener(this);
        //这句要想有效果必须放在监听器之后 要想不满屏时不能上拉加载，需要放在监听器之后 然后每次刷新数据都要再调用
      //  mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void refresh(final boolean isLoadMore) {
        wrapHttp(mHttpManager.getPassengerService().findTrip())
                .compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>(true) {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        if(isLoadMore){
                            if(value==null||value.size()==0){
                                mOrderAdapter.loadMoreEnd();
                            }else {
                                mOrderAdapter.addData(value);
                                mOrderAdapter.loadMoreComplete();
                            }
                        }else {
                            refreshLayout.setRefreshing(false);
                            refreshAdapter(value);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        if(isLoadMore){
                            mOrderAdapter.loadMoreFail();
                        }else {
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        refresh(false);
    }


    @Override
    public void onLoadMoreRequested() {
     //   refresh(true);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
      //  ToastUtils.showString(position+"");
    }
}
