package bus.passenger.module.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bus.passenger.R;
import bus.passenger.base.Constants;
import bus.passenger.bean.OrderInfo;
import bus.passenger.module.AMapFragment;
import bus.passenger.service.PassengerService;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;


/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 */

public class OrderOngoingrFragment extends AMapFragment {

    private OrderInfo mOrderInfo;
    private Polyline polyline;
    private Marker mPassengerMarker;
    private PolylineOptions polylineOptions;

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


    @Override
    protected void onMapCreated() {
        super.onMapCreated();
        addPassengerMarker(mAMap, new LatLng(PassengerService.latitude, PassengerService.longitude));
//        addStartEndMark(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), mOrderInfo.getOriginAddress()
//                , new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), mOrderInfo.getDestAddress());
        //   routeCaculate(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null);
        test();
    }

    double lon = 113.372861;
    double lat = 23.122778;

    private void test() {
        Flowable.interval(3, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        lon += 0.005;
                        lat += 0.005;
                        LatLng latLng = new LatLng(lat, lon);
                        smoothMarker(latLng);
                        drawLine(latLng);
                       // animateCamera(latLng);
                    }
                });
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


    //    private List<LatLng> mMoveList = new ArrayList<>();
//    private void drawRoute(LatLng latLng) {
//        mMoveList.add(latLng);
//        if (polyline == null) {
//            PolylineOptions polylineOptions = new PolylineOptions();
//            polylineOptions.setPoints(mMoveList);
//            polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.custtexture));
//            polylineOptions.width(18);
//            polylineOptions.useGradient(true);
//            polyline = mAMap.addPolyline(polylineOptions);
//        } else {
//            polyline.setPoints(mMoveList);//连续画线
//        }
//        if (!polyline.getOptions().isVisible()) {
//            polyline.getOptions().visible(true);
//        }
//        if (!polyline.isVisible()) {
//            polyline.setVisible(true);
//        }
//    }
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
}
