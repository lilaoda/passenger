package bus.passenger.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lilaoda on 2017/10/9.
 * Email:749948218@qq.com
 * 我的行程里 乘客的历史订单
 */

public class OrderInfo implements Parcelable {


    /**
     * passengerUuid : ec38b002a87841d7bca69a19d2998690
     * orderUuid : 5a1a36923f5045ef862e6ec934558c84
     * actualPasNam : null
     * actualPasMob : null
     * actualPasNum : null
     * leaveTime : null
     * mapType : null
     * originCityUuid : null
     * originBuscircleUuid : null
     * originLng : 143
     * originLat : 132
     * originCity : null
     * originAddress : 广州科韵路
     * originDetailAddress : null
     * destCityUuid : null
     * destBuscircleUuid : null
     * destLng : 145
     * destLat : 142
     * destCity : null
     * destAddress : 广州黄埔区
     * destDetailAddress : null
     * mainStatus : 1
     * subStatus : 100
     */

    private String passengerUuid;
    private String orderUuid;
    private String actualPasNam;
    private String actualPasMob;
    private String actualPasNum;
    private String leaveTime;
    private int mapType;
    private String originCityUuid;
    private String originBuscircleUuid;
    private double originLng;
    private double originLat;
    private String originCity;
    private String originAddress;
    private String originDetailAddress;
    private String destCityUuid;
    private String destBuscircleUuid;
    private double destLng;
    private double destLat;
    private String destCity;
    private String destAddress;
    private String destDetailAddress;
    private int mainStatus;
    /**
     * 订单子状态(100.等待应答（拼车中） 200.等待接驾-预约 201.等待接驾-已出发未到达 202.等待接驾-已到达 210.出发接乘客 220.司机到达等待乘客 300.行程开始 301到达目的地400.待支付 500.已完成(待评价) 501.已完成-已评价 600.取消-自主取消 601.取消-后台取消 602.取消-应答前取消)
     */
    private int subStatus;

    protected OrderInfo(Parcel in) {
        passengerUuid = in.readString();
        orderUuid = in.readString();
        actualPasNam = in.readString();
        actualPasMob = in.readString();
        actualPasNum = in.readString();
        leaveTime = in.readString();
        mapType = in.readInt();
        originCityUuid = in.readString();
        originBuscircleUuid = in.readString();
        originLng = in.readDouble();
        originLat = in.readDouble();
        originCity = in.readString();
        originAddress = in.readString();
        originDetailAddress = in.readString();
        destCityUuid = in.readString();
        destBuscircleUuid = in.readString();
        destLng = in.readDouble();
        destLat = in.readDouble();
        destCity = in.readString();
        destAddress = in.readString();
        destDetailAddress = in.readString();
        mainStatus = in.readInt();
        subStatus = in.readInt();
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    public String getPassengerUuid() {
        return passengerUuid;
    }

    public void setPassengerUuid(String passengerUuid) {
        this.passengerUuid = passengerUuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getActualPasNam() {
        return actualPasNam;
    }

    public void setActualPasNam(String actualPasNam) {
        this.actualPasNam = actualPasNam;
    }

    public String getActualPasMob() {
        return actualPasMob;
    }

    public void setActualPasMob(String actualPasMob) {
        this.actualPasMob = actualPasMob;
    }

    public String getActualPasNum() {
        return actualPasNum;
    }

    public void setActualPasNum(String actualPasNum) {
        this.actualPasNum = actualPasNum;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public String getOriginCityUuid() {
        return originCityUuid;
    }

    public void setOriginCityUuid(String originCityUuid) {
        this.originCityUuid = originCityUuid;
    }

    public String getOriginBuscircleUuid() {
        return originBuscircleUuid;
    }

    public void setOriginBuscircleUuid(String originBuscircleUuid) {
        this.originBuscircleUuid = originBuscircleUuid;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getOriginDetailAddress() {
        return originDetailAddress;
    }

    public void setOriginDetailAddress(String originDetailAddress) {
        this.originDetailAddress = originDetailAddress;
    }

    public String getDestCityUuid() {
        return destCityUuid;
    }

    public void setDestCityUuid(String destCityUuid) {
        this.destCityUuid = destCityUuid;
    }

    public String getDestBuscircleUuid() {
        return destBuscircleUuid;
    }

    public void setDestBuscircleUuid(String destBuscircleUuid) {
        this.destBuscircleUuid = destBuscircleUuid;
    }

    public double getDestLng() {
        return destLng;
    }

    public void setDestLng(double destLng) {
        this.destLng = destLng;
    }

    public double getDestLat() {
        return destLat;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getDestDetailAddress() {
        return destDetailAddress;
    }

    public void setDestDetailAddress(String destDetailAddress) {
        this.destDetailAddress = destDetailAddress;
    }

    public int getMainStatus() {
        return mainStatus;
    }

    public void setMainStatus(int mainStatus) {
        this.mainStatus = mainStatus;
    }

    public int getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(int subStatus) {
        this.subStatus = subStatus;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(passengerUuid);
        dest.writeString(orderUuid);
        dest.writeString(actualPasNam);
        dest.writeString(actualPasMob);
        dest.writeString(actualPasNum);
        dest.writeString(leaveTime);
        dest.writeInt(mapType);
        dest.writeString(originCityUuid);
        dest.writeString(originBuscircleUuid);
        dest.writeDouble(originLng);
        dest.writeDouble(originLat);
        dest.writeString(originCity);
        dest.writeString(originAddress);
        dest.writeString(originDetailAddress);
        dest.writeString(destCityUuid);
        dest.writeString(destBuscircleUuid);
        dest.writeDouble(destLng);
        dest.writeDouble(destLat);
        dest.writeString(destCity);
        dest.writeString(destAddress);
        dest.writeString(destDetailAddress);
        dest.writeInt(mainStatus);
        dest.writeInt(subStatus);
    }
}
