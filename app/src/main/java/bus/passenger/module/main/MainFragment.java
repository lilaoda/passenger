package bus.passenger.module.main;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.bigkoo.pickerview.TimePickerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bus.passenger.R;
import bus.passenger.base.BaseApplication;
import bus.passenger.base.Constants;
import bus.passenger.bean.CallCarResult;
import bus.passenger.bean.CancelCarResult;
import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.PoiInfo;
import bus.passenger.bean.event.LocationResultEvent;
import bus.passenger.bean.event.OrderEvent;
import bus.passenger.bean.event.StartLocationEvent;
import bus.passenger.bean.param.CallCarParam;
import bus.passenger.bean.param.CancelCarParam;
import bus.passenger.bean.param.IsCancelCarParam;
import bus.passenger.bean.param.PageParam;
import bus.passenger.data.AMapManager;
import bus.passenger.data.DbManager;
import bus.passenger.data.HttpManager;
import bus.passenger.data.local.entity.User;
import bus.passenger.module.route.RouteActivity;
import bus.passenger.overlay.AMapUtil;
import bus.passenger.overlay.DrivingRouteOverlay;
import bus.passenger.service.LocationService;
import bus.passenger.utils.DialogUtis;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.DateUtils;
import lhy.lhylibrary.utils.ImageFactory;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.view.tablayout.SegmentTabLayout;
import lhy.lhylibrary.view.tablayout.listener.OnTabSelectListener;

import static bus.passenger.utils.RxUtils.wrapAsync;
import static bus.passenger.utils.RxUtils.wrapHttp;

/**
 * Created by Liheyu on 2017/9/12.
 * Email:liheyu999@163.com
 */

