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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import bus.passenger.R;
import bus.passenger.base.BaseApplication;
import bus.passenger.bean.PoiInfo;
import bus.passenger.data.AMapManager;
import bus.passenger.module.AMapFragment;
import bus.passenger.module.DaggerCommonComponent;
import bus.passenger.overlay.AMapUtil;
import bus.passenger.overlay.DrivingRouteOverlay;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import lhy.lhylibrary.http.ObserverResult;
import lhy.lhylibrary.utils.ImageFactory;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.view.tablayout.SegmentTabLayout;
import lhy.lhylibrary.view.tablayout.listener.OnTabSelectListener;

import static lhy.lhylibrary.utils.RxUtils.wrapAsync;

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


    @BindView(R.id.mapView)
    TextureMapView mapView;
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

    @Inject
    AMapManager mAMapManager;
    @Inject
    PoiInfo mLocatedPoiInfo;//地图定位的用户位置(不为空)
    PoiInfo mStartPoiInfo;//用户当前选择的起始位置
    PoiInfo mTargetPoiInfo;//用户当前选择的目的位置


    private AMap mAMap;
    private Marker mScreenMarker;
    private BottomSheetDialog bottomSheetDialog;
    private String[] mTitles = {"现在", "预约"};

    /**
     * 移动地图中心点时，是否需要定位 （用于定位超始地点），当起点终点都定位完成时，应该改为false
     */
    private boolean isNeedLocation = true;
    private int mTextTimeHeight;
    private MyLocationStyle mMyLocationStyle;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DaggerCommonComponent.builder().applicationComponent(BaseApplication.getApplicationComponent()).build().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, null);
        ButterKnife.bind(this, rootView);
        mapView.onCreate(savedInstanceState);
        initView();
        initMap();
        initLocation();
        return rootView;
    }


    private void initView() {
        btnCommit.setVisibility(View.GONE);
        mTextTimeHeight = textTime.getLayoutParams().height;
        textTime.getLayoutParams().height = 0;
        textTime.requestLayout();

        textStart.setText(getString(R.string.is_locationing));
        tabLayout.setTabData(mTitles);
        tabLayout.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 1) {
                    showTextTime(true);
                } else {
                    showTextTime(false);
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    private void showTextTime(boolean b) {
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
//        mMyLocationStyle.interval(INTERVAL_lOCATION);
        mMyLocationStyle.showMyLocation(true);
        mAMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_SCALE_VALUE));
        mAMap.setMyLocationStyle(mMyLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        mAMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void searchPoiInfo(LatLng target) {
        wrapAsync(mAMapManager.search(target))
                .compose(this.<PoiInfo>bindToLifecycle())
                .subscribe(new ObserverResult<PoiInfo>(true) {
                    @Override
                    public void onSuccess(PoiInfo value) {
                        mStartPoiInfo = value;
                        textStart.setText(mStartPoiInfo.getTitle());
                    }
                });
    }


    /**
     * 在屏幕中心添加一个Marker
     */
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

    @OnClick({R.id.text_start, R.id.text_end, R.id.ibtn_refresh, R.id.btn_commit})
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
                callCar();
                break;
        }
    }

    private void callCar() {
        ToastUtils.showString("呼叫转车");
    }

    private void showTimeDialog() {
        //显示预约时间
    }

    private void startActivityForResult(int requestCityCode) {
        Intent intent = new Intent(getContext(), SearchAddressActivity.class);
        startActivityForResult(intent, requestCityCode);
    }

    //重新定位，移动到定位位置
    private void refreshLocation() {
        if (mLocatedPoiInfo != null) {
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(new LatLng(mLocatedPoiInfo.getLatitude(), mLocatedPoiInfo.getLongitude()), ZOOM_SCALE_VALUE);
            mAMap.animateCamera(cameraUpate);
        }
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
            textEnd.setText(mTargetPoiInfo.getTitle());
            showResultView();
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

        LatLngBounds lngBounds = new LatLngBounds.Builder()
                .include(new LatLng(mTargetPoiInfo.getLatitude(), mTargetPoiInfo.getLongitude()))
                //   .include(new LatLng(mLocatedPoiInfo.getLatitude(), mLocatedPoiInfo.getLongitude()))
                .include(new LatLng(mStartPoiInfo.getLatitude(), mStartPoiInfo.getLongitude()))
                .build();
        int bottom = CommonUtils.dp2px(getContext(), 200);
        int padding = CommonUtils.dp2px(getContext(), 50);
        int top = CommonUtils.dp2px(getContext(), 75);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(lngBounds, padding, padding, top, bottom));//左右上下的PADDING
//        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(lngBounds,bottom,bottom,top));//宽高和PADDING
        //    mAMapManager.routeCaculate(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()), new LatLng(poiInfo.getLatitude(), poiInfo.getLongitude()), this);
        //  addMarkerInScreenCenter();
    }

    private void showResultView() {
        resetToolBar();
        llAddress.setVisibility(View.GONE);
        llResult.setVisibility(View.VISIBLE);
        btnCommit.setVisibility(View.VISIBLE);
    }

    private void resetToolBar() {
        MainActivity activity = (MainActivity) getActivity();
        activity.updateDrawerToggle(false);
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
        mLocatedPoiInfo.setLatitude(location.getLatitude());
        mLocatedPoiInfo.setLongitude(location.getLongitude());
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    //点返回键时调用
    public void refreshView() {
        mAMap.clear();
        mAMap.setMyLocationStyle(mMyLocationStyle);//重新显示定位蓝点
        addMarkerInScreenCenter();

        wrapAsync(Observable.timer(500, TimeUnit.MILLISECONDS)).subscribe(new ObserverResult<Long>() {
            @Override
            public void onSuccess(Long value) {
                refreshLocation();
            }
        });
        llAddress.setVisibility(View.VISIBLE);
        llResult.setVisibility(View.GONE);
        textResult.setText("");
        textEnd.setText("");
        btnCommit.setVisibility(View.GONE);
        isNeedLocation = true;
        mTargetPoiInfo = null;
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
}
