package bus.passenger.module.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import bus.passenger.R;
import bus.passenger.base.Constants;
import bus.passenger.bean.OrderInfo;
import bus.passenger.bean.OrderStatus;
import bus.passenger.module.AMapFragment;
import bus.passenger.service.PassengerService;


/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 */

public class OrderOngoingrFragment extends AMapFragment {

    private OrderInfo mOrderInfo;
    private Polyline polyline;
    private Marker mPassengerMarker;
    private PolylineOptions polylineOptions;
    private LatLng mPassengerLatlng;

    public OrderOngoingrFragment() {
    }

    public static OrderOngoingrFragment newInstance(OrderInfo orderInfo) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.ORDER_INFO, orderInfo);
        OrderOngoingrFragment fragment = new OrderOngoingrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mOrderInfo = arguments.getParcelable(Constants.ORDER_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onMapCreated() {
        super.onMapCreated();
        mPassengerLatlng = new LatLng(PassengerService.latitude, PassengerService.longitude);
        addPassengerMarker(mAMap, mPassengerLatlng);

//        addStartEndMark(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), mOrderInfo.getOriginAddress()
//                , new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), mOrderInfo.getDestAddress());
        //   routeCaculate(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null);
    }

    private List<LatLng> driveList = new ArrayList<>();
    private SmoothMoveMarker smoothMoveMarker;

    private void smoothMarker(LatLng latLng) {
        driveList.add(latLng);
        if (driveList.size() == 3) {
            driveList.remove(0);
        }
        if (driveList.size() < 2) return;

        if (smoothMoveMarker == null) {
            smoothMoveMarker = new SmoothMoveMarker(mAMap);
            smoothMoveMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car));
            smoothMoveMarker.setPoints(driveList);
            smoothMoveMarker.setTotalDuration(3);
        } else {
            smoothMoveMarker.setPoints(driveList);
        }

        smoothMoveMarker.startSmoothMove();
    }

    private void drawLine(LatLng latLng) {
        if (polyline == null) {
            polylineOptions = new PolylineOptions();
            polylineOptions.add(latLng);
            polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.custtexture));
            polylineOptions.width(18);
            polylineOptions.useGradient(true);
            polyline = mAMap.addPolyline(polylineOptions);
        } else {
            polylineOptions.add(latLng);
            polyline.setOptions(polylineOptions);
        }
        if (!polyline.getOptions().isVisible()) {
            polyline.getOptions().visible(true);
        }
        if (!polyline.isVisible()) {
            polyline.setVisible(true);
        }
    }

    /**
     * 添加乘客Marker
     */
    public void addPassengerMarker(AMap aMap, LatLng latLng) {
        if (mPassengerMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_location))
                    .position(latLng);
            mPassengerMarker = aMap.addMarker(markerOptions);
        } else {
            mPassengerMarker.setPosition(latLng);
        }
    }

    /**
     * 第一次收到司机定位时，根据司机定位和乘客定位缩放到合适位置
     */
    private boolean isFirst = true;

    //订单状态改变通知
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderStatus event) {
        if (TextUtils.equals(event.getOrderUuid(), mOrderInfo.getOrderUuid()) && event.getSubStatus() > 100) {
            if (event.getLat() > 0 && event.getLng() > 0) {
                LatLng latLng = new LatLng(event.getLat(), event.getLng());
                smoothMarker(latLng);
                drawLine(latLng);
                if (isFirst) {
                    LatLngBounds build = LatLngBounds.builder().include(latLng).include(mPassengerLatlng).build();
                    mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(build, 150));
                    isFirst = false;
                }
            }
        }
    }
}