public class MainFragment extends AMapFragment implements AMap.OnMapLoadedListener,
        AMap.OnCameraChangeListener, RouteSearch.OnRouteSearchListener, AMap.OnMyLocationChangeListener, LocationSource {

    public static final String TAG = "MainFragment";
    private static final int REQUEST_CITY_START = 101;
    private static final int REQUEST_CITY_END = 102;
    //3--19 越大越精细
    public static final int ZOOM_SCALE_VALUE = 15;
    //定位时间间隔
    public static final int INTERVAL_lOCATION = 3000;
    //页面的三种状态
    private static final int STATUS_SELECT_ADDRESS = 1;//正在选择起始或终点地址
    private static final int STATUS_READY_CALL = 2;//已选好地址，还没点击叫车
    private static final int STATUS_IS_CALLING = 3;//正在叫车中

    @BindView(R.id.text_begin_time)
    TextView textBeginTime;
    @BindView(R.id.ll_cancel)
    LinearLayout llCancel;
    @BindView(R.id.text_start)
    TextView textStart;
    @BindView(R.id.text_end)
    TextView textEnd;
    @BindView(R.id.text_result)
    TextView textResult;
    @BindView(R.id.tabLayout)
    SegmentTabLayout tabLayout;
    @BindView(R.id.text_time)
    TextView textTime;
    @BindView(R.id.ll_address)
    LinearLayout llAddress;
    @BindView(R.id.btn_commit)
    Button btnCommit;
    @BindView(R.id.ll_result)
    LinearLayout llResult;
    @BindView(R.id.line)
    View line;
    @BindView(R.id.ll_call_car)
    LinearLayout llCallCar;
    @BindView(R.id.btn_cancel)
    Button btnCancel;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    TextureMapView mapView;
    @BindView(R.id.text_passenger)
    TextView textPassenger;

    private PoiInfo mStartPoiInfo;//用户当前选择的起始位置
    private PoiInfo mTargetPoiInfo;//用户当前选择的目的位置
    private AMap mAMap;
    private Marker mScreenMarker;
    private BottomSheetDialog bottomSheetDialog;
    private MyLocationStyle mMyLocationStyle;
    private Unbinder mUnbinder;
    private HttpManager mHttpManager;
    private AMapManager mAMapManager;
    private Disposable mTimeDisposable;
    private AlertDialog mCancelCarDialog;
    private OrderInfo mOndoingOrder;

    private String[] mTitles = {"现在", "预约"};
    private boolean isNeedLocation = true;//是否根据经纬度查询POI
    private int mCurrentStatus = 1;//目前的页面状态
    private int mTextTimeHeight;
    private String mOrderUuid;
    private AlertDialog mAlertDialog;
    private TimePickerView mTimePickerView;
    private User mUser;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mRootView = inflater.inflate(R.layout.fragment_main, null);
        mUnbinder = ButterKnife.bind(this, mRootView);
        initData();
        addMapView(savedInstanceState);
        initView();
        initMap();
        initLocation();
        return mRootView;
    }

    private void initData() {
        mHttpManager = HttpManager.instance();
        mAMapManager = AMapManager.instance();
        mUser = DbManager.instance().getUser();
    }

    private void addMapView(@Nullable Bundle savedInstanceState) {
        LatLng centerMyPoint = new LatLng(LocationService.latitude, LocationService.longitude);
        AMapOptions mapOptions = new AMapOptions();
        mapOptions.camera(new CameraPosition(centerMyPoint, ZOOM_SCALE_VALUE, 0, 0));
        mapView = new TextureMapView(getContext(), mapOptions);
        flRoot.addView(mapView, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mapView.onCreate(savedInstanceState);
    }


    private void initView() {
        setViewStatus(STATUS_SELECT_ADDRESS);
        textPassenger.setText(mUser.getPhone());
        mTextTimeHeight = textTime.getLayoutParams().height;
        textTime.getLayoutParams().height = 0;
        textTime.requestLayout();
        textStart.setText(getString(R.string.is_locationing));
        tabLayout.setTabData(mTitles);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 1) {
                    showTimeTextView(true);
                } else {
                    showTimeTextView(false);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void setViewStatus(int status) {
        mCurrentStatus = status;
        switch (status) {
            case STATUS_SELECT_ADDRESS:
                resetToolBar(true);
                llAddress.setVisibility(View.VISIBLE);
                llResult.setVisibility(View.GONE);
                textResult.setText("");
                textEnd.setText("");
                btnCommit.setVisibility(View.GONE);
                llCallCar.setVisibility(View.VISIBLE);
                llCancel.setVisibility(View.GONE);
                btnCommit.setVisibility(View.GONE);
                break;
            case STATUS_READY_CALL:
                resetToolBar(false);
                llAddress.setVisibility(View.GONE);
                llCancel.setVisibility(View.GONE);
                llCallCar.setVisibility(View.VISIBLE);
                llResult.setVisibility(View.VISIBLE);
                btnCommit.setVisibility(View.VISIBLE);
                break;
            case STATUS_IS_CALLING:
//                resetToolBar(false);
                textBeginTime.setText("");
                llCallCar.setVisibility(View.GONE);
                llCancel.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void showTimeTextView(boolean b) {
        ValueAnimator valueAnimator;
        if (b) {
            valueAnimator = ValueAnimator.ofInt(0, mTextTimeHeight);
        } else {
            valueAnimator = ValueAnimator.ofInt(mTextTimeHeight, 0);
        }
        valueAnimator.setDuration(400);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) animation.getAnimatedValue();
                textTime.getLayoutParams().height = value;
                textTime.requestLayout();
                if (value > 0) {
                    if (line.getVisibility() != View.VISIBLE)
                        line.setVisibility(View.VISIBLE);
                } else {
                    line.setVisibility(View.GONE);
                }
            }
        });
        valueAnimator.start();
    }

    /**
     * 初始化AMap对象
     */
    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
            mAMap.setOnMapLoadedListener(this);
            mAMap.setOnMyLocationChangeListener(this);
            mAMap.setOnCameraChangeListener(this);
        }
    }

    private void initLocation() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_map_location);
        ImageFactory imageFactory = new ImageFactory();
        Bitmap ratio = imageFactory.ratio(bitmap, CommonUtils.dp2px(getContext(), 5), CommonUtils.dp2px(getContext(), 5));
        BitmapDescriptor pointIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMyLocationStyle = new MyLocationStyle();
        mMyLocationStyle.myLocationIcon(pointIcon);
        mMyLocationStyle.strokeWidth(0F);
        mMyLocationStyle.radiusFillColor(getResources().getColor(android.R.color.transparent));
        mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        mMyLocationStyle.showMyLocation(true);
