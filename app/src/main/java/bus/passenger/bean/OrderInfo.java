package bus.passenger.bean;

/**
 * Created by Lilaoda on 2017/10/9.
 * Email:749948218@qq.com
 * 我的行程里 乘客的历史订单
 */

public class OrderInfo {


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
    private int subStatus;

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
}
