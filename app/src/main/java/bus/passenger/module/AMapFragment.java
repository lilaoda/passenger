package bus.passenger.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import lhy.lhylibrary.base.LhyFragment;

/**
 * Created by Liheyu on 2017/9/12.
 * Email:liheyu999@163.com
 */

public class AMapFragment extends LhyFragment implements AMapLocationListener, AMap.OnPOIClickListener, AMap.OnMarkerClickListener {

    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private AMapLocation mAMapLocation;
    private Marker mGrowMarker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initLocationClient();
    }

    private void initLocationClient() {
        mlocationClient = new AMapLocationClient(getContext());
        mLocationOption = new AMapLocationClientOption();
        mlocationClient.setLocationListener(this);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setInterval(2000); //设置定位间隔,单位毫秒,默认为2000ms
        mlocationClient.setLocationOption(mLocationOption);   //设置定位参数
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mlocationClient.onDestroy();
    }

    protected void startlocation(boolean flag) {
        if (flag) {
            mlocationClient.startLocation();
        } else {
            mlocationClient.stopLocation();
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                this.mAMapLocation = aMapLocation;
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getLatitude();//获取纬度
                aMapLocation.getLongitude();//获取经度
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                aMapLocation.getAoiName();//获取当前定位点的AOI信息
            } else {
                //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Override
    public void onPOIClick(Poi poi) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public AMapLocation getAMapLocation() {
        return mAMapLocation;
    }

    /**
     * 添加一个从地上生长的Marker
     */
    public void addGrowMarker(AMap aMap,LatLng latLng) {
        if (mGrowMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(latLng);
            mGrowMarker = aMap.addMarker(markerOptions);
        }
        startGrowAnimation(mGrowMarker);
    }

    private void startGrowAnimation(Marker marker) {
        if (marker != null) {
            Animation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(1000);
            marker.setAnimation(animation);
            marker.startAnimation();
        }
    }

    protected void startNavi(Poi start,Poi end,AmapNaviType naviType){
        AmapNaviParams amapNaviParams = new AmapNaviParams(start, null, end, naviType);
        AmapNaviPage.getInstance().showRouteActivity(getContext(), amapNaviParams, null);
    }
}