//        mAMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_SCALE_VALUE));
        moveMapToCurrentLocation();
        mAMap.setMyLocationStyle(mMyLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        mAMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void searchPoiInfo(LatLng target) {
        wrapAsync(AMapManager.instance().search(target))
                .compose(this.<PoiInfo>bindToLifecycle())
                .subscribe(new ResultObserver<PoiInfo>(true) {
                    @Override
                    public void onSuccess(PoiInfo value) {
                        mStartPoiInfo = value;
                        textStart.setText(mStartPoiInfo.getTitle());
                    }
                });
    }

    private void addMarkerInScreenCenter() {
        LatLng latLng = mAMap.getCameraPosition().target;
        Point screenPosition = mAMap.getProjection().toScreenLocation(latLng);
        mScreenMarker = mAMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.purple_pin)));
        //设置Marker在屏幕上,不跟随地图移动
        mScreenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
    }

    @Override
    public void onPOIClick(Poi poi) {
        mAMap.clear(true);
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(poi.getCoordinate());
        TextView textView = new TextView(getContext());
        textView.setText("到" + poi.getName() + "去");
        textView.setTextSize(CommonUtils.dp2px(getContext(), 11));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
        textView.setBackgroundResource(R.drawable.custom_info_bubble);
        markOptiopns.icon(BitmapDescriptorFactory.fromView(textView));
        mAMap.addMarker(markOptiopns);
    }

    @OnClick({R.id.text_start, R.id.text_end, R.id.ibtn_refresh, R.id.btn_commit, R.id.btn_cancel, R.id.text_time
            , R.id.text_change_passenger})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_start:
                startActivityForResult(REQUEST_CITY_START);
                break;
            case R.id.text_end:
                startActivityForResult(REQUEST_CITY_END);
                break;
            case R.id.ibtn_refresh:
                refreshLocation();
                break;
            case R.id.text_time:
                showTimeDialog();
                break;
            case R.id.btn_commit:
                getTrip();
//                callCar();
                break;
            case R.id.btn_cancel:
                isCanCancelCar();
//                if (mOndoingOrder != null) {
//                    ToastUtils.showString("司机已接单，不允许取消");
//                } else {
//                    showCancelCarDialog();
//                }
                break;
            case R.id.text_change_passenger:
                changePassenger();
                break;
        }
    }

    private void changePassenger() {
        ToastUtils.showString("更换乘车人");
    }

    private void getTrip() {
        //TODO 第一期主动获取订单列表 ，发现有正在进行中的订单，不让下单
        PageParam pageParam = new PageParam();
        pageParam.setPageNo(1);
        pageParam.setPageSize(15);
        wrapHttp(mHttpManager.getPassengerService().findTrip(pageParam))
                .compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>(getActivity(), "正在加载...", true) {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        boolean showDialog = false;
                        for (OrderInfo order : value) {
                            if (order.getMainStatus() == 2 || order.getMainStatus() == 1) {
                                showDialog = true;
                                break;
                            }
                        }
                        if (showDialog) {
                            showCannotDialog();
                        } else {
                            callCar();
                        }
                    }
                });
    }

    private void showCannotDialog() {
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(getActivity())
                    .setMessage("您有进行中的订单，请先完成！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(getActivity(), RouteActivity.class));
                        }
                    }).setNegativeButton("取消", null)
                    .show();
        } else {
            mAlertDialog.show();
        }
    }

    private void callCar() {
        CallCarParam callCarParam = new CallCarParam();
        callCarParam.setCarType("0");//轿车类型 0默认
        callCarParam.setMapType(Constants.MAP_TYPE_GAODE);
        callCarParam.setSource(1);//来源全部为APP
        callCarParam.setTypeTime(1);//第一期全部为实时订单
        callCarParam.setDestAddress(mTargetPoiInfo.getTitle());
        callCarParam.setDestLat(mTargetPoiInfo.getLatitude());
        callCarParam.setDestLng(mTargetPoiInfo.getLongitude());
        callCarParam.setOriginAddress(mStartPoiInfo.getTitle());
        callCarParam.setOriginLat(mStartPoiInfo.getLatitude());
        callCarParam.setOriginLng(mStartPoiInfo.getLongitude());
        callCarParam.setOriginCityUuid("020");
        wrapHttp(mHttpManager.getPassengerService().callCar(callCarParam))
                .compose(this.<CallCarResult>bindToLifecycle())
                .subscribe(new ResultObserver<CallCarResult>(getActivity(), "正在下单...", true) {
                    @Override
                    public void onSuccess(CallCarResult value) {
                        mOrderUuid = value.getOrderUuid();
                        //叫车成功
                        setViewStatus(STATUS_IS_CALLING);
                        setPullOrderResult(true);
                        updateTimer();
                    }
                });
    }

    //显示下单时间
    private void updateTimer() {
        mTimeDisposable = Flowable.interval(1, TimeUnit.SECONDS)
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        int i = aLong.intValue();
                        return i / 60 + "分" + i % 60 + "秒";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@io.reactivex.annotations.NonNull String s) throws Exception {
                        textBeginTime.setText(s);
                    }
                });

    }

    private void setPullOrderResult(boolean isEnable) {
        EventBus.getDefault().post(isEnable ? OrderEvent.PULL_RESULT_ENABLE : OrderEvent.PULL_RESULT_UNABLE);
    }

    private void showCancelCarDialog() {
        if (mCancelCarDialog == null) {
            mCancelCarDialog = new AlertDialog.Builder(getActivity()).setTitle("确定取消叫车吗？")
                    .setNegativeButton("点错了", null)
                    .setPositiveButton("取消订单", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cancelCallCar();
                        }
                    }).show();
        } else {
            mCancelCarDialog.show();
        }
    }

    //司机接单后的通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderInfo event) {
        Log.i(TAG, "onMessageEvent: OrderInfo");
        if (TextUtils.equals(event.getOrderUuid(), mOrderUuid) && mCurrentStatus == STATUS_IS_CALLING) {
            setPullOrderResult(false);
            mOndoingOrder = event;
            removeTimeTask();
            textBeginTime.setText("已被接单\n订单编号：" + event.getOrderUuid());
        }
    }

    private void isCanCancelCar() {
        IsCancelCarParam param = new IsCancelCarParam();
        param.setOrderUuid(mOrderUuid);
        wrapHttp(mHttpManager.getPassengerService().isCancelCar(param))
                .compose(this.<OrderInfo>bindToLifecycle())
                .subscribe(new ResultObserver<OrderInfo>(getActivity(), "正在取消订单...", true) {
                    @Override
                    public void onSuccess(OrderInfo value) {
                    }
                });
    }

    private void cancelCallCar() {
        CancelCarParam cancelCarParam = new CancelCarParam();
        cancelCarParam.setOrderUuid(mOrderUuid);
        wrapHttp(mHttpManager.getPassengerService().cancelCar(cancelCarParam))
                .compose(this.<CancelCarResult>bindToLifecycle())
                .subscribe(new ResultObserver<CancelCarResult>(getActivity(), "正在取消订单...", true) {
                    @Override
                    public void onSuccess(CancelCarResult value) {
                        setViewStatus(STATUS_READY_CALL);
                        setPullOrderResult(false);
                        mOndoingOrder = null;
                        removeTimeTask();
                    }
                });
    }

    private void removeTimeTask() {
        //移除下单计时器
        if (mTimeDisposable != null && !mTimeDisposable.isDisposed()) {
            mTimeDisposable.dispose();
            textBeginTime.setText("");
        }
    }

    private void showTimeDialog() {
        if (mTimePickerView == null) {
            mTimePickerView = DialogUtis.createTimePickView(getActivity(), new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    textTime.setText(DateUtils.getCurrentTime(date));
                }
            });
        }
        if (!mTimePickerView.isShowing())
            mTimePickerView.show();
    }

    private void startActivityForResult(int requestCityCode) {
        Intent intent = new Intent(getContext(), SearchAddressActivity.class);
        if (mStartPoiInfo != null) {
            intent.putExtra(Constants.CITY_INFO, mStartPoiInfo);
        }
        startActivityForResult(intent, requestCityCode);
    }

    //重新定位，移动到定位位置
    private void refreshLocation() {
        StartLocationEvent event = new StartLocationEvent(true, true);
        EventBus.getDefault().post(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationResultEvent event) {
        EventBus.getDefault().post(new StartLocationEvent(false));
        if (event.isLocationSuccess()) {
            moveMapToCurrentLocation();
        } else {
            ToastUtils.showString(event.getErrorInfo());
        }
    }

    private void moveMapToCurrentLocation() {
        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(new LatLng(LocationService.latitude, LocationService.longitude), ZOOM_SCALE_VALUE);
        mAMap.animateCamera(cameraUpate);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CITY_START || requestCode == REQUEST_CITY_END) {
                setAddress(requestCode, data);
            }
        }
    }

    private void setAddress(int requestCode, Intent intent) {
        PoiInfo poiInfo = intent.getParcelableExtra(SearchAddressActivity.RESULT_ADDRESS);
        if (requestCode == REQUEST_CITY_START) {
            mStartPoiInfo = poiInfo;
            textStart.setText(mStartPoiInfo.getTitle());
            mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mStartPoiInfo.getLatitude(), mStartPoiInfo.getLongitude()), ZOOM_SCALE_VALUE));
            // setStartMarker();
            //   isNeedLocation = false;
        } else if (requestCode == REQUEST_CITY_END) {
            isNeedLocation = false;
            mTargetPoiInfo = poiInfo;
            setViewStatus(STATUS_READY_CALL);
            textEnd.setText(mTargetPoiInfo.getTitle());
            textResult.setText("共计60大洋");
            setTargetMarker();
        }
    }

    @NonNull
    private MarkerOptions getStartMarkerOptions() {
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(new LatLng(mStartPoiInfo.getLatitude(), mStartPoiInfo.getLongitude()));
        View mAddressView = LayoutInflater.from(getContext()).inflate(R.layout.map_location, null);
        TextView mAddressText = (TextView) mAddressView.findViewById(R.id.text_location);
        ImageView mAddressImgIndicator = (ImageView) mAddressView.findViewById(R.id.img_indicate);
        ImageView mAddressImgPoint = (ImageView) mAddressView.findViewById(R.id.img_point);
        mAddressText.setText(mStartPoiInfo.getTitle());
        mAddressImgIndicator.setImageResource(R.mipmap.map_start);
        mAddressImgPoint.setImageResource(R.mipmap.map_start_point);
        markOptiopns.icon(BitmapDescriptorFactory.fromView(mAddressView));
        return markOptiopns;
    }

    @NonNull
    private MarkerOptions getTargetMarkerOptions() {
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(new LatLng(mTargetPoiInfo.getLatitude(), mTargetPoiInfo.getLongitude()));
        View mAddressView = LayoutInflater.from(getContext()).inflate(R.layout.map_location, null);
        TextView mAddressText = (TextView) mAddressView.findViewById(R.id.text_location);
        ImageView mAddressImgIndicator = (ImageView) mAddressView.findViewById(R.id.img_indicate);
        ImageView mAddressImgPoint = (ImageView) mAddressView.findViewById(R.id.img_point);
        mAddressText.setText(mTargetPoiInfo.getTitle());
        mAddressImgIndicator.setImageResource(R.mipmap.map_end);
        mAddressImgPoint.setImageResource(R.mipmap.map_end_point);
        markOptiopns.icon(BitmapDescriptorFactory.fromView(mAddressView));
        return markOptiopns;
    }

    private void setStartMarker() {
        mAMap.clear();
        mAMap.addMarker(getStartMarkerOptions());
        //  addMarkerInScreenCenter();
        mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mStartPoiInfo.getLatitude(), mStartPoiInfo.getLongitude()), ZOOM_SCALE_VALUE));
    }

    private void setTargetMarker() {
        mAMap.clear();
        ArrayList<MarkerOptions> optionses = new ArrayList<MarkerOptions>();
        optionses.add(getTargetMarkerOptions());
        optionses.add(getStartMarkerOptions());
        mAMap.addMarkers(optionses, false);

        LatLng endLatLng = new LatLng(mTargetPoiInfo.getLatitude(), mTargetPoiInfo.getLongitude());
        LatLng startLatLng = new LatLng(mStartPoiInfo.getLatitude(), mStartPoiInfo.getLongitude());
        LatLngBounds lngBounds = new LatLngBounds.Builder()
                .include(endLatLng)
                .include(startLatLng)
                .build();
        int bottom = CommonUtils.dp2px(getContext(), 200);
        int padding = CommonUtils.dp2px(getContext(), 50);
        int top = CommonUtils.dp2px(getContext(), 75);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(lngBounds, padding, padding, top, bottom));//左右上下的PADDING
//        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(lngBounds,bottom,bottom,top));//宽高和PADDING
        mAMapManager.routeCaculate(startLatLng, endLatLng, this);
    }


    private void showBuottomDialog() {
        if (bottomSheetDialog == null) {
            bottomSheetDialog = new BottomSheetDialog(getActivity());
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bottom_map_des, null);
            bottomSheetDialog.setContentView(view);
            final BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
            view.measure(0, 0);
            bottomSheetBehavior.setPeekHeight(view.getMeasuredHeight());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            bottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            });
        }
        bottomSheetDialog.show();
    }

    /**
     * 地图加载完成后回调
     */
    @Override
    public void onMapLoaded() {
        addMarkerInScreenCenter();
    }

    /**
     * 地图中心点移动时回调
     */
    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (isNeedLocation) {
            textStart.setText(getString(R.string.is_locationing));
        }
    }

    /**
     * 地图中心点移动结束时回调
     */
    @Override
    public void onCameraChangeFinish(CameraPosition cameraPosition) {
        if (isNeedLocation) {
            searchPoiInfo(cameraPosition.target);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        drawRoute(driveRouteResult, i);
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    private void drawRoute(DriveRouteResult result, int errorCode) {
        mAMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    final DrivePath drivePath = result.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            getContext(), mAMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.setRouteWidth(12F);
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    int taxiCost = (int) result.getTaxiCost();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                    textResult.setText(des + "\n" + "打车约" + taxiCost + "元");
                } else if (result.getPaths() == null) {
                    ToastUtils.showString("没有结果");
                }
            } else {
                ToastUtils.showString("没有结果");
            }
        } else {
            ToastUtils.showInt(errorCode);
        }
    }

    @Override
    public void onMyLocationChange(Location location) {
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAlertDialog != null) {
            mAlertDialog.dismiss();
            mAlertDialog = null;
        }
        EventBus.getDefault().unregister(this);
        mapView.onDestroy();
    }

    //点击返回键且不是第一层时调用
    public void onBackCallback() {
        if (mCurrentStatus == STATUS_IS_CALLING) {
            setViewStatus(STATUS_READY_CALL);
            setPullOrderResult(false);
            mOndoingOrder = null;
            removeTimeTask();
//            if (mOndoingOrder != null) {
//                ToastUtils.showString("司机已接单，不允许取消");
//            } else {
//                showCancelCarDialog();
//            }
        } else if (mCurrentStatus == STATUS_READY_CALL) {
            setViewStatus(STATUS_SELECT_ADDRESS);
            isNeedLocation = true;
            mTargetPoiInfo = null;
            mAMap.clear();
            mAMap.setMyLocationStyle(mMyLocationStyle);//重新显示定位蓝点
            addMarkerInScreenCenter();

            wrapAsync(Observable.timer(500, TimeUnit.MILLISECONDS)).subscribe(new ResultObserver<Long>() {
                @Override
                public void onSuccess(Long value) {
                    moveMapToCurrentLocation();
                }
            });
        } else {
            doExit();
        }
    }

    private long exitTime = 0;

    private void doExit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showString("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            BaseApplication.getInstance().closeApplication();
        }
    }

    private void resetToolBar(boolean isRoot) {
        MainActivity activity = (MainActivity) getActivity();
        activity.updateDrawerToggle(isRoot);
    }

    private OnLocationChangedListener mOnLocationChangedListener;

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mOnLocationChangedListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mOnLocationChangedListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
